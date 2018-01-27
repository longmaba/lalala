package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.ShapeUtil;
import com.dongbat.vocal.object.Mechanism;

public class Wall extends Mechanism {

  public boolean decorated = false;
  private float margin = 0;
  private final Array<Sprite> deco = new Array<Sprite>();
  private static Color color = Color.valueOf("1c1c1c");

  @Override
  public void init() {
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
          if(decorators.size == 0) {
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


}
