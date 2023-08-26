package me.lake.librestreaming.ws;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lake.librestreaming.client.RESClient;
import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.core.listener.RESScreenShotListener;
import me.lake.librestreaming.core.listener.RESVideoChangeListener;
import me.lake.librestreaming.encoder.MediaAudioEncoder;
import me.lake.librestreaming.encoder.MediaEncoder;
import me.lake.librestreaming.encoder.MediaMuxerWrapper;
import me.lake.librestreaming.encoder.MediaVideoEncoder;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.model.Size;
import me.lake.librestreaming.tools.CameraUtil;
import me.lake.librestreaming.ws.filter.audiofilter.SetVolumeAudioFilter;

public class StreamLiveCameraView extends FrameLayout {
    private static final String TAG = "CCLive"; // "StreamLiveCameraView";
//  private static int quality_value_min = 400 * 1024;
//  private static int quality_value_max = 700 * 1024;
    private static RESClient  mResClient;
    private static RESConfig  mResConfig;

    private MediaMuxerWrapper mMuxer;
    private AspectTextureView mTextureView;
    private Context           mContext;
    private boolean           mIsRecording = false;
    private final List<RESConnectionListener> mStreamStateListeners = new ArrayList<>();

    public StreamLiveCameraView(Context context) {
        super(context);
        this.mContext = context;
    }

    public StreamLiveCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public static synchronized RESClient getRESClient() {
        if (mResClient == null) {
            mResClient = new RESClient();
        }
        return mResClient;
    }

    // 根据 AVOption 初始化 & 开始预览
    public void init(Context context, StreamAVOption avOption) {
        if (avOption == null) {
            throw new IllegalArgumentException("AVOption is null");
        }
        compatibleSize(avOption);
        mResClient = getRESClient();
        setContext(mContext);
        mResConfig = StreamConfig.build(context, avOption);
        boolean isSucceed = mResClient.prepare(context, mResConfig);
        if (!isSucceed) {
            Log.w(TAG, "推流 prepare() false, 状态异常");
            return;
        }
        initPreviewTextureView();
        addListenerAndFilter();
    }

    private void compatibleSize(StreamAVOption avOptions) {
        Camera.Size cameraSize = CameraUtil.getInstance().getBestSize(CameraUtil.getFrontCameraSize(), Integer.parseInt("800"));
        if (!CameraUtil.hasSupportedFrontVideoSizes) {
            if (null == cameraSize || cameraSize.width <= 0) {
                avOptions.videoWidth = 720;
                avOptions.videoHeight = 480;
            } else {
                avOptions.videoWidth = cameraSize.width;
                avOptions.videoHeight = cameraSize.height;
            }
        }
    }

    private void initPreviewTextureView() {
        if (mTextureView == null && mResClient != null) {
            mTextureView = new AspectTextureView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            this.removeAllViews();
            this.addView(mTextureView);
            mTextureView.setKeepScreenOn(true);
            mTextureView.setSurfaceTextureListener(surfaceTextureListenerImpl);
            Size s = mResClient.getVideoSize();
            mTextureView.setAspectRatio(AspectTextureView.MODE_OUTSIDE, ((double) s.getWidth() / s.getHeight()));
        }
    }

    private void addListenerAndFilter() {
        if (mResClient != null) {
            mResClient.setConnectionListener(ConnectionListener);
            mResClient.setVideoChangeListener(VideoChangeListener);
            mResClient.setSoftAudioFilter(new SetVolumeAudioFilter());
        }
    }

    public boolean isStreaming() {
        if (mResClient != null) {
            return mResClient.isStreaming;
        }
        return false;
    }

    public void startStreaming(String rtmpUrl) {
        if (mResClient != null && !isStreaming()) {
            mResClient.startStreaming(rtmpUrl);
        }
    }

    public void stopStreaming() {
        if (mResClient != null && isStreaming()) {
            mResClient.stopStreaming();
        }
    }

    public void startRecord() {
        if (mResClient != null && !isRecording()) {
            mResClient.setNeedResetEglContext(true);
            try {
                // not reached
                mMuxer = new MediaMuxerWrapper(".mp4"); // if you record audio only, ".m4a" is also OK.
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener,
                                      StreamAVOption.recordVideoWidth, StreamAVOption.recordVideoHeight);
                mMuxer.prepare();
                mMuxer.startRecording();
                mIsRecording = true;
                Toast.makeText(mContext, "开始录制视频", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                mIsRecording = false;
                //e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }
    }

    public String stopRecord() {
        mIsRecording = false;
        if (mMuxer != null && isRecording()) {
            String path = mMuxer.getFilePath();
            mMuxer.stopRecording();
            mMuxer = null;
            System.gc();
            Toast.makeText(mContext, "视频已保存至 /sdcard/Movies/WSLive/", Toast.LENGTH_LONG).show();
            return path;
        }
        System.gc();
        return null;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void swapCamera() {
        if (mResClient != null) {
            mResClient.swapCamera();
        }
    }

    // 摄像头焦距 [0.0f, 1.0f]
    public void setZoomByPercent(float targetPercent) {
        if (mResClient != null) {
            mResClient.setZoomByPercent(targetPercent);
        }
    }

    public void toggleFlashLight() {
        if (mResClient != null) {
            mResClient.toggleFlashLight();
        }
    }

    public void setVideoFPS(int fps) {
        if (mResClient != null) {
            mResClient.setVideoFPS(fps);
        }
    }

    public void setVideoBitRate(int bitrate) {
        if (mResClient != null) {
            mResClient.setVideoBitRate(bitrate);
        }
    }

    public void takeScreenShot(RESScreenShotListener listener) {
        if (mResClient != null) {
            mResClient.takeScreenShot(listener);
        }
    }

    /**
     * @param isEnableMirror        镜像功能总开关
     * @param isEnablePreviewMirror 预览镜像
     * @param isEnableStreamMirror  推流镜像
     */
    public void setMirror(boolean isEnableMirror, boolean isEnablePreviewMirror, boolean isEnableStreamMirror) {
        if (mResClient != null) {
            mResClient.setMirror(isEnableMirror, isEnablePreviewMirror, isEnableStreamMirror);
        }
    }

    public void setHardVideoFilter(BaseHardVideoFilter baseHardVideoFilter) {
        if (mResClient != null) {
            mResClient.setHardVideoFilter(baseHardVideoFilter);
        }
    }

    public float getSendBufferFreePercent() {
        return mResClient.getSendBufferFreePercent();
    }

    public int getAVSpeed() {
        return mResClient.getAVSpeed();
    }

    public void setContext(Context context) {
        if (mResClient != null) {
            mResClient.setContext(context);
        }
    }

    public void destroy() {
        if (mResClient != null) {
            mResClient.setConnectionListener(null);
            mResClient.setVideoChangeListener(null);
            if (mResClient.isStreaming) {
                mResClient.stopStreaming();
            }
            if (mIsRecording) {
                stopRecord();
            }
            mResClient.destroy();
        }
    }

    public void addStreamStateListener(RESConnectionListener listener) {
        if (listener != null && !mStreamStateListeners.contains(listener)) {
            mStreamStateListeners.add(listener);
        }
    }

    RESConnectionListener ConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            if (result == 1) {
                mResClient.stopStreaming();
            }

            for (RESConnectionListener listener : mStreamStateListeners) {
                listener.onOpenConnectionResult(result);
            }
        }

        @Override
        public void onWriteError(int errno) {
            for (RESConnectionListener listener : mStreamStateListeners) {
                listener.onWriteError(errno);
            }
        }

        @Override
        public void onCloseConnectionResult(int result) {
            for (RESConnectionListener listener : mStreamStateListeners) {
                listener.onCloseConnectionResult(result);
            }
        }
    };

    RESVideoChangeListener VideoChangeListener = new RESVideoChangeListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            if (mTextureView != null) {
                mTextureView.setAspectRatio(AspectTextureView.MODE_INSIDE, ((double) width) / height);
            }
        }
    };

    TextureView.SurfaceTextureListener surfaceTextureListenerImpl = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (mResClient != null) {
                mResClient.startPreview(surface, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (mResClient != null) {
                mResClient.updatePreview(width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (mResClient != null) {
                mResClient.stopPreview(true);
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    // callback methods from encoder
    MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder && mResClient != null)
                mResClient.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder && mResClient != null)
                mResClient.setVideoEncoder(null);
        }
    };
}
