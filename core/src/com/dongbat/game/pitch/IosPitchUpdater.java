package com.dongbat.game.pitch;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.tarsos.dsp.io.libgdx.IosAudioDispatcher;

public class IosPitchUpdater implements PitchDetectionHandler {

  private static IosAudioDispatcher audioDispatcher;
  private static SilenceDetector silenceDetector;

  public static void init() {
    TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(44100, 16, 1, true, false);
    audioDispatcher = new IosAudioDispatcher(audioFormat, 1024);
    audioDispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 44100, 1024, new IosPitchUpdater()));
    silenceDetector = new SilenceDetector();
    audioDispatcher.addAudioProcessor(silenceDetector);
  }

  public static void handle(float[] data) {
    audioDispatcher.process(data);
  }

  @Override
  public void handlePitch(final PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
    PitchUtil.update(pitchDetectionResult.getPitch(), (float) silenceDetector.currentSPL(), pitchDetectionResult.getProbability());
  }
}
