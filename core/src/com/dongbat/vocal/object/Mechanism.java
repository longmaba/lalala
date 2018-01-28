package com.dongbat.vocal.object;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.dongbat.game.util.ReflectionUtil;
import com.dongbat.lalala.Level;
import com.dongbat.vocal.object.mechanism.Ball;
import com.dongbat.vocal.object.mechanism.DurationVacuum;
import com.dongbat.vocal.object.mechanism.Goal;
import com.dongbat.vocal.object.mechanism.MovingPlatform;
import com.dongbat.vocal.object.mechanism.PatternGate;
import com.dongbat.vocal.object.mechanism.PitchAndDurationBomb;
import com.dongbat.vocal.object.mechanism.PitchOnlyCannon;
import com.dongbat.vocal.object.mechanism.Spawn;
import com.dongbat.vocal.object.mechanism.Vacuum;
import com.dongbat.vocal.object.mechanism.Wall;

/**
 *
 * @author tao
 */
public abstract class Mechanism {

  protected float x, y, width, height;
  protected Level level;

  public static Mechanism create(Level level, int type, float x, float y, float width, float height, Object... args) {
    Mechanism mechanism = null;
    switch (type) {
      case 0:
        mechanism = new Spawn();
        break;
      case 1:
        mechanism = new Goal();
        break;
      case 2:
        mechanism = new PitchOnlyCannon();
        break;
      case 3:
        mechanism = new DurationVacuum();
        break;
      case 4:
        mechanism = new PitchAndDurationBomb();
        break;
      case 5:
        mechanism = new PatternGate();
        break;
      case 6:
        mechanism = new MovingPlatform();
        break;
      case 7:
        mechanism = new Vacuum();
        break;
      case 99:
        mechanism = new Ball();
        break;
      case 999:
        mechanism = new Wall();
        break;
    }
    if (mechanism != null) {
      int max = args.length;
      if (max % 2 != 0) {
        max--;
      }
      for (int i = 0; i < max; i += 2) {
        String key = (String) args[i];
        Object value = args[i + 1];
        if (key != null) {
          ReflectionUtil.setProperty(mechanism, key, value);
        }
      }
      mechanism.x = x;
      mechanism.y = y;
      mechanism.width = width;
      mechanism.height = height;
      mechanism.level = level;
      mechanism.init();
    }
    return mechanism;
  }

  public void collide(Body body1, Body body2) {

  }

  public void disconnect(Body body1, Body body2) {

  }

  public void update(float delta) {

  }

  public void draw(Batch batch) {

  }
  
  public void init() {

  }

  public void dispose() {

  }
}
