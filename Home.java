public class Home extends AbstractItem {
    private String type;

    public Home(String name, double cost, User soldBy, String image,
                String category, String type) {
        super(name, cost, soldBy, image, category);
        this.type = type;
    }

    // home specific methods
    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Type: %s", type);
    }
}
