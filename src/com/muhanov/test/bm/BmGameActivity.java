package com.muhanov.test.bm;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;

public class BmGameActivity extends MenuGameActivity {
    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;

    private float C(final int c) {
        return c / 255f;
    }

    private Camera mCamera;
    private TextureRegion mBubbleTextureRegion;

    @Override
    public void onLoadComplete() {
        // do nothing
    }

    @Override
    public Engine onLoadEngine() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera));
    }

    @Override
    public void onLoadResources() {
        BitmapTextureAtlas texture = new BitmapTextureAtlas(128, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        mBubbleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture,
                this, "bubble.png", 0, 0);
        mEngine.getTextureManager().loadTexture(texture);
    }

    @Override
    public Scene onLoadScene() {
        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(C(64), C(159), C(255)));

        final Sprite bubble = new Sprite(0, 100, mBubbleTextureRegion);
        bubble.registerUpdateHandler(new TimerHandler(0.01f, new InternalTimerCallback(bubble)));
        
        final Sprite bubble2 = new Sprite(bubble.getX() + 2 * bubble.getWidth(), 100,
                mBubbleTextureRegion);
        
        final Sprite bubble3 = new Sprite(bubble.getX() + 4 * bubble.getWidth(), 100,
                mBubbleTextureRegion);
        
        final Sprite bubble4 = new Sprite(bubble.getX() + 3 * bubble.getWidth(), bubble.getY() + 1
                * bubble.getHeight(), mBubbleTextureRegion);
        
        scene.attachChild(bubble);
        scene.attachChild(bubble2);
        scene.attachChild(bubble3);
        scene.attachChild(bubble4);
        
        scene.registerUpdateHandler(new InternalSceneUpdateHandler());
        return scene;
    }
    
    private class InternalSceneUpdateHandler implements IUpdateHandler {
        

        @Override
        public void onUpdate(float pSecondsElapsed) {
            final Scene scene = mEngine.getScene();
            IShape e0 = (IShape)scene.getChild(0);
            IShape e1 = (IShape)scene.getChild(1);
            if (e0.collidesWith(e1)){
                e0.clearUpdateHandlers();
                e1.registerUpdateHandler(new TimerHandler(0.01f, new InternalTimerCallback(e1)));
            }
        }

        @Override
        public void reset() {
        }
        
    }

    /**
     * Change bubble position on the scene.
     *
     */
    private class InternalTimerCallback implements ITimerCallback {
        private IShape mShape;

        public InternalTimerCallback(IShape shape) {
            mShape = shape;
        }
        
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
            float x = mShape.getX();
            float y = mShape.getY();
            mShape.setPosition(++x, y);
            pTimerHandler.reset();
        }
    }
}