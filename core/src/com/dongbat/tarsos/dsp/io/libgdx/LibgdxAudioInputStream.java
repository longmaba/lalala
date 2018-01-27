package com.dongbat.tarsos.dsp.io.libgdx;

import com.badlogic.gdx.audio.AudioRecorder;

import java.io.IOException;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

/**
 * @author implicit-invocation
 */
public class LibgdxAudioInputStream implements TarsosDSPAudioInputStream {

  private final AudioRecorder underlyingStream;
  private final TarsosDSPAudioFormat format;

  public LibgdxAudioInputStream(AudioRecorder underlyingStream, TarsosDSPAudioFormat format) {
    this.underlyingStream = underlyingStream;
    this.format = format;
  }

  @Override
  public long skip(long bytesToSkip) throws IOException {
    throw new IOException("Can not skip in audio stream");
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    short[] s = new short[len / 2];
    underlyingStream.read(s, off, len / 2);
    for (int i = 0; i < s.length; i++) {
      short t = s[i];
      b[i * 2] = (byte) (t & 0xff);
      b[i * 2 + 1] = (byte) ((t >> 8) & 0xff);
    }
    return s.length * 2;
  }

  @Override
  public void close() throws IOException {
    underlyingStream.dispose();
  }

  @Override
  public TarsosDSPAudioFormat getFormat() {
    return format;
  }

  @Override
  public long getFrameLength() {
    return -1;
  }
}
