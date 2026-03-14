package com.javarush.island.animal;

import com.javarush.island.model.Island;
import com.javarush.island.model.Location;
import com.javarush.island.model.Plant;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Animal {
    private static final int CHANCE_OF_REPRODUCTION = 30; // шанс размножения
    protected int gender; // пол животного
    protected boolean alive = true; // живое
    protected int maxCount; // максимальное количество в клетке
    protected int jamp = 1; // скорость перемешения
    protected double weight; // вес
    protected double maxSatiety; // максимальная сытость
    protected double currentSatiety; // текущая сытость
    protected volatile Location currentLocation; // текущее местонахождение животного
    private String animalName;
    protected Map<String, Integer> eatListAnimal = new HashMap<String, Integer>();

    public Animal(String animalName, double weight, double maxSatiety, int maxCount, int jamp, String listEat, boolean bGenger) {
        //название животного
        this.animalName = animalName;
        //максимальный вес
        this.weight = weight;
        //максимальная сытость
        this.maxSatiety = maxSatiety;
        //пол животного, если не учитываем, то -1, 0/1 М/Ж
        if (bGenger == true) {this.gender = ThreadLocalRandom.current().nextInt(2);}
        else {this.gender = -1;}
        //максимальное количество в локации
        this.maxCount = maxCount;
        //на сколько локаций может переместиться за 1 ход
        this.jamp = jamp;
        String[] eatAnimals = listEat.split(";");
        //заполнение списка, что и сколько можно кушать (животных и траву)
        for (int i = 0; i < eatAnimals.length; i++)
        {
            String[] a = eatAnimals[i].split(":");
            eatListAnimal.put(a[0], Integer.parseInt(a[1]));
        }
    }
    public Animal(){
    }

    //кушать
    public void eat(Location locate){
        //жив
        if (!alive) {return;}
        //животное сыто
        if (currentSatiety >=  maxSatiety) {return;}
        for (Animal prey : locate.getAnimals()) {
            if (prey == this || !prey.isAlive()) continue;
            if (currentSatiety >= maxSatiety) continue;
                if (eatListAnimal.containsKey(prey.getAnimalName())){
                //хищник
                Integer prob = eatListAnimal.get(prey.getAnimalName());
                for (int i = 0; i < 3; i++){
                    if (prob != null && ThreadLocalRandom.current().nextInt(100) < prob) {
                      locate.removeAnimal(prey);
                      prey.die();
                      currentSatiety = Math.min(maxSatiety, currentSatiety + prey.getWeight());
                      System.out.println(this.getAnimalName() + " съел " + prey.getAnimalName());
                      if (eatListAnimal.containsKey("plant") && currentSatiety < maxSatiety) {
                        //травоядные+хищник
                        Integer probPlant = eatListAnimal.get("plant");
                        if (probPlant > 0) {
                            Plant plant = locate.removePlant();
                            if (plant != null) {
                                currentSatiety = Math.min(maxSatiety, currentSatiety + plant.getWeight());
                                System.out.println(this.getAnimalName() + " съел растение ");
                            }
                        }
                      }
                       if (currentSatiety >= maxSatiety) {
                          break;
                       }
                    }
                }

            }
            //травоядные
            for (int i = 0; i < 3; i++) {
                if (eatListAnimal.containsKey("plant") && currentSatiety < maxSatiety) {
                    Integer probPlant = eatListAnimal.get("plant");
                    if (probPlant > 0) {
                        Plant plant = locate.removePlant();
                        if (plant != null) {
                            currentSatiety = Math.min(maxSatiety, currentSatiety + plant.getWeight());
                            System.out.println(this.getAnimalName() + " съел растение ");
                        }
                    }
                }
            }
        }
    }

    // перемещение
    public void move(Island island, int currentX, int currentY){
        if (!alive) {return;}
        int newX = currentX;
        int newY = currentY;
        int oldX = currentX;
        int oldY = currentY;
        for (int i = 0; i < jamp; i++) {
            int dirction = ThreadLocalRandom.current().nextInt(10);
            switch ((dirction)) {
                case 0:
                    // вверх Y
                    newY = Math.max(0, currentY - 1);
                    break;
                case 1:
                    // вправо X
                    newX = Math.min(island.getWidth() -1, currentX + 1);
                    break;
                case 2:
                    // вниз Y
                    newY = Math.min(island.getHeight() - 1, currentY + 1);
                    break;
                case 3:
                    // влево X
                    newX = Math.max(0, currentX - 1);
                    break;
                default:
                    //остаемся на месте
                    continue;
            }
            if (newX != oldX || newY != oldY) {
                Location location = island.getLocation(newX, newY);
                if (Animal.getCountAnimalLocate(location, this.animalName) < this.maxCount) {
                    location.addAnimal(this);
                    Location locationOld = island.getLocation(oldX, oldY);
                    locationOld.removeAnimal(this);
                    oldX = newX;
                    oldY = newY;
                }
            }
        }
        if (oldX != currentX || oldY != currentY) {
            System.out.println("Переместился " + this.animalName + " c (" + currentX + "," + currentY+") на ("+oldX+","+oldY+")");
        }
    }
    // количество живых животных в 1 локации
    public static long getCountAnimalLocate(Location location, String nameAnimal){
       return location.getAnimals().stream()
                .filter(a -> a.getClass().getSimpleName().equalsIgnoreCase(nameAnimal.toLowerCase()) && a.isAlive() ) // промежуточная
                .count(); // терминальная
    }

    //  размножение
    public void reproduce(Location location) {
        if (!alive) {return;}
        //если Ж голодная (менее 1/3 сытости), то выходим. если М- выходим
        if (((this.gender == 1) && (this.getMaxSatiety() / 3 > this.getCurrentSatiety())) || (this.gender == 0)){ return;}
        // Подсчет особей того же вида, с учетом пола
        long sameSpeciesGenderCount = location.getAnimals().stream()
                .filter(a -> a.getClass() == this.getClass() && a != this && a.isAlive() && (this.gender != a.gender || this.gender == -1)) // промежуточная
                .count(); // терминальная
        // Подсчет особей того же вида
        long sameSpeciesCount = location.getAnimals().stream()
                .filter(a -> a.getClass() == this.getClass() && a.isAlive() ) // промежуточная
                .count(); // терминальная
        // Условие для размножения животных: наличие хотя бы одной особи того же вида (sameSpeciesCount), шанс размножения, с учетом
        if ((sameSpeciesGenderCount > 0 || this.gender == -1) && ThreadLocalRandom.current().nextInt(100) < CHANCE_OF_REPRODUCTION && sameSpeciesCount < this.maxCount) {
            try {
                // Создание потомка через рефлексию (не требуется значение конкретного подкласса во время компиляции)
                Animal baby = this.getClass().getDeclaredConstructor().newInstance();
                baby.setInitBaby(this);
                location.addAnimal(baby); // родившееся животное добавляем в локацию
                System.out.println("Родилось животное "+ baby.getClass().getSimpleName()) ;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                System.out.println("Ошибка при создании нового животного!");
                throw new RuntimeException(e);
            }
        }
    }

    public void die(){this.alive = false;}

    public void setCurrentLocation(Location location){this.currentLocation = location;}

    public boolean isAlive(){return this.alive;}

    public void setCurrentSatiety(double currentSatiety) {this.currentSatiety = currentSatiety;}

    public double getMaxSatiety() {return maxSatiety;}

    public String getAnimalName() {return animalName;}

    public double getWeight() {return this.weight; }

    public Location getCurrentLocation() {return this.currentLocation; }

    public double getCurrentSatiety() { return this.currentSatiety;}

    public void setInitBaby(Animal parent){
        this.animalName = parent.animalName;
        this.jamp = 0;
        this.maxCount = parent.maxCount;
        this.maxSatiety = parent.maxSatiety / 2;
        this.eatListAnimal = parent.eatListAnimal;
        if (parent.gender == -1) {this.gender = -1;}
        else {this.gender = ThreadLocalRandom.current().nextInt(2);}
    }
}
