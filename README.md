# Elastic Wave

[Русская версия](#русская-версия)

## English Version

### Description

A model of elastic wave propagation in a chain of masses connected by springs.

#### Radial wave example:

https://github.com/user-attachments/assets/18500018-c1e7-4b61-9a73-bc3a12c01537

#### Linear wave example:

https://github.com/user-attachments/assets/2453cdb9-25df-4647-a22a-02b2cba768fa

#### Multiple radial waves animation:

https://github.com/user-attachments/assets/c45fe04f-290a-4471-8c3a-81602ef8e401

---

### Simulation Controls

- **Mouse Wheel** – zoom in / zoom out  
- **Left Mouse Button (hold)** – rotate the camera view  
- **Spacebar** – launch a wave from the center of the matrix *(radial)*  
- **1** – launch a wave from the central column of masses  

---

### Model Description

The model represents a two-dimensional matrix of masses connected by springs.  
The simulation takes into account the following physical laws:

- Newton’s Second Law  
- Hooke’s Law  
- Conservation of Momentum  
- Conservation of Energy in Elastic Collisions  
- Perfectly Elastic Reflection from Walls  

To calculate the positions of the masses, the following second-order differential equation is solved:

$m_i x_i'' = k_{i-1}(x_{i-1} - x_i + l_{i-1}) - k_i(x_{i+1} - x_i - l_i)$

where $l_i$ is the equilibrium length of the *i*-th spring, $m_i$ is the mass of the *i*-th weight, and $x_i$ is its position.  

The equation is solved numerically using the fourth-order Runge–Kutta method.

---

### Tech Stack

- Java 8 (using SDK 21 Temurin)  
- libGDX  
- Gradle  

---

### Project Team

- @Samyrai47
- @Vlad-Ali


## Русская версия

### Описание

Модель распространения упругой волны в цепочке грузов, соединённых пружинами.


#### Пример круговой волны:

https://github.com/user-attachments/assets/18500018-c1e7-4b61-9a73-bc3a12c01537

#### Пример линейной волны:

https://github.com/user-attachments/assets/2453cdb9-25df-4647-a22a-02b2cba768fa

#### Пример нескольких радиальных волн:

https://github.com/user-attachments/assets/c45fe04f-290a-4471-8c3a-81602ef8e401

---

### Управление симуляцией

- **Колесо мыши** - увеличить/уменьшить масштаб.
- **ЛКМ (зажать)** - менять ракурс камеры.
- **Пробел** - запустить волну из центра матрицы *(радиальная)*.
- **1** - запустить волну от центрального столбца грузов.

---

### Описание модели

Модель представляет собой двумерную матрицу грузов, соединенных пружинами. В симуляции учитываются:

- Второй закон Ньютона 
- Закон Гука
- Закон сохранения импульса
- Закон сохранения энергии при упругих столкновениях
- Идеально упругое отражение от стенок

Для просчета позиций грузов решается дифференциальное уравнение второго порядка вида:

$m_i x_i'' = k_{i-1}(x_{i-1} - x_i + l_{i-1}) - k_i(x_{i+1} - x_i - l_i)$

где $l_i$ - длина i-ой пружины в равновесии, $m_i$ - масса i-го груза, $x_i$ - позиция i-го груза.

Решается уравнение численным методом Рунге-Кутты 4-го порядка.

---

### Стэк

- Java 8 (используется SDK 21 temurin)
- libGDX
- Gradle

---

### Команда проекта

- @Samyrai47
- @Vlad-Ali
