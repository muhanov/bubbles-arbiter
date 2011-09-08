package com.muhanov.test.ba;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.SmartList;

import android.util.Log;

public class BaGameActivity extends MenuGameActivity {
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
        scene.setOnSceneTouchListener(new InternalOnSceneTouchListener());
        scene.registerUpdateHandler(new InternalSceneUpdateHandler());
        
    	ProjectionsMap m = new ProjectionsMap();
        final Sprite bubble = new PhysicalSprite(0, 100, mBubbleTextureRegion);
        m.addEntity(bubble);
        
        final Sprite bubble2 = new PhysicalSprite(bubble.getX() + 2 * bubble.getWidth(), 100,
                mBubbleTextureRegion);
        m.addEntity(bubble2);

        final Sprite bubble3 = new PhysicalSprite(bubble.getX() + 4 * bubble.getWidth(), 100,
                mBubbleTextureRegion);
        m.addEntity(bubble3);
        
        final Sprite bubble4 = new PhysicalSprite(bubble.getX() + 3 * bubble.getWidth(), bubble.getY() + 1
                * bubble.getHeight(), mBubbleTextureRegion);
        m.addEntity(bubble4);

    	m.buildMap();

        scene.attachChild(bubble);
        scene.attachChild(bubble2);
        scene.attachChild(bubble3);
        scene.attachChild(bubble4);

        return scene;
    }
    
    private class InternalOnSceneTouchListener implements IOnSceneTouchListener {

        @Override
        public boolean onSceneTouchEvent(Scene scene, TouchEvent event) {
            boolean result = false;
            int count = scene.getChildCount();
            for (int i = 0; i < count; ++i) {
                IShape shape = (IShape) scene.getChild(i);
                result = shape.contains(event.getX(), event.getY());
                if (result) {
                    PhysicsHandler ph = ((PhysicalSprite)shape).getPhysicsHandler();
                    ph.setVelocityX(100f);
                    break;
                }
            }
            return result;
        }
    }
    
    private class InternalSceneUpdateHandler implements IUpdateHandler {
        

        @Override
        public void onUpdate(float pSecondsElapsed) {
/*
            final Scene scene = mEngine.getScene();
            IShape e0 = (IShape)scene.getChild(0);
            IShape e1 = (IShape)scene.getChild(1);
            if (e0.collidesWith(e1)){
                e0.clearUpdateHandlers();
                e1.registerUpdateHandler(new TimerHandler(0.01f, new InternalTimerCallback(e1)));
            }
*/            
        }

        @Override
        public void reset() {
        }
        
    }
}