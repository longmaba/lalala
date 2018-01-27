package com.dongbat.vocal.object.mechanism;

import com.dongbat.vocal.object.Mechanism;

public class RotatingMechanism extends Mechanism {

  protected float angle = 0;
  protected float angularSpeed = 0;

  @Override
  public void update(float delta) {
    angle += angularSpeed * delta;
    angle %= 360;
  }

}
