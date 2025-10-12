package org.mipt.entity;

import java.util.ArrayList;
import java.util.List;

public class Weight {
  private float mass;
  private List<Spring> attachedSprings = new ArrayList<>();

  private float x;
  private float y;
  private float width;
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
