package org.mipt;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import org.mipt.entity.Weight;

/** Класс для пересчета физики. */
public class Physics {
  private static final float EPSILON = 0.0001F;

  public Physics() {}

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
    float[] stateX = new float[weights.size() * 2];
    float[] stateY = new float[weights.size() * 2];

    for (int i = 0; i < weights.size(); i++) {
      stateX[i] = weights.get(i).getX();
      stateY[i] = weights.get(i).getY();
    }

    for (int i = 0; i < weights.size(); i++) {
      stateX[i + weights.size()] = weights.get(i).getVelocityX();
      stateY[i + weights.size()] = weights.get(i).getVelocityY();
    }

    float[][] states =  RT4(stateX, stateY, weights, deltaTime);

    handleCollisions(weights, states);

    stateX  = states[0];
    stateY  = states[1];

    for (int i = 0; i < weights.size(); i++) {
      weights.get(i).setX(stateX[i]);
      weights.get(i).setY(stateY[i]);
      weights.get(i).setVelocityX(stateX[weights.size() + i]);
      weights.get(i).setVelocityY(stateY[weights.size() + i]);
    }
  }

  /** Обрабатываем коллизии грузов при абсолютно упругом ударе */
  //TODO Отладить процесс коллизий, так как во время колебаний грузы могут исчезнуть
  private static void handleCollisions(List<Weight> weights, float[][] states) {
      int n = weights.size();
      float[] newX = states[0];
      float[] newY = states[1];
      float[] newVx = new float[n];
      float[] newVy = new  float[n];
      for (int i = 0; i < n; i++) {
          newVx[i] = states[0][i + n];
          newVy[i] = states[1][i + n];
      }

      for (int i = 0; i < weights.size() - 1; i++) {
          int j = i + 1;
          if (isColliding(weights.get(i), newX[i], newY[i],
                  weights.get(j), newX[j], newY[j])) {
              resolveCollision(weights.get(i), newX[i], newY[i], weights.get(j), newX[j], newY[j], newVx, newVy, i, j);
          }
      }

      //TODO переделать обработку коллизий со стенками, из-за непонятного поведения груза
      if (newX[0] < weights.get(0).getLeftSpring().getLeftX()) {
          //newX[0] = weights.get(0).getX();
          newVx[0] = -newVx[0];
      }
      if (newX[n - 1] + weights.get(n - 1).getWidth() > weights.get(n - 1).getRightSpring().getRightX()){
          //newX[n - 1] = weights.get(n - 1).getX();
          newVx[n - 1] = -newVx[n - 1];
      }

      for (int i = n; i < 2 * n; ++i){
          states[0][i] = newVx[i - n];
          states[1][i] = newVy[i - n];
      }
  }

    /** Проверка коллизий грузов
     * @param first - первый груз
     * @param x1 - координата левая по x
     * @param y1 - координата нижняя по y
     */
  private static boolean isColliding(Weight first, float x1, float y1,
                                     Weight second, float x2, float y2) {

      return x1 < x2 + second.getWidth() &&
                x1 + first.getWidth() > x2 &&
                y1 < y2 + second.getHeight() &&
                y1 + first.getHeight() > y2;
  }

  /**
   * Обрабатывает абсолютно упругое столкновение между двумя грузами.
   * Используем законы сохранения импульса и энергии
   * <a href="https://en.wikipedia.org/wiki/Elastic_collision">...</a>
   * */
  private static void resolveCollision(Weight first, float x1, float y1, Weight second, float x2, float y2, float[] vx, float[] vy, int i, int j) {
      Vector2 v1 = new Vector2(vx[i], vy[i]);
      Vector2 v2 = new Vector2(vx[j], vy[j]);

      Vector2 center1 = new Vector2(x1 + first.getWidth() / 2, y1 + first.getHeight() / 2);
      Vector2 center2 = new Vector2(x2 + second.getWidth() / 2, y2 + second.getHeight() / 2);

      Vector2 normal = center2.cpy().sub(center1).nor();

      Vector2 relativeVelocity = v1.cpy().sub(v2);

      float velocityAlongNormal = relativeVelocity.dot(normal);

      float m1 = first.getMass();
      float m2 = second.getMass();

      float impulseScalar = -2 * velocityAlongNormal / (1/m1 + 1/m2);

      Vector2 impulse = normal.cpy().scl(impulseScalar);

      Vector2 v1New = v1.cpy().add(impulse.cpy().scl(1/m1));
      Vector2 v2New = v2.cpy().sub(impulse.cpy().scl(1/m2));

      vx[i] = v1New.x;
      vy[i] = v1New.y;
      vx[j] = v2New.x;
      vy[j] = v2New.y;
  }

  /**
   * Реализует численный метод Рунге-Кутты 4-го порядка
   *
   * @param stateX текущее положение грузов по x
   * @param stateY текущее положение грузов по y
   * @param weights набор весов
   * @param h шаг
   * @return обновленные положения грузов (решение дифференциального уравнения)
   */
  private static float[][] RT4(float[] stateX, float[] stateY, List<Weight> weights, float h) {
    float[] k1X = multipleVector(makeDiffX(weights, stateX), h);
    float[] k2X = multipleVector(makeDiffX(weights, addVectors(stateX, multipleVector(k1X, 0.5f))), h);
    float[] k3X = multipleVector(makeDiffX(weights, addVectors(stateX, multipleVector(k2X, 0.5f))), h);
    float[] k4X = multipleVector(makeDiffX(weights, addVectors(stateX, k3X)), h);

    float[] k1Y = multipleVector(makeDiffY(weights, stateY), h);
    float[] k2Y = multipleVector(makeDiffY(weights,  addVectors(stateY, multipleVector(k1Y, 0.5f))), h);
    float[] k3Y = multipleVector(makeDiffY(weights,  addVectors(stateY, multipleVector(k2Y, 0.5f))), h);
    float[] k4Y = multipleVector(makeDiffY(weights, addVectors(stateY, k3Y)), h);


    float[] newZx =
        addVectors(
            addVectors(addVectors(k1X, multipleVector(k2X, 2.0f)), multipleVector(k3X, 2.0f)), k4X);
    float[] newZy =
            addVectors(
                    addVectors(addVectors(k1Y, multipleVector(k2Y, 2.0f)), multipleVector(k3Y, 2.0f)), k4Y);
    stateX = addVectors(stateX, multipleVector(newZx, 1.0f / 6.0f));
    stateY = addVectors(stateY, multipleVector(newZy, 1.0f / 6.0f));
    return new float[][]{stateX, stateY};
  }

  /**
   * Создает вектор из производных x и z
   *
   * @param weights набор грузов
   * @param state текущие положения грузов
   * @return вектор производных
   */
  private static float[] makeDiffX(List<Weight> weights, float[] state) {
    int variablesNumber = state.length / 2;
    float[] res = new float[2 * variablesNumber];

    for (int i = 0; i < variablesNumber; i++) {
      float zi = state[i + variablesNumber];

      res[i] = zi;
      res[variablesNumber + i] = diffZx(i, weights);
    }
    return res;
  }

  /**
   * Создает вектор из производных y и z
   *
   * @param weights набор грузов
   * @param state текущие положения грузов
   * @return вектор производных
   */
  private static float[] makeDiffY(List<Weight> weights, float[] state) {
    int variablesNumber = state.length / 2;
    float[] res = new float[2 * variablesNumber];

    for (int i = 0; i < variablesNumber; i++) {
        float zi = state[i + variablesNumber];

        res[i] = zi;
        res[variablesNumber + i] = diffZy(i, weights);
    }
    return res;
  }

  /**
   * Находит производную от z <=> Находит вторую производную x.
   *
   * @param index номер груза
   * @param weights набор грузов
   * @return производную z <=> вторую производную x
   */
  private static float diffZx(int index, List<Weight> weights) {
    /*float leftSpringK = weights.get(index).getLeftSpring().getK();
    float rightSpringK = weights.get(index).getRightSpring().getK();
    float leftSpringLen = weights.get(index).getLeftSpring().getLength();
    float rightSpringLen = weights.get(index).getRightSpring().getLength();
    float width = weights.get(index).getWidth();*/
      float weightM = weights.get(index).getMass();
      float leftForce = weights.get(index).getLeftSpring().getRightForce().x;
      float rightForce = weights.get(index).getRightSpring().getLeftForce().x;

      return (leftForce + rightForce) / weightM;

    /*float currWeightX = state[index];

    float prevRightX;
    if (index > 0) {
      float prevWeightX = state[index - 1];
      float prevWidth = weights.get(index - 1).getWidth();
      prevRightX = prevWeightX + prevWidth;
    } else {
      prevRightX = weights.get(0).getLeftSpring().getLeftX(); // левая стенка
    }

    float nextLeftX;
    if (index < state.length / 2 - 1) {
      nextLeftX = state[index + 1];
    } else {
      nextLeftX = weights.get(weights.size() - 1).getRightSpring().getRightX(); // правая стенка
    }

    return (leftSpringK * (prevRightX - currWeightX + leftSpringLen)
            + rightSpringK * (nextLeftX - (currWeightX + width) - rightSpringLen))
        / weightM;*/
  }

    /**
     * Находит производную от z <=> Находит вторую производную y.
     *
     * @param index номер груза
     * @param weights набор грузов
     * @return производную z <=> вторую производную y
     */
  private static float diffZy(int index, List<Weight> weights) {
      float weightM = weights.get(index).getMass();
      float leftForce = weights.get(index).getLeftSpring().getRightForce().y;
      float rightForce = weights.get(index).getRightSpring().getLeftForce().y;
      return (leftForce + rightForce) / weightM;
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
