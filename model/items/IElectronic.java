package model.items;

/**
 * Interface for electronic items
 */

public interface IElectronic {
    String getType();
    int getYear();
    void setType(String type);
    void setYear(int year);
}
