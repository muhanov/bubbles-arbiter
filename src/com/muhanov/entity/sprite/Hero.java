package com.muhanov.entity.sprite;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.muhanov.entity.ITouchEntity;

public class Hero extends AnimatedSprite implements ITouchEntity {

    public Hero(float pX, float pY, TiledTextureRegion pTiledTextureRegion) {
        super(pX, pY, pTiledTextureRegion);
    }

    @Override
    public void touch() {
        // TODO Auto-generated method stub

    }

}
