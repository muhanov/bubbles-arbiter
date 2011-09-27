package com.muhanov.test.ba;

import java.util.ArrayList;

import org.anddev.andengine.entity.scene.menu.animator.BaseMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;

public class GameMenuAnimator extends BaseMenuAnimator {
    final private int DEFAULT_RAW_LENGTH = 5;

    @Override
    public void buildAnimations(ArrayList<IMenuItem> pMenuItems, float pCameraWidth,
            float pCameraHeight) {
        // do nothing

    }

    @Override
    public void prepareAnimations(ArrayList<IMenuItem> pMenuItems, float pCameraWidth,
            float pCameraHeight) {
        float baseX = 70;
        float baseY = 70;
        for (int col = 0; col < pMenuItems.size(); ++col) {
            final IMenuItem menuItem = pMenuItems.get(col);
            float offsetX = baseX + 2 * (col % DEFAULT_RAW_LENGTH) * menuItem.getWidthScaled();
            float offsetY = baseY + 2 * (col / DEFAULT_RAW_LENGTH) * menuItem.getHeightScaled();
            menuItem.setPosition(offsetX, offsetY);
        }
    }

}
