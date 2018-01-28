/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.util;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;
import com.dongbat.lalala.screen.CutsceneScreen;
import com.dongbat.lalala.screen.GameplayScreen;

/**
 *
 * @author tao
 */
public class LevelUtil {

  public static void start() {
    LevelUtil.reload();
  }

  public static class LevelInfo {

    public String map;
    public boolean isCutscene;
  }

  public static Array<LevelInfo> infos = new Array<LevelUtil.LevelInfo>();
  private static Game game;
  private static int currentLevel = 0;

  public static void init(Game game) {
    LevelUtil.game = game;
    infos.addAll(level("0.1.tmx"),
      level("0.2.tmx"),
      level("0.3.tmx"),
      level("1.1.tmx"),
      level("1.2.tmx"),
      level("1.3.tmx"),
      level("1.4.tmx"));
  }

  public static LevelInfo level(String map) {
    LevelInfo levelInfo = new LevelInfo();
    levelInfo.map = map;
    levelInfo.isCutscene = false;
    return levelInfo;
  }

  public static LevelInfo cutscene(String map) {
    LevelInfo levelInfo = new LevelInfo();
    levelInfo.map = map;
    levelInfo.isCutscene = true;
    return levelInfo;
  }

  public static void reload() {
    LevelInfo levelInfo = infos.get(currentLevel);
    if (!levelInfo.isCutscene) {
      game.setScreen(new GameplayScreen(game, levelInfo.map));
    } else {
      game.setScreen(new CutsceneScreen(game, levelInfo.map));
    }
  }
  
  public static void next() {
    if (currentLevel == infos.size - 1) {
      return;
    }
    currentLevel++;
    reload();
  }
  
  public static void prev() {
    if (currentLevel == 0) {
      return;
    }
    currentLevel--;
    reload();
  }
}
