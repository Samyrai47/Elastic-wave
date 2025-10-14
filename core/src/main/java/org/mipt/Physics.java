package org.mipt;

import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

/** Класс для пересчета физики. */
public class Physics {
  private static final float EPSILON = 0.0001F;

  public Physics() {}

  /**
   * Решаем для каждого груза диф. уравнение каждое dt времени,
   * получаем для каждого груза текущую позицию
   * @param weights грузы
   * @param deltaTime dt
   * */
  public void applyPhysics(List<Weight> weights, float deltaTime) {
    for (Weight weight : weights) {
      calcPhysicsForWeight(weight, deltaTime);
    }
  }

  /**
   * Решение диф. уравнения для груза
   * m_i * x_i'' = k_(i - 1) * (x_(i - 1) - x_i + L_(i - 1))
   * + k_i * (x_(i + 1) - x_i - L_i - width)
   * k - жесткость пружины
   * L - начальная длина пружины
   * width - ширина груза
   * @param weight груз
   * @param deltaTime delta по времени
   */
  private static void calcPhysicsForWeight(Weight weight, float deltaTime) {
    // TODO: добавить проверку на коллизии (или тут, или в weight)
    Spring leftSpring = weight.getLeftSpring();
    Spring rightSpring = weight.getRightSpring();
    float xPrev = leftSpring.getLeftX() / Main.PIXELS_PER_METER;
    float xCurr = weight.getLeftX() / Main.PIXELS_PER_METER;
    float xNext = rightSpring.getRightX() / Main.PIXELS_PER_METER;
    float kPrev = leftSpring.getK();
    float kNext = rightSpring.getK();
    float leftLength = leftSpring.getLength() / Main.PIXELS_PER_METER;
    float rightLength = rightSpring.getLength() / Main.PIXELS_PER_METER;
    float width = weight.getWidth() / Main.PIXELS_PER_METER;
    float w = (float) Math.sqrt((kNext + kPrev) / weight.getMass());
    float xLocal =
        (kPrev * xPrev + kNext * xNext + kPrev * leftLength - kNext * rightLength - kPrev * width)
            / (kPrev + kNext);
    float a = xCurr - xLocal;
    float b = weight.getVelocityX() / w;
    float newX = (float) (a * Math.cos(w * deltaTime) + b * Math.sin(w * deltaTime) + xLocal);
    float newVelocityX =
        (float) (-a * w * Math.sin(w * deltaTime) + b * w * Math.cos(w * deltaTime));

    weight.setVelocityX(newVelocityX);
    weight.setX(newX * Main.PIXELS_PER_METER);
  }

  /**
   * Двигаем первый груз по X
   *
   * @param weights груз
   * @param dx сдвиг
   */
  public void changeFirstWeightX(List<Weight> weights, float dx) {
      Weight firstWeight = weights.get(0);
      if (firstWeight.getRightX() + dx < weights.get(1).getLeftX()) {
          firstWeight.setX(firstWeight.getX() + dx);
      }
  }
}
