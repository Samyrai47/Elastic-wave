package org.mipt;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

public class Main extends ApplicationAdapter {
  private Physics physics;
  PhysicsLogger logger;
  private ShapeRenderer shapeRenderer;

  private FillViewport viewport;
  private OrthographicCamera camera;

  public static final float PIXELS_PER_METER = 1000.0f;
  private static final float WORLD_HEIGHT = 600;
  private static final float WORLD_WIDTH = 1000;
  private static final float FIXED_TIME_STEP = 0.00025f;
  private float accumulator = 0f;
  private static float simTime = 0f;

  private List<Weight> weights = new ArrayList<>();
  private List<Spring> springs = new ArrayList<>();
  private static final int WEIGHTS_NUMBER = 30;

  @Override
  public void create() {
    camera = new OrthographicCamera();
    viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

    shapeRenderer = new ShapeRenderer();
    physics = new Physics();
    try {
      logger = new PhysicsLogger("dataset", WEIGHTS_NUMBER);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    float mass = 0.25f;
    float leftX = 200;
    float length = 200;
    float width = 50;
    float k = 47;
    float SpringY = 300;
    float weightY = 275;
    float wallHeight = 460;
    float weightHeight = 50;
    for (int i = 0; i < WEIGHTS_NUMBER; i++) {
      Spring spring;
      if (i == 0) {
        spring =
            new Spring(
                k,
                new Vector2(leftX, SpringY),
                new Vector2(leftX + length, SpringY),
                20,
                8.0f,
                wallHeight,
                weightHeight);
      } else {
        spring =
            new Spring(
                k,
                new Vector2(leftX, SpringY),
                new Vector2(leftX + length, SpringY),
                20,
                8.0f,
                weightHeight,
                weightHeight);
      }
      springs.add(spring);
      if (i != 0) {
        weights.get(weights.size() - 1).attachHorizontalSprings(spring);
      }
      Weight weight = new Weight(mass, leftX + length, weightY, width, 50);
      weight.attachHorizontalSprings(spring);
      weights.add(weight);
      leftX = leftX + length + width;
    }
    Spring lastSpring =
        new Spring(
            k,
            new Vector2(leftX, SpringY),
            new Vector2(leftX + length, SpringY),
            20,
            8.0f,
            weightHeight,
            wallHeight);
    weights.get(weights.size() - 1).attachHorizontalSprings(lastSpring);
    springs.add(lastSpring);

    Gdx.input.setInputProcessor(
        new InputAdapter() {
          @Override
          public boolean touchDragged(int screenX, int screenY, int pointer) {
            float deltaX = -Gdx.input.getDeltaX() * camera.zoom;
            float deltaY = Gdx.input.getDeltaY() * camera.zoom;
            camera.translate(deltaX, deltaY);
            return true;
          }

          @Override
          public boolean scrolled(float amountX, float amountY) {
            float zoomFactor = 1.1f;
            if (amountY > 0) {
              camera.zoom *= zoomFactor;
            } else if (amountY < 0) {
              camera.zoom /= zoomFactor;
            }

            camera.zoom = MathUtils.clamp(camera.zoom, 1f, 10f);
            return true;
          }
        });
  }

  @Override
  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    handleInput();
    camera.update();
    shapeRenderer.setProjectionMatrix(camera.combined);

    float frameTime = Gdx.graphics.getDeltaTime();
    accumulator += frameTime;
    while (accumulator >= FIXED_TIME_STEP) {
      physics.applyPhysics(weights, FIXED_TIME_STEP);
      simTime += FIXED_TIME_STEP;
      try {
        logger.logX(weights, simTime);
        logger.logY(weights, simTime);
        logger.logEnergyTime(weights.get(weights.size() - 1).getRightSpring(), simTime);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      accumulator -= FIXED_TIME_STEP;
    }

    // отрисовка стен
    Spring lastSpring = springs.get(springs.size() - 1);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.line(200, 70, 200, 70 + springs.get(0).getLeftHeight());
    shapeRenderer.line(lastSpring.getRightX(), 70, lastSpring.getRightX(), 530);
    shapeRenderer.end();

    for (Spring spring : springs) {
      drawSpring(spring);
    }

    for (Weight weight : weights) {
      drawWeight(weight);
    }
  }

  /**
   * Метод для отрисовки пружин. Вычисляет ширину и высоту отдельного витка. Для каждого витка
   * находит координату x и y относительно направления пружины. Вычисляет нормаль к направлению
   * пружины. Смещает концы витка от центральной линии по нормали.
   *
   * @param spring пружина
   */
  private void drawSpring(Spring spring) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setColor(Color.WHITE);

    float dx = (spring.getRightAnchor().x - spring.getLeftAnchor().x) / spring.getCoils();
    float dy = (spring.getRightAnchor().y - spring.getLeftAnchor().y) / spring.getCoils();

    for (int i = 0; i < spring.getCoils(); i++) {
      float sign = (i % 2 == 0) ? 1 : -1;

      float x1 = spring.getLeftAnchor().x + dx * i;
      float y1 = spring.getLeftAnchor().y + dy * i;
      float x2 = spring.getLeftAnchor().x + dx * (i + 1);
      float y2 = spring.getLeftAnchor().y + dy * (i + 1);

      // нормаль
      float nx = -dy;
      float ny = dx;
      float len = (float) Math.sqrt(nx * nx + ny * ny);
      nx /= len;
      ny /= len;

      x1 += nx * spring.getWidth() * sign;
      y1 += ny * spring.getWidth() * sign;
      x2 += nx * spring.getWidth() * -sign;
      y2 += ny * spring.getWidth() * -sign;

      shapeRenderer.line(x1, y1, x2, y2);
    }

    shapeRenderer.end();
  }

  private void drawWeight(Weight weight) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(Color.DARK_GRAY);

    shapeRenderer.rect(weight.getX(), weight.getY(), weight.getWidth(), weight.getHeight());

    shapeRenderer.end();
  }

  private void handleInput() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      physics.pushFirstWeight(weights, 1000f, 0f);
    }
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    try {
      logger.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
