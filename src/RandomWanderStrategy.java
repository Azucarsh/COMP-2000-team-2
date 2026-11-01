import java.util.List;
import java.util.Random;

public class RandomWanderStrategy implements MovementStrategy {
    private static final Random rand = new Random();

    @Override
    public void move(Animal animal, List<Animal> animals, List<Grass> grassList) {
        // only choose direction if the animal isn't moving yet
        if (animal.dx == 0 && animal.dy == 0) {
            animal.dx = (Math.random() < 0.5 ? -1 : 1) * (int) animal.speed;
            animal.dy = (Math.random() < 0.5 ? -1 : 1) * (int) animal.speed;
        }
    }
}