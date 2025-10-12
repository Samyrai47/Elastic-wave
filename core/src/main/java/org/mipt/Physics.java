package org.mipt;

import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

/** Класс для пересчета физики. Template версия. */
public class Physics {
  private static final float EPSILON = 0.0001F;

  public Physics() {}

  public void applyPhysics(List<Weight> weights, float deltaTime) {
    for (Weight weight : weights) {
      calcPhysicsForWeight(weight, deltaTime);
    }
  }

  /**
   * Решение диф. уравнения для груза
   *
   * @param weight груз
   * @param deltaTime delta по времени
   */
  private static void calcPhysicsForWeight(Weight weight, float deltaTime) {
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

    weight.setX(newX * Main.PIXELS_PER_METER);
    weight.setVelocityX(newVelocityX);
  }

  /**
   * Двигаем груз по X
   *
   * @param weight груз
   * @param dx сдвиг
   */
  public void changeWeightX(Weight weight, float dx) {
    weight.setX(weight.getX() + dx);
  }
}
