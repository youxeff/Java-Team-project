package model.items;

import model.users.User;

public class Electronic extends AbstractItem {
    private String type;
    private int year;
    private final Object lock = new Object();

    public Electronic(String name, double cost, User soldBy, String image,
                      String category, String type, int year) {
        super(name, cost, soldBy, image, category);
        this.type = type;
        this.year = year;
    }

    // IElectronic specific methods
    public String getType() { 
        synchronized(lock) {
            return type;
        }
    }
    
    public int getYear() { 
        synchronized(lock) {
            return year;
        }
    }

    public void setType(String type) { 
        synchronized(lock) {
            this.type = type;
        }
    }
    
    public void setYear(int year) { 
        synchronized(lock) {
            this.year = year;
        }
    }

    @Override
    public String toString() {
        synchronized(lock) {
            return super.toString() + String.format(" - Type: %s - Year: %d", type, year);
        }
    }
}
