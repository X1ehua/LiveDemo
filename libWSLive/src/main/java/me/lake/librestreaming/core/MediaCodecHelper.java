package me.lake.librestreaming.core;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.io.IOException;

import me.lake.librestreaming.model.RESCoreParameters;
import me.lake.librestreaming.tools.LogTools;


public class MediaCodecHelper {
    public static MediaCodec createSoftVideoMediaCodec(RESCoreParameters coreParameters, MediaFormat videoFormat) {
        videoFormat.setString(MediaFormat.KEY_MIME, "video/avc");
        videoFormat.setInteger(MediaFormat.KEY_WIDTH, coreParameters.videoWidth);
        videoFormat.setInteger(MediaFormat.KEY_HEIGHT, coreParameters.videoHeight);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, coreParameters.mediaCodecAVCBitRate);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, coreParameters.mediaCodecAVCFrameRate);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, coreParameters.mediaCodecAVCIFrameInterval);
        videoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
        videoFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel31);
        videoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        MediaCodec result = null;
        try {
            result = MediaCodec.createEncoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
            //select color
            int[] colorful = result.getCodecInfo().getCapabilitiesForType(videoFormat.getString(MediaFormat.KEY_MIME)).colorFormats;
            int dstVideoColorFormat = -1;
            //select mediacodec colorformat
            if (isArrayContain(colorful, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)) {
                dstVideoColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
                coreParameters.mediaCodecAVCColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
            }
            if (dstVideoColorFormat == -1 && isArrayContain(colorful, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar)) {
                dstVideoColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
                coreParameters.mediaCodecAVCColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
            }
            if (dstVideoColorFormat == -1) {
                LogTools.e("!!!!!!!!!!!UnSupport,mediaCodecColorFormat");
                return null;
            }
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, dstVideoColorFormat);
            //selectprofile
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                MediaCodecInfo.CodecProfileLevel[] profileLevels = result.getCodecInfo().getCapabilitiesForType(videoFormat.getString(MediaFormat.KEY_MIME)).profileLevels;
//                if (isProfileContain(profileLevels, MediaCodecInfo.CodecProfileLevel.AVCProfileMain)) {
//                    coreParameters.mediaCodecAVCProfile = MediaCodecInfo.CodecProfileLevel.AVCProfileMain;
//                    coreParameters.mediaCodecAVCLevel = MediaCodecInfo.CodecProfileLevel.AVCLevel31;
//                } else {
//                    coreParameters.mediaCodecAVCProfile = MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline;
//                    coreParameters.mediaCodecAVCLevel = MediaCodecInfo.CodecProfileLevel.AVCLevel31;
//                }
//                videoFormat.setInteger(MediaFormat.KEY_PROFILE, coreParameters.mediaCodecAVCProfile);
//                //level must be set even below M
//                videoFormat.setInteger(MediaFormat.KEY_LEVEL, coreParameters.mediaCodecAVCLevel);
//            }
        } catch (IOException e) {
            LogTools.trace(e);
            return null;
        }
        return result;
    }

    public static MediaCodec createAudioMediaCodec(RESCoreParameters coreParameters, MediaFormat audioFormat) {
        //Audio
        MediaCodec result;
        audioFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, coreParameters.mediaCodecAACProfile);
        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, coreParameters.mediaCodecAACSampleRate);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, coreParameters.mediaCodecAACChannelCount);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, coreParameters.mediaCodecAACBitRate);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, coreParameters.mediaCodecAACMaxInputSize);
        LogTools.d("creatingAudioEncoder,format=" + audioFormat.toString());
        try {
            result = MediaCodec.createEncoderByType(audioFormat.getString(MediaFormat.KEY_MIME));
        } catch (Exception e) {
            LogTools.trace("can`t create audioEncoder!", e);
            return null;
        }
        return result;
    }

    public static MediaCodec createHardVideoMediaCodec(RESCoreParameters coreParameters, MediaFormat videoFormat) {
        videoFormat.setString(MediaFormat.KEY_MIME, "video/avc");
        videoFormat.setInteger(MediaFormat.KEY_WIDTH, coreParameters.videoWidth);
        videoFormat.setInteger(MediaFormat.KEY_HEIGHT, coreParameters.videoHeight);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, coreParameters.mediaCodecAVCBitRate);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, coreParameters.mediaCodecAVCFrameRate);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, coreParameters.mediaCodecAVCIFrameInterval);
        videoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
        videoFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel31);
        videoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        videoFormat.setInteger(MediaFormat.KEY_COMPLEXITY, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);//added by wangshuo
        MediaCodec result = null;
        try {
            result = MediaCodec.createEncoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
            //selectprofile
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                MediaCodecInfo.CodecProfileLevel[] profileLevels = result.getCodecInfo().getCapabilitiesForType(videoFormat.getString(MediaFormat.KEY_MIME)).profileLevels;
//                if (isProfileContain(profileLevels, MediaCodecInfo.CodecProfileLevel.AVCProfileMain)) {
//                    coreParameters.mediaCodecAVCProfile = MediaCodecInfo.CodecProfileLevel.AVCProfileMain;
//                    coreParameters.mediaCodecAVCLevel = MediaCodecInfo.CodecProfileLevel.AVCLevel31;
//                } else {
//                    coreParameters.mediaCodecAVCProfile = MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline;
//                    coreParameters.mediaCodecAVCLevel = MediaCodecInfo.CodecProfileLevel.AVCLevel31;
//                }
//                videoFormat.setInteger(MediaFormat.KEY_PROFILE, coreParameters.mediaCodecAVCProfile);
//                //level must be set even below M
//                videoFormat.setInteger(MediaFormat.KEY_LEVEL, coreParameters.mediaCodecAVCLevel);
//            }
        } catch (IOException e) {
            LogTools.trace(e);
            return null;
        }
        return result;
    }

    private static boolean isArrayContain(int[] src, int target) {
        for (int color : src) {
            if (color == target) {
                return true;
            }
        }
        return false;
    }

    private static boolean isProfileContain(MediaCodecInfo.CodecProfileLevel[] src, int target) {
        for (MediaCodecInfo.CodecProfileLevel color : src) {
            if (color.profile == target) {
                return true;
            }
        }
        return false;
    }
}