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
import com.dongbat.game.util.InputUtil;
import com.dongbat.lalala.Level;
import com.dongbat.lalala.UI;

/**
 *
 * @author tao
 */
public class GameplayScreen extends ScreenAdapter {

  private final Level level;
  private final Game game;
  private final UI ui;

  public GameplayScreen(Game game) {
    level = new Level("1.4.tmx");
    this.game = game;
    ui = new UI();
    InputUtil.addProcessor(ui);
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Keys.R)) {
      game.setScreen(new GameplayScreen(game));
    }
    level.update(delta);
    level.draw();
    
    ui.act();
    ui.draw();
  }

  @Override
  public void dispose() {
    level.dispose();
    InputUtil.removeProcessor(ui);
  }

}
