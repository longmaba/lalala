/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.lalala.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

/**
 *
 * @author longmaba
 */
public class CutsceneScreen extends ScreenAdapter {

  private final TiledMap map;
  private final OrthographicCamera camera;
  private final int width;
  private final int height;
  private final SpriteBatch batch;
  private final Game game;
  private final Array<Rectangle> viewports = new Array<Rectangle>();
  private final FloatArray ts = new FloatArray();
  private final Rectangle currentVp = new Rectangle();

  public CutsceneScreen(Game game) {
    this.game = game;
    map = new TmxMapLoader().load("test.tmx");

    loadViewports(map);
    width = Gdx.graphics.getWidth();
    height = Gdx.graphics.getHeight();
    camera = new OrthographicCamera(width, height);
    camera.position.set(width / 2, height / 2, 0);
    batch = new SpriteBatch();
  }

  private float accumulate = 0;

  private Rectangle getCurrentViewport() {
    int state = 0;
    for (int i = 0; i < ts.size; i++) {
      float t = ts.get(i);
      if (accumulate > t) {
        state = i + 1;
      }
    }
    if (state == 0) {
      return viewports.first();
    }
    if (state > viewports.size - 1) {
      return viewports.get(viewports.size - 1);
    }
    Rectangle c = viewports.get(state);
    Rectangle p = viewports.get(state - 1);
    float t1 = ts.get(state - 1);
    float t2 = ts.get(state);
    float duration = t2 - t1;
    float elapsed = accumulate - t1;
    float progress = elapsed / duration;
    float x = p.x + (c.x - p.x) * progress;
    float y = p.y + (c.y - p.y) * progress;
    float w = p.width + (c.width - p.width) * progress;
    float h = p.height + (c.height - p.height) * progress;
    currentVp.set(x, y, w, h);
    return currentVp;
  }

  @Override
  public void render(float delta) {
    accumulate += delta;
    if (Gdx.input.isKeyJustPressed(Keys.R)) {
      game.setScreen(new CutsceneScreen(game));
    }
    if (viewports.size == 0) {
      currentVp.set(0, 0, width, height);
    } else {
      currentVp.set(getCurrentViewport());
    }
    setToViewport(currentVp);
    camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    MapLayers layers = map.getLayers();
    AnimatedTiledMapTile.updateAnimationBaseTime();

    for (MapLayer layer : layers) {
      MapObjects objects = layer.getObjects();
      for (MapObject object : objects) {
        if (object instanceof TiledMapTileMapObject) {
          TiledMapTileMapObject tmo = (TiledMapTileMapObject) object;
          TiledMapTile tile = tmo.getTile();
          TextureRegion region = tile.getTextureRegion();

          float start = tmo.getProperties().get("start", -1f, Float.class);
          float end = tmo.getProperties().get("end", -1f, Float.class);

          boolean toDraw = false;
          if (start == -1) {
            toDraw = true;
          } else {
            if (start <= accumulate) {
              if (end == -1 || end >= accumulate) {
                toDraw = true;
              }
            }
          }
          if (toDraw) {
            batch.draw(region, tmo.getX(), tmo.getY(), region.getRegionWidth() * tmo.getScaleX(), region.getRegionHeight() * tmo.getScaleY());
          }
        }
      }
    }
    batch.end();
    resetViewport();
  }

  @Override
  public void dispose() {
    map.dispose();
    batch.dispose();
  }

  private void loadViewports(TiledMap map) {
    viewports.clear();
    ts.clear();
    MapLayer cameraLayer = map.getLayers().get("camera");
    if (cameraLayer == null) {
      return;
    }
    MapObjects objects = cameraLayer.getObjects();
    for (MapObject object : objects) {
      if (object instanceof RectangleMapObject) {
        RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
        viewports.add(rectangleMapObject.getRectangle());
        ts.add(object.getProperties().get("t", 0f, Float.class));
      }
    }
  }

  private Vector2 tmp = new Vector2();
  private Rectangle displayRect;

  private void setToViewport(Rectangle vp) {
//    vp = calculateSmoothRect(vp);
    float w = vp.getWidth();
    float h = vp.getHeight();
    vp.getCenter(tmp);
    float x = tmp.x;
    float y = tmp.y;

    float r = w / h;
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
    Gdx.gl.glViewport(MathUtils.round(vX), MathUtils.round(vY), MathUtils.round(vW), MathUtils.round(vH));
    camera.setToOrtho(false, w, h);
    camera.position.set(x, y, 0);
  }

  private void resetViewport() {
    Gdx.gl.glViewport(0, 0, width, height);
    camera.setToOrtho(false, width, height);
    camera.position.set(width / 2, height / 2, 0);
  }

  private Rectangle calculateSmoothRect(Rectangle vp) {
    if (displayRect == null) {
      displayRect = new Rectangle(vp);
    } else {
      displayRect.setX(displayRect.getX() + (vp.getX() - displayRect.getX()) * 0.1f);
      displayRect.setY(displayRect.getY() + (vp.getY() - displayRect.getY()) * 0.1f);
      displayRect.setWidth(displayRect.getWidth() + (vp.getWidth() - displayRect.getWidth()) * 0.1f);
      displayRect.setHeight(displayRect.getHeight() + (vp.getHeight() - displayRect.getHeight()) * 0.1f);
    }
    return displayRect;
  }

}
