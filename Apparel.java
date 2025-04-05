public class Apparel extends AbstractItem {
    private String size;
    private String color;
    private String brand;

    public Apparel(String name, double cost, User soldBy, String image,
                   String category, String size, String color, String brand) {
        super(name, cost, soldBy, image, category);
        this.size = size;
        this.color = color;
        this.brand = brand;
    }

    // IApparel specific methods
    public String getSize() { return size; }
    public String getColor() { return color; }
    public String getBrand() { return brand; }

    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
    public void setBrand(String brand) { this.brand = brand; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Size: %s - Color: %s - Brand: %s",
                size, color, brand);
    }
}
