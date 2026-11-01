import java.util.List;

/// decides how animal picks direction
@FunctionalInterface
public interface MovementStrategy {
    void move(Animal animal, List<Animal> animals, List<Grass> grassList);
}