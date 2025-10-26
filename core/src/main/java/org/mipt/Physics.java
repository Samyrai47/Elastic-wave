package org.mipt;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.mipt.entity.Weight;

/** Класс для пересчета физики. */
public class Physics {
  private static final float EPSILON = 0.0001F;
  private static float leftWallX;
  private static float rightWallX;
  private static float upperWallY;
  private static float lowerWallY;

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
    //System.out.println("Initial state: " + Arrays.toString(state));

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
    //System.out.println("Make diff k1: " + Arrays.toString(makeDiff(weights, state)));
    float[] k2 = multipleVector(makeDiff(weights, addVectors(state, multipleVector(k1, 0.5f))), h);
    float[] k3 = multipleVector(makeDiff(weights, addVectors(state, multipleVector(k2, 0.5f))), h);
    float[] k4 = multipleVector(makeDiff(weights, addVectors(state, k3)), h);

    //System.out.println("RT4: " + Arrays.toString(k1) + " " + Arrays.toString(k2) + " " + Arrays.toString(k3) +  " " + Arrays.toString(k4));

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
    float[] res = new float[state.length];

    for (int i = 0; i < weights.size(); i++) {
      int offset = i * 4;

      float vx = state[offset + 2];
      float vy = state[offset + 3];
      Vector2 acceleration = diffZ(i, weights);
      //System.out.println("Acceleration: " + acceleration);

      res[offset] = vx;
      res[offset + 1] = vy;
      res[offset + 2] = acceleration.x;
      res[offset + 3] = acceleration.y;
    }
    return res;
  }

  /**
   * Находит производную от z <=> Находит ускорение груза
   *
   * @param index номер груза
   * @param weights набор грузов
   * @return производную z <=> ускорение груза
   */
  private static Vector2 diffZ(int index, List<Weight> weights) {
    Weight weight = weights.get(index);
    float m = weight.getMass();
    Vector2 force = new Vector2();

    if (weight.getLeftSpring() != null) {
      Vector2 leftForce = weight.getLeftSpring().getLeftForce();
      //System.out.println("Left Force: " + leftForce);
      force.add(leftForce);
    }

    if (weight.getRightSpring() != null) {
      Vector2 rightForce = weight.getRightSpring().getRightForce();
      //System.out.println("Right Force: " + rightForce);
      force.add(rightForce);
    }

    if (weight.getLowerSpring() != null) {
        Vector2 lowerForce = weight.getLowerSpring().getLowerForce();
        //System.out.println("Lower Force: " + lowerForce);
        force.add(lowerForce);
    }

    if (weight.getUpperSpring() != null) {
        Vector2 upperForce = weight.getUpperSpring().getUpperForce();
        //System.out.println("Upper Force: " + upperForce);
        force.add(upperForce);
    }

    //System.out.println(force);
    //    float damping = 0.05f;
    //    Vector2 velocity = new Vector2(weight.getVelocityX(), weight.getVelocityY());
    //    force.sub(velocity.scl(damping));

    return force.scl(1f / m);
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
  // TODO Отладить процесс коллизий, так как во время колебаний грузы могут исчезнуть
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

    for (int i = 0; i < weights.size() - 1; i++) {
      int j = i + 1;
      if (isColliding(weights.get(i), newX[i], newY[i], weights.get(j), newX[j], newY[j])) {
        resolveCollision(
            weights.get(i), newX[i], newY[i], weights.get(j), newX[j], newY[j], newVx, newVy, i, j);
      }
    }

    // TODO переделать обработку коллизий со стенками, из-за непонятного поведения груза
    for (int i = 0; i < n; i++) {
        if (newX[i] < leftWallX){
            newVx[i] = -newVx[i];
        }

        if (newX[i] + weights.get(i).getWidth() > rightWallX){
            newVx[i] = -newVx[i];
        }

        if (newY[i] < lowerWallY){
            newVy[i] = -newVy[i];
        }

        if (newY[i] + weights.get(i).getHeight() > upperWallY){
            newVy[i] = -newVy[i];
        }
    }
    /*if (newX[0] < leftWallX) {
      // newX[0] = weights.get(0).getX();
      newVx[0] = -newVx[0];
    }
    if (newX[n - 1] + weights.get(n - 1).getWidth()
        > rightWallX) {
      // newX[n - 1] = weights.get(n - 1).getX();
      newVx[n - 1] = -newVx[n - 1];
    }*/

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
}
