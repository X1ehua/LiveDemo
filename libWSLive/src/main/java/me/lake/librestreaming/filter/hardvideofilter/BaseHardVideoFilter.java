package me.lake.librestreaming.filter.hardvideofilter;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import me.lake.librestreaming.core.GLHelper;


public class BaseHardVideoFilter {
    protected int SIZE_WIDTH;
    protected int SIZE_HEIGHT;
    protected int directionFlag=-1;
    protected ShortBuffer drawIndexesBuffer;

    public void onInit(int VWidth, int VHeight) {
        SIZE_WIDTH = VWidth;
        SIZE_HEIGHT = VHeight;
        drawIndexesBuffer = GLHelper.getDrawIndecesBuffer();
    }

    public void onDraw(final int cameraTexture, final int targetFrameBuffer,
                       final FloatBuffer shapeBuffer, final FloatBuffer textureBuffer) {
    }

    public void onDestroy() {
    }

    public void onDirectionUpdate(int _directionFlag) {
        this.directionFlag = _directionFlag;
    }
}
