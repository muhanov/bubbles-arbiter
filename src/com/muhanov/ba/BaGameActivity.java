package com.muhanov.ba;

import java.util.ArrayList;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.collision.CollisionHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.view.Display;

import com.muhanov.entity.ITouchEntity;
import com.muhanov.entity.collision.BaCollisionHandler;
import com.muhanov.entity.collision.CircleCollisionCallback;
import com.muhanov.entity.collision.DummyCollisionCallback;
import com.muhanov.entity.collision.HeroCollisionCallback;
import com.muhanov.entity.collision.HeroLackCollisionCallback;
import com.muhanov.entity.sprite.Circle;
import com.muhanov.entity.sprite.Hero;
import com.muhanov.entity.util.ProjectionsMap;
import com.muhanov.level.BaLoader;

public class BaGameActivity extends MenuGameActivity {
    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    private int mWidth;
    private int mHeight;

    private float C(final int c) {
        return c / 255f;
    }

    private Camera mCamera;
    private BaLoader mLevelLoader;
    private Hero mHero;

    @Override
    public Engine onLoadEngine() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        Display d = getWindowManager().getDefaultDisplay();
        mWidth = d.getWidth();
        mHeight = d.getHeight();
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        engineOptions.getTouchOptions().enableRunOnUpdateThread();
        return new Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {
        BitmapTextureAtlas texture = new BitmapTextureAtlas(128, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        TextureRegion mBubbleTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(texture, this, "bubble.png", 0, 0);

        setItemBgTextureRegion(mBubbleTextureRegion);
        mEngine.getTextureManager().loadTextures(texture);
        loadMenuTextures();
    }

    @Override
    public Scene onLoadScene() {
        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(C(64), C(159), C(255)));
        scene.setOnSceneTouchListener(new InternalOnSceneTouchListener());
        scene.registerUpdateHandler(new InternalSceneUpdateHandler());
        createMenuScene(mCamera);
        mLevelLoader = new BaLoader(this, scene, mEngine.getTextureManager());
        return scene;
    }

    @Override
    public void onLoadComplete() {
        openMenu();
    }

    @Override
    public void loadLevel(int levelId) {
        mLevelLoader.loadLevel(this, levelId);
        mHero = mLevelLoader.getHero();
    }

    private class InternalOnSceneTouchListener implements IOnSceneTouchListener {

        private Rectangle mCursor;

        private void handleCursor(final Scene scene, final TouchEvent event) {
            float w = 70f;
            float h = 70f;
            float sx = -0.5f * w;
            float sy = -0.5f * h;
            switch (event.getAction()) {
            case TouchEvent.ACTION_DOWN:
                mCursor = new Rectangle(event.getX() + sx, event.getY() + sy, w, h);
                mCursor.setColor(C(0), C(255), C(0), 0.4f);
                scene.attachChild(mCursor);
                break;
            case TouchEvent.ACTION_UP:
                scene.detachChild(mCursor);
                mCursor = null;
                PhysicsHandler ph = mHero.getPhysicsHandler();
                ph.setVelocity(100, 0);
                mHero.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 8, 15, true);
                mHero.setEnabled(true);
                break;
            case TouchEvent.ACTION_MOVE:
                mCursor.setPosition(event.getX() + sx, event.getY() + sy);
                break;
            }
        }

        @Override
        public boolean onSceneTouchEvent(Scene scene, TouchEvent event) {
            boolean result = false;
            handleCursor(scene, event);
            int count = scene.getChildCount();
            for (int i = 0; i < count; ++i) {
                IShape shape = (IShape) scene.getChild(i);
                result = shape.contains(event.getX(), event.getY());
                if (result) {
                    if (shape instanceof ITouchEntity) {
                        ITouchEntity te = (ITouchEntity) shape;
                        te.touch();
                    }
                    break;
                }
            }
            return result;
        }
    }

    private class InternalSceneUpdateHandler implements IUpdateHandler {
        private final CircleCollisionCallback mCircleCb = new CircleCollisionCallback();
        private final DummyCollisionCallback mDummyCb = new DummyCollisionCallback();
        private final HeroCollisionCallback mHeroCb = new HeroCollisionCallback();
        private final HeroLackCollisionCallback mHeroLackCb = new HeroLackCollisionCallback();

        @Override
        public void onUpdate(float pSecondsElapsed) {
            final ProjectionsMap pm = new ProjectionsMap();
            final Scene scene = mEngine.getScene();

            final ArrayList<IShape> p = buildProjectionsMap(scene, pm);
            final int size = p.size();
            for (int i = 0; i < size; ++i) {
                IShape shape = p.get(i);
                BaCollisionHandler ch = createCollisionHandler(shape, p);
                ch.onUpdate(pSecondsElapsed);
            }
        }

        @Override
        public void reset() {
            // do nothing
        }

        private BaCollisionHandler createCollisionHandler(final IShape shape,
                final ArrayList<IShape> entities) {
            BaCollisionHandler ch = null;
            if (shape instanceof Circle) {
                ch = new BaCollisionHandler(mCircleCb, shape, entities);
            } else if (shape instanceof Hero) {
                ch = new BaCollisionHandler(mHeroCb, mHeroLackCb, shape, entities);
            } else {
                ch = new BaCollisionHandler(mDummyCb, shape, entities);
            }
            return ch;
        }

        private ArrayList<IShape> buildProjectionsMap(final Scene scene, final ProjectionsMap pm) {
            for (int i = 0; i < scene.getChildCount(); ++i) {
                IShape s = (IShape) scene.getChild(i);
                pm.addEntity(s);
            }
            return pm.buildMap();
        }
    }

}