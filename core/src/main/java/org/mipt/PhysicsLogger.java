package org.mipt;

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

  public PhysicsLogger(String filename, int weightsNumber) throws IOException {
    xWriter = new FileWriter(filename + "_xPos.csv");
    yWriter = new FileWriter(filename + "_yPos.csv");
    energyWriter = new FileWriter(filename + "_energy.csv");
    xWriter.write("time");
    yWriter.write("time");
    energyWriter.write("time,energy\n");
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
   * Собирает показатели энергии на правом креплении пружины
   *
   * @param spring пружина
   * @param time текущий момент времени
   * @throws IOException
   */
  public void logEnergyTime(Spring spring, float time) throws IOException {
    energyWriter.write(Float.toString(time));
    float energy = spring.getRightForce().x;
    energyWriter.write("," + energy);
    energyWriter.write("\n");
  }

  public void close() throws IOException {
    xWriter.close();
    yWriter.close();
    energyWriter.close();
  }
}
