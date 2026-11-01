import java.util.List;

public class ReproductionState implements AnimalState {

    private static final int MAX_POPULATION_PER_SPECIES = 12;
    private static final int REPRODUCTION_COOLDOWN = 600;
    private static final int MIN_HUNGER_TO_BREED = 200;
    private static final int BIRTH_HUNGER_COST = 200;

    @Override
    public void update(Animal animal, int screenWidth, int screenHeight, List<Animal> animals, List<Grass> grassList) {

        // If hungry, eating over breeding
        if (animal.isHungry) {
            animal.setState(new HuntingState());
            return;
        }

        // Too many of this species already -> give up and go wandering
        if (countSpecies(animal, animals) >= MAX_POPULATION_PER_SPECIES) {
            animal.reproductionCooldown = REPRODUCTION_COOLDOWN;
            animal.setState(new WanderingState());
            return;
        }

        Animal partner = findNearestPartner(animal, animals);

        if (partner == null) {
            animal.updateCore(screenWidth, screenHeight);
            animal.setState(new WanderingState());
            return;
        }

        animal.moveToward(partner.x, partner.y);

        animal.updateCore(screenWidth, screenHeight);

        if (animal.getBounds().intersects(partner.getBounds())) {
            Animal child = null;
            if (animal instanceof Dinosaur) {
                child = new Dinosaur(animal.x + 15, animal.y + 15);
            } else if (animal instanceof Caveman) {
                child = new Caveman(animal.x + 15, animal.y + 15);
            } else if (animal instanceof Cow) {
                child = new Cow(animal.x + 15, animal.y + 15);
            }

            if (child != null) {
                // newborns can't breed right away (also stops parent breeding with its own baby)
                child.reproductionCooldown = REPRODUCTION_COOLDOWN;
                animals.add(child);
            }

            animal.hunger -= BIRTH_HUNGER_COST;
            partner.hunger -= BIRTH_HUNGER_COST;

            animal.reproductionCooldown = REPRODUCTION_COOLDOWN;
            partner.reproductionCooldown = REPRODUCTION_COOLDOWN;

            animal.setState(new WanderingState());
            partner.setState(new WanderingState());
        }
    }

    private Animal findNearestPartner(Animal animal, List<Animal> animals) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal a : animals) {
            if (a != animal
                    && a.getClass().equals(animal.getClass())
                    && !a.isDead()
                    && !a.isHungry
                    && a.hunger > MIN_HUNGER_TO_BREED
                    && a.reproductionCooldown <= 0) {   // partner must also be ready
                double dist = animal.getDistanceTo(a);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = a;
                }
            }
        }
        return nearest;
    }

    private int countSpecies(Animal animal, List<Animal> animals) {
        int count = 0;
        for (Animal a : animals) {
            if (a.getClass().equals(animal.getClass()) && !a.isDead()) count++;
        }
        return count;
    }
}