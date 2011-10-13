package com.muhanov.entity.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.anddev.andengine.entity.shape.IShape;

public class ProjectionsMap {
    private ArrayList<IShape> mInitialMap;
    private ArrayList<IShape> mSelectedMap;

    public ProjectionsMap() {
        mInitialMap = new ArrayList<IShape>();
    }

    public void addEntity(final IShape shape) {
        mInitialMap.add(shape);
    }

    public ArrayList<IShape> buildMap() {
        if (mInitialMap.size() < 2) {
            return mInitialMap;
        }
        mSelectedMap = new ArrayList<IShape>();
        Collections.sort(mInitialMap, new EntityComparator());
        // objects selection
        IShape s = null;
        for (int i = 0; i < mInitialMap.size() - 1; ++i) {
            IShape s1 = mInitialMap.get(i);
            IShape s2 = mInitialMap.get(i + 1);
            if (s2.getX() <= s1.getX() + s1.getWidth()) {
                if (s1 != s) {
                    mSelectedMap.add(s1);
                }
                mSelectedMap.add(s2);
                s = s2; // to track a possible take
            }
        }
        return mSelectedMap;
    }

    private class EntityComparator implements Comparator<IShape> {

        @Override
        public int compare(IShape s1, IShape s2) {
            float x1 = s1.getX();
            float y1 = s2.getX();
            float d1 = x1 - y1;
            if (d1 != 0) {
                return (d1 < 0) ? -1 : +1;
            }
            float x2 = x1 + s1.getWidth();
            float y2 = y1 + s2.getWidth();
            float d2 = x2 - y2;
            if (d2 != 0) {
                return (d2 < 0) ? -1 : +1;
            }
            return 0;
        }

    }
}
