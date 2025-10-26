package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/** Класс груза */
public class Weight {
  /** Масса груза */
  private float mass;

  /** Список пружин, прикрепленных к грузу по x */
  private Spring leftSpring;
  private Spring rightSpring;

  /** Список пружин прикрепленных к грузу по y */
  private Spring upperSpring;
  private Spring lowerSpring;

  /** Координата x левой нижней точки груза */
  private float x;

  /** Координата y левой нижней точки груза */
  private float y;

  /** Ширина груза */
  private float width;

  /** Высота груза */
  private float height;

  /** Скорость */
  private Vector2 velocity;

  public Weight(float mass, float x, float y, float width, float height) {
    this.mass = mass;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.velocity = new Vector2(0, 0);
  }

  public void setLeftSpring(Spring leftSpring) {
    this.leftSpring = leftSpring;
  }

  public void setRightSpring(Spring rightSpring) {
      this.rightSpring = rightSpring;
  }

  public void setUpperSpring(Spring upperSpring) {
      this.upperSpring = upperSpring;
  }

  public void setLowerSpring(Spring lowerSpring) {
      this.lowerSpring = lowerSpring;
  }

  public float getMass() {
    return mass;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }

  public Spring getLeftSpring() {
    return leftSpring;
  }

  public Spring getRightSpring() {
    return rightSpring;
  }

  public Spring getLowerSpring() {
    return lowerSpring;
  }

  public Spring getUpperSpring() {
    return upperSpring;
  }

  public float getVelocityX() {
    return velocity.x;
  }

  public float getVelocityY() {
    return velocity.y;
  }

  public void setVelocityX(float velocityX) {
    this.velocity.x = velocityX;
  }

  public void setVelocityY(float velocityY) {
    this.velocity.y = velocityY;
  }

  public void setX(float x) {
    this.x = x;
    if (leftSpring != null) {
        leftSpring.setRightAnchor(new Vector2(x, leftSpring.getRightY()));
    }

    if (rightSpring != null) {
        rightSpring.setLeftAnchor(new Vector2(x + width, rightSpring.getLeftY()));
    }

    if (lowerSpring != null) {
        lowerSpring.setUpperAnchor(new Vector2(x + width / 2, lowerSpring.getUpperY()));
    }

    if (upperSpring != null) {
        upperSpring.setLowerAnchor(new Vector2(x + width / 2, upperSpring.getLowerY()));
    }
  }

  public void setY(float y) {
    /*Spring leftSpring = horizontalSprings.get(0);
    Spring rightSpring = horizontalSprings.get(1);*/
    this.y = y;
    if (leftSpring != null) {
        leftSpring.setRightAnchor(new Vector2(leftSpring.getRightX(), y + height / 2));
    }

    if (rightSpring != null) {
        rightSpring.setLeftAnchor(new Vector2(rightSpring.getLeftX(), y + height / 2));
    }

    if (lowerSpring != null) {
        lowerSpring.setUpperAnchor(new Vector2(lowerSpring.getUpperX(), y));
    }

    if (upperSpring != null) {
        upperSpring.setLowerAnchor(new Vector2(upperSpring.getLowerX(), y + height));
    }
  }
}
