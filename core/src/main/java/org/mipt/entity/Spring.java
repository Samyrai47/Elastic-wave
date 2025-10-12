package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;

/** Класс пружины */
public class Spring {
  /** Коэффициент упругости */
  private float k;

  /** Число витков пружины */
  private int coils;

  /** Ширина пружины. Требуется для отрисовки витков. */
  private float width;

  /** Левая сторона крепления пружины */
  private Vector2 leftAnchor;

  /** Правая сторона крепления пружины */
  private Vector2 rightAnchor;

  /** Изначальная длина пружины */
  private final float length;

  public Spring(float k, Vector2 leftAnchor, Vector2 rightAnchor, int coils, float width) {
    this.k = k;
    this.leftAnchor = leftAnchor;
    this.rightAnchor = rightAnchor;
    this.coils = coils;
    this.width = width;
    this.length = rightAnchor.x - leftAnchor.x;
  }

  public float getK() {
    return k;
  }

  public int getCoils() {
    return coils;
  }

  public float getWidth() {
    return width;
  }

  public Vector2 getLeftAnchor() {
    return leftAnchor;
  }

  public Vector2 getRightAnchor() {
    return rightAnchor;
  }

  public void setLeftAnchor(Vector2 leftAnchor) {
      this.leftAnchor = leftAnchor;
  }

  public void setRightAnchor(Vector2 rightAnchor) {
      this.rightAnchor = rightAnchor;
  }

  public float getLeftX() {
    return leftAnchor.x;
  }

  public float getRightX() {
    return rightAnchor.x;
  }

  public float getY() {
    return leftAnchor.y;
  }

  public float getLength() {
    return length;
  }

}
