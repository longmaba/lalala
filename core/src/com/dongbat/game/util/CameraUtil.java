package com.dongbat.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 *
 * @author tao
 */
public class CameraUtil {

  public static OrthographicCamera createCamera(float smallerSize) {
    return createCamera(smallerSize, 0);
  }

  public static OrthographicCamera createCamera(float smallerSize, float offset) {
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight() - offset;
    float ratio = w / h;
    if (ratio < 1) {
      w = smallerSize;
      h = w / ratio;
    } else {
      h = smallerSize;
      w = h * ratio;
    }

    OrthographicCamera camera = new OrthographicCamera(w, h);
    camera.position.set(w / 2, h / 2, 0);
    return camera;
  }

  public static OrthographicCamera createFullScreenCamera() {
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();
    OrthographicCamera camera = new OrthographicCamera(w, h);
    camera.position.set(w / 2, h / 2, 0);
    return camera;
  }
}
