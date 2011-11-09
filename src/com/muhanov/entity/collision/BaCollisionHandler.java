package com.muhanov.entity.collision;

import java.util.ArrayList;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.collision.ICollisionCallback;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.util.ListUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 12:19:35 - 11.03.2010
 */
public class BaCollisionHandler implements IUpdateHandler {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final ICollisionCallback mCollisionCallback;
    private final ILackCollisionCallback mLackCollisionCallback;
    private final IShape mCheckShape;
    private final ArrayList<? extends IShape> mTargetStaticEntities;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BaCollisionHandler(final ICollisionCallback pCollisionCallback,
            final IShape pCheckShape, final ArrayList<? extends IShape> pTargetStaticEntities) throws IllegalArgumentException {
        this(pCollisionCallback, null, pCheckShape, pTargetStaticEntities);
    }

    public BaCollisionHandler(final ICollisionCallback pCollisionCallback,
            final ILackCollisionCallback pLackCollisionCallback, final IShape pCheckShape,
            final IShape pTargetShape) throws IllegalArgumentException {
        this(pCollisionCallback, pLackCollisionCallback, pCheckShape, ListUtils
                .toList(pTargetShape));
    }

    public BaCollisionHandler(final ICollisionCallback pCollisionCallback,
            final ILackCollisionCallback pLackCollisionCallback, final IShape pCheckShape,
            final ArrayList<? extends IShape> pTargetStaticEntities)
            throws IllegalArgumentException {
        if (pCollisionCallback == null) {
            throw new IllegalArgumentException("pCollisionCallback must not be null!");
        }
        if (pCheckShape == null) {
            throw new IllegalArgumentException("pCheckShape must not be null!");
        }
        if (pTargetStaticEntities == null) {
            throw new IllegalArgumentException("pTargetStaticEntities must not be null!");
        }

        this.mCollisionCallback = pCollisionCallback;
        this.mLackCollisionCallback = pLackCollisionCallback;
        this.mCheckShape = pCheckShape;
        this.mTargetStaticEntities = pTargetStaticEntities;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onUpdate(final float pSecondsElapsed) {
        final IShape checkShape = this.mCheckShape;
        final ArrayList<? extends IShape> staticEntities = this.mTargetStaticEntities;
        final int staticEntityCount = staticEntities.size();

        boolean hasCollisions = false;
        for (int i = 0; i < staticEntityCount; i++) {
            if (checkShape.collidesWith(staticEntities.get(i))) {
                hasCollisions = true;
                final boolean proceed = this.mCollisionCallback.onCollision(checkShape,
                        staticEntities.get(i));
                if (!proceed) {
                    return;
                }
            }
        }
        if (mLackCollisionCallback != null && hasCollisions == false) {
            mLackCollisionCallback.onLackCollision(checkShape);
        }
    }

    @Override
    public void reset() {
        // do nothing
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
