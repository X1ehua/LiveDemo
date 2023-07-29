package me.lake.librestreaming.client;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import me.lake.librestreaming.core.RESSoftAudioCore;
import me.lake.librestreaming.filter.softaudiofilter.BaseSoftAudioFilter;
import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.model.RESCoreParameters;
import me.lake.librestreaming.rtmp.RESFlvDataCollecter;
import me.lake.librestreaming.tools.LogTools;


public class RESAudioClient {
    RESCoreParameters resCoreParameters;
    private final Object syncOp = new Object();
    private AudioRecordThread audioRecordThread;
    private AudioRecord audioRecord;
    private byte[] audioBuffer;
    private RESSoftAudioCore softAudioCore;

    public RESAudioClient(RESCoreParameters parameters) {
        resCoreParameters = parameters;
    }

    public boolean prepare(RESConfig resConfig) {
        synchronized (syncOp) {
            resCoreParameters.audioBufferQueueNum = 5;
            softAudioCore = new RESSoftAudioCore(resCoreParameters);
            if (!softAudioCore.prepare(resConfig)) {
                LogTools.e("RESAudioClient,prepare");
                return false;
            }
            resCoreParameters.audioEncodeFormat = AudioFormat.ENCODING_PCM_16BIT;
            resCoreParameters.audioEncodeChannelConfig = AudioFormat.CHANNEL_IN_MONO;
            resCoreParameters.audioEncodeSliceSize = resCoreParameters.mediaCodecAACSampleRate / 10;
            resCoreParameters.audioEncodeBufferSize = resCoreParameters.audioEncodeSliceSize * 2;
            resCoreParameters.audioEncodeSource = MediaRecorder.AudioSource.DEFAULT;
            resCoreParameters.audioEncodeSampleRate = resCoreParameters.mediaCodecAACSampleRate;
            prepareAudio();
            return true;
        }
    }

    public boolean start(RESFlvDataCollecter flvDataCollecter) {
        synchronized (syncOp) {
            softAudioCore.start(flvDataCollecter);
            audioRecord.startRecording();
            audioRecordThread = new AudioRecordThread();
            audioRecordThread.start();
            LogTools.d("RESAudioClient,start()");
            return true;
        }
    }

    public boolean stop() {
        synchronized (syncOp) {
            if(audioRecordThread != null) {
                audioRecordThread.quit();
                try {
                    audioRecordThread.join();
                } catch (InterruptedException ignored) {
                }
                softAudioCore.stop();
                audioRecordThread = null;
                audioRecord.stop();
                return true;
            }
            return true;
        }
    }

    public boolean destroy() {
        synchronized (syncOp) {
            audioRecord.release();
            return true;
        }
    }
    public void setSoftAudioFilter(BaseSoftAudioFilter baseSoftAudioFilter) {
        softAudioCore.setAudioFilter(baseSoftAudioFilter);
    }
    public BaseSoftAudioFilter acquireSoftAudioFilter() {
        return softAudioCore.acquireAudioFilter();
    }

    public void releaseSoftAudioFilter() {
        softAudioCore.releaseAudioFilter();
    }

    @SuppressLint("MissingPermission")
    private boolean prepareAudio() {
        int minBufferSize = AudioRecord.getMinBufferSize(resCoreParameters.audioEncodeSampleRate,
                resCoreParameters.audioEncodeChannelConfig,
                resCoreParameters.audioEncodeFormat);
        audioRecord = new AudioRecord(resCoreParameters.audioEncodeSource,
                resCoreParameters.audioEncodeSampleRate,
                resCoreParameters.audioEncodeChannelConfig,
                resCoreParameters.audioEncodeFormat,
                minBufferSize * 5);
        audioBuffer = new byte[resCoreParameters.audioEncodeBufferSize];
        if (AudioRecord.STATE_INITIALIZED != audioRecord.getState()) {
            LogTools.e("audioRecord.getState()!=AudioRecord.STATE_INITIALIZED!");
            return false;
        }
        if (AudioRecord.SUCCESS != audioRecord.setPositionNotificationPeriod(resCoreParameters.audioEncodeSliceSize)) {
            LogTools.e("AudioRecord.SUCCESS != audioRecord.setPositionNotificationPeriod(" + resCoreParameters.audioEncodeSliceSize + ")");
            return false;
        }
        return true;
    }

    class AudioRecordThread extends Thread {
        private boolean isRunning = true;

        AudioRecordThread() {
            isRunning = true;
        }

        public void quit() {
            isRunning = false;
        }

        @Override
        public void run() {
            LogTools.d("AudioRecordThread,tid=" + Thread.currentThread().getId());
            while (isRunning) {
                int size = audioRecord.read(audioBuffer, 0, audioBuffer.length);
                if (isRunning && softAudioCore != null && size > 0) {
                    softAudioCore.queueAudio(audioBuffer);
                }
            }
        }
    }
}
