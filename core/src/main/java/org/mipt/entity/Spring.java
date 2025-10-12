package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;

public class Spring {
  private float k;
  private int coils;
  private float width;
  private Vector2 leftAnchor;
  private Vector2 rightAnchor;

  public Spring(float k, Vector2 leftAnchor, Vector2 rightAnchor, int coils, float width) {
    this.k = k;
    this.leftAnchor = leftAnchor;
    this.rightAnchor = rightAnchor;
    this.coils = coils;
    this.width = width;
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
}
