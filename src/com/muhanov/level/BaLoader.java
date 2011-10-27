package com.muhanov.level;

import java.util.ArrayList;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectGroup;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

import com.muhanov.entity.sprite.Circle;
import com.muhanov.entity.sprite.Hero;

public class BaLoader {
    private final Scene mScene;
    private final TextureManager mTextureManager;
    private final TMXLoader mLevelLoader;
    private TiledTextureRegion mHeroTextureRegion;
    private Hero mHero;

    public BaLoader(final Context context, final Scene scene, final TextureManager manager) {
        mScene = scene;
        mTextureManager = manager;
        mLevelLoader = new TMXLoader(context, mTextureManager,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA, null);
        loadResources(context, mTextureManager);
    }

    public void loadLevel(final Context context, int levelId) {
        final Scene scene = mScene;
        scene.reset();
        scene.detachChildren();
        TMXTiledMap tiledMap;
        try {
            tiledMap = mLevelLoader.loadFromAsset(context, "level/" + (levelId % 2 + 1) + ".tmx");
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
        correctHeroPosition();
    }
    
    public Hero getHero() {
        return mHero;
    }

    private void loadResources(final Context context, final TextureManager mTextureManager2) {
        BitmapTextureAtlas textureHero = new BitmapTextureAtlas(512, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mHeroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                textureHero, context, "cat.png", 0, 0, 8, 4);
        mTextureManager.loadTexture(textureHero);
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
            final Hero hero = new Hero(object.getX(), object.getY(), mHeroTextureRegion);
            hero.setCurrentTileIndex(8);
            hero.setScale(1.9f, 1.9f);
            hero.setScaleCenter(0, 0);
            mHero = hero;
            e = hero;
        }
        return e;
    }

    private void correctHeroPosition() {
        IShape shape = findCollisionObject(mHero);
        if (shape != null) {
            // has collision
            float x = mHero.getX();
            float y = shape.getY() - mHero.getHeightScaled();
            mHero.setPosition(x, y);
        } else {
            // no collisions
            mHero.getPhysicsHandler().setVelocity(0, 100);
            mHero.touch();
        }
    }

    private IShape findCollisionObject(final IShape obj) {
        IShape foundShape = null;
        final Scene scene = mScene;
        int count = scene.getChildCount();
        for (int i = 0; i < count; ++i) {
            IShape shape = (IShape) scene.getChild(i);
            if (shape != obj && obj.collidesWith(shape)) {
                foundShape = shape;
                break;
            }
        }
        return foundShape;
    }

}
