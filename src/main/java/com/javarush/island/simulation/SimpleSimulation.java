package com.javarush.island.simulation;

import com.javarush.island.animal.*;
import com.javarush.island.config.SimulationConfig;
import com.javarush.island.model.Location;
import com.javarush.island.model.Island;
import com.javarush.island.model.Plant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class SimpleSimulation {
    private final Island island;
    private final SimulationConfig config;
    private static final double SATIETY_PER_TICK = 0.01;

    public SimpleSimulation(SimulationConfig config) {
        this.config = config;
        this.island = new Island(config.getIslandWidth(), config.getIslandHeight());
    }

    public void initialize() {
        for (String key: SimulationConfig.initialAnimals.keySet()) {
          Integer value = SimulationConfig.initialAnimals.get(key);
          if (value > 0) {
            double weight   = SimulationConfig.getDbProperties(key + ".weight");
            double eat      = SimulationConfig.getDbProperties(key + ".eat");
            int maxcount    = SimulationConfig.getIntProperties(key + ".maxcount");
            int jump        = SimulationConfig.getIntProperties(key + ".jump");
            String eatlist  = SimulationConfig.getStrProperties(key + ".eatlist");
            boolean bGender = SimulationConfig.getBoolProperties(key + ".gender");
            for (int i = 0; i < value; i++) {
              int x = ThreadLocalRandom.current().nextInt(config.getIslandWidth());
              int y = ThreadLocalRandom.current().nextInt(config.getIslandHeight());
              if (key.equals("bear")){
                  Bear bear = new Bear(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(bear);
              }
              else if (key.equals("boa")){
                  Boa boa = new Boa(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(boa);
              }
              else if (key.equals("bull")){
                  Bull bull = new Bull(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(bull);
              }
              else if (key.equals("deer")){
                  Deer deer = new Deer(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(deer);
              }
              else if (key.equals("duck")){
                  Duck duck = new Duck(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(duck);
              }
              else if (key.equals("eagle")){
                  Eagle eagle = new Eagle(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(eagle);
              }
              else if (key.equals("fox")){
                  Fox fox = new Fox(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(fox);
              }
              else if (key.equals("goat")){
                  Goat goat = new Goat(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(goat);
              }
              else if (key.equals("horse")){
                  Horse horse = new Horse(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(horse);
              }
              else if (key.equals("mouse")){
                  Mouse mouse = new Mouse(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(mouse);
              }
              else if (key.equals("rabbit")){
                  Rabbit rabbit = new Rabbit(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(rabbit);
              }
              else if (key.equals("sheep")){
                  Sheep sheep = new Sheep(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(sheep);
              }
              else if (key.equals("wildboar")){
                  WildBoar wildboar = new WildBoar(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(wildboar);
              }
              else if (key.equals("wolf")){
                  Wolf wolf = new Wolf(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(wolf);
              }
              else if (key.equals("worm")){
                  Worm worm = new Worm(key, weight, eat, maxcount, jump, eatlist, bGender);
                  island.getLocation(x, y).addAnimal(worm);
              }
            }
          }
      }
        // Размещаем Растения
        for (int y = 0; y < island.getHeight(); y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Location location = island.getLocation(x, y);
                for (int p = 0; p < config.getInitialPlants(); p++) {
                    location.addPlant(new Plant());
                }
            }
        }
        System.out.println("Инициализация завершена. Животные и растения размещены.");
        printStatistics();
    }

    public void tick() {
        // 1) Рост растений
        for (int y = 0; y < island.getHeight(); y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Location location = island.getLocation(x, y);
                for (int i = 0; i < config.getPlantsPerCell(); i++) {
                    location.addPlant(new Plant());
                }
            }
        }
        // 2) Обработка животных (пройти по всем клеткам)
        for (int y = 0; y < island.getHeight(); y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Location location = island.getLocation(x, y);
                List<Animal> animals = List.copyOf(location.getAnimals());
                for (Animal animal : animals) {
                    if (!animal.isAlive()) {
                        continue;
                    }
                    animal.eat(location);
                    animal.move(island, x, y);
                    animal.reproduce(location);
                    // Уменьшить сытость
                    animal.setCurrentSatiety(animal.getCurrentSatiety() - SATIETY_PER_TICK);
                    if (animal.getCurrentSatiety() <= 0) {
                        System.out.println("die "+ animal.getAnimalName());
                        animal.die();
                        location.removeAnimal(animal);
                    }
                }
            }
        }
        printStatistics();
    }

    public void printStatistics() {;
        long plants = 0;
        String statistic = "";
        //Растений
        for (int y = 0; y < island.getHeight(); y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Location location = island.getLocation(x, y);
                plants += location.getPlants().size();
            }
        }
        //Животных
        for (String key: SimulationConfig.initialAnimals.keySet()) {
            long countAnimal = 0;
            for (int y = 0; y < island.getHeight(); y++) {
                for (int x = 0; x < island.getWidth(); x++) {
                    Location location = island.getLocation(x, y);
                    countAnimal += Animal.getCountAnimalLocate(location, key);
                }
            }
            statistic += ", "+ key + " = " + countAnimal + " ";
        }
        System.out.println("Статистика: растений = "  + plants + statistic);
    }

    public void run(int ticks) throws InterruptedException {
        for (int i = 0; i < ticks; i++) {
            System.out.println("Такт {"+ i + 1+"}");
            tick();
            Thread.sleep(config.getTickDurationMs());
        }
    }
}