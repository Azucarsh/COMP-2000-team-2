import java.util.List;
import java.util.Random;

public class WanderingState implements AnimalState {
    private Random rand = new Random();
    private int wanderTimer = 0;

    @Override
    public void update(Animal animal, int screenWidth, int screenHeight, List<Animal> animals, List<Grass> grassList) {
    animal.updateCore(screenWidth, screenHeight);

    if (animal.isHungry) {
        animal.setState(new HuntingState());
        return;
    }

    if (animal.hunger > 400 && animal.reproductionCooldown <= 0) {
        animal.setState(new ReproductionState());
        return;
    }
}
}