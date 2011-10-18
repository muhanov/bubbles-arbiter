package com.muhanov.entity.sprite;

import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Circle extends PhysicalSprite {
    private float mRadius;

    public Circle(float x, float y, TextureRegion texture) {
        this(x, y, 0.0f, 0.0f, texture);
    }

    public Circle(float x, float y, float vx, float vy, TextureRegion texture) {
        super(x, y, vx, vy, texture);
        mRadius = 0.5f * getWidthScaled();
    }

    public float getRadius() {
        return mRadius;
    }

    public float getCenterX() {
        return getX() + 0.5f * getWidthScaled();
    }

    public float getCenterY() {
        return getY() + 0.5f * getHeightScaled();
    }

    @Override
    public boolean collidesWith(IShape other) {
        boolean res = false;
        if (other instanceof Circle) {
            final Circle c = (Circle) other;
            float r = getRadius() + c.getRadius();
            float l = calculateCenterSpacing(c);
            res = (l <= r);
        } else {
            res = super.collidesWith(other);
        }
        return res;
    }

    public static float calculateCenterSpacing(final Circle c1, final Circle c2) {
        float lx = c1.getCenterX() - c2.getCenterX();
        float ly = c1.getCenterY() - c2.getCenterY();
        return (float) Math.hypot(lx, ly);
    }

    private float calculateCenterSpacing(final Circle c) {
        return Circle.calculateCenterSpacing(this, c);
    }
}
