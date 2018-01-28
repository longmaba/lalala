package com.dongbat.lalala;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.dongbat.game.pitch.PitchUpdater;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.InputUtil;
import com.dongbat.game.util.LevelUtil;
import com.dongbat.lalala.screen.ConfigScreen;
import com.dongbat.lalala.screen.GameplayScreen;

public class MyGdxGame extends Game {

  @Override
  public void create() {
    PitchUpdater.init();
    AssetUtil.load();
    InputUtil.init();
    LevelUtil.init(this);
    setScreen(new ConfigScreen(this));
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(13f / 255, 15f / 255, 24f / 255, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    super.render();
    
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
      LevelUtil.reload();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
      LevelUtil.next();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
      LevelUtil.prev();
    }
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
