/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.lalala.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.game.util.ShapeUtil;

/**
 *
 * @author tao
 */
public class TestScreen extends ScreenAdapter {

  private int level = -1;
  private SpriteBatch batch;
  private OrthographicCamera camera;
  private final Game game;
  public static final Color[] COLORS = new Color[]{
    Color.valueOf("f80101"),
    Color.valueOf("f28900"),
    Color.valueOf("8df500"),
    Color.valueOf("00ffff")
  };

  public TestScreen(Game game) {
    this.game = game;
    batch = new SpriteBatch();
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  @Override
  public void render(float delta) {
    int keyLevel = PitchUtil.getKeyLevel(4);
    
    if (keyLevel == level + 1) {
      level = keyLevel;
    } else if(keyLevel < level && keyLevel > -1) {
      level = -1;
    }
    
    if (level == 3) {
      game.setScreen(new GameplayScreen(game));
    }
    
    camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    for (int i = 0; i < 4; i++) {
      ShapeUtil.drawRect(batch, -Gdx.graphics.getWidth() / 2 + Gdx.graphics.getWidth() / 4 * i,
        -Gdx.graphics.getHeight() / 2,
        Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight(), COLORS[i], level + 1 > i? 1: 0.5f, AssetUtil.getWhite());
    }
    batch.end();
  }

}
