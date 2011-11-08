package com.muhanov.entity.sprite;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.shape.IShape;
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

    @Override
    public boolean collidesWith(IShape other) {
        boolean res = false;
        if (this == other) {
            return res;
        }
        if (other instanceof Circle) {
            final Circle circle = (Circle) other;
            float closestX = clamp(circle.getX(), getX(), getX() + getWidthScaled());
            float closestY = clamp(circle.getY(), getY(), getY() + getHeightScaled());

            // Calculate the distance between the circle's center and this
            // closest point
            float distanceX = circle.getX() - closestX;
            float distanceY = circle.getY() - closestY;

            // If the distance is less than the circle's radius, an intersection
            // occurs
            float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
            res = distanceSquared < (circle.getRadius() * circle.getRadius());
        } else {
            res = super.collidesWith(other);
        }
        return res;
    }

    private float clamp(float input, float min, float max) {
        return (input < min) ? min : (input > max) ? max : input;
    }
}
