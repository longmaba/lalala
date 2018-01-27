package com.dongbat.vocal.object.mechanism;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.game.util.PitchUtil;

public class PitchOnlyCannon extends RotatingMechanism {

  private int pitchLevel = 0;
  private static final float COOLDOWN = 1f;
  private static final float BULLET_TIME = 0.3f;
  private static final float BULLET_SPEED = 25f;
  private static final float HITBOX_SIZE = 1.5f;
  private long lastUsed = 0;
  private Body bullet;

  private final Vector2 v = new Vector2();
  private Sprite cannonDock;
  private Sprite cannonDockCd;
  private Sprite cannonTrajectory;
  private Sprite cannonBullet;

  @Override
  public void init() {
    cannonDock = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("cannon_dock", pitchLevel));
    float h = cannonDock.getHeight();
    cannonDock.setSize(cannonDock.getWidth() / h * height, height);
    cannonDock.setOrigin(cannonDock.getWidth() / 4, cannonDock.getHeight() / 2);

    cannonDockCd = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("cannon_dock_cd", pitchLevel));
    cannonDockCd.setSize(cannonDockCd.getWidth() / h * height, height);
    cannonDockCd.setOrigin(cannonDockCd.getWidth() / 4, cannonDockCd.getHeight() / 2);

    cannonTrajectory = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("cannon_trajectory"));
    cannonTrajectory.setSize(cannonTrajectory.getWidth() * height / h, cannonTrajectory.getHeight() * height / h);
    cannonTrajectory.setOrigin(0.1f, cannonTrajectory.getHeight() / 2);

    cannonBullet = new Sprite(AssetUtil.getAssetManager().get("stuff.atlas", TextureAtlas.class).findRegion("cannon_bullet", pitchLevel));
    cannonBullet.setSize(cannonBullet.getWidth() * height / h, cannonBullet.getHeight() * height / h);
    cannonBullet.setOriginCenter();
  }

  @Override
  public void update(float delta) {
    super.update(delta);
    if (bullet != null) {
      if (collided) {
//        if (TimeUtils.timeSinceMillis(lastUsed) >= MIN_BULLET_TIME * 1000) {
        level.getWorld().destroyBody(bullet);
        bullet = null;
        collided = false;
//        }
      } else if (TimeUtils.timeSinceMillis(lastUsed) >= BULLET_TIME * 1000) {
        level.getWorld().destroyBody(bullet);
        bullet = null;
        collided = false;
      }
    }

    if (TimeUtils.timeSinceMillis(lastUsed) >= COOLDOWN * 1000 && bullet == null) {
      if (PitchUtil.getKeyLevel(4) == pitchLevel) {
        bullet = PhysicsUtil.createBox(level.getWorld(), new Rectangle(x - HITBOX_SIZE / 2, y - HITBOX_SIZE / 2, HITBOX_SIZE, HITBOX_SIZE), true, false);
        bullet.setTransform(bullet.getPosition(), MathUtils.degreesToRadians * angle);
        bullet.setType(BodyDef.BodyType.KinematicBody);
        v.set(0, BULLET_SPEED);
        v.rotate(angle);
        bullet.setLinearVelocity(v);
        lastUsed = TimeUtils.millis();
      }
    }
  }

  @Override
  public void draw(Batch batch) {
//    if (TimeUtils.timeSinceMillis(lastUsed) >= COOLDOWN * 1000) {
//      ShapeUtil.drawRect(batch, x - 0.5f, y - 0.5f, 1, 1, COLORS[pitchLevel], AssetUtil.getWhite());
//    } else {
//      ShapeUtil.drawRect(batch, x - 0.5f, y - 0.5f, 1, 1, Color.GRAY, AssetUtil.getWhite());
//    }

    if (TimeUtils.timeSinceMillis(lastUsed) >= COOLDOWN * 1000) {
      cannonDock.setPosition(x - width / 4, y - height / 2);
      cannonDock.setRotation(angle + 90);
      cannonDock.draw(batch);
    } else {
      cannonDockCd.setPosition(x - width / 4, y - height / 2);
      cannonDockCd.setRotation(angle + 90);
      cannonDockCd.draw(batch);
    }

    cannonTrajectory.setPosition(x, y - cannonTrajectory.getHeight() / 2);
    cannonTrajectory.setRotation(angle + 90);
    cannonTrajectory.draw(batch);

    if (bullet != null) {
      cannonBullet.setPosition(bullet.getPosition().x - cannonBullet.getWidth() / 2, bullet.getPosition().y - cannonBullet.getHeight() / 2);
      cannonBullet.setRotation(bullet.getAngle() * MathUtils.radiansToDegrees + 90);
      cannonBullet.draw(batch);
    }

//    ShapeUtil.drawRect(batch, x - 0.5f, y - 0.8f, 1, 0.2f, Color.WHITE, AssetUtil.getWhite());
//    ShapeUtil.drawRect(batch, x - 0.5f, y - 0.8f, MathUtils.clamp(1 - 1 * ((TimeUtils.timeSinceMillis(lastUsed) / 1000f) / COOLDOWN), 0, 1), 0.2f, Color.GRAY, AssetUtil.getWhite());
//    v.set(0, 5);
//    v.rotate(angle);
//    v.add(x, y);
//    ShapeUtil.drawLine(batch, x, y, v.x, v.y, 0.1f, AssetUtil.getWhite());
  }

  private boolean collided = false;

  @Override
  public void collide(Body body1, Body body2) {
    Body collidee = null;
    if (body1 == bullet && body2.getUserData() instanceof Ball) {
      collidee = body2;
      collided = true;
    } else if (body2 == bullet && body1.getUserData() instanceof Ball) {
      collidee = body1;
      collided = true;
    } else {
      return;
    }
//    v.set(0, BULLET_SPEED).rotate(bullet.getAngle() * MathUtils.radiansToDegrees);
//    collidee.applyLinearImpulse(v, collidee.getPosition(), true);
  }

}
