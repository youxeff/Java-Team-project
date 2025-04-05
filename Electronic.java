public class Electronic extends AbstractItem {
    private String type;
    private int year;

    public Electronic(String name, double cost, User soldBy, String image,
                      String category, String type, int year) {
        super(name, cost, soldBy, image, category);
        this.type = type;
        this.year = year;
    }

    // IElectronic specific methods
    public String getType() { return type; }
    public int getYear() { return year; }

    public void setType(String type) { this.type = type; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Type: %s - Year: %d", type, year);
    }
}
