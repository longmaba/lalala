/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.lalala.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.game.util.SignalUtil;
import com.dongbat.game.util.VoiceConfig;
import java.util.Arrays;

/**
 *
 * @author longmaba
 */
public class ConfigScreen extends ScreenAdapter {

  private final Game game;
  private boolean touched = false;

  public ConfigScreen(Game game) {
    this.game = game;
  }

  @Override
  public void render(float delta) {
    if (touched) {
      if (!Gdx.input.isTouched()) {
        done();
        touched = false;
      } else {
        update();
      }
    } else {
      if (Gdx.input.isTouched()) {
        start();
        System.out.println("recording");
        touched = true;
      } else {
        int keyLevel = PitchUtil.getKeyLevel(4);
        if (keyLevel >= 0) {
          System.out.printf("Key level: %d\n", keyLevel);
        }
      }
    }
  }

  private final Array<Float> levels = new Array<Float>();
  private final Array<Float> pitches = new Array<Float>();
  private final Array<Float> probabilities = new Array<Float>();
  private final Array<Float> tmp = new Array<Float>();

  private void done() {
    tmp.clear();
    tmp.addAll(levels);
    SignalUtil.removeOutliers(tmp, levels);
    Array<Array<Float>> bins = SignalUtil.cluster1d(levels, 2);
    float thres = 0;
    for (Array<Float> bin : bins) {
      float s = 0;
      for (Float f : bin) {
        s += f;
      }
      s /= bin.size;
      thres += s;
    }
    thres /= bins.size;
    VoiceConfig.setSilentThreshold(thres);
    Array<Float> goodPitches = new Array<Float>();
    for (int i = 0; i < pitches.size; i++) {
      if (levels.get(i) >= thres && probabilities.get(i) > 0.9f) {
        goodPitches.add(pitches.get(i));
      }
    }
    tmp.clear();
    tmp.addAll(goodPitches);
    SignalUtil.removeOutliers(tmp, goodPitches);
    float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
    for (float p : goodPitches) {
      if (min > p) {
        min = p;
      }
      if (max < p) {
        max = p;
      }
    }
    VoiceConfig.setPitchRange(min * 0.9f, max * 1.1f);

    try {
      Array<Array<Float>> pitchClusters = SignalUtil.cluster1d(goodPitches, 0.96f);
      if (pitchClusters.size != 4) {
        throw new Exception("cannot get pitch");
      }
      float[] preferedPitches = new float[4];
      for (int i = 0; i < pitchClusters.size; i++) {
        Array<Float> pitchCluster = pitchClusters.get(i);
        float sum = 0;
        for (float p : pitchCluster) {
          sum += p;
        }
        preferedPitches[i] = sum / pitchCluster.size;
      }
      Arrays.sort(preferedPitches);
      VoiceConfig.setPreferedPitches(preferedPitches);
      System.out.println("ok");
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  private void start() {
    levels.clear();
    pitches.clear();
    probabilities.clear();
  }

  private void update() {
    levels.add(PitchUtil.getLevel());
    pitches.add(PitchUtil.getPitch(true));
    probabilities.add(PitchUtil.getProbability());
  }

}
