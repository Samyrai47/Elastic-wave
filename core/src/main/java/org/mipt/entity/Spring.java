package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;

/** Класс пружины */
public class Spring {
  /** Коэффициент упругости */
  private final float k;

  /** Число витков пружины */
  private final int coils;

  /** Ширина пружины. Требуется для отрисовки витков. */
  private final float width;

  /** Левая сторона крепления пружины */
  private Vector2 leftAnchor;

  /** Правая сторона крепления пружины */
  private Vector2 rightAnchor;

  /** Верхняя сторона крепления пружины */
  private Vector2 upperAnchor;

  /** Нижняя сторона крепления пружины */
  private Vector2 lowerAnchor;

  /** Изначальная длина пружины */
  private final float length;

  private float visualStrain = 0f;

  public float getVisualStrain() {
    return visualStrain;
  }

  public void setVisualStrain(float visualStrain) {
    this.visualStrain = visualStrain;
  }

  private Vector2 anchorA;
  private Vector2 anchorB;

  public void setAnchors(Vector2 a, Vector2 b) {
    this.anchorA = a;
    this.anchorB = b;
  }

  public void setAnchorA(Vector2 a) {
    this.anchorA = a;
  }

  public void setAnchorB(Vector2 b) {
    this.anchorB = b;
  }

  public Vector2 getAnchorA() {
    return anchorA;
  }

  public Vector2 getAnchorB() {
    return anchorB;
  }

  public Spring(float k, Vector2 leftAnchor, Vector2 rightAnchor, int coils, float width) {
    this.k = k;
    this.leftAnchor = leftAnchor;
    this.rightAnchor = rightAnchor;
    this.coils = coils;
    this.width = width;
    this.length = rightAnchor.dst(leftAnchor);
  }

  public Spring(float k, int coils, float width, float length) {
    this.k = k;
    this.coils = coils;
    this.width = width;
    this.length = length;
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

  public Vector2 getUpperAnchor() {
    return upperAnchor;
  }

  public Vector2 getLowerAnchor() {
    return lowerAnchor;
  }

  public void setLeftAnchor(Vector2 leftAnchor) {
    this.leftAnchor = leftAnchor;
  }

  public void setRightAnchor(Vector2 rightAnchor) {
    this.rightAnchor = rightAnchor;
  }

  public void setUpperAnchor(Vector2 upperAnchor) {
    this.upperAnchor = upperAnchor;
  }

  public void setLowerAnchor(Vector2 lowerAnchor) {
    this.lowerAnchor = lowerAnchor;
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

  public float getUpperX() {
    return upperAnchor.x;
  }

  public float getUpperY() {
    return upperAnchor.y;
  }

  public float getLowerX() {
    return lowerAnchor.x;
  }

  public float getLowerY() {
    return lowerAnchor.y;
  }

  public float getLength() {
    return length;
  }

  public float getCurrentLength() {
    if (rightAnchor != null) {
      return rightAnchor.cpy().sub(leftAnchor).len();
    }
    return upperAnchor.cpy().sub(lowerAnchor).len();
  }

  public Vector2 getLeftForce() {
    if (leftAnchor == null) {
      return new Vector2(0, 0);
    }
    Vector2 diffVectors = rightAnchor.cpy().sub(leftAnchor);
    float currentLength = getCurrentLength();
    return diffVectors.scl((-k * (currentLength - length)) / currentLength);
  }

  public Vector2 getRightForce() {
    if (rightAnchor == null) {
      return new Vector2(0, 0);
    }
    Vector2 diffVectors = leftAnchor.cpy().sub(rightAnchor);
    float currentLength = getCurrentLength();
    return diffVectors.scl((-k * (currentLength - length)) / currentLength);
  }

  public Vector2 getUpperForce() {
    if (upperAnchor == null) {
      return new Vector2(0, 0);
    }
    Vector2 diffVectors = lowerAnchor.cpy().sub(upperAnchor);
    float currentLength = getCurrentLength();
    return diffVectors.scl((-k * (currentLength - length)) / currentLength);
  }

  public Vector2 getLowerForce() {
    if (lowerAnchor == null) {
      return new Vector2(0, 0);
    }
    Vector2 diffVectors = upperAnchor.cpy().sub(lowerAnchor);
    float currentLength = getCurrentLength();
    return diffVectors.scl((-k * (currentLength - length)) / currentLength);
  }
}
