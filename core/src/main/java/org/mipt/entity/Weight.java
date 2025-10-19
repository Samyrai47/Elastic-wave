package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/** Класс груза */
public class Weight {
  /** Масса груза */
  private float mass;

  /** Список пружин, прикрепленных к грузу по x */
  private List<Spring> horizontalSprings = new ArrayList<>();

  /** Список пружин прикрепленных к грузу по y */
  private List<Spring> verticalSprings = new ArrayList<>();

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

  public void attachHorizontalSprings(Spring spring) {
    horizontalSprings.add(spring);
  }

  public void attachVerticalSprings(Spring spring) {
    verticalSprings.add(spring);
  }

  public float getMass() {
    return mass;
  }

  public List<Spring> getHorizontalSprings() {
    return horizontalSprings;
  }

  public List<Spring> getVerticalSprings() {
    return verticalSprings;
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
    return horizontalSprings.get(0);
  }

  public Spring getRightSpring() {
    return horizontalSprings.get(1);
  }

  public Spring getLowerSpring() {
    return verticalSprings.get(0);
  }

  public Spring getUpperSpring() {
    return verticalSprings.get(1);
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
    Spring leftSpring = horizontalSprings.get(0);
    Spring rightSpring = horizontalSprings.get(1);
    this.x = x;
    leftSpring.setRightAnchor(new Vector2(x, leftSpring.getRightY()));
    rightSpring.setLeftAnchor(new Vector2(x + width, rightSpring.getLeftY()));
  }

  public void setY(float y) {
    Spring leftSpring = horizontalSprings.get(0);
    Spring rightSpring = horizontalSprings.get(1);
    this.y = y;
    leftSpring.setRightAnchor(new Vector2(leftSpring.getRightX(), y + height / 2));
    rightSpring.setLeftAnchor(new Vector2(rightSpring.getLeftX(), y + height / 2));
  }
}
