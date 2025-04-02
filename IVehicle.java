/**
 * This interface represents a vehicle item.
 * The fields of the Vehicle class that will extend IVehicle will be the same as listed in the Item interface, but
 * will also include fields like:
 *  mileage: int, mileage of the vehicle (2000, 50000, etc.)
 *  year: int, year vehicle was manufactured (2018, 1950, etc.)
 *  brand: String, brand of the car ("Ford", "Fiat", etc.)
 */
public interface IVehicle extends Item{
    // inherits all methods from Item
    // will include getters and setters for additional fields as listed above
}