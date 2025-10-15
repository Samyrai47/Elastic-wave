package org.mipt;

import java.util.List;
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
    calcPhysicsForWeights(weights, deltaTime);
  }

  /**
   * Просчитывает положения грузов при помощи решения дифференциального уравнения второго порядка
   * численным методом Рунге-Кутты
   *
   * @param weights грузы
   * @param deltaTime дельта времени
   */
  private static void calcPhysicsForWeights(List<Weight> weights, float deltaTime) {
    float[] state = new float[weights.size() * 2];

    for (int i = 0; i < weights.size(); i++) {
      state[i] = weights.get(i).getX();
    }

    for (int i = 0; i < weights.size(); i++) {
      state[i + weights.size()] = weights.get(i).getVelocityX();
    }

    state = RT4(state, weights, deltaTime);

    for (int i = 0; i < weights.size(); i++) {
      weights.get(i).setX(state[i]);
      weights.get(i).setVelocityX(state[weights.size() + i]);
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
    int variablesNumber = state.length / 2;
    float[] res = new float[2 * variablesNumber];

    for (int i = 0; i < variablesNumber; i++) {
      float zi = state[i + variablesNumber];

      res[i] = zi;
      res[variablesNumber + i] = diffZ(i, weights, state);
    }
    return res;
  }

  /**
   * Находит производную от z <=> Находит вторую производную x.
   *
   * @param index номер груза
   * @param weights набор грузов
   * @param state текущие положения грузов
   * @return производную z <=> вторую производную x
   */
  private static float diffZ(int index, List<Weight> weights, float[] state) {
    float leftSpringK = weights.get(index).getLeftSpring().getK();
    float rightSpringK = weights.get(index).getRightSpring().getK();
    float leftSpringLen = weights.get(index).getLeftSpring().getLength();
    float rightSpringLen = weights.get(index).getRightSpring().getLength();
    float weightM = weights.get(index).getMass();
    float width = weights.get(index).getWidth();

    float currWeightX = state[index];

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
        / weightM;
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
  public void pushFirstWeight(List<Weight> weights, float velocityX) {
    weights.get(0).setVelocityX(weights.get(0).getVelocityX() + velocityX);
  }
}
