package org.mipt.entity;

import com.badlogic.gdx.math.Vector2;

/** Класс груза */
public class Weight {
  /** Масса груза */
  private final float mass;

  /** Список пружин, прикрепленных к грузу по x */
  private Spring leftSpring;

  private Spring rightSpring;

  /** Список пружин прикрепленных к грузу по y */
  private Spring upperSpring;

  private Spring lowerSpring;

  private Spring upperLeftSpring;
  private Spring upperRightSpring;
  private Spring lowerLeftSpring;
  private Spring lowerRightSpring;

  /** Координата x левой нижней точки груза */
  private float x;

  /** Координата y левой нижней точки груза */
  private float y;

  /** Ширина груза */
  private final float width;

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
    float midY = y + height / 2f;

    if (leftSpring != null) leftSpring.setRightAnchor(new Vector2(x, midY));

    if (rightSpring != null) rightSpring.setLeftAnchor(new Vector2(x + width, midY));

    if (lowerSpring != null) lowerSpring.setUpperAnchor(new Vector2(x + width / 2f, y));

    if (upperSpring != null) upperSpring.setLowerAnchor(new Vector2(x + width / 2f, y + height));

    Vector2 center = new Vector2(this.x + width / 2f, this.y + height / 2f);

    if (upperRightSpring != null) upperRightSpring.setAnchorA(center);
    if (upperLeftSpring != null) upperLeftSpring.setAnchorA(center);
    if (lowerRightSpring != null) lowerRightSpring.setAnchorB(center);
    if (lowerLeftSpring != null) lowerLeftSpring.setAnchorB(center);
  }

  public void setY(float y) {
    this.y = y;
    float midXLeft = x;
    float midXRight = x + width;
    float midXCenter = x + width / 2f;

    if (leftSpring != null) leftSpring.setRightAnchor(new Vector2(midXLeft, y + height / 2f));

    if (rightSpring != null) rightSpring.setLeftAnchor(new Vector2(midXRight, y + height / 2f));

    if (lowerSpring != null) lowerSpring.setUpperAnchor(new Vector2(midXCenter, y));

    if (upperSpring != null) upperSpring.setLowerAnchor(new Vector2(midXCenter, y + height));

    Vector2 center = new Vector2(this.x + width / 2f, this.y + height / 2f);
    if (upperRightSpring != null) upperRightSpring.setAnchorA(center);
    if (upperLeftSpring != null) upperLeftSpring.setAnchorA(center);
    if (lowerRightSpring != null) lowerRightSpring.setAnchorB(center);
    if (lowerLeftSpring != null) lowerLeftSpring.setAnchorB(center);
  }

  public Spring getUpperLeftSpring() {
    return upperLeftSpring;
  }

  public Spring getUpperRightSpring() {
    return upperRightSpring;
  }

  public Spring getLowerLeftSpring() {
    return lowerLeftSpring;
  }

  public Spring getLowerRightSpring() {
    return lowerRightSpring;
  }

  public void setUpperLeftSpring(Spring s) {
    upperLeftSpring = s;
  }

  public void setUpperRightSpring(Spring s) {
    upperRightSpring = s;
  }

  public void setLowerLeftSpring(Spring s) {
    lowerLeftSpring = s;
  }

  public void setLowerRightSpring(Spring s) {
    lowerRightSpring = s;
  }
}
