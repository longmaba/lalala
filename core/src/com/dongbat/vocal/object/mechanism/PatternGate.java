package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.game.util.PitchUtil;
import com.dongbat.game.util.ShapeUtil;
import com.dongbat.game.util.VoiceConfig;
import com.dongbat.vocal.object.Mechanism;

/**
 *
 * @author tao
 */
public class PatternGate extends Mechanism {

  private String patternString;
  private final Array<float[]> patterns = new Array<float[]>();
  private static final Color[] COLORS = PitchUtil.getCOLORS();;
  private float accumulate = 0;
  private int stage = 0;
  private Body hitbox;

  @Override
  public void init() {
    hitbox = PhysicsUtil.createBox(level.getWorld(), new Rectangle(x - width / 2, y - height / 2, width, height), true, false);
    if (patternString != null) {
      String[] patternStrings = patternString.split(";");
      for (String pattern : patternStrings) {
        String[] frags = pattern.split(":");
        if (frags.length == 2) {
          try {
            patterns.add(new float[]{Float.parseFloat(frags[0]), Float.parseFloat(frags[1])});
          } catch (NumberFormatException ex) {

          }
        }
      }
    }
  }

  @Override
  public void update(float delta) {
    if (stage >= patterns.size) {
      stage = 0;
      accumulate = 0;
      level.getWorld().destroyBody(hitbox);
    }
    if (PitchUtil.getLevel() >= VoiceConfig.getSilentThreshold() && PitchUtil.getKeyLevel(4) == patterns.get(stage)[0]) {
      accumulate += delta;
      if (accumulate >= patterns.get(stage)[1]) {
        stage++;
      }
    } else {
      accumulate = 0;
    }
  }

  @Override
  public void draw(Batch batch) {
    Texture white = AssetUtil.getWhite();
    float totalDuration = 0;
    float startX = 0;
    for (float[] pattern : patterns) {
      totalDuration += pattern[1];
    }
    ShapeUtil.drawRect(batch, x - 0.1f, y - 0.55f, 3.2f, 0.3f, Color.WHITE, white);
    ShapeUtil.drawRect(batch, x, y - 0.5f, 3, 0.2f, Color.BLACK, white);
    for (int i = 0; i < patterns.size; i++) {
      float[] pattern = patterns.get(i);
      int level = (int) pattern[0];
      float duration = pattern[1];
      if (stage > i) {
        ShapeUtil.drawRect(batch, x + startX, y - 0.5f, (duration / totalDuration) * 3, 0.2f, COLORS[level], white);
      } else {
        ShapeUtil.drawRect(batch, x + startX, y - 0.5f, (duration / totalDuration) * 3, 0.2f, COLORS[level], 0.4f, white);
        if (stage == i) {
          ShapeUtil.drawRect(batch, x + startX, y - 0.5f, (accumulate / totalDuration) * 3, 0.2f, COLORS[level], white);
        }
      }
      startX += (duration / totalDuration) * 3;
    }
  }

}
