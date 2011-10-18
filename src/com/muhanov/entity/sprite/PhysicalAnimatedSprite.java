package com.muhanov.entity.sprite;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.muhanov.entity.ITouchEntity;

public class PhysicalAnimatedSprite extends AnimatedSprite implements ITouchEntity {

    private PhysicsHandler mPhysicsHandler;
    protected float mInitialVelocityX;
    protected float mInitialVelocityY;

    public PhysicalAnimatedSprite(final AnimatedSprite sprite) {
        this(sprite.getX(), sprite.getY(), sprite.getTextureRegion());
    }

    public PhysicalAnimatedSprite(float x, float y, TiledTextureRegion texture) {
        this(x, y, 0.0f, 0.0f, texture);
    }

    public PhysicalAnimatedSprite(float x, float y, float vx, float vy, TiledTextureRegion texture) {
        super(x, y, texture);
        mInitialVelocityX = vx;
        mInitialVelocityY = vy;
        mPhysicsHandler = new PhysicsHandler(this);
        mPhysicsHandler.setEnabled(false);
        registerUpdateHandler(mPhysicsHandler);
        
    }
    
    public PhysicsHandler getPhysicsHandler() {
        return mPhysicsHandler;
    }

    public void setEnabled(boolean isEnabled) {
        mPhysicsHandler.setEnabled(isEnabled);
    }
    
    @Override
    public void touch() {
        //mPhysicsHandler.setVelocity(mInitialVelocityX, mInitialVelocityY);
        mPhysicsHandler.setEnabled(true);
    }
    
    @Override
    public void reset() {
        mPhysicsHandler.reset();
        mPhysicsHandler.setEnabled(false);
        super.reset();
    }

    public boolean isMoving() {
        return mPhysicsHandler.getVelocityX() != 0 || mPhysicsHandler.getVelocityY() != 0;
    }
    
}
