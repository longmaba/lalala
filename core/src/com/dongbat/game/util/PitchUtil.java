package com.dongbat.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class PitchUtil {

  private static final Color[] COLORS = new Color[]{
    Color.valueOf("fb0000"),
    Color.valueOf("f48a00"),
    Color.valueOf("ebd902"),
    Color.valueOf("8df800")
  };

  public static Color[] getCOLORS() {
    return COLORS;
  }

  private static float level = -100;
  private static final Array<Float> pitches = new Array<Float>();
  private static final Array<Float> tmp = new Array<Float>();
  private static final Array<Float> probabilities = new Array<Float>();
  private static long duration = 0;

  public static float getPitch() {
    return getPitch(false);
  }

  private static long test = TimeUtils.millis();

  public static float getPitch(boolean raw) {
    if (pitches.size <= 0) {
      return -1;
    }
    if (raw) {
      return pitches.get(pitches.size - 1);
    }
    // TODO: isPitchAvailable(), sticky?
    tmp.clear();
    for (int i = 0; i < pitches.size; i++) {
      Float p = pitches.get(i);
      tmp.add(p);
    }
    if (tmp.size <= 0) {
      return -1;
    }
    return tmp.get(tmp.size - 1);
  }

  public static float getProbability() {
    if (probabilities.size <= 0) {
      return 0;
    }
    return probabilities.get(probabilities.size - 1);
  }

  public static float getLevel() {
    return level;
  }

  private static final Runnable updateRunnable = new Runnable() {

    @Override
    public void run() {
      while (probabilities.size > 20) {
        probabilities.removeIndex(0);
      }
      probabilities.add(lastProbability);
      while (pitches.size > 20) {
        pitches.removeIndex(0);
      }
      pitches.add(lastPitch);
      PitchUtil.level = lastLevel;
      updateDuration();
    }
  };

  private static long lastUpdate = TimeUtils.millis();

  private static void updateDuration() {
    if (lastLevel < VoiceConfig.getSilentThreshold()) {
      duration = 0;
    } else {
      duration += TimeUtils.timeSinceMillis(lastUpdate);
    }
    lastUpdate = TimeUtils.millis();
  }

  public static long getDuration() {
    return duration;
  }

  private static float lastPitch, lastLevel, lastProbability;

  public static void update(float pitch, float level, float probability) {
    lastPitch = pitch;
    lastLevel = level;
    lastProbability = probability;
    Gdx.app.postRunnable(updateRunnable);
  }

  public static void gwtUpdate(float pitch, float level, float probability) {
    if (pitch >= 10000) {
      pitch = -1;
      probability = 0;
    }
    lastPitch = pitch;
    lastLevel = level;
    lastProbability = probability;
    while (probabilities.size > 20) {
      probabilities.removeIndex(0);
    }
    probabilities.add(lastProbability);
    while (pitches.size > 20) {
      pitches.removeIndex(0);
    }
    pitches.add(lastPitch);
    PitchUtil.level = lastLevel;
    updateDuration();
  }

  public static void update(float pitch, float level) {
    update(pitch, level, 1);
  }

  public static int getKeyLevel(int devidedTo) {
    if (getLevel() < VoiceConfig.getSilentThreshold() || getProbability() < 0.96f) {
      return -1;
    }
    if (VoiceConfig.getPreferedPitches() != null && VoiceConfig.getPreferedPitches().length > 0) {
      return getKeyLevelNearestToPreferedPitches();
    }
    float minPitch = VoiceConfig.getMinPitch();
    float maxPitch = VoiceConfig.getMaxPitch();
    int minNote = NoteFrequencyConverter.frequencyToNote(minPitch);
    int maxNote = NoteFrequencyConverter.frequencyToNote(maxPitch);

    int note = NoteFrequencyConverter.frequencyToNote(getPitch());
    if (maxNote - minNote == 0) {
      return 0;
    }
    float l = (note - minNote) * 1f / (maxNote - minNote) * devidedTo;
    l = MathUtils.clamp(l, 0, devidedTo - 1);
    return MathUtils.floor(l);
  }

  private static int getKeyLevelNearestToPreferedPitches() {
    float[] preferedPitches = VoiceConfig.getPreferedPitches();
    int minDst = Integer.MAX_VALUE;
    int l = -1;
    float pitch = getPitch();
    if (pitch < 0) {
      return -1;
    }
    int note = NoteFrequencyConverter.frequencyToNote(pitch);
    for (int i = 0; i < preferedPitches.length; i++) {
      float p = preferedPitches[i];
      int n = NoteFrequencyConverter.frequencyToNote(p);
      int dst = Math.abs(n - note);
      if (dst < minDst) {
        minDst = dst;
        l = i;
      }
    }
    return l;
  }
}
