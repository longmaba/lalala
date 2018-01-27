/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.game.util;

/**
 * @author implicit-invocation
 */
public class NoteFrequencyConverter {

  private static final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
  private static final double BASE_A4 = 440;

  public static double noteToFrequency(int n) {
    double result = -1;
    if (n >= 0 && n <= 119) {
      result = BASE_A4 * Math.pow(2, ((float) n - 57) / 12);
    }
    return result;
  }

  public static String noteToName(int n) {
    String result = "---";
    if (n >= 0 && n <= 119) {
      result = NOTES[n % 12] + n / 12;
    }
    return result;
  }

  public static int nameToNote(String name) {
    String noteName = null;
    int octave = 0;
    if (name.length() == 3) {
      noteName = name.substring(0, 2);
      octave = Integer.parseInt(name.substring(2, 3));
    } else {
      noteName = name.substring(0, 1);
      octave = Integer.parseInt(name.substring(1, 2));
    }
    int noteIndex = 0;
    for (int i = 0; i < NOTES.length; i++) {
      if (noteName.equalsIgnoreCase(NOTES[i])) {
        noteIndex = i;
        break;
      }
    }

    return noteIndex + octave * 12;
  }

  public static double nameToFrequency(String name) {
    int note = nameToNote(name);
    return noteToFrequency(note);
  }

  public static int frequencyToNote(double f) {
    return (int) (Math.round(12 * log2(f / BASE_A4)) + 57);
  }

  public static float frequencyToUnroundedNote(double f) {
    return (float) (12 * log2(f / BASE_A4)) + 57;
  }

  public static double noteToFrequency(float n) {
    double result = -1;
    if (n >= 0 && n <= 119) {
      result = BASE_A4 * Math.pow(2, (n - 57) / 12);
    }
    return result;
  }

  public static String frequencyToName(double f) {
    return noteToName(frequencyToNote(f));
  }

  public static double toClosestStandardFrequency(double f) {
    int note = (int) (Math.round(12 * log2(f / BASE_A4)) + 57);
    return f - noteToFrequency(note);
  }

  private static double log2(double n) {
    return (Math.log(n) / Math.log(2));
  }
}
