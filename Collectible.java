public class Collectible extends AbstractItem {
    private String type;
    private String condition;

    public Collectible(String name, double cost, Seller soldBy, String image,
                       String category, String type, String condition) {
        super(name, cost, soldBy, image, category);
        this.type = type;
        this.condition = condition;
    }

    // ICollectibles specific methods
    public String getType() { return type; }
    public String getCondition() { return condition; }

    public void setType(String type) { this.type = type; }
    public void setCondition(String condition) { this.condition = condition; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Type: %s - Condition: %s", type, condition);
    }
}
