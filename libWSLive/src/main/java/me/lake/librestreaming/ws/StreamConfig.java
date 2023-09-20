package me.lake.librestreaming.ws;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;

import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.model.Size;

import static me.lake.librestreaming.ws.StreamConfig.AVOptionsHolder.DEFAULT_FILTER_MODE;
import static me.lake.librestreaming.ws.StreamConfig.AVOptionsHolder.DEFAULT_RENDER_MODE;

public class StreamConfig {
    public static class AVOptionsHolder {
        public static final int DEFAULT_CAMERA_INDEX    = Camera.CameraInfo.CAMERA_FACING_BACK;
        public static final int DEFAULT_FILTER_MODE     = RESConfig.FilterMode.HARD;
        public static final int DEFAULT_RENDER_MODE     = RESConfig.RenderingMode.OpenGLES;
        public static final int DEFAULT_PREVIEW_WIDTH   = 1280;
        public static final int DEFAULT_PREVIEW_HEIGHT  = 720;
        public static final int DEFAULT_VIDEO_WIDTH     = 1280;
        public static final int DEFAULT_VIDEO_HEIGHT    = 720;
        public static final int DEFAULT_VIDEO_BITRATE   = 1024 * 1024; // 600 * 1024
        public static final int DEFAULT_VIDEO_FPS       = 24;
        public static final int DEFAULT_VIDEO_GOP       = 2;
    }

    public static RESConfig build(Context context, StreamAVOption option) {
        RESConfig res = RESConfig.obtain();
        res.setFilterMode(DEFAULT_FILTER_MODE);
        res.setRenderingMode(DEFAULT_RENDER_MODE);
        res.setTargetPreviewSize(new Size(option.previewWidth, option.previewHeight));
        res.setTargetVideoSize(new Size(option.videoWidth, option.videoHeight));
        res.setBitRate(option.videoBitRate);
        res.setVideoFPS(option.videoFrameRate);
        res.setVideoGOP(option.videoGOP);
        res.setDefaultCamera(option.cameraIndex);
        res.setRtmpAddr(option.streamUrl);

        int frontDirection, backDirection;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
        frontDirection = cameraInfo.orientation;
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
        backDirection = cameraInfo.orientation;

        // TODO: sensorLandscape 有的方向画面颠倒
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int mode1 = (frontDirection == 90
                            ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270
                            : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90
                        ); // | RESConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL;
            res.setFrontCameraDirectionMode(mode1);

            int mode2 = (backDirection == 90
                            ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90
                            : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270
                        );
            res.setBackCameraDirectionMode(mode2);
        }
        else {
            int mode1 =(backDirection == 90
                            ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0
                            : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180
                        );
            res.setBackCameraDirectionMode(mode1);

            int mode2 =(frontDirection == 90
                            ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180
                            : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0
                        ); // | RESConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL;
            res.setFrontCameraDirectionMode(mode2);
        }
        return res;
    }
}
