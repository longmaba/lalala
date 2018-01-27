/*
 *      _______                       _____   _____ _____  
 *     |__   __|                     |  __ \ / ____|  __ \ 
 *        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
 *        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
 *        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
 *        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
 *                                                         
 * -------------------------------------------------------------
 *
 * TarsosDSP is developed by Joren Six at IPEM, University Ghent
 *  
 * -------------------------------------------------------------
 *
 *  Info: http://0110.be/tag/TarsosDSP
 *  Github: https://github.com/JorenSix/TarsosDSP
 *  Releases: http://0110.be/releases/TarsosDSP/
 *  
 *  TarsosDSP includes modified source code by various authors,
 *  for credits and info, see README.
 * 
 */
package com.dongbat.tarsos.dsp.io.libgdx;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;

/**
 * This class plays a file and sends float arrays to registered AudioProcessor
 * implementors. This class can be used to feed FFT's, pitch detectors, audio
 * players, ... Using a (blocking) audio player it is even possible to
 * synchronize execution of AudioProcessors and sound. This behavior can be used
 * for visualization.
 *
 * @author Joren Six
 */
public class IosAudioDispatcher {

  /**
   * Log messages.
   */
  private static final Logger LOG = Logger.getLogger(IosAudioDispatcher.class.getName());

  /**
   * The audio event that is send through the processing chain.
   */
  private final AudioEvent audioEvent;
  private final ArrayList<AudioProcessor> audioProcessors;

  public IosAudioDispatcher(TarsosDSPAudioFormat format, int bufferSize) {
    audioProcessors = new ArrayList<AudioProcessor>();
    audioEvent = new AudioEvent(format);
    audioEvent.setOverlap(0);
  }

  /**
   * Adds an AudioProcessor to the chain of processors.
   *
   * @param audioProcessor The AudioProcessor to add.
   */
  public void addAudioProcessor(final AudioProcessor audioProcessor) {
    audioProcessors.add(audioProcessor);
    LOG.log(Level.FINE, "Added an audioprocessor to the list of processors: {0}", audioProcessor.toString());
  }

  /**
   * Removes an AudioProcessor to the chain of processors and calls its
   * <code>processingFinished</code> method.
   *
   * @param audioProcessor The AudioProcessor to remove.
   */
  public void removeAudioProcessor(final AudioProcessor audioProcessor) {
    audioProcessors.remove(audioProcessor);
    audioProcessor.processingFinished();
    LOG.log(Level.FINE, "Remove an audioprocessor to the list of processors: {0}", audioProcessor.toString());
  }

  public void process(float[] data) {
    audioEvent.setFloatBuffer(data);
    audioEvent.setOverlap(0);

    for (final AudioProcessor processor : audioProcessors) {
      if (!processor.process(audioEvent)) {
        break;
      }
    }
  }
}
