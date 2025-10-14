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
import java.util.ArrayList;
import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

public class Main extends ApplicationAdapter {
  private Physics physics;
  private ShapeRenderer shapeRenderer;

  private FillViewport viewport;
  private OrthographicCamera camera;

  public static final float PIXELS_PER_METER = 1000.0f;
  private static final float WORLD_HEIGHT = 600;
  private static final float WORLD_WIDTH = 1000;
  private static final float FIXED_TIME_STEP = 0.001f;
  private float accumulator = 0f;

  private List<Weight> weights = new ArrayList<>();
  private List<Spring> springs = new ArrayList<>();

  @Override
  public void create() {
    camera = new OrthographicCamera();
    viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    camera.position.set(WORLD_WIDTH / 2f + 2500, WORLD_HEIGHT / 2f, 0);

    shapeRenderer = new ShapeRenderer();
    physics = new Physics();

    // TODO: переделать этот позор в создание через запросы (или через цикл)
    Weight weight1 = new Weight(10, 400, 275, 50, 50);
    Weight weight2 = new Weight(10, 650, 275, 50, 50);
    Weight weight3 = new Weight(10, 900, 275, 50, 50);
    Weight weight4 = new Weight(10, 1150, 275, 50, 50);
    Weight weight5 = new Weight(10, 1400, 275, 50, 50);
    Weight weight6 = new Weight(10, 1650, 275, 50, 50);
    Weight weight7 = new Weight(10, 1900, 275, 50, 50);
    Weight weight8 = new Weight(10, 2150, 275, 50, 50);
    Weight weight9 = new Weight(10, 2400, 275, 50, 50);
    Weight weight10 = new Weight(10, 2650, 275, 50, 50);
    Weight weight11 = new Weight(10, 2900, 275, 50, 50);
    Weight weight12 = new Weight(10, 3150, 275, 50, 50);
    Weight weight13 = new Weight(10, 3400, 275, 50, 50);
    Weight weight14 = new Weight(10, 3650, 275, 50, 50);
    Weight weight15 = new Weight(10, 3900, 275, 50, 50);

    weights.add(weight1);
    weights.add(weight2);
    weights.add(weight3);
    weights.add(weight4);
    weights.add(weight5);
    weights.add(weight6);
    weights.add(weight7);
    weights.add(weight8);
    weights.add(weight9);
    weights.add(weight10);
    weights.add(weight11);
    weights.add(weight12);
    weights.add(weight13);
    weights.add(weight14);
    weights.add(weight15);

    Spring spring1 = new Spring(47, new Vector2(200, 300), new Vector2(400, 300), 20, 8.0f);
    Spring spring2 = new Spring(47, new Vector2(450, 300), new Vector2(650, 300), 20, 8.0f);
    Spring spring3 = new Spring(47, new Vector2(700, 300), new Vector2(900, 300), 20, 8.0f);
    Spring spring4 = new Spring(47, new Vector2(950, 300), new Vector2(1150, 300), 20, 8.0f);
    Spring spring5 = new Spring(47, new Vector2(1200, 300), new Vector2(1400, 300), 20, 8.0f);
    Spring spring6 = new Spring(47, new Vector2(1450, 300), new Vector2(1650, 300), 20, 8.0f);
    Spring spring7 = new Spring(47, new Vector2(1700, 300), new Vector2(1900, 300), 20, 8.0f);
    Spring spring8 = new Spring(47, new Vector2(1950, 300), new Vector2(2150, 300), 20, 8.0f);
    Spring spring9 = new Spring(47, new Vector2(2200, 300), new Vector2(2400, 300), 20, 8.0f);
    Spring spring10 = new Spring(47, new Vector2(2450, 300), new Vector2(2650, 300), 20, 8.0f);
    Spring spring11 = new Spring(47, new Vector2(2700, 300), new Vector2(2900, 300), 20, 8.0f);
    Spring spring12 = new Spring(47, new Vector2(2950, 300), new Vector2(3150, 300), 20, 8.0f);
    Spring spring13 = new Spring(47, new Vector2(3200, 300), new Vector2(3400, 300), 20, 8.0f);
    Spring spring14 = new Spring(47, new Vector2(3450, 300), new Vector2(3650, 300), 20, 8.0f);
    Spring spring15 = new Spring(47, new Vector2(3700, 300), new Vector2(3900, 300), 20, 8.0f);
    Spring spring16 = new Spring(47, new Vector2(3950, 300), new Vector2(4150, 300), 20, 8.0f);

    springs.add(spring1);
    springs.add(spring2);
    springs.add(spring3);
    springs.add(spring4);
    springs.add(spring5);
    springs.add(spring6);
    springs.add(spring7);
    springs.add(spring8);
    springs.add(spring9);
    springs.add(spring10);
    springs.add(spring11);
    springs.add(spring12);
    springs.add(spring13);
    springs.add(spring14);
    springs.add(spring15);
    springs.add(spring16);

    weight1.attachSprings(spring1);
    weight1.attachSprings(spring2);
    weight2.attachSprings(spring2);
    weight2.attachSprings(spring3);
    weight3.attachSprings(spring3);
    weight3.attachSprings(spring4);
    weight4.attachSprings(spring4);
    weight4.attachSprings(spring5);
    weight5.attachSprings(spring5);
    weight5.attachSprings(spring6);
    weight6.attachSprings(spring6);
    weight6.attachSprings(spring7);
    weight7.attachSprings(spring7);
    weight7.attachSprings(spring8);
    weight8.attachSprings(spring8);
    weight8.attachSprings(spring9);
    weight9.attachSprings(spring9);
    weight9.attachSprings(spring10);
    weight10.attachSprings(spring10);
    weight10.attachSprings(spring11);
    weight11.attachSprings(spring11);
    weight11.attachSprings(spring12);
    weight12.attachSprings(spring12);
    weight12.attachSprings(spring13);
    weight13.attachSprings(spring13);
    weight13.attachSprings(spring14);
    weight14.attachSprings(spring14);
    weight14.attachSprings(spring15);
    weight15.attachSprings(spring15);
    weight15.attachSprings(spring16);

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
      accumulator -= FIXED_TIME_STEP;
    }

    // отрисовка стен
    Spring lastSpring = springs.getLast();
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.line(200, 70, 200, 530);
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
      physics.pushFirstWeight(weights, 400f);
    }
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
  }
}
