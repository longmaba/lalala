package com.dongbat.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputUtil {

  private static InputMultiplexer inputMultiplexer;

  public static void init() {
    inputMultiplexer = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMultiplexer);
  }

  public static void addProcessor(InputProcessor processor) {
    inputMultiplexer.addProcessor(processor);
  }

  public static void removeProcessor(InputProcessor processor) {
    inputMultiplexer.removeProcessor(processor);
  }
}
