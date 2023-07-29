package me.lake.librestreaming.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import me.lake.librestreaming.tools.LogTools;


public class RESCoreParameters {
    public static final int FILTER_MODE_HARD = 1;
    public static final int FILTER_MODE_SOFT = 2;

    public static final int RENDERING_MODE_NATIVE_WINDOW = 1;
    public static final int RENDERING_MODE_OPENGLES = 2;
    /**
     * same with jni
     */
    public static final int FLAG_DIRECTION_FLIP_HORIZONTAL = 0x01;
    public static final int FLAG_DIRECTION_FLIP_VERTICAL = 0x02;
    public static final int FLAG_DIRECTION_ROATATION_0 = 0x10;
    public static final int FLAG_DIRECTION_ROATATION_90 = 0x20;
    public static final int FLAG_DIRECTION_ROATATION_180 = 0x40;
    public static final int FLAG_DIRECTION_ROATATION_270 = 0x80;

    public boolean done;
    public boolean printDetailMsg;
    public int filterMode;
    public int renderingMode;
    public String rtmpAddr;
    public int frontCameraDirectionMode;
    public int backCameraDirectionMode;
    public boolean isPortrait;
    public int previewVideoWidth;
    public int previewVideoHeight;
    public int videoWidth;
    public int videoHeight;
    public int videoFPS;
    public int videoGOP;
    public float cropRatio;
    public int previewColorFormat;
    public int previewBufferSize;
    public int mediaCodecAVCColorFormat;
    public int mediaCodecAVCBitRate;
    public int videoBufferQueueNum;
    public int audioBufferQueueNum;
    public int audioEncodeFormat;
    public int audioEncodeSampleRate;
    public int audioEncodeChannelConfig;
    public int audioEncodeSliceSize;
    public int audioEncodeSource;
    public int audioEncodeBufferSize;
    public int previewMaxFps;
    public int previewMinFps;
    public int mediaCodecAVCFrameRate;
    public int mediaCodecAVCIFrameInterval;
    public int mediaCodecAVCProfile;
    public int mediaCodecAVCLevel;

    public int mediaCodecAACProfile;
    public int mediaCodecAACSampleRate;
    public int mediaCodecAACChannelCount;
    public int mediaCodecAACBitRate;
    public int mediaCodecAACMaxInputSize;

    //sender
    public int senderQueueLength;

    public RESCoreParameters() {
        done = false;
        printDetailMsg = false;
        filterMode=-1;
        videoWidth = -1;
        videoHeight = -1;
        previewVideoWidth = 1280;
        previewVideoHeight = 720;
        videoFPS=-1;
        videoGOP=1;
        previewColorFormat = -1;
        mediaCodecAVCColorFormat = -1;
        mediaCodecAVCBitRate = -1;
        videoBufferQueueNum = -1;
        audioBufferQueueNum = -1;
        mediaCodecAVCFrameRate = -1;
        mediaCodecAVCIFrameInterval = -1;
        mediaCodecAVCProfile = -1;
        mediaCodecAVCLevel = -1;
        mediaCodecAACProfile = -1;
        mediaCodecAACSampleRate = -1;
        mediaCodecAACChannelCount = -1;
        mediaCodecAACBitRate = -1;
        mediaCodecAACMaxInputSize = -1;
    }

    public void dump() {
        LogTools.e(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResParameter:");
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                sb.append(field.getName());
                sb.append('=');
                sb.append(field.get(this));
                sb.append(';');
            } catch (IllegalAccessException e) {
            }
        }
        return sb.toString();
    }
}
