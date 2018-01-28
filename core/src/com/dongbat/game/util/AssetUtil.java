package com.dongbat.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author tao
 */
public class AssetUtil {

  private static AssetManager assetManager;
  private static Texture white;
  private static BitmapFont debugFont;
  private static BitmapFont normalFont;

  private static Texture createBgTexture() {
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444); // or RGBA8888
    pixmap.setColor(Color.WHITE);
    pixmap.fill();
    Texture texture = new Texture(pixmap); // must be manually disposed
    pixmap.dispose();
    return texture;
  }

  private static final Array<Sprite> deco = new Array<Sprite>();

  public static void load() {
    assetManager = new AssetManager();
    TextureParameter param = new TextureParameter();
    param.minFilter = TextureFilter.Linear;
    param.magFilter = TextureFilter.Linear;

    assetManager.load("whistle.mp3", Sound.class);
    assetManager.load("ambient.mp3", Sound.class);
    assetManager.load("sing.mp3", Sound.class);
    assetManager.load("hum.mp3", Sound.class);
    assetManager.load("record.png", Texture.class, param);
    assetManager.load("recording.png", Texture.class, param);
    assetManager.load("rune.fnt", BitmapFont.class);
    assetManager.load("normal.fnt", BitmapFont.class);
    assetManager.load("status.fnt", BitmapFont.class);
    assetManager.load("background.png", Texture.class, param);

    assetManager.load("stuff.atlas", TextureAtlas.class);

    assetManager.load("images/record.png", Texture.class, param);
    assetManager.load("images/recording.png", Texture.class, param);

    assetManager.finishLoading();

    TextureAtlas stuff = assetManager.get("stuff.atlas", TextureAtlas.class);
    Array<TextureAtlas.AtlasRegion> decoRegions = stuff.findRegions("deco");
    float TARGET_HEIGHT = 3f;
    float shrink = TARGET_HEIGHT / 100f;
    deco.clear();
    for (TextureAtlas.AtlasRegion decoRegion : decoRegions) {
      Sprite sprite = new Sprite(decoRegion);
      sprite.setSize(decoRegion.getRegionWidth() * shrink, decoRegion.getRegionHeight() * shrink);
      deco.add(sprite);
    }

    white = createBgTexture();
    debugFont = new BitmapFont();
    debugFont.getData().setScale(0.1f);
    normalFont = new BitmapFont();

  }

  public static Array<Sprite> getDecorators() {
    return deco;
  }

  public static Texture getWhite() {
    return white;
  }

  public static BitmapFont getDebugFont() {
    return debugFont;
  }

  public static BitmapFont getNormalFont() {
    return normalFont;
  }

  public static AssetManager getAssetManager() {
    return assetManager;
  }

  public static void dispose() {
    if (white != null) {
      white.dispose();
      white = null;
    }
    if (debugFont != null) {
      debugFont.dispose();
      debugFont = null;
    }
    if (assetManager != null) {
      assetManager.dispose();
      assetManager = null;
    }
  }
}
