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
  private static final float FIXED_TIME_STEP = 0.01f;
  private float accumulator = 0f;
  private static float simTime = 0f;

  private final List<Weight> weights = new ArrayList<>();
  private final List<Spring> springs = new ArrayList<>();
  private static final int WEIGHTS_NUMBER_X = 30;
  private static final int WEIGHTS_NUMBER_Y = 30;

  /** Границы для стен */
  private float leftWallX;
  private float rightWallX;
  private float upperWallY;
  private float lowerWallY;

  @Override
  public void create() {
    camera = new OrthographicCamera();
    viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

    shapeRenderer = new ShapeRenderer();
    physics = new Physics();
    try {
      logger = new PhysicsLogger("dataset", WEIGHTS_NUMBER_X * WEIGHTS_NUMBER_Y);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    physics.setWeightsNumberX(WEIGHTS_NUMBER_X);
    physics.setWeightsNumberY(WEIGHTS_NUMBER_Y);
    leftWallX = 200;
    float mass = 1f;
    float length = 200;
    float width = 50;
    float k = 60;
    float SpringY = 300;
    float weightY = 275;
    float wallHeight = 460;
    float weightHeight = 50;
    float lowerY = 275;
    lowerWallY = lowerY - length;
    for (int j = 0; j < WEIGHTS_NUMBER_Y; j++) {
      float leftX = leftWallX;
      for (int i = 0; i < WEIGHTS_NUMBER_X; i++) {
        Spring horizontalSpring;
        if (i == 0) {
          horizontalSpring =
              new Spring(
                  k,
                  new Vector2(leftX, lowerY + weightHeight / 2),
                  new Vector2(leftX + length, lowerY + weightHeight / 2),
                  20,
                  8.0f);
        } else {
          horizontalSpring =
              new Spring(
                  k,
                  new Vector2(leftX, lowerY + weightHeight / 2),
                  new Vector2(leftX + length, lowerY + weightHeight / 2),
                  20,
                  8.0f);
        }
        springs.add(horizontalSpring);
        if (i != 0) {
          weights.get(weights.size() - 1).setRightSpring(horizontalSpring);
        }
        Spring verticalSpring = new Spring(k, 20, 8.0f, length);
        verticalSpring.setUpperAnchor(new Vector2(leftX + width / 2 + length, lowerY));
        verticalSpring.setLowerAnchor(new Vector2(leftX + width / 2 + length, lowerY - length));
        springs.add(verticalSpring);
        if (j != 0) {
          weights.get(weights.size() - WEIGHTS_NUMBER_X).setUpperSpring(verticalSpring);
        }
        Weight weight = new Weight(mass, leftX + length, lowerY, width, weightHeight);
        weight.setLeftSpring(horizontalSpring);
        weight.setLowerSpring(verticalSpring);
        weights.add(weight);
        leftX = leftX + length + width;
      }
      Spring lastSpring =
          new Spring(
              k,
              new Vector2(leftX, lowerY + weightHeight / 2),
              new Vector2(leftX + length, lowerY + weightHeight / 2),
              20,
              8.0f);
      weights.get(weights.size() - 1).setRightSpring(lastSpring);
      springs.add(lastSpring);
      lowerY = lowerY + weightHeight + length;
    }
    rightWallX = springs.get(springs.size() - 1).getRightX();
    upperWallY = weights.get(weights.size() - 1).getY() + weightHeight + length;
    for (int i = 0; i < WEIGHTS_NUMBER_X; i++) {
      Spring verticalSpring = new Spring(k, 20, 8.0f, length);
      Weight weight = weights.get(weights.size() - WEIGHTS_NUMBER_X + i);
      verticalSpring.setUpperAnchor(
          new Vector2(weight.getX() + width / 2, weight.getY() + weightHeight + length));
      verticalSpring.setLowerAnchor(
          new Vector2(weight.getX() + width / 2, weight.getY() + weightHeight));
      weight.setUpperSpring(verticalSpring);
      springs.add(verticalSpring);
    }

    float diagK = k * 0.7f;

    for (int j = 0; j < WEIGHTS_NUMBER_Y; j++) {
      for (int i = 0; i < WEIGHTS_NUMBER_X; i++) {
        int idx = j * WEIGHTS_NUMBER_X + i;
        Weight w = weights.get(idx);
        Vector2 c = new Vector2(w.getX() + width / 2f, w.getY() + weightHeight / 2f);

        if (i < WEIGHTS_NUMBER_X - 1 && j < WEIGHTS_NUMBER_Y - 1) {
          int idxUR = (j + 1) * WEIGHTS_NUMBER_X + (i + 1);
          Weight wUR = weights.get(idxUR);
          Vector2 cUR = new Vector2(wUR.getX() + width / 2f, wUR.getY() + weightHeight / 2f);

          float diagRest = c.dst(cUR);

          Spring diagUR = new Spring(diagK, 20, 8.0f, diagRest);
          diagUR.setAnchors(c.cpy(), cUR.cpy());
          springs.add(diagUR);

          w.setUpperRightSpring(diagUR);
          wUR.setLowerLeftSpring(diagUR);
        }

        if (i > 0 && j < WEIGHTS_NUMBER_Y - 1) {
          int idxUL = (j + 1) * WEIGHTS_NUMBER_X + (i - 1);
          Weight wUL = weights.get(idxUL);
          Vector2 cUL = new Vector2(wUL.getX() + width / 2f, wUL.getY() + weightHeight / 2f);

          float diagRest = c.dst(cUL);

          Spring diagUL = new Spring(diagK, 20, 8.0f, diagRest);
          diagUL.setAnchors(c.cpy(), cUL.cpy());
          springs.add(diagUL);

          w.setUpperLeftSpring(diagUL);
          wUL.setLowerRightSpring(diagUL);
        }
      }
    }

    physics.setLeftWallX(leftWallX);
    physics.setRightWallX(rightWallX);
    physics.setUpperWallY(upperWallY);
    physics.setLowerWallY(lowerWallY);

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

            camera.zoom = MathUtils.clamp(camera.zoom, 1f, 100f);
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
        logger.logTotalEnergy(weights, springs, simTime);
        logger.logBorderKineticEnergy(weights, WEIGHTS_NUMBER_X, WEIGHTS_NUMBER_Y, simTime);
        logger.logSideBorderEnergy(weights, WEIGHTS_NUMBER_X, WEIGHTS_NUMBER_Y, simTime);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      accumulator -= FIXED_TIME_STEP;
    }

    if ((int) (simTime * 100) % 100 == 0) {
      try {
        logger.logDisplacementField(weights, WEIGHTS_NUMBER_X, WEIGHTS_NUMBER_Y, simTime);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    // отрисовка стен
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.line(leftWallX, lowerWallY, leftWallX, upperWallY);
    shapeRenderer.line(rightWallX, lowerWallY, rightWallX, upperWallY);
    shapeRenderer.line(leftWallX, lowerWallY, rightWallX, lowerWallY);
    shapeRenderer.line(leftWallX, upperWallY, rightWallX, upperWallY);
    shapeRenderer.end();

    drawSprings(springs);

    drawWeights(weights);
  }

  /**
   * Отрисовка пружин с цветом, зависящим от растяжения/сжатия: красный – растянута, синий – сжата,
   * белый – в равновесии.
   */
  private void drawSprings(List<Spring> springs) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

    for (Spring spring : springs) {
      Vector2 a = null, b = null;

      if (spring.getLeftAnchor() != null && spring.getRightAnchor() != null) {
        a = spring.getLeftAnchor();
        b = spring.getRightAnchor();
      } else if (spring.getLowerAnchor() != null && spring.getUpperAnchor() != null) {
        a = spring.getLowerAnchor();
        b = spring.getUpperAnchor();
      } else if (spring.getAnchorA() != null && spring.getAnchorB() != null) {
        a = spring.getAnchorA();
        b = spring.getAnchorB();
      } else {
        continue;
      }

      float dx = (b.x - a.x) / spring.getCoils();
      float dy = (b.y - a.y) / spring.getCoils();
      Vector2 boardAnchor = a;

      float currentLength = a.dst(b);
      float restLength = spring.getLength();
      float strain = (currentLength - restLength) / restLength;

      float prev = spring.getVisualStrain();
      float blended = MathUtils.lerp(prev, strain, 0.01f);
      spring.setVisualStrain(blended);

      float s = MathUtils.clamp(blended * 100f, -1f, 1f);

      float r, g, bl;
      if (s >= 0) {
        r = 1f;
        g = 1f - 0.6f * s;
        bl = 1f - 0.6f * s;
      } else {
        r = 1f + 0.3f * s;
        g = 1f + 0.3f * s;
        bl = 1f - s;
      }
      shapeRenderer.setColor(r, g, bl, 1f);

      for (int i = 0; i < spring.getCoils(); i++) {
        float sign = (i % 2 == 0) ? 1 : -1;

        float x1 = boardAnchor.x + dx * i;
        float y1 = boardAnchor.y + dy * i;
        float x2 = boardAnchor.x + dx * (i + 1);
        float y2 = boardAnchor.y + dy * (i + 1);

        float nx = -dy, ny = dx;
        float len = (float) Math.sqrt(nx * nx + ny * ny);
        nx /= len;
        ny /= len;

        x1 += nx * spring.getWidth() * sign;
        y1 += ny * spring.getWidth() * sign;
        x2 += nx * spring.getWidth() * -sign;
        y2 += ny * spring.getWidth() * -sign;

        shapeRenderer.line(x1, y1, x2, y2);
      }
    }
    shapeRenderer.end();
  }

  private void drawWeights(List<Weight> weights) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(Color.DARK_GRAY);

    for (Weight weight : weights) {
      shapeRenderer.rect(weight.getX(), weight.getY(), weight.getWidth(), weight.getHeight());
    }

    shapeRenderer.end();
  }

  private void handleInput() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      final int NX = WEIGHTS_NUMBER_X;
      final int NY = WEIGHTS_NUMBER_Y;
      int i0 = NX / 2, j0 = NY / 2;
      float v = 400f;

      for (int dj = -2; dj <= 2; dj++) {
        for (int di = -2; di <= 2; di++) {
          int i = i0 + di;
          int j = j0 + dj;
          if (i >= 0 && i < NX && j >= 0 && j < NY) {
            int idx = j * NX + i;
            physics.pushWeight(weights, idx, di * v, dj * v);
          }
        }
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      final int NX = WEIGHTS_NUMBER_X;
      final int NY = WEIGHTS_NUMBER_Y;
      int i = NX / 2;
      float v = 400f;

      for (int j = 0; j < NY; j++) {
        int idx = j * NX + i;
        physics.pushWeight(weights, idx, v, 0f);
      }
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
