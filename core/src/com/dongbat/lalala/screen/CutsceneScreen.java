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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

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

  public CutsceneScreen(Game game) {
    this.game = game;
    map = new TmxMapLoader().load("test.tmx");
    width = Gdx.graphics.getWidth();
    height = Gdx.graphics.getHeight();
    camera = new OrthographicCamera(width, height);
    camera.position.set(width / 2, height / 2, 0);
    batch = new SpriteBatch();
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Keys.R)) {
      game.setScreen(new CutsceneScreen(game));
    }
    if (Gdx.input.isKeyPressed(Keys.SPACE)) {
      Gdx.gl.glViewport(0, height / 4, width, height / 2);
      camera.setToOrtho(false, width, height / 2);
      camera.position.set(width / 2, height / 2, 0);
    }
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

          batch.draw(region, tmo.getX(), tmo.getY(), region.getRegionWidth() * tmo.getScaleX(), region.getRegionHeight() * tmo.getScaleY());
        }
      }
    }
    batch.end();

    Gdx.gl.glViewport(0, 0, width, height);
    camera.setToOrtho(false, width, height);
  }

  @Override
  public void dispose() {
    map.dispose();
    batch.dispose();
  }

}
