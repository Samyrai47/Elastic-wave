package org.mipt.entity;

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
}
