package com.javarush.island.animal;

//кролик
public class Rabbit extends Animal{
    public Rabbit(String animalName, double weight, double maxSatiety, int maxCount, int jamp, String listEat, boolean bGenger) {
        super(animalName, weight, maxSatiety, maxCount, jamp, listEat, bGenger);
    }
    public Rabbit(){super();}
}
