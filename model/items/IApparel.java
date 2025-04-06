package model.items;

/**
 * Interface for apparel items
 */

public interface IApparel {
    String getSize();
    String getColor();
    String getBrand();
    void setSize(String size);
    void setColor(String color);
    void setBrand(String brand);
}
