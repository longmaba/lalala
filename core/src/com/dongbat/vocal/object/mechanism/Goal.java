package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.FontUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.vocal.object.Mechanism;

/**
 *
 * @author tao
 */
public class Goal extends Mechanism {

  public int max = 1;

  private float stateTime;
  private Body hitbox;
  private int count = 0;
  private Sprite sprite;
  private Animation<TextureRegion> animation;

  @Override
  public void init() {
    animation = new Animation(0.3f, AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegions("goal"));
    hitbox = PhysicsUtil.createBox(level.getWorld(), new Rectangle(x - width / 2, y - height / 2, width, height), false, false);
    sprite = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("goal_done"));
    sprite.setSize(width, width * sprite.getHeight() / sprite.getWidth());
    stateTime = 0;
  }

  @Override
  public void collide(Body body1, Body body2) {
    Body collidee;
    if (body1 == hitbox && body2.getUserData() instanceof Ball) {
      collidee = body2;
    } else if (body2 == hitbox && body1.getUserData() instanceof Ball) {
      collidee = body1;
    } else {
      return;
    }
    if (count >= max) {
      return;
    }
    level.getWorld().destroyBody(collidee);
    collidee.setActive(false);
    count++;
    if (count == max) {
      level.finishOneGoal();
    }
  }

  @Override
  public void draw(Batch batch) {
    if (max - count > 0) {
      stateTime += Gdx.graphics.getDeltaTime();
      TextureRegion keyFrame = animation.getKeyFrame(stateTime, true);
      batch.draw(keyFrame, x - width / 2, y - height / 2, width, height);
    } else {
      sprite.setPosition(x - width / 2, y - height / 2);
      sprite.draw(batch);
    }
  }

  private final Vector2 p = new Vector2();

  
}
