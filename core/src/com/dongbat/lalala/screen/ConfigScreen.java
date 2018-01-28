/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.lalala.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.game.util.ScreenShake;
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
  private final SpriteBatch batch;
  private final Sprite sprite;
  private final OrthographicCamera camera;
  private final ScreenShake screenShake;
  private final BitmapFont font;

  public ConfigScreen(Game game) {
    this.game = game;

    sprite = new Sprite();
    sprite.setSize(Gdx.graphics.getWidth() / 6, Gdx.graphics.getWidth() / 6);
    sprite.setOriginCenter();
    sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
    
    font = new BitmapFont();
    

    batch = new SpriteBatch();

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    screenShake = new ScreenShake();

  }

  @Override
  public void render(float delta) {
    camera.position.x = 0;
    camera.position.y = 0;
    
    screenShake.update(delta, camera);

    camera.update();
    batch.setProjectionMatrix(camera.combined);
    if (touched) {
      Texture recording = AssetUtil.getAssetManager().get("images/recording.png", Texture.class);
      sprite.setRegion(recording);
      if (!Gdx.input.isTouched()) {
        done();
        touched = false;
      } else {
        update();
      }
    } else {
      Texture record = AssetUtil.getAssetManager().get("images/record.png", Texture.class);
      sprite.setRegion(record);
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
    batch.begin();
    sprite.draw(batch);
    font.draw(batch, "Sing 4 notes", -Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 5f, Gdx.graphics.getWidth(), Align.center, true);
    batch.end();

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
      game.setScreen(new TestScreen(game));
    } catch (Exception ex) {
      screenShake.shake(Gdx.graphics.getWidth() / 20f, 300);
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
