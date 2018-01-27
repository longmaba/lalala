package com.dongbat.lalala;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.dongbat.game.pitch.PitchUpdater;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.InputUtil;
import com.dongbat.lalala.screen.ConfigScreen;

public class MyGdxGame extends Game {

  @Override
  public void create() {
    PitchUpdater.init();
    AssetUtil.load();
    InputUtil.init();
    setScreen(new ConfigScreen(this));
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    super.render();
  }

  @Override
  public void resume() {
    AssetUtil.load();
  }

  @Override
  public void dispose() {
    AssetUtil.dispose();
  }
}
