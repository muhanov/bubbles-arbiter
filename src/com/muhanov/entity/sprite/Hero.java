package com.muhanov.entity.sprite;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Hero extends PhysicalAnimatedSprite {

    public Hero(float pX, float pY, TiledTextureRegion pTiledTextureRegion) {
        super(pX, pY, pTiledTextureRegion);
    }
    
    public void doFall() {
        if (!isEnabled()) {
            return;
        }
        final PhysicsHandler ph = getPhysicsHandler();
        ph.setVelocity(10, 300);
    }
}
