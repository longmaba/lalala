package com.dongbat.lalala;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.dongbat.game.util.AssetUtil;
import com.dongbat.game.util.CameraUtil;
import com.dongbat.game.util.PhysicsUtil;
import com.dongbat.vocal.object.Mechanism;
import com.dongbat.vocal.object.mechanism.Goal;
import java.util.Iterator;

/**
 *
 * @author tao
 */
public class Level {

  // TODO: ECS can fix stuff, but not now
  public static final float GRAVITY = -30;

  // TODO: ppt should be configurable
  public static final float PPT = 18f;
  public static final float SMALLER_SIZE = 20;

  private final Sprite background;
  private final TiledMap map;
  private final DelayedRemovalArray<Mechanism> mechanisms = new DelayedRemovalArray<Mechanism>();
  private final Batch batch;
  private final Box2DDebugRenderer box2DDebugRenderer;
  private final OrthographicCamera camera;
  private final OrthogonalTiledMapRenderer mapRenderer;
  private World world;
  private final Rectangle viewport;

  public World getWorld() {
    return world;
  }

  private final Vector3 v3 = new Vector3();

  public void getScreenCoord(Vector2 worldCoord, Vector2 screenCoord) {
    v3.set(worldCoord, 0);
    camera.project(v3);
    screenCoord.set(v3.x, v3.y);
  }

  public void getWorldCoord(Vector2 screenCoord, Vector2 worldCoord) {
    v3.set(worldCoord, 0);
    camera.unproject(v3);
    screenCoord.set(v3.x, v3.y);
  }

  public float getScreenSize(float worldSize) {
    return worldSize * PPT;
  }

  public float getWorldSize(float screenSize) {
    return screenSize / PPT;
  }

  public boolean done = false;
  private boolean debug = false;

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  private final Array<Body[]> collisions = new Array<Body[]>();
  private final Array<Body[]> disconnections = new Array<Body[]>();

  public Level(String mapName) {
    this(mapName, false);
  }

  public Level(String mapName, ChangeListener changeListener) {
    this(mapName, false);
  }

  public Level(String mapName, boolean tutorial) {
    background = new Sprite(AssetUtil.getAssetManager().get("background.png", Texture.class));

    TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();
    p.textureMinFilter = Texture.TextureFilter.Linear;
    p.textureMagFilter = Texture.TextureFilter.Linear;
    map = new TmxMapLoader().load(mapName, p);
    MapProperties prop = map.getProperties();
    int mapWidth = prop.get("width", Integer.class);
    int mapHeight = prop.get("height", Integer.class);
    int tilePixelWidth = prop.get("tilewidth", Integer.class);
    int tilePixelHeight = prop.get("tileheight", Integer.class);

    float mapPixelWidth = mapWidth * tilePixelWidth;
    float mapPixelHeight = mapHeight * tilePixelHeight;

    camera = CameraUtil.createCamera(mapPixelWidth / mapPixelHeight, SMALLER_SIZE);
    viewport = CameraUtil.calculateViewport(mapPixelWidth / mapPixelHeight);
    batch = new SpriteBatch();
    box2DDebugRenderer = new Box2DDebugRenderer();
    world = new World(new Vector2(0, GRAVITY), false);

    mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / PPT, batch);

    loadWalls();
    loadMechanisms();

    world.setContactListener(new ContactListener() {
      @Override
      public void beginContact(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        collisions.add(new Body[]{body1, body2});
      }

      @Override
      public void endContact(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        disconnections.add(new Body[]{body1, body2});
      }

      @Override
      public void preSolve(Contact contact, Manifold oldManifold) {

      }

      @Override
      public void postSolve(Contact contact, ContactImpulse impulse) {

      }
    });
  }

  public void update(float delta) {
    collisions.clear();
    disconnections.clear();
    world.step(delta, 8, 3);
    mechanisms.begin();
    // TODO: reduce to the calls to collide()
    for (Mechanism mechanism : mechanisms) {
      // TODO: normal
      for (Body[] collision : collisions) {
        mechanism.collide(collision[0], collision[1]);
      }
      for (Body[] disconnection : disconnections) {
        mechanism.disconnect(disconnection[0], disconnection[1]);
      }
      mechanism.update(delta);
    }
    mechanisms.end();
    if (goalUnfinished == 0) {
      done = true;
    }
  }

  private final Rectangle rec = new Rectangle();

  private void loadWalls() {
    MapLayer layer = map.getLayers().get("walls");
    if (layer == null) {
      return;
    }
    MapObjects objects = layer.getObjects();
    for (MapObject mapObject : objects) {
      if (mapObject instanceof RectangleMapObject) {
        RectangleMapObject rmo = (RectangleMapObject) mapObject;
        rec.set(rmo.getRectangle());
        rec.setX(rec.x / PPT).setY(rec.y / PPT)
          .setWidth(rec.width / PPT).setHeight(rec.height / PPT);
        PhysicsUtil.createBox(world, rec, false, false);
        MapProperties properties = rmo.getProperties();
        int size = 0;
        for (Iterator<String> iterator = properties.getKeys(); iterator.hasNext();) {
          iterator.next();
          size++;
        }
        int i = 0;
        Object[] args = new Object[size * 2];
        for (Iterator<String> iterator = properties.getKeys(); iterator.hasNext();) {
          String key = iterator.next();
          args[i] = key;
          args[i + 1] = properties.get(key);
          i += 2;
        }
        createMechanism(rec.x + rec.width / 2, rec.y + rec.height / 2, rec.width, rec.height, 999, args);
      }
    }
  }

  private void loadMechanisms() {
    MapLayer layer = map.getLayers().get("mechanisms");
    if (layer == null) {
      return;
    }
    MapObjects objects = layer.getObjects();
    for (MapObject mapObject : objects) {
      if (mapObject instanceof RectangleMapObject) {
        RectangleMapObject rmo = (RectangleMapObject) mapObject;
        String typeString = rmo.getProperties().get("type", String.class);
        int type = -1;
        try {
          type = Integer.parseInt(typeString);
        } catch (NumberFormatException exception) {
        }
        if (type != -1) {
          Rectangle rectangle = rmo.getRectangle();
          MapProperties properties = rmo.getProperties();
          int size = 0;
          for (Iterator<String> iterator = properties.getKeys(); iterator.hasNext();) {
            iterator.next();
            size++;
          }
          int i = 0;
          Object[] args = new Object[size * 2];
          for (Iterator<String> iterator = properties.getKeys(); iterator.hasNext();) {
            String key = iterator.next();
            args[i] = key;
            args[i + 1] = properties.get(key);
            i += 2;
          }
//          createMechanism(rectangle.x / PPT + rectangle.width / PPT / 2, rectangle.y / PPT + rectangle.height / PPT / 2, type);
          createMechanism(rectangle.x / PPT + rectangle.width / PPT / 2, rectangle.y / PPT + rectangle.height / PPT / 2, rectangle.width / PPT, rectangle.height / PPT, type, args);
        }
      }
    }
  }

  public void spawnBall(float x, float y, float r) {
    createMechanism(x, y, r, r, 99, "radius", r);
  }

  public void createMechanism(float x, float y, float width, float height, int type, Object... args) {
    Mechanism mechanism = Mechanism.create(this, type, x, y, width, height, args);
    if (mechanism != null) {
      mechanisms.add(mechanism);
      if (mechanism instanceof Goal) {
        goalUnfinished++;
      }
    }
  }

  public void destroyMechanism(Mechanism mechanism) {
    mechanism.dispose();
    mechanisms.removeValue(mechanism, true);
  }

  public void draw() {
    Gdx.gl.glViewport(MathUtils.round(viewport.x), MathUtils.round(viewport.y),
      MathUtils.round(viewport.width), MathUtils.round(viewport.height));
    camera.update();
    mapRenderer.setView(camera);
    mapRenderer.render();
    batch.begin();
    batch.setProjectionMatrix(camera.combined);
    background.setSize(Gdx.graphics.getWidth() / PPT, Gdx.graphics.getHeight() / PPT);
    background.draw(batch);
    for (Mechanism mechanism : mechanisms) {
      mechanism.draw(batch);
    }
    batch.end();
    if (debug) {
      box2DDebugRenderer.render(world, camera.combined);
    }
    Gdx.gl.glViewport(0, 0,
      Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  public void dispose() {
    for (Mechanism mechanism : mechanisms) {
      mechanism.dispose();
    }
    world.dispose();
    map.dispose();
    batch.dispose();
    box2DDebugRenderer.dispose();
  }

  private int goalUnfinished = 0;

  public void finishOneGoal() {
    goalUnfinished--;
  }

}
