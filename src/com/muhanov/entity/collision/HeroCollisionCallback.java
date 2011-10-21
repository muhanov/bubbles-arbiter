package com.muhanov.entity.collision;

import org.anddev.andengine.engine.handler.collision.ICollisionCallback;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.shape.IShape;

import android.graphics.RectF;

import com.muhanov.entity.sprite.Hero;
import com.muhanov.entity.sprite.PhysicalSprite;

public class HeroCollisionCallback implements ICollisionCallback {
    private final static RectF mIntersection = new RectF();

    @Override
    public boolean onCollision(IShape pCheckShape, IShape pTargetShape) {
        if (pCheckShape == pTargetShape) {
            return true;
        }
        final Hero hero = (Hero) pCheckShape;
        final PhysicsHandler ph1 = hero.getPhysicsHandler();
        float vx1 = ph1.getVelocityX();
        float vy1 = ph1.getVelocityY();
        if (pTargetShape instanceof PhysicalSprite) {
            // TODO: collision with physical sprite
            return true;
        }
        float vx2 = 0.0f;
        float vy2 = 0.0f;

        float vx = vx1 + vx2;
        float vy = vy1 + vy2;
        final RectF intersection = getIntersection(pCheckShape, pTargetShape);
        if (intersection != null) {
            float h = intersection.height();
            float w = intersection.width();
            float s = h * vx - w * vy;
            float w0 = (vy == 0) ? w : -s / vy;
            float h0 = (vx == 0) ? h : s / vx;
            hero.setPosition(hero.getX() - w + w0, hero.getY() - h + h0);
            ph1.setVelocity(0, 0);
        }
        return true;
    }

    private RectF getIntersection(final IShape shape1, final IShape shape2) {
        float left = shape1.getX();
        float top = shape1.getY();
        float right = left + shape1.getWidthScaled();
        float bottom = top + shape1.getHeightScaled();
        mIntersection.set(left, top, right, bottom);
        left = shape2.getX();
        top = shape2.getY();
        right = left + shape2.getWidthScaled();
        bottom = top + shape2.getHeightScaled();
        boolean isIntersect = mIntersection.intersect(left, top, right, bottom);
        return (isIntersect) ? mIntersection : null;
    }
}
