package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/** Класс груза */
public class Weight {
  /** Масса груза */
  private float mass;

  /** Список пружин, прикрепленных к грузу */
  private List<Spring> attachedSprings = new ArrayList<>();

  /** Координата x левой нижней точки груза */
  private float x;

  /** Координата y левой нижней точки груза */
  private float y;

  /** Ширина груза */
  private float width;

  /** Высота груза */
  private float height;

  /** Скорость по X */
  private float velocityX;

  public Weight(float mass, float x, float y, float width, float height) {
    this.mass = mass;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public void attachSprings(Spring spring) {
    attachedSprings.add(spring);
  }

  public float getMass() {
    return mass;
  }

  public List<Spring> getAttachedSprings() {
    return attachedSprings;
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

  public float getLeftX() {
    return x; // attachedSprings.get(0).getRightX
  }

  public float getRightX() {
    return x + width; // attachedSprings.get(1).getLeftX
  }

  public Spring getLeftSpring() {
    return attachedSprings.get(0);
  }

  public Spring getRightSpring() {
    return attachedSprings.get(1);
  }

  public float getVelocityX() {
    return velocityX;
  }

  public void setVelocityX(float velocityX) {
    this.velocityX = velocityX;
  }

  public void setX(float x) {
    Spring leftSpring = attachedSprings.get(0);
    Spring rightSpring = attachedSprings.get(1);
    if (x < leftSpring.getLeftX() || x + width > rightSpring.getRightX()){
        return;
    }
    this.x = x;
    leftSpring.setRightAnchor(new Vector2(x, leftSpring.getY()));
    rightSpring.setLeftAnchor(new Vector2(x + width, rightSpring.getY()));
  }
}
