package com.dongbat.lalala;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.dongbat.game.pitch.PitchUpdater;
import com.dongbat.lalala.screen.CutsceneScreen;

public class MyGdxGame extends Game {

  @Override
  public void create() {
    PitchUpdater.init();
    setScreen(new CutsceneScreen(this));
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    super.render();
  }

  @Override
  public void dispose() {

  }
}
