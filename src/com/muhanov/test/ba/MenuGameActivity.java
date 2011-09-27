package com.muhanov.test.ba;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

abstract public class MenuGameActivity extends BaseGameActivity implements IOnMenuItemClickListener {
    private MenuScene mMenuScene;
    private BitmapTextureAtlas mMenuTexture;
    private TextureRegion mMenuItemBgRegion;
    
    public void openMenu() {
        mEngine.getScene().setChildScene(mMenuScene, false, true, true);
    }
    
    public void closeMenu() {
        mEngine.getScene().getChildScene().back();
    }
    
    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
            float pMenuItemLocalX, float pMenuItemLocalY) {
        int itemId = pMenuItem.getID();
        loadLevel(itemId);
        return true;
    }
    
    public abstract void loadLevel(int levelId);
    
    protected void setItemBgTextureRegion(final TextureRegion bg) {
        mMenuItemBgRegion = bg;
    }
    
    protected void loadMenuTextures() {
        mMenuTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mEngine.getTextureManager().loadTexture(mMenuTexture);
    }
    
    protected MenuScene createMenuScene(final Camera camera) {
        mMenuScene = new MenuScene(camera);
        mMenuScene.setMenuAnimator(new GameMenuAnimator());
        for (int i = 0; i < 7; ++i) {
            addMenuItem(i);
        }
        mMenuScene.buildAnimations();
        mMenuScene.setBackgroundEnabled(true);
        mMenuScene.setOnMenuItemClickListener(this);
        return mMenuScene;
    }

    private void addMenuItem(int id) {
        final SpriteMenuItem menuItem = new SpriteMenuItem(id, mMenuItemBgRegion);
        menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mMenuScene.addMenuItem(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            final Scene scene = mEngine.getScene();
            if(scene.hasChildScene()) {
                /* Remove the menu and reset it. */
                closeMenu();
            } else {
                /* Attach the menu. */
                openMenu();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
