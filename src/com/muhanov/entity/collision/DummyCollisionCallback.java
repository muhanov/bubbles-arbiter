package com.muhanov.entity.collision;

import org.anddev.andengine.engine.handler.collision.ICollisionCallback;
import org.anddev.andengine.entity.shape.IShape;

public class DummyCollisionCallback implements ICollisionCallback {

    @Override
    public boolean onCollision(IShape pCheckShape, IShape pTargetShape) {
        return false;
    }

}
