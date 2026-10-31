import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;

public class Dinosaur extends Animal implements AnimalBehaviours {

    //Dinosaur stats can change later for balancing
    private static float defaultHealth = 10f;
    private static int defaultSpeed = 2;
    private static int defaultDamage = 2;
    private static int defaultMaxHunger = 800;
    private static int defaultFoodValue = 400;

private static final BufferedImage sprite =
        SpriteLoader.load("src/assets/dinosaur.png");

        Dinosaur () {
            setStats();
            x = 0;
            y = 0;

        }

        //constructor for reproduced dinosaurs, pass the position of the parent (or next to them)
        Dinosaur(int spawnX, int spawnY) {
            setStats();
            x = spawnX;
            y = spawnY;
            width = 100;
            height = 60;
        }

    @Override
    public void setStats(){
        this.health = defaultHealth;
        this.dx = (Math.random() < 0.5 ? -1 : 1) * defaultSpeed;
        this.dy = (Math.random() < 0.5 ? -1 : 1) * defaultSpeed;
        this.hunger = defaultMaxHunger;
        this.maxHunger = defaultMaxHunger;
        this.foodValue = defaultFoodValue;
        this.damage = defaultDamage;
    }

    @Override
    public void createNest(){

    }

 @Override
public void draw(Graphics2D g2) {
    if (sprite != null) {
        g2.drawImage(sprite, x, y, 120, 120, null);
    } else {
        g2.setColor(new Color(60, 150, 60));
        g2.fillOval(x, y, 50, 30);
    }

    drawStats(g2);
}

    @Override
    public void update(int screenWidth, int screenHeight) {
        // move randomly until hungry
        super.update(screenWidth, screenHeight);
    }

    // if hungry => go to the nearest caveman
    public void trackNearestCaveman(List<Animal> animals) {
        if (this.isHungry == false) return;

        Caveman nearestCaveman = null;
        double minDistance = Double.MAX_VALUE;

        // Find nearest caveman from animal list
        for (int i = 0; i < animals.size(); i++) {
            Animal a = animals.get(i);
            if (a instanceof Caveman && a.isDead() == false) {
                double dist = this.getDistanceTo(a);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestCaveman = (Caveman) a;
                }
            }
        }

        // change direction to nearest caveman
        if (nearestCaveman != null) {
            if (this.x < nearestCaveman.x) {
                this.dx = Math.abs(this.speed > 0 ? (int)this.speed : 3);
            } else {
                this.dx = -Math.abs(this.speed > 0 ? (int)this.speed : 3);
            }

            if (this.y < nearestCaveman.y) {
                this.dy = Math.abs(this.speed > 0 ? (int)this.speed : 3);
            } else {
                this.dy = -Math.abs(this.speed > 0 ? (int)this.speed : 3);
            }
        }
    }
}
