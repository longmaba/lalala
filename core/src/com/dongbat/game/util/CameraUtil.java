package com.dongbat.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

/**
 *
 * @author tao
 */
public class CameraUtil {

  public static OrthographicCamera createCamera(float mapRatio, float smallerSize) {
    return createCamera(mapRatio, smallerSize, 0);
  }

  public static OrthographicCamera createCamera(float mapRatio, float smallerSize, float offset) {
    float W, H;
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight() - offset;
    float ratio = w / h;
    if (ratio < 1) {
      w = smallerSize;
      h = w / ratio;
      W = w;
      H = W / mapRatio;
    } else {
      h = smallerSize;
      w = h * ratio;
      H = h;
      W = H * mapRatio;
    }

    OrthographicCamera camera = new OrthographicCamera(W, H);
    camera.position.set(W / 2, H / 2, 0);
    return camera;
  }
  
  public static Rectangle calculateViewport(float r) {
    float width = Gdx.graphics.getWidth();
    float height = Gdx.graphics.getHeight();
    float R = width / height;
    float vW;
    float vH;
    float vX;
    float vY;
    
    if (r > R) {
      vW = width;
      vH = vW / r;
      vX = 0;
      vY = (height - vH) / 2;
    } else if (r < R) {
      vH = height;
      vW = vH * r;
      vY = 0;
      vX = (width - vW) / 2;
    } else {
      vW = width;
      vH = height;
      vX = 0;
      vY = 0;
    }
    
    return new Rectangle(vX, vY, vW, vH);
  }

  public static OrthographicCamera createFullScreenCamera() {
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();
    OrthographicCamera camera = new OrthographicCamera(w, h);
    camera.position.set(w / 2, h / 2, 0);
    return camera;
  }
}
