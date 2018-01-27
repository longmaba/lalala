package com.dongbat.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureUtil {

  public static Texture getRingTexture(int r, Color color) {
    Pixmap pixmap = new Pixmap(r * 2, r * 2, Pixmap.Format.RGBA8888);
    pixmap.setColor(color);
    pixmap.fillCircle(r, r, r);
//    pixmap.setFilter(Pixmap.Filter.BiLinear);
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    return texture;
  }
}
