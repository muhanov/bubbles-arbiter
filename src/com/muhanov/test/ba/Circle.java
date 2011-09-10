package com.muhanov.test.ba;

import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Circle extends PhysicalSprite {
    private float mRadius;

    public Circle(float x, float y, TextureRegion texture) {
        super(x, y, texture);
        mRadius = 0.5f * getWidth();
    }

    public float getRadius() {
        return mRadius;
    }

    public float getCenterX() {
        return getX() + 0.5f * getWidth();
    }

    public float getCenterY() {
        return getY() + 0.5f * getHeight();
    }

    @Override
    public boolean collidesWith(IShape other) {
        final Circle c = (Circle) other;
        float r = getRadius() + c.getRadius();
        float l = calculateCenterSpacing(c);
        return l <= r;
    }

    private float calculateCenterSpacing(Circle c) {
        float lx = getCenterX() - c.getCenterX();
        float ly = getCenterY() - c.getCenterY();
        return (float) Math.hypot(lx, ly);
    }
}
