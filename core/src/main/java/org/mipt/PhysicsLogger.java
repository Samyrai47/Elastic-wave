package org.mipt;

import com.badlogic.gdx.math.Vector2;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.mipt.entity.Spring;
import org.mipt.entity.Weight;

/** Логер класса Physics. Собирает статистику (например положения) в csv файлы. */
public class PhysicsLogger {

  private final FileWriter xWriter;
  private final FileWriter yWriter;
  private final FileWriter energyWriter;
  private final FileWriter borderWriter;

  public PhysicsLogger(String filename, int weightsNumber) throws IOException {
    xWriter = new FileWriter(filename + "_xPos.csv");
    yWriter = new FileWriter(filename + "_yPos.csv");
    borderWriter = new FileWriter(filename + "_border.csv");
    energyWriter = new FileWriter(filename + "_energy.csv");
    xWriter.write("time");
    yWriter.write("time");
    energyWriter.write("time,energy\n");
    borderWriter.write("time,energy\n");
    for (int i = 0; i < weightsNumber; i++) {
      xWriter.write(",x" + i);
      yWriter.write(",y" + i);
    }
    xWriter.write("\n");
    yWriter.write("\n");
  }

  /**
   * Собирает позиции грузов по Y координате
   *
   * @param weights грузы
   * @param time текущий момент времени
   * @throws IOException
   */
  public void logY(List<Weight> weights, float time) throws IOException {
    yWriter.write(Float.toString(time));
    for (Weight w : weights) {
      yWriter.write("," + w.getY());
    }
    yWriter.write("\n");
  }

  /**
   * Собирает позиции грузов по X координате
   *
   * @param weights грузы
   * @param time текущий момент времени
   * @throws IOException
   */
  public void logX(List<Weight> weights, float time) throws IOException {
    xWriter.write(Float.toString(time));
    for (Weight w : weights) {
      xWriter.write("," + w.getX());
    }
    xWriter.write("\n");
  }

  /**
   * Собирает показатели полной энергии системы
   *
   * @param weights веса
   * @param springs пружины
   * @throws IOException
   */
  public void logTotalEnergy(List<Weight> weights, List<Spring> springs, float time)
      throws IOException {
    float total = 0f;

    // Кинетическая энергия всех масс
    for (Weight w : weights) {
      float vx = w.getVelocityX();
      float vy = w.getVelocityY();
      total += 0.5f * w.getMass() * (vx * vx + vy * vy);
    }

    // Потенциальная энергия всех пружин
    for (Spring s : springs) {
      Vector2 a = null, b = null;
      if (s.getLeftAnchor() != null && s.getRightAnchor() != null) {
        a = s.getLeftAnchor();
        b = s.getRightAnchor();
      } else if (s.getLowerAnchor() != null && s.getUpperAnchor() != null) {
        a = s.getLowerAnchor();
        b = s.getUpperAnchor();
      } else if (s.getAnchorA() != null && s.getAnchorB() != null) {
        a = s.getAnchorA();
        b = s.getAnchorB();
      }
      if (a == null || b == null) continue;

      float currentLength = a.dst(b);
      float restLength = s.getLength();
      float dx = currentLength - restLength;
      total += 0.5f * s.getK() * dx * dx;
    }

    energyWriter.write(Float.toString(time));
    energyWriter.write("," + total);
    energyWriter.write("\n");
  }

  /**
   * Суммирует кинетическую энергию грузов у границ сетки. Позволяет анализировать прохождение и
   * отражение волны у стен.
   *
   * @param weights веса
   * @throws IOException
   */
  public void logBorderKineticEnergy(
      List<Weight> weights, int weightsNumberX, int weightsNumberY, float time) throws IOException {
    float total = 0f;
    for (int j = 0; j < weightsNumberY; j++) {
      for (int i = 0; i < weightsNumberX; i++) {
        boolean isBorder = (i == 0 || i == weightsNumberX - 1 || j == 0 || j == weightsNumberY - 1);
        if (!isBorder) continue;
        int idx = j * weightsNumberX + i;
        Weight w = weights.get(idx);
        float vx = w.getVelocityX();
        float vy = w.getVelocityY();
        total += 0.5f * w.getMass() * (vx * vx + vy * vy);
      }
    }

    borderWriter.write(time + "," + total + "\n");
  }

  public void close() throws IOException {
    xWriter.close();
    yWriter.close();
    energyWriter.close();
    borderWriter.close();
  }
}
