package model.items;

/**
 * Interface for collectible items
 */
public interface ICollectible {
    String getType();
    String getCondition();
    void setType(String type);
    void setCondition(String condition);
}
