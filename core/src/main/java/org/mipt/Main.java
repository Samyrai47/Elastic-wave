package org.mipt;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

public class Main extends ApplicationAdapter {
  private Physics physics;
  private ShapeRenderer shapeRenderer;

  private float time;

  private boolean isForceApllied = false;

  private List<Weight> weights = new ArrayList<>();
  private List<Spring> springs = new ArrayList<>();

  @Override
  public void create() {
    shapeRenderer = new ShapeRenderer();
    physics = new Physics();

    Weight weight1 = new Weight(10, 400, 275, 50, 50);
    Weight weight2 = new Weight(10, 650, 275, 50, 50);

    weights.add(weight1);
    weights.add(weight2);

    Spring spring1 = new Spring(47, new Vector2(200, 300), new Vector2(400, 300), 20, 8.0f);
    Spring spring2 = new Spring(47, new Vector2(450, 300), new Vector2(650, 300), 20, 8.0f);
    Spring spring3 = new Spring(47, new Vector2(700, 300), new Vector2(900, 300), 20, 8.0f);

    springs.add(spring1);
    springs.add(spring2);
    springs.add(spring3);
  }

  @Override
  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    handleInput();

    time += Gdx.graphics.getDeltaTime();

    // отрисовка стен
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.line(200, 70, 200, 530);
    shapeRenderer.line(900, 70, 900, 530);
    shapeRenderer.end();

    for (Spring spring : springs) {
      drawSpring(spring);
    }

    for (Weight weight : weights) {
      drawWeight(weight);
    }
  }

  //// Моделируем колебания
  //    float currentLength = baseLength + physics.getOscillation(time);
  //
  //    // Конечная точка «вибрирует» вдоль линии
  //    Vector2 dir = new Vector2(pointB).sub(pointA).nor();
  //    Vector2 dynamicB = new Vector2(pointA).add(dir.scl(currentLength));
  //
  //    temp = dynamicB;
  //
  //    drawSpring(pointA, dynamicB, 20, 8f);

  private void drawSpring(Spring spring) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

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

    shapeRenderer.rect(weight.getX(), weight.getY(), weight.getWidth(), weight.getHeight());

    shapeRenderer.end();
  }

  private void handleInput() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      isForceApllied = !isForceApllied;
    }
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
  }
}
