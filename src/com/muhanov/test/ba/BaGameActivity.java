package com.muhanov.test.ba;

import java.util.ArrayList;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;
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
    private TextureRegion mBubbleTextureRegion;

    @Override
    public void onLoadComplete() {
        // do nothing
    }

    @Override
    public Engine onLoadEngine() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        Display d = getWindowManager().getDefaultDisplay();
        mWidth = d.getWidth();
        mHeight = d.getHeight();
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

        final PhysicalSprite bubble = new Circle(0, 100, mBubbleTextureRegion);
        //bubble.getPhysicsHandler().setVelocityX(100f);
        //bubble.setIgnoreUpdate(true);
        
        final PhysicalSprite bubble2 = new Circle(bubble.getX() + 2 * bubble.getWidth(), 100,
                mBubbleTextureRegion);
        //bubble2.getPhysicsHandler().setVelocityX(-100f);
        //bubble2.setIgnoreUpdate(true);

        final PhysicalSprite bubble3 = new Circle(bubble.getX() + 4 * bubble.getWidth(), 100,
                mBubbleTextureRegion);
        //bubble3.getPhysicsHandler().setVelocityX(100f);
        //bubble3.setIgnoreUpdate(true);

        final Sprite bubble4 = new Circle(bubble.getX() + 3 * bubble.getWidth(), bubble.getY() + 1
                * bubble.getHeight(), mBubbleTextureRegion);

        final Sprite bubble5 = new Circle(bubble.getX() + 6 * bubble.getWidth(), 100,
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
        
        scene.attachChild(bubble);
        scene.attachChild(bubble2);
        scene.attachChild(bubble3);
        //scene.attachChild(bubble4);
        scene.attachChild(line);
        scene.attachChild(line2);
        scene.attachChild(line3);
        // scene.attachChild(bubble5);
        Log.e("", bubble + "," + bubble2 + "," + bubble3 + "," + bubble4);
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
                    //((PhysicalSprite) shape).setIgnoreUpdate(false);
                    PhysicsHandler ph = ((PhysicalSprite) shape).getPhysicsHandler();
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
            ProjectionsMap pm = new ProjectionsMap();
            final Scene scene = mEngine.getScene();

            ArrayList<IShape> p = buildProjectionsMap(scene, pm);
            
            String str = "";
            for (IShape s : p) {
                str += s + ",";
            }
            Log.e("", str);

            int next;
            for (int i = 0; i < p.size() - 1; ++i) {
                Circle c1 = (Circle) p.get(i);
                Circle c2 = (Circle) p.get(i + 1);
                if (c1.collidesWith(c2)) {
                    handleCollision(c1, c2);
                } else {
                    // Log.i("",
                    // "i="+i+", s1.x="+s1.getX()+", s1.y="+s1.getY()+", s2.x="+s2.getX()+", s2.y="+s2.getY());
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
            // current distance between centers
            float L = Circle.calculateCenterSpacing(c1, c2);
            // distance between centers at collision moment
            float L0 = c1.getRadius() + c2.getRadius();
            PhysicsHandler ph1 = c1.getPhysicsHandler();
            float vx1 = ph1.getVelocityX();
            float vy1 = ph1.getVelocityY();
            PhysicsHandler ph2 = c2.getPhysicsHandler();
            float vx2 = ph2.getVelocityX();
            float vy2 = ph2.getVelocityY();
            float v1 = (float)Math.hypot(vx1, vy1);
            float v2 = (float)Math.hypot(vx2, vy2);
            
            // calculate time at collision moment
            float dTc = (L - L0) / (v1 + v2);
            // get coordinates at collision moment
            float x1c = c1.getX() + vx1 * dTc;
            float y1c = c1.getY() + vy1 * dTc;
            float x1 = x1c - vx2 * dTc;
            float y1 = y1c - vy2 * dTc;
            c1.getPhysicsHandler().setVelocity(vx2, vy2);
            c1.setPosition(x1, y1);
            
            float x2c = c2.getX() + vx2 * dTc;
            float y2c = c2.getY() + vy2 * dTc;
            float x2 = x2c - vx1 * dTc;
            float y2 = y2c - vy1 * dTc;            
            c2.getPhysicsHandler().setVelocity(vx1, vy1);
            c2.setPosition(x2, y2);
        }

        private ArrayList<IShape> buildProjectionsMap(final Scene scene, final ProjectionsMap pm) {
            for (int i = 0; i < scene.getChildCount(); ++i) {
                IShape s = (IShape) scene.getChild(i);
/*                
                if (isOnScreen(s) == false) {
                    PhysicalSprite ps = (PhysicalSprite) s;
                    ps.getPhysicsHandler().setVelocity(0, 0);
                    continue;
                }
*/                
                if (s instanceof RectangularShape) {
                    pm.addEntity(s);
                }
            }
            return pm.buildMap();
        }
    }
}