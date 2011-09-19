package com.muhanov.test.ba;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.BaseRectangle;
import org.anddev.andengine.entity.sprite.BaseSprite;
import org.anddev.andengine.opengl.texture.region.BaseTextureRegion;
import org.anddev.andengine.opengl.texture.region.buffer.TextureRegionBuffer;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;

public class DoubleSprite extends BaseRectangle {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    protected final BaseTextureRegion mTextureRegion1;
    protected final BaseTextureRegion mTextureRegion2;

    // ===========================================================
    // Constructors
    // ===========================================================

    public DoubleSprite(final float pX, final float pY, final float pWidth, final float pHeight, final BaseTextureRegion pTextureRegion1, final BaseTextureRegion pTextureRegion2) {
        super(pX, pY, pWidth, pHeight);

        this.mTextureRegion1 = pTextureRegion1;
        this.mTextureRegion2 = pTextureRegion2;
        this.initBlendFunction();
    }

    public DoubleSprite(final float pX, final float pY, final float pWidth, final float pHeight, final BaseTextureRegion pTextureRegion1, final BaseTextureRegion pTextureRegion2, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pWidth, pHeight, pRectangleVertexBuffer);

        this.mTextureRegion1 = pTextureRegion1;
        this.mTextureRegion2 = pTextureRegion2;
        this.initBlendFunction();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void setFlippedHorizontal(final boolean pFlippedHorizontal) {
        this.mTextureRegion1.setFlippedHorizontal(pFlippedHorizontal);
        this.mTextureRegion2.setFlippedHorizontal(pFlippedHorizontal);
    }

    public void setFlippedVertical(final boolean pFlippedVertical) {
        this.mTextureRegion1.setFlippedVertical(pFlippedVertical);
        this.mTextureRegion2.setFlippedVertical(pFlippedVertical);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void reset() {
        super.reset();

        this.initBlendFunction();
    }

    @Override
    protected void onInitDraw(final GL10 pGL) {
        super.onInitDraw(pGL);
        GLHelper.enableTextures(pGL);
        GLHelper.enableTexCoordArray(pGL);
    }

    @Override
    protected void doDraw(final GL10 pGL, final Camera pCamera) {
        this.mTextureRegion1.onApply(pGL);
        super.doDraw(pGL, pCamera);
        this.mTextureRegion2.onApply(pGL);

        super.doDraw(pGL, pCamera);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        TextureRegionBuffer textureRegionBuffer = this.mTextureRegion1.getTextureBuffer();
        if(textureRegionBuffer.isManaged()) {
            textureRegionBuffer.unloadFromActiveBufferObjectManager();
        }
        textureRegionBuffer = this.mTextureRegion2.getTextureBuffer();
        if(textureRegionBuffer.isManaged()) {
            textureRegionBuffer.unloadFromActiveBufferObjectManager();
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void initBlendFunction() {
        if(this.mTextureRegion1.getTexture().getTextureOptions().mPreMultipyAlpha) {
            this.setBlendFunction(BLENDFUNCTION_SOURCE_PREMULTIPLYALPHA_DEFAULT, BLENDFUNCTION_DESTINATION_PREMULTIPLYALPHA_DEFAULT);
        }
        if(this.mTextureRegion2.getTexture().getTextureOptions().mPreMultipyAlpha) {
            this.setBlendFunction(BLENDFUNCTION_SOURCE_PREMULTIPLYALPHA_DEFAULT, BLENDFUNCTION_DESTINATION_PREMULTIPLYALPHA_DEFAULT);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
