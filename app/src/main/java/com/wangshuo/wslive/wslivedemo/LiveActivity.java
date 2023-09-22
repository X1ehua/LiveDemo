package com.wangshuo.wslive.wslivedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Set;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageFilter;
import me.lake.librestreaming.ws.filter.hardfilter.WatermarkFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;

public class LiveActivity extends AppCompatActivity {
    private final static String  TAG = "CCLive";
//  private final static String  mRtmpUrl = "rtmp://mozicode.com:2023/live/home";
    private final static String  mRtmpUrl = "rtmp://192.168.2.8:2023/live/home";
    private StreamLiveCameraView mLiveCameraView;
    private StreamAVOption       mStreamAVOption;
    private LiveUI               mLiveUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        StatusBarUtils.setTranslucentStatus(this);

        initLiveConfig();
        mLiveUI = new LiveUI(this, mLiveCameraView, mRtmpUrl);
    }

    public void initLiveConfig() {
        mStreamAVOption = new StreamAVOption(mRtmpUrl);

        mLiveCameraView = (StreamLiveCameraView)findViewById(R.id.stream_previewView);
        mLiveCameraView.init(this, mStreamAVOption);
        mLiveCameraView.addStreamStateListener(mConnectionListener);

        LinkedList<BaseHardVideoFilter> filters = new LinkedList<>();
        //files.add(new GPUImageCompatibleFilter(new GPUImageBeautyFilter()));
        filters.add(new GPUImageCompatibleFilter(new GPUImageFilter()));

        /*
        Rect rect = new Rect(100, 100, 200, 200);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.live);
        files.add(new WatermarkFilter(bitmap, rect));
        */
        mLiveCameraView.setHardVideoFilter(new HardVideoGroupFilter(filters));
    }

    RESConnectionListener mConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) { // 0: success, 1: failed
            String msg = result == 0 ? "Streaming started:\n" : "Start streaming failed:\n";
            Toast.makeText(LiveActivity.this, msg + mRtmpUrl, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWriteError(int err) {
            Toast.makeText(LiveActivity.this, "onWriteError: " + err, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCloseConnectionResult(int result) { // 0: success, 1: failed
            String ret = result == 0 ? "success" : "failed";
            Toast.makeText(LiveActivity.this, "onCloseConnection: " + ret, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveCameraView.destroy();
    }
}
