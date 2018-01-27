package com.dongbat.game.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author tao
 */
public class PhysicsUtil {

  public static Body createBox(World world, Rectangle rectangle, boolean moveable, boolean sensor) {
    PolygonShape polygon = new PolygonShape();
    Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f),
      (rectangle.y + rectangle.height * 0.5f));
    polygon.setAsBox(rectangle.width * 0.5f,
      rectangle.height * 0.5f,
      new Vector2(),
      0.0f);

    BodyDef bodyDef = new BodyDef();
    bodyDef.position.set(size);
    if (moveable) {
      bodyDef.type = BodyDef.BodyType.KinematicBody;
    } else if (sensor) {
      bodyDef.type = BodyDef.BodyType.DynamicBody;
    } else {
      bodyDef.type = BodyDef.BodyType.KinematicBody;
    }
    Body body = world.createBody(bodyDef);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 1;
    fixtureDef.shape = polygon;

    if (sensor) {
      fixtureDef.isSensor = true;
    }

    body.createFixture(fixtureDef);
    polygon.dispose();
    return body;
  }

  public static Body createCircle(World world, float x, float y, float r) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.position.set(x, y);
    bodyDef.type = BodyDef.BodyType.DynamicBody;
//    bodyDef.bullet = true;

    Body body = world.createBody(bodyDef);

    CircleShape shape = new CircleShape();
    shape.setRadius(r);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 1;
    fixtureDef.shape = shape;

//    body.setFixedRotation(true);
    body.createFixture(fixtureDef);
    shape.dispose();
    return body;
  }
  
  public static Body createSensor(World world, float x, float y, float r) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.position.set(x, y);
    bodyDef.type = BodyDef.BodyType.KinematicBody;
//    bodyDef.bullet = true;

    Body body = world.createBody(bodyDef);

    CircleShape shape = new CircleShape();
    shape.setRadius(r);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 1;
    fixtureDef.shape = shape;
    fixtureDef.isSensor = true;

    body.setFixedRotation(true);
    body.createFixture(fixtureDef);
    shape.dispose();
    return body;
  }
}
