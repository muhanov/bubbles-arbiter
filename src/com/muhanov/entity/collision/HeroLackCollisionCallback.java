package com.muhanov.entity.collision;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.shape.IShape;

import com.muhanov.entity.sprite.Hero;


public class HeroLackCollisionCallback implements ILackCollisionCallback {

    @Override
    public void onLackCollision(IShape checkShape) {
        final Hero hero = (Hero)checkShape;
        final PhysicsHandler ph = hero.getPhysicsHandler();
        ph.setVelocity(10, 300);
    }
}
