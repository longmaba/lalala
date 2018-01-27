package com.dongbat.game.pitch;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import com.badlogic.gdx.Gdx;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.tarsos.dsp.io.libgdx.LibgdxAudioInputStream;

public class PitchUpdater implements PitchDetectionHandler {

  private static AudioDispatcher audioDispatcher;
  private static SilenceDetector silenceDetector;

  public static void init() {
    TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(16000, 16, 1, true, false);
    LibgdxAudioInputStream audioInputStream = new LibgdxAudioInputStream(Gdx.audio.newAudioRecorder(16000, true), audioFormat);

    audioDispatcher = new AudioDispatcher(audioInputStream, 1024, 0);
    audioDispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 16000, 1024, new PitchUpdater()));
    silenceDetector = new SilenceDetector();
    audioDispatcher.addAudioProcessor(silenceDetector);
    new Thread(audioDispatcher).start();
  }

  @Override
  public void handlePitch(final PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
    PitchUtil.update(pitchDetectionResult.getPitch(), (float) silenceDetector.currentSPL(), pitchDetectionResult.getProbability());
  }
}
