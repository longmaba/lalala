package com.dongbat.game.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 *
 * @author tao
 */
public class FontUtil {

  private static final GlyphLayout glyphLayout = new GlyphLayout();
  
  public static void draw(BitmapFont font, Batch batch, String text, float x, float y) {
    glyphLayout.setText(font, text);
    font.draw(batch, glyphLayout, x - glyphLayout.width / 2, y + glyphLayout.height / 2);
  }
}
