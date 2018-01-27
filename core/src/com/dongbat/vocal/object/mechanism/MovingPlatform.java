/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.game.util.ShapeUtil;
import com.dongbat.vocal.object.Mechanism;

/**
 *
 * @author tao
 */
public class MovingPlatform extends Mechanism {

  private Body box;
  public boolean decorated = false;
  private float margin = 0;
  private final Array<Sprite> deco = new Array<Sprite>();
  private static Color color = Color.valueOf("1c1c1c");

  public float offsetX;
  public float speedX;
  private boolean goingBackX = false;

  @Override
  public void init() {
    Rectangle rec = new Rectangle(
      x, y, width, height
    );
    rec.setX(rec.x - rec.width / 2);
    rec.setY(rec.y - rec.height / 2);
    box = PhysicsUtil.createBox(level.getWorld(), rec, true, false);
    if (decorated) {
      float w = width;
      Array<Sprite> decorators = new Array<Sprite>(AssetUtil.getDecorators());
      deco.clear();
      while (w > 0) {
        int random = MathUtils.random(decorators.size - 1);
        Sprite sprite = decorators.get(random);
        if (w >= sprite.getWidth()) {
          deco.add(sprite);
          w -= sprite.getWidth();
        } else {
          decorators.removeValue(sprite, true);
          if (decorators.size == 0) {
            margin = w / 2;
            break;
          }
        }
      }
    }
  }

  @Override
  public void draw(Batch batch) {
    Texture white = AssetUtil.getWhite();
    float x = box.getPosition().x;
    float y = box.getPosition().y;
    ShapeUtil.drawRect(batch, x - width / 2, y - height / 2, width, height, color, white);
    if (decorated) {
      float tx = x - width / 2 + margin;
      float ty = y - height / 2;
      for (int i = 0; i < deco.size; i++) {
        Sprite sprite = deco.get(i);
        sprite.setPosition(tx, ty - sprite.getHeight());
        sprite.draw(batch);
        tx += sprite.getWidth();
      }
    }
  }

  @Override
  public void update(float delta) {
    if (!goingBackX) {
      box.setLinearVelocity(speedX, 0);
      float currentOffsetX = box.getPosition().x - x;
      if (speedX < 0) {
        currentOffsetX = -currentOffsetX;
      }
      if (currentOffsetX >= offsetX) {
        goingBackX = true;
      }
    } else {
      box.setLinearVelocity(-speedX, 0);
      float currentOffsetX = box.getPosition().x - x;
      if (speedX < 0) {
        currentOffsetX = -currentOffsetX;
      }
      if (currentOffsetX <= 0) {
        goingBackX = false;
      }
    }
  }

}
