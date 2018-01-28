/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.lalala.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.dongbat.lalala.Level;

/**
 *
 * @author tao
 */
public class GameplayScreen extends ScreenAdapter {

  private final Level level;
  private final Game game;

  public GameplayScreen(Game game) {
    level = new Level("1.4.tmx");
    this.game = game;
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Keys.R)) {
      game.setScreen(new GameplayScreen(game));
    }
    level.update(delta);
    level.draw();
  }

  @Override
  public void dispose() {
    level.dispose();
  }

}
