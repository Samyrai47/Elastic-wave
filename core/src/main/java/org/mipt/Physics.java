package org.mipt;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

/** Класс для пересчета физики. */
public class Physics {
  private static final float EPSILON = 0.0001F;
  private static float leftWallX;
  private static float rightWallX;
  private static float upperWallY;
  private static float lowerWallY;
  private static int weightsNumberX;
  private static int weightsNumberY;
  private static final int NEIGHBOURS_DEPTH = 3;

  public Physics() {}

  public void setLeftWallX(float leftWallX) {
    Physics.leftWallX = leftWallX;
  }

  public void setRightWallX(float rightWallX) {
    Physics.rightWallX = rightWallX;
  }

  public void setUpperWallY(float upperWallY) {
    Physics.upperWallY = upperWallY;
  }

  public void setLowerWallY(float lowerWallY) {
    Physics.lowerWallY = lowerWallY;
  }

  public void setWeightsNumberX(int weightsNumberX) {
    Physics.weightsNumberX = weightsNumberX;
  }

  public void setWeightsNumberY(int weightsNumberY) {
    Physics.weightsNumberY = weightsNumberY;
  }

  /**
   * Просчитывает физику для модели
   *
   * @param weights грузы
   * @param deltaTime dt
   */
  public void applyPhysics(List<Weight> weights, float deltaTime) {
    float subSteps = 12;
    float subDelta = deltaTime / subSteps;
    for (int i = 0; i < subSteps; i++) {
      calcPhysicsForWeights(weights, subDelta);
    }
  }

  /**
   * Просчитывает положения грузов при помощи решения дифференциального уравнения второго порядка
   * численным методом Рунге-Кутты
   *
   * @param weights грузы
   * @param deltaTime дельта времени
   */
  private static void calcPhysicsForWeights(List<Weight> weights, float deltaTime) {
    float[] state = new float[weights.size() * 4];

    for (int i = 0; i < weights.size(); i++) {
      int offset = i * 4;
      state[offset] = weights.get(i).getX();
      state[offset + 1] = weights.get(i).getY();
      state[offset + 2] = weights.get(i).getVelocityX();
      state[offset + 3] = weights.get(i).getVelocityY();
    }

    state = RT4(state, weights, deltaTime);

    handleCollisions(weights, state);

    for (int i = 0; i < weights.size(); i++) {
      int offset = i * 4;
      weights.get(i).setX(state[offset]);
      weights.get(i).setY(state[offset + 1]);
      weights.get(i).setVelocityX(state[offset + 2]);
      weights.get(i).setVelocityY(state[offset + 3]);
    }
  }

  /**
   * Реализует численный метод Рунге-Кутты 4-го порядка
   *
   * @param state текущее положение грузов
   * @param weights набор весов
   * @param h шаг
   * @return обновленные положения грузов (решение дифференциального уравнения)
   */
  private static float[] RT4(float[] state, List<Weight> weights, float h) {
    float[] k1 = multipleVector(makeDiff(weights, state), h);
    float[] k2 = multipleVector(makeDiff(weights, addVectors(state, multipleVector(k1, 0.5f))), h);
    float[] k3 = multipleVector(makeDiff(weights, addVectors(state, multipleVector(k2, 0.5f))), h);
    float[] k4 = multipleVector(makeDiff(weights, addVectors(state, k3)), h);

    float[] newZ =
        addVectors(
            addVectors(addVectors(k1, multipleVector(k2, 2.0f)), multipleVector(k3, 2.0f)), k4);
    state = addVectors(state, multipleVector(newZ, 1.0f / 6.0f));
    return state;
  }

  /**
   * Создает вектор из производных x и z
   *
   * @param weights набор грузов
   * @param state текущие положения грузов
   * @return вектор производных
   */
  private static float[] makeDiff(List<Weight> weights, float[] state) {
    int n = weights.size();
    float[] res = new float[state.length];

    for (int i = 0; i < n; i++) {
      int off = i * 4;

      float vx = state[off + 2];
      float vy = state[off + 3];

      Vector2 a = accelFromState(i, weights, state);

      res[off] = vx;
      res[off + 1] = vy;
      res[off + 2] = a.x;
      res[off + 3] = a.y;
    }
    return res;
  }

  private static Vector2 accelFromState(int idx, List<Weight> weights, float[] state) {
    final int nx = weightsNumberX;
    final int ny = weightsNumberY;
    int j = idx / nx, i = idx % nx;

    Weight w = weights.get(idx);
    float m = w.getMass();

    int off = idx * 4;
    float x = state[off];
    float y = state[off + 1];
    float wWidth = w.getWidth();
    float wHeight = w.getHeight();

    Vector2 leftEnd = new Vector2(x, y + wHeight / 2f);
    Vector2 rightEnd = new Vector2(x + wWidth, y + wHeight / 2f);
    Vector2 bottomEnd = new Vector2(x + wWidth / 2f, y);
    Vector2 topEnd = new Vector2(x + wWidth / 2f, y + wHeight);

    Vector2 force = new Vector2();

    if (w.getLeftSpring() != null) {
      Spring s = w.getLeftSpring();
      Vector2 other =
          (i == 0)
              ? new Vector2(leftWallX, y + wHeight / 2f)
              : rightEdgeCenterOf(i - 1, j, nx, weights, state);

      force.add(hooke(other, leftEnd, s.getK(), s.getLength()));
    }

    if (w.getRightSpring() != null) {
      Spring s = w.getRightSpring();
      Vector2 other =
          (i == nx - 1)
              ? new Vector2(rightWallX, y + wHeight / 2f)
              : leftEdgeCenterOf(i + 1, j, nx, weights, state);

      force.add(hooke(other, rightEnd, s.getK(), s.getLength()));
    }

    if (w.getLowerSpring() != null) {
      Spring s = w.getLowerSpring();
      Vector2 other =
          (j == 0)
              ? new Vector2(x + wWidth / 2f, lowerWallY)
              : topEdgeCenterOf(i, j - 1, nx, weights, state);

      force.add(hooke(other, bottomEnd, s.getK(), s.getLength()));
    }

    if (w.getUpperSpring() != null) {
      Spring s = w.getUpperSpring();
      Vector2 other =
          (j == ny - 1)
              ? new Vector2(x + wWidth / 2f, upperWallY)
              : bottomEdgeCenterOf(i, j + 1, nx, weights, state);

      force.add(hooke(other, topEnd, s.getK(), s.getLength()));
    }

    if (w.getUpperLeftSpring() != null && i > 0 && j < ny - 1) {
      Spring s = w.getUpperLeftSpring();
      Vector2 other = topEdgeCenterOf(i - 1, j + 1, nx, weights, state);
      force.add(hooke(other, topEnd, s.getK(), s.getLength()));
    }
    if (w.getUpperRightSpring() != null && i < nx - 1 && j < ny - 1) {
      Spring s = w.getUpperRightSpring();
      Vector2 other = topEdgeCenterOf(i + 1, j + 1, nx, weights, state);
      force.add(hooke(other, topEnd, s.getK(), s.getLength()));
    }
    if (w.getLowerLeftSpring() != null && i > 0 && j > 0) {
      Spring s = w.getLowerLeftSpring();
      Vector2 other = bottomEdgeCenterOf(i - 1, j - 1, nx, weights, state);
      force.add(hooke(other, bottomEnd, s.getK(), s.getLength()));
    }
    if (w.getLowerRightSpring() != null && i < nx - 1 && j > 0) {
      Spring s = w.getLowerRightSpring();
      Vector2 other = bottomEdgeCenterOf(i + 1, j - 1, nx, weights, state);
      force.add(hooke(other, bottomEnd, s.getK(), s.getLength()));
    }

    return force.scl(1f / m);
  }

  private static Vector2 hooke(Vector2 from, Vector2 to, float k, float restLen) {
    Vector2 d = to.cpy().sub(from);
    float L = d.len();
    if (L < EPSILON) return new Vector2();
    float ext = L - restLen;
    return d.scl((-k * ext) / L);
  }

  private static Vector2 leftEdgeCenterOf(int i, int j, int nx, List<Weight> w, float[] st) {
    int idx = j * nx + i, off = idx * 4;
    float x = st[off], y = st[off + 1];
    return new Vector2(x, y + w.get(idx).getHeight() / 2f);
  }

  private static Vector2 rightEdgeCenterOf(int i, int j, int nx, List<Weight> w, float[] st) {
    int idx = j * nx + i, off = idx * 4;
    float x = st[off], y = st[off + 1];
    Weight wi = w.get(idx);
    return new Vector2(x + wi.getWidth(), y + wi.getHeight() / 2f);
  }

  private static Vector2 bottomEdgeCenterOf(int i, int j, int nx, List<Weight> w, float[] st) {
    int idx = j * nx + i, off = idx * 4;
    float x = st[off], y = st[off + 1];
    Weight wi = w.get(idx);
    return new Vector2(x + wi.getWidth() / 2f, y);
  }

  private static Vector2 topEdgeCenterOf(int i, int j, int nx, List<Weight> w, float[] st) {
    int idx = j * nx + i, off = idx * 4;
    float x = st[off], y = st[off + 1];
    Weight wi = w.get(idx);
    return new Vector2(x + wi.getWidth() / 2f, y + wi.getHeight());
  }

  /**
   * Умножает вектор на число
   *
   * @param vector вектор
   * @param multiplier число, на которое нужно умножить вектор
   * @return результирующий вектор
   */
  private static float[] multipleVector(float[] vector, float multiplier) {
    float[] diffCopy = vector.clone();
    for (int i = 0; i < vector.length; i++) {
      diffCopy[i] = vector[i] * multiplier;
    }

    return diffCopy;
  }

  /**
   * Сложение двух векторов
   *
   * @param vector1 первый вектор
   * @param vector2 второй вектор
   * @return результат сложения
   */
  private static float[] addVectors(float[] vector1, float[] vector2) {
    float[] res = new float[vector1.length];
    for (int i = 0; i < vector1.length; i++) {
      res[i] = vector2[i] + vector1[i];
    }
    return res;
  }

  /** Обрабатываем коллизии грузов при абсолютно упругом ударе */
  private static void handleCollisions(List<Weight> weights, float[] state) {
    int n = weights.size();
    float[] newX = new float[n];
    float[] newY = new float[n];
    float[] newVx = new float[n];
    float[] newVy = new float[n];
    for (int i = 0; i < n; i++) {
      int offset = i * 4;
      newX[i] = state[offset];
      newY[i] = state[offset + 1];
      newVx[i] = state[offset + 2];
      newVy[i] = state[offset + 3];
    }

    for (int j = 0; j < weightsNumberY; ++j) {
      for (int i = 0; i < weightsNumberX; ++i) {
        int index = j * weightsNumberX + i;
        for (int x = 0; x <= NEIGHBOURS_DEPTH && x + i < weightsNumberX; ++x) {
          for (int y = 0; y + x <= NEIGHBOURS_DEPTH && y + j < weightsNumberY; ++y) {
            int neighbour = (j + y) * weightsNumberX + x + i;
            if (isColliding(
                weights.get(index),
                newX[index],
                newY[index],
                weights.get(neighbour),
                newX[neighbour],
                newY[neighbour])) {
              resolveCollision(
                  weights.get(index),
                  newX[index],
                  newY[index],
                  weights.get(neighbour),
                  newX[neighbour],
                  newY[neighbour],
                  newVx,
                  newVy,
                  index,
                  neighbour);
            }
          }
        }
      }
    }

    for (int i = 0; i < n; i++) {
      if (newX[i] < leftWallX) {
        newX[i] = leftWallX;
        newVx[i] = -newVx[i];
      }

      if (newX[i] + weights.get(i).getWidth() > rightWallX) {
        newX[i] = rightWallX - weights.get(i).getWidth();
        newVx[i] = -newVx[i];
      }

      if (newY[i] < lowerWallY) {
        newY[i] = lowerWallY;
        newVy[i] = -newVy[i];
      }

      if (newY[i] + weights.get(i).getHeight() > upperWallY) {
        newY[i] = upperWallY - weights.get(i).getHeight();
        newVy[i] = -newVy[i];
      }
    }

    for (int i = 0; i < n; i++) {
      int offset = i * 4;
      state[offset + 2] = newVx[i];
      state[offset + 3] = newVy[i];
    }
  }

  /**
   * Проверка коллизий грузов
   *
   * @param first - первый груз
   * @param x1 - координата левая по x
   * @param y1 - координата нижняя по y
   */
  private static boolean isColliding(
      Weight first, float x1, float y1, Weight second, float x2, float y2) {

    return x1 < x2 + second.getWidth()
        && x1 + first.getWidth() > x2
        && y1 < y2 + second.getHeight()
        && y1 + first.getHeight() > y2;
  }

  /**
   * Обрабатывает абсолютно упругое столкновение между двумя грузами. Используем законы сохранения
   * импульса и энергии <a href="https://en.wikipedia.org/wiki/Elastic_collision">...</a>
   */
  private static void resolveCollision(
      Weight first,
      float x1,
      float y1,
      Weight second,
      float x2,
      float y2,
      float[] vx,
      float[] vy,
      int i,
      int j) {
    Vector2 v1 = new Vector2(vx[i], vy[i]);
    Vector2 v2 = new Vector2(vx[j], vy[j]);

    Vector2 center1 = new Vector2(x1 + first.getWidth() / 2, y1 + first.getHeight() / 2);
    Vector2 center2 = new Vector2(x2 + second.getWidth() / 2, y2 + second.getHeight() / 2);

    Vector2 normal = center2.cpy().sub(center1).nor();

    Vector2 relativeVelocity = v1.cpy().sub(v2);

    float velocityAlongNormal = relativeVelocity.dot(normal);

    float m1 = first.getMass();
    float m2 = second.getMass();

    float impulseScalar = -2 * velocityAlongNormal / (1 / m1 + 1 / m2);

    Vector2 impulse = normal.cpy().scl(impulseScalar);

    Vector2 v1New = v1.cpy().add(impulse.cpy().scl(1 / m1));
    Vector2 v2New = v2.cpy().sub(impulse.cpy().scl(1 / m2));

    vx[i] = v1New.x;
    vy[i] = v1New.y;
    vx[j] = v2New.x;
    vy[j] = v2New.y;
  }

  /**
   * Придает начальное ускорение первому грузу
   *
   * @param weights набор грузов
   * @param velocityX ускорение, которое нужно придать грузу
   */
  public void pushFirstWeight(List<Weight> weights, float velocityX, float velocityY) {
    weights.get(0).setVelocityX(weights.get(0).getVelocityX() + velocityX);
    weights.get(0).setVelocityY(weights.get(0).getVelocityY() + velocityY);
  }

  public void pushWeight(List<Weight> weights, int index, float velocityX, float velocityY) {
    weights.get(index).setVelocityX(weights.get(index).getVelocityX() + velocityX);
    weights.get(index).setVelocityY(weights.get(index).getVelocityY() + velocityY);
  }
}
