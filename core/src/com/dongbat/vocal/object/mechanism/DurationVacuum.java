package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.game.util.ShapeUtil;
import com.dongbat.game.util.VoiceConfig;

/**
 *
 * @author tao
 */
public class DurationVacuum extends RotatingMechanism {

  private float duration = 1;
  private float accumulate = 0;
  private Body hitbox;

  private float dragSpeed = 20;
  private float rate = 1;
  private Sprite battery;
  private Sprite radius;
  private Sprite fan;
  private float effectRadius = 4f;

  @Override
  public void init() {
    hitbox = PhysicsUtil.createSensor(level.getWorld(), x, y, effectRadius);
    battery = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("battery"));
    battery.setSize(battery.getWidth() * 1f / battery.getHeight(), 1f);
    
    radius = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("vacuum_radius"));
    radius.setSize(effectRadius * 2, effectRadius * 2);
    
    fan = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("vacuum"));
    fan.setSize(radius.getWidth(), radius.getHeight());
    fan.setOriginCenter();
  }

  private final Vector2 v = new Vector2();

  @Override
  public void update(float delta) {
    if (vacuum) {
      fanSpeed = 3f;
      accumulate -= delta * rate;
      for (Body body : toVacuum) {
        body.setGravityScale(0);
        v.set(hitbox.getPosition()).sub(body.getPosition());
        if (v.len2() > (dragSpeed * delta) * (dragSpeed * delta)) {
          body.setLinearVelocity(v.nor().scl(dragSpeed));
        } else {
          body.setLinearVelocity(0, 0);
          body.setTransform(hitbox.getPosition(), body.getAngle());
        }
      }
      if (accumulate <= 0) {
        accumulate = 0;
        vacuum = false;
        for (Body body : toVacuum) {
          body.setGravityScale(1);
        }
      }
      return;
    }
    if (PitchUtil.getLevel() >= VoiceConfig.getSilentThreshold()) {
      fanSpeed = -3f;
      accumulate += delta;
    } else if (Math.abs(accumulate - duration) <= 0.4f * duration) {
      vacuum = true;
    } else {
      accumulate = 0;
      fanSpeed = 0;
    }
  }

  @Override
  public void draw(Batch batch) {
    radius.setPosition(x - effectRadius, y - effectRadius);
    radius.draw(batch);
    
    fan.rotate(fanSpeed);
    fan.setAlpha(MathUtils.clamp(accumulate / duration, 0, 1f));
    fan.setPosition(x - effectRadius, y - effectRadius);
    fan.draw(batch);
    
    battery.setPosition(x, y);
    ShapeUtil.drawRect(batch, x, y, battery.getWidth(), battery.getHeight(), Color.DARK_GRAY, AssetUtil.getWhite());
    ShapeUtil.drawRect(batch, x, y, MathUtils.clamp(battery.getWidth() * 0.7f * (accumulate / duration), 0, battery.getWidth()), battery.getHeight(), Color.WHITE, AssetUtil.getWhite());
    battery.draw(batch);
  }

  private boolean vacuum = false;
  private float fanSpeed = 0;
  
  private final Array<Body> toVacuum = new Array<Body>();

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
    toVacuum.add(collidee);
  }

  @Override
  public void disconnect(Body body1, Body body2) {
    Body collidee;
    if (body1 == hitbox && body2.getUserData() instanceof Ball) {
      collidee = body2;
    } else if (body2 == hitbox && body1.getUserData() instanceof Ball) {
      collidee = body1;
    } else {
      return;
    }
    toVacuum.removeValue(collidee, true);
    collidee.setGravityScale(1);
  }
}
