package com.muhanov.test.ba;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class PhysicalSprite extends Sprite {

    private PhysicsHandler mPhysicsHandler;

    public PhysicalSprite(final Sprite sprite) {
        this(sprite.getX(), sprite.getY(), sprite.getTextureRegion());
    }
    
    public PhysicalSprite (float x, float y, TextureRegion texture) {
        super(x, y, texture);
        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);
    }
    
    public PhysicsHandler getPhysicsHandler() {
        return mPhysicsHandler;
    }
    
    @Override
    public void reset() {
        mPhysicsHandler.reset();
        super.reset();
    }
}
