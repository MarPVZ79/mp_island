package com.javarush.island.model;

/*
* Остров
* */
public class Island {
  private final int width;

  private final int height;
  private final Location[][] locations;

  public Island(int height, int width){
      this.height = height;
      this.width = width;
      this.locations = new Location[height][width];
      for (int i = 0; i < height; i++){
          for(int j = 0; j < width; j++){
              locations[i][j] = new Location();
          }
      }
  }
  public Location getLocation(int x, int y){
      if (x < 0|| x>= width || y < 0 || y >= height){
          throw new IllegalArgumentException("Координаты выходят за размеры острова");
      }
      return locations[y][x];
  }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
