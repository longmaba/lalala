package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.game.util.ShapeUtil;
import com.dongbat.game.util.VoiceConfig;

public class PitchAndDurationBomb extends RotatingMechanism {

  private int pitchLevel = 3;
  private float duration = 2;
  private float accumulate = 0;
  
  private static final Color[] COLORS = PitchUtil.getCOLORS();;
  private Body hitbox;

  @Override
  public void init() {
    hitbox = PhysicsUtil.createSensor(level.getWorld(), x, y, 4);
  }
  
  private final Vector2 v = new Vector2();
  
  @Override
  public void update(float delta) {
    super.update(delta);
    if (PitchUtil.getLevel() >= VoiceConfig.getSilentThreshold() && PitchUtil.getKeyLevel(4) == pitchLevel) {
      accumulate += delta;
      if(accumulate >= duration) {
        accumulate = 0;
        for (Body body : toPush) {
          v.set(hitbox.getPosition()).sub(body.getPosition()).scl(-20);
          body.applyLinearImpulse(v.x, v.y, 0, 0, true);
        }
      }
    } else {
      accumulate = 0;
    }
  }

  @Override
  public void draw(Batch batch) {
    ShapeUtil.drawRect(batch, x - 0.5f, y - 0.5f, 1, 1, COLORS[pitchLevel], AssetUtil.getWhite());
    ShapeUtil.drawRect(batch, x - 0.5f, y - 0.8f, 1, 0.2f, Color.WHITE, AssetUtil.getWhite());
    ShapeUtil.drawRect(batch, x - 0.5f, y - 0.8f, MathUtils.clamp(1 * (accumulate / duration), 0, 1), 0.2f, Color.GRAY, AssetUtil.getWhite());
  }
  
  private final Array<Body> toPush = new Array<Body>();

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
    toPush.add(collidee);
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
    toPush.removeValue(collidee, true);
  }
  
}
