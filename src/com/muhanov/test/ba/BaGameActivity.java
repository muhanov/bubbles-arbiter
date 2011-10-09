package com.muhanov.test.ba;

import java.io.File;
import java.util.ArrayList;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectGroup;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.primitive.Line;
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

import android.view.Display;

public class BaGameActivity extends MenuGameActivity {
    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    private int mWidth;
    private int mHeight;

    private float C(final int c) {
        return c / 255f;
    }

    private Camera mCamera;
    private TMXLoader mLevelLoader;
    private TextureRegion mBubbleTextureRegion;
    private TextureRegion mDouble;

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
        BitmapTextureAtlas texture2 = new BitmapTextureAtlas(128, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        mBubbleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture,
                this, "bubble.png", 0, 0);
        mDouble = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture2, this,
                "arrow.png", 0, 0);

        setItemBgTextureRegion(mBubbleTextureRegion);
        mEngine.getTextureManager().loadTexture(texture);
        mEngine.getTextureManager().loadTexture(texture2);
        loadMenuTextures();
    }

    @Override
    public Scene onLoadScene() {
        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(C(64), C(159), C(255)));
        scene.setOnSceneTouchListener(new InternalOnSceneTouchListener());
        scene.registerUpdateHandler(new InternalSceneUpdateHandler());
        createMenuScene(mCamera);
        mLevelLoader = new TMXLoader(this, mEngine.getTextureManager(),
                TextureOptions.BILINEAR_PREMULTIPLYALPHA, null);
        return scene;
    }

    @Override
    public void onLoadComplete() {
        openMenu();
    }

    @Override
    public void loadLevel(int levelId) {
        final Scene scene = mEngine.getScene();
        scene.reset();
        scene.detachChildren();
        TMXTiledMap tiledMap;
        try {
            tiledMap = mLevelLoader.loadFromAsset(this, "level/" + (levelId % 2 + 1) + ".tmx");
            ArrayList<TMXObjectGroup> groups = tiledMap.getTMXObjectGroups();
            TMXObjectGroup group = groups.get(0); // we have only single group
            for (TMXObject object : group.getTMXObjects()) {
                final TMXProperties<TMXObjectProperty> props = object.getTMXObjectProperties();
                IEntity c = createEntity(tiledMap, object, props);
                scene.attachChild(c);
            }
        } catch (final TMXLoadException e) {
            // do nothing
        }
    }

    private IEntity createEntity(final TMXTiledMap tiledMap, final TMXObject object,
            final TMXProperties<TMXObjectProperty> props) {
        final int gid = object.getGid();
        float vx = 0f;
        float vy = 0f;
        for (final TMXObjectProperty p : props) {
            if (p.getName().equals("vx")) {
                vx = Float.parseFloat(p.getValue());
            } else if (p.getName().equals("vy")) {
                vy = Float.parseFloat(p.getValue());
            }
        }
        IEntity e = null;
        String type = object.getType();
        if (type == null) {
            e = new Sprite(object.getX(), object.getY(), tiledMap
                    .getTextureRegionFromGlobalTileID(gid));
        } else if (type.equals("circle")) {
            e = new Circle(object.getX(), object.getY(), vx, vy, tiledMap
                    .getTextureRegionFromGlobalTileID(gid));
        } else if (type.equals("hero")) {
            e = new Sprite(object.getX(), object.getY(), tiledMap
                    .getTextureRegionFromGlobalTileID(gid));
            e.setScale(1.9f, 1.9f);
        }
        return e;
    }

    private void addChildren(final Scene scene) {
        final PhysicalSprite bubble = new Circle(0, 100, 100f, 0f, mBubbleTextureRegion);

        final PhysicalSprite bubble2 = new Circle(bubble.getX() + 2 * bubble.getWidth(), 100,
                -100f, 0, mBubbleTextureRegion);

        final PhysicalSprite bubble3 = new Circle(bubble.getX() + 4 * bubble.getWidth(), 100, 100f,
                0f, mBubbleTextureRegion);

        final Sprite bubble4 = new Circle(bubble.getX() + 3 * bubble.getWidth(), bubble.getY() + 1
                * bubble.getHeight(), -100f, -100f, mBubbleTextureRegion);

        final Sprite bubble5 = new Circle(bubble.getX() + 6 * bubble.getWidth(), 100, 0f, 0f,
                mBubbleTextureRegion);

        float lineX = bubble.getX() + 3 * bubble.getWidth();
        final Line line = new Line(lineX, 0, lineX, mHeight);
        line.setColor(C(255), C(0), C(0));
        float lineX2 = lineX + bubble.getWidth();
        final Line line2 = new Line(lineX2, 0, lineX2, mHeight);
        line2.setColor(C(255), C(0), C(0));
        float lineX3 = lineX - bubble.getWidth();
        final Line line3 = new Line(lineX3, 0, lineX3, mHeight);
        line3.setColor(C(255), C(0), C(0));

        final Sprite ds = new Circle(0, 300, 100f, 0f, mBubbleTextureRegion);
        final Sprite arrow = new Sprite(0, 0, mDouble);
        ds.attachChild(arrow);

        scene.attachChild(bubble);
        scene.attachChild(bubble2);
        scene.attachChild(bubble3);
        scene.attachChild(bubble4);
        scene.attachChild(bubble5);
        scene.attachChild(line);
        scene.attachChild(line2);
        scene.attachChild(line3);
        scene.attachChild(ds);
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
                    if (shape instanceof PhysicalSprite) {
                        PhysicalSprite ps = (PhysicalSprite) shape;
                        ps.update();
                    }
                    break;
                }
            }
            return result;
        }
    }

    private class InternalSceneUpdateHandler implements IUpdateHandler {

        @Override
        public void onUpdate(float pSecondsElapsed) {
            ProjectionsMap pm = new ProjectionsMap();
            final Scene scene = mEngine.getScene();

            ArrayList<IShape> p = buildProjectionsMap(scene, pm);
            final int size = p.size();
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    Circle c1 = (Circle) p.get(i);
                    Circle c2 = (Circle) p.get(j);
                    if (c1 != c2 && c1.collidesWith(c2)) {
                        handleCollision(c1, c2);
                    }
                }
            }
        }

        @Override
        public void reset() {
            // do nothing
        }

        private boolean isOnScreen(IShape shape) {
            return !(Math.abs(shape.getX()) > mWidth || Math.abs(shape.getY()) > mHeight);
        }

        private void handleCollision(final Circle c1, final Circle c2) {
            PhysicsHandler ph1 = c1.getPhysicsHandler();
            float vx1 = ph1.isEnabled() ? ph1.getVelocityX() : 0.0f;
            float vy1 = ph1.isEnabled() ? ph1.getVelocityY() : 0.0f;
            float v1 = (float) Math.hypot(vx1, vy1);
            PhysicsHandler ph2 = c2.getPhysicsHandler();
            float vx2 = ph2.isEnabled() ? ph2.getVelocityX() : 0.0f;
            float vy2 = ph2.isEnabled() ? ph2.getVelocityY() : 0.0f;
            float v2 = (float) Math.hypot(vx2, vy2);

            // current distance between centers
            float L = Circle.calculateCenterSpacing(c1, c2);
            // distance between centers at collision moment
            float L0 = c1.getRadius() + c2.getRadius();
            // calculate time at collision moment
            float dTc = (L - L0) / (v1 + v2);
            // get coordinates at collision moment
            float x1 = c1.getX() + (vx1 - vx2) * dTc;
            float y1 = c1.getY() + (vy1 - vy2) * dTc;
            ph1.setVelocity(vx2, vy2);
            c1.setPosition(x1, y1);
            c1.setEnabled(true);

            float x2 = c2.getX() + (vx2 - vx1) * dTc;
            float y2 = c2.getY() + (vy2 - vy1) * dTc;
            ph2.setVelocity(vx1, vy1);
            c2.setPosition(x2, y2);
            c2.setEnabled(true);
        }

        private ArrayList<IShape> buildProjectionsMap(final Scene scene, final ProjectionsMap pm) {
            for (int i = 0; i < scene.getChildCount(); ++i) {
                IShape s = (IShape) scene.getChild(i);
                /*
                 * if (isOnScreen(s) == false) { PhysicalSprite ps =
                 * (PhysicalSprite) s; ps.getPhysicsHandler().setVelocity(0, 0);
                 * continue; }
                 */
                if (s instanceof Circle) {
                    pm.addEntity(s);
                }
            }
            return pm.buildMap();
        }
    }
}