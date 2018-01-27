package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.vocal.object.Mechanism;

/**
 *
 * @author tao
 */
public class Ball extends Mechanism {

  private Body ball;
  private float radius = .5f;
  private Sprite ballSprite;

  @Override
  public void init() {
    ball = PhysicsUtil.createCircle(level.getWorld(), x, y, radius);
    ball.setUserData(this);
    ballSprite = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("ball"));
  }

  @Override
  public void update(float delta) {
    if (!ball.isActive()) {
      level.destroyMechanism(this);
      return;
    }
    x = ball.getPosition().x;
    y = ball.getPosition().y;
  }

  @Override
  public void draw(Batch batch) {
    if (!ball.isActive()) {
      return;
    }
    ballSprite.setSize(2 * radius, 2 * radius);
    ballSprite.setPosition(x - radius, y - radius);
    ballSprite.draw(batch);
  }

  @Override
  public void dispose() {
    if (ball != null && ball.isActive()) {
      level.getWorld().destroyBody(ball);
    }
  }

}
