package com.javarush.island.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class SimulationConfig {
    // Размеры острова
    private int islandWidth;
    private int islandHeight;
    // популяции
    public static HashMap<String, Integer> initialAnimals;
    // растения
    private int initialPlants;

    private int initSimulationTick;

    // Количество растений которые будут добавляться за 1 такт в каждую клетку
    private int plantsPerCell;
    // Дюрация в мс.
    private long tickDurationMs;

    private int plantsMaxCount;
    private double plantsWeight;
    private int threads;
    private String version;
    //Настройки
    public static Properties properties;

    public static SimulationConfig builder(){
        InitProperty();
        SimulationConfig s = new SimulationConfig();
        initialAnimals = new HashMap<String, Integer>();
        s.islandWidth = Integer.parseInt(properties.getProperty("island.width"));
        s.islandHeight = Integer.parseInt(properties.getProperty("island.height"));
        s.tickDurationMs = Integer.parseInt(properties.getProperty("island.tick"));
        s.initSimulationTick = Integer.parseInt(properties.getProperty("island.maxsimpletick"));
        s.plantsPerCell = Integer.parseInt(properties.getProperty("plant.percell"));
        //количество растений при инициализации
        s.initialPlants = Integer.parseInt(properties.getProperty("init.plant"));
        //режим работы
        s.threads = Integer.parseInt(properties.getProperty("threads"));
        //версия программы
        s.version = properties.getProperty("version");
        s.plantsMaxCount = Integer.parseInt(properties.getProperty("plant.maxcount"));
        s.plantsWeight = Double.parseDouble(properties.getProperty("plant.weight"));
        //инициализация животных
        String[] iniAnimals = properties.getProperty("init.animal").split(";");
        for (int i = 0; i < iniAnimals.length; ++i)
        {
            String[] a = iniAnimals[i].split(":");
            initialAnimals.put(a[0], Integer.parseInt(a[1]));
        }
        return s;
    }
    public static void InitProperty() throws RuntimeException {
        properties = new Properties();
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try{
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    public static int getIntProperties(String name){
        return Integer.parseInt(properties.getProperty(name));
    }

    public static double getDbProperties(String name){
        return Double.parseDouble(properties.getProperty(name));
    }

    public static boolean getBoolProperties(String name){
        return properties.getProperty(name).equals("true");
    }

    public static String getStrProperties(String name){
        return properties.getProperty(name);
    }
    public int getThreads() {return threads;}

    public String getVersion() {return version;}

    public int getIslandWidth() {return this.islandWidth; }

    public int getIslandHeight() {return this.islandHeight;}

    public int getPlantsPerCell() {return this.plantsPerCell; }

    public long getTickDurationMs() {return  this.tickDurationMs; }

    public int getInitialPlants() {return this.initialPlants;}

    public int getInitSimulationTick() {return this.initSimulationTick;}

    public double getPlantsWeight() {return plantsWeight;}

    public int getPlantsMaxCount() {return plantsMaxCount;}
}
