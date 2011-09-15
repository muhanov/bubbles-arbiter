package com.muhanov.test.ba;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Menu;
import android.view.MenuItem;

abstract public class MenuGameActivity extends BaseGameActivity {
    private static final int MENU_RECET = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(final Menu pMenu) {
        pMenu.add(Menu.NONE, MENU_RECET, Menu.NONE, "Reset");
        return super.onCreateOptionsMenu(pMenu);
    }

    @Override
    public boolean onMenuItemSelected(final int pFeatureId, final MenuItem pItem) {
        switch (pItem.getItemId()) {
        case MENU_RECET:
            final Scene scene = mEngine.getScene();
            scene.reset();
            return true;
        default:
            return super.onMenuItemSelected(pFeatureId, pItem);
        }
    }

}
