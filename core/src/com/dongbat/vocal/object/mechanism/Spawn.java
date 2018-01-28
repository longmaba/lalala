package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.vocal.object.Mechanism;

/**
 *
 * @author tao
 */
public class Spawn extends Mechanism {

  public float delay = 0;
  public int max = 5;
  public float rate = 0.2f;
  public float radius = 0.5f;
  private Sprite sprite;

  private float accumulate = 0;
  private int count = 0;
  public boolean noSilent = false;

  @Override
  public void init() {
    accumulate = -delay;
    sprite = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("spawner"));
    sprite.setSize(width, width * sprite.getHeight() / sprite.getWidth());
  }

  @Override
  public void update(float delta) {
    if (count >= max && max >= 0) {
      return;
    }
    if (noSilent && PitchUtil.getKeyLevel(4) < 0) {
      return;
    }
    accumulate += delta;
    while (accumulate >= 0) {
      accumulate -= rate;
      level.spawnBall(x, y, radius);
      count++;
    }
  }

  @Override
  public void draw(Batch batch) {
    sprite.setPosition(x - width / 2, y - height / 2);
    sprite.draw(batch);
  }

  private final Vector2 p = new Vector2();

}
