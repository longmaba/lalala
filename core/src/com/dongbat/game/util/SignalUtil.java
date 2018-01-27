package com.dongbat.game.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;

public class SignalUtil {

  public static void removeOutliers(Array<Float> source, Array<Float> target) {
    target.clear();
    float sum = 0;
    for (Float f : source) {
      sum += f;
    }
    float avg = sum / source.size;
    sum = 0;
    for (Float f : source) {
      sum += Math.pow(f, 2);
    }
    float stdDev = (float) Math.sqrt(sum / source.size);
    for (Float f : source) {
      if (Math.abs(f - avg) <= 2 * stdDev && f != null) {
        target.add(f);
      }
    }
  }

  public static Array<Array<Float>> kde(Array<Float> data) {
    Array<Array<Float>> clusters = new Array<Array<Float>>();
    data = new Array<Float>(data);
    Sort.instance().sort(data);
    Array<Float> diffs = new Array<Float>();
    float prev = 0;
    for (Float f : data) {
      diffs.add(f - prev);
      prev = f;
    }
    Array<Float> currentCluster = new Array<Float>();
    for (int i = 0; i < data.size; i++) {
      if (diffs.get(i) / data.get(i) >= 0.4f) {
        if (i > 0) {
          currentCluster = new Array<Float>();
        }
        clusters.add(currentCluster);
      }
      currentCluster.add(data.get(i));
    }
    return clusters;
  }

  public static Array<Array<Float>> cluster1d(Array<Float> list, float fitThreshold) {
    Array<Array<Float>> clusters = null;
    float gvf = 0;
    int numclass = 2;
    while (gvf < fitThreshold) {
      clusters = cluster1d(list, numclass);
      numclass++;
      gvf = gvf(list, clusters);
    }
    return clusters;
  }

  /**
   * @return int[]
   * @param list com.sun.java.util.collections.ArrayList
   * @param numclass int
   */
  public static Array<Array<Float>> cluster1d(Array<Float> list, int numclass) {
    Array<Array<Float>> clusters = new Array<Array<Float>>();
    list = new Array<Float>(list);
//    list.sort();
    //int numclass;
    int[] kclass = getJenksBreaks(list, numclass);
    if (kclass == null) {
      clusters.add(list);
      return clusters;
    }
    int start = 0;
    for (int q : kclass) {
      Array<Float> cluster = new Array<Float>();
      for (int i = start; i <= q; i++) {
        cluster.add(list.get(i));
      }
      clusters.add(cluster);
      start = q + 1;
    }
    return clusters;
  }

  private static float avg(Array<Float> values) {
    float sum = 0;
    for (Float value : values) {
      sum += value;
    }
    return sum / values.size;
  }

  public static float sdam(Array<Float> values) {
    float sdam = 0;
    float avg = avg(values);
    for (Float value : values) {
      sdam += Math.pow(value - avg, 2);
    }
    return sdam;
  }

  public static float sdcm(Array<Array<Float>> clusters) {
    float sdcm = 0;
    for (Array<Float> cluster : clusters) {
      sdcm += sdam(cluster);
    }
    return sdcm;
  }

  public static float gvf(Array<Float> values, Array<Array<Float>> clusters) {
    float sdcm = sdcm(clusters);
    float sdam = sdam(values);
    return 1 - (sdcm / sdam);
  }

  private static int[] getJenksBreaks(Array<Float> list, int numclass) {

    if (list.size <= 0) {
      return null;
    }
    //int numclass;
    int numdata = list.size;

    float[][] mat1 = new float[numdata + 1][numclass + 1];
    float[][] mat2 = new float[numdata + 1][numclass + 1];
    float[] st = new float[numdata];

    for (int i = 1; i <= numclass; i++) {
      mat1[1][i] = 1;
      mat2[1][i] = 0;
      for (int j = 2; j <= numdata; j++) {
        mat2[j][i] = Float.MAX_VALUE;
      }
    }
    float v = 0;
    for (int l = 2; l <= numdata; l++) {
      float s1 = 0;
      float s2 = 0;
      float w = 0;
      for (int m = 1; m <= l; m++) {
        int i3 = l - m + 1;

        float val = list.get(i3 - 1);

        s2 += val * val;
        s1 += val;

        w++;
        v = s2 - (s1 * s1) / w;
        int i4 = i3 - 1;
        if (i4 != 0) {
          for (int j = 2; j <= numclass; j++) {
            if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
              mat1[l][j] = i3;
              mat2[l][j] = v + mat2[i4][j - 1];
            }
          }
        }
      }
      mat1[l][1] = 1;
      mat2[l][1] = v;
    }
    int k = numdata;

    int[] kclass = new int[numclass];

    kclass[numclass - 1] = list.size - 1;

    for (int j = numclass; j >= 2; j--) {
      int id = (int) (mat1[k][j]) - 2;

      kclass[j - 2] = id;

      k = (int) mat1[k][j] - 1;
    }
    return kclass;
  }

}
