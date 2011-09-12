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

        final Sprite bubble = new Circle(0, 100, mBubbleTextureRegion);

        final Sprite bubble2 = new Circle(bubble.getX() + 2 * bubble.getWidth(), 100,
                mBubbleTextureRegion);

        final Sprite bubble3 = new Circle(bubble.getX() + 4 * bubble.getWidth(), 100,
                mBubbleTextureRegion);

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
        
        scene.attachChild(bubble);
        scene.attachChild(bubble2);
        scene.attachChild(bubble3);
        scene.attachChild(bubble4);
        scene.attachChild(line);
        scene.attachChild(line2);
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
                    PhysicsHandler ph = ((PhysicalSprite) shape).getPhysicsHandler();
                    ph.setVelocityX(300f);
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
            PhysicsHandler ph1 = c1.getPhysicsHandler();
            float vx1 = ph1.getVelocityX();
            float vy1 = ph1.getVelocityY();
            PhysicsHandler ph2 = c2.getPhysicsHandler();
            float vx2 = ph2.getVelocityX();
            float vy2 = ph2.getVelocityY();
            c2.getPhysicsHandler().setVelocity(vx1, vy1);
            c1.getPhysicsHandler().setVelocity(vx2, vy2);
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