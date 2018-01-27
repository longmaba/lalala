package com.dongbat.game.util;

public class VoiceConfig {

  public enum SoundType {

    WHISTLING("whistling", "whistle"), HUMMING("humming", "hum"), SINGING("singing", "sing");
    private final String label;
    private final String primitiveLabel;

    private SoundType(String label, String primitiveLabel) {
      this.label = label;
      this.primitiveLabel = primitiveLabel;
    }

    public String getLabel() {
      return label;
    }

    public String getPrimitiveLabel() {
      return primitiveLabel;
    }

  }

  private static float silent;
  private static float noise;
  private static float silentThreshold = Float.NEGATIVE_INFINITY;
  private static boolean overrideSilentThreshold = true;
  private static float minPitch, maxPitch;
  private static float[] preferedPitches;
  private static SoundType soundType = SoundType.SINGING;

  public static float[] getPreferedPitches() {
    return preferedPitches;
  }

  public static SoundType getSoundType() {
    return soundType;
  }

  public static void setSoundType(SoundType soundType) {
    VoiceConfig.soundType = soundType;
  }

  public static void setPreferedPitches(float[] preferedPitches) {
    VoiceConfig.preferedPitches = preferedPitches;
  }

  public static float getMinPitch() {
    return minPitch;
  }

  public static float getMaxPitch() {
    return maxPitch;
  }

  public static void setPitchRange(float min, float max) {
    minPitch = min;
    maxPitch = max;
  }

  public static void setMinPitch(float minPitch) {
    VoiceConfig.minPitch = minPitch;
  }

  public static void setMaxPitch(float maxPitch) {
    VoiceConfig.maxPitch = maxPitch;
  }

  public static void setSilentThreshold(float silentThreshold) {
    VoiceConfig.silentThreshold = silentThreshold;
    overrideSilentThreshold = true;
  }

  public static float getSilent() {
    return silent;
  }

  public static void setSilent(float silent) {
    VoiceConfig.silent = silent;
  }

  public static float getNoise() {
    return noise;
  }

  public static void setNoise(float noise) {
    VoiceConfig.noise = noise;
    overrideSilentThreshold = false;
  }

  public static float getSilentThreshold() {
    if (overrideSilentThreshold) {
      return silentThreshold;
    }
    return (noise + silent) / 2;
  }
}
