package com.wangshuo.wslive.wslivedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import me.lake.librestreaming.core.listener.RESScreenShotListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.FishEyeFilterHard;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;

/**
 * Created by WangShuo on 2018/2/26.
 */

public class LiveUI implements View.OnClickListener {
    private final LiveActivity         mLiveActivity;
    private final StreamLiveCameraView mLiveCameraView;
    private ImageView            mImageView;
    private String               mRtmpUrl = "";

    /*
    boolean isFilter = false;
    boolean isMirror = false;
    */

    public LiveUI(LiveActivity liveActivity, StreamLiveCameraView liveCameraView, String mRtmpUrl) {
        this.mLiveActivity = liveActivity;
        this.mLiveCameraView = liveCameraView;
        this.mRtmpUrl = mRtmpUrl;

        init();
        //mLiveCameraView.swapCamera();
    }

    private void init() {
        int btnIds[] = {
                R.id.btn_startStreaming,
                R.id.btn_stopStreaming,
                R.id.btn_swapCamera
                //btn_startRecord, btn_stopRecord, btn_filter, btn_screenshot, btn_mirror
        };
        for (int id : btnIds) {
            Button btn = (Button)mLiveActivity.findViewById(id);
            btn.setOnClickListener(this);
        }

        mImageView = (ImageView)mLiveActivity.findViewById(R.id.iv_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_startStreaming: // 开始推流
                mLiveCameraView.startStreaming(mRtmpUrl);
                break;
            case R.id.btn_stopStreaming: // 停止推流
                mLiveCameraView.stopStreaming();
                break;
            case R.id.btn_swapCamera: // 切换摄像头
                mLiveCameraView.swapCamera();
                break;
            /*
            case R.id.btn_startRecord: // 开始录制
                mLiveCameraView.startRecord();
                break;
            case R.id.btn_stopRecord: // 停止录制
                mLiveCameraView.stopRecord();
                break;
            case R.id.btn_filter: // 切换滤镜
                BaseHardVideoFilter baseHardVideoFilter = null;
                if (isFilter) {
                    baseHardVideoFilter = new GPUImageCompatibleFilter(new GPUImageBeautyFilter());
                } else {
                    //baseHardVideoFilter = new FishEyeFilterHard();
                    baseHardVideoFilter = new GPUImageCompatibleFilter(new GPUImageFilter());
                }
                mLiveCameraView.setHardVideoFilter(baseHardVideoFilter);
                isFilter = !isFilter;
                break;
            case R.id.btn_screenshot: // 截帧
                mLiveCameraView.takeScreenShot(new RESScreenShotListener() {
                    @Override
                    public void onScreenShotResult(Bitmap bitmap) {
                        if (bitmap != null) {
                            mImageView.setVisibility(View.VISIBLE);
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });
                break;
            case R.id.btn_mirror: // 镜像
                isMirror = !isMirror;
                mLiveCameraView.setMirror(true, isMirror, isMirror);
                break;
            */
            default:
                break;
        }
    }
}
