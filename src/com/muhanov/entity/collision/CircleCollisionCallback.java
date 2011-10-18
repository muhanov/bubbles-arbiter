package com.muhanov.entity.collision;

import org.anddev.andengine.engine.handler.collision.ICollisionCallback;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.shape.IShape;

import com.muhanov.entity.sprite.Circle;

public class CircleCollisionCallback implements ICollisionCallback {

    @Override
    public boolean onCollision(IShape pCheckShape, IShape pTargetShape) {
        if (pCheckShape == pTargetShape) {
            return true;
        }
        final Circle c1 = (Circle) pCheckShape;
        if (pTargetShape instanceof Circle) {
            final Circle c2 = (Circle) pTargetShape;
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
            // c1.setEnabled(true);

            float x2 = c2.getX() + (vx2 - vx1) * dTc;
            float y2 = c2.getY() + (vy2 - vy1) * dTc;
            ph2.setVelocity(vx1, vy1);
            c2.setPosition(x2, y2);
            // c2.setEnabled(true);
        }
        return false;
    }
}
