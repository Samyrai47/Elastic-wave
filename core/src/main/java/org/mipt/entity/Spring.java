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

  /** Высота левого закрепленного объекта */
  private final float leftHeight;

  /** Высота правого закрепленного объекта */
  private final float rightHeight;

  public Spring(
      float k,
      Vector2 leftAnchor,
      Vector2 rightAnchor,
      int coils,
      float width,
      float leftHeight,
      float rightHeight) {
    this.k = k;
    this.leftAnchor = leftAnchor;
    this.rightAnchor = rightAnchor;
    this.coils = coils;
    this.width = width;
    this.length = rightAnchor.dst(leftAnchor);
    this.leftHeight = leftHeight;
    this.rightHeight = rightHeight;
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

  public float getLeftY() {
    return leftAnchor.y;
  }

  public float getRightY() {
    return rightAnchor.y;
  }

  public float getY() {
    return leftAnchor.y;
  }

  public float getLength() {
    return length;
  }

  public float getCurrentLength() {
    return rightAnchor.cpy().sub(leftAnchor).len();
  }

  public Vector2 getLeftForce() {
    Vector2 diffVectors = rightAnchor.cpy().sub(leftAnchor);
    float currentLength = getCurrentLength();
    return diffVectors.scl((-k * (currentLength - length)) / currentLength);
  }

  public Vector2 getRightForce() {
    Vector2 diffVectors = leftAnchor.cpy().sub(rightAnchor);
    float currentLength = getCurrentLength();
    return diffVectors.scl((-k * (currentLength - length)) / currentLength);
  }

  public float getLeftHeight() {
    return leftHeight;
  }

  public float getRightHeight() {
    return rightHeight;
  }
}
