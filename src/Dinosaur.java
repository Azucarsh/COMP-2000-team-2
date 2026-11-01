import java.awt.*;

public class Dinosaur extends Animal implements AnimalBehaviours {

    //Dinosaur stats can change later for balancing
    private static float defaultHealth = 16f;
    private static int defaultSpeed = 2;
    private static int defaultDamage = 2;
    private static int defaultMaxHunger = 800;
    private static int defaultFoodValue = 400;


        Dinosaur () {
            setStats();
            setStrategies();
            x = 0;
            y = 0;
        }

        //constructor for reproduced dinosaurs
        Dinosaur(int spawnX, int spawnY) {
            setStats();
            setStrategies();
            x = spawnX;
            y = spawnY;
            width = 100;
            height = 60;
        }

    @Override
    public void setStats(){
        this.health = defaultHealth;
        this.speed = defaultSpeed;
        this.hunger = defaultMaxHunger;
        this.maxHunger = defaultMaxHunger;
        this.foodValue = defaultFoodValue;
        this.damage = defaultDamage;
    }

    @Override
    public void createNest(){
    }

    @Override
    public void setStrategies() {
        setWanderStrategy(new RandomWanderStrategy());
        setHuntStrategy(new HuntStrategy(a -> a instanceof Caveman));
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(60, 150, 60));

        // Body (oval)
        g2.fillOval(x, y - 20, 50, 30);

        // Tail (triangle-ish using polygon)
        int[] tailX = {x, x - 20, x};
        int[] tailY = {y - 15, y - 5, y};
        g2.fillPolygon(tailX, tailY, 3);

        // Neck + head
        g2.fillOval(x + 40, y - 40, 15, 25);   // neck
        g2.fillOval(x + 47, y - 50, 18, 15);  // head

        // Legs
        g2.fillRect(x + 7, y + 5, 6, 15);
        g2.fillRect(x + 32, y + 5, 6, 15);

        // Spikes on back
        g2.setColor(new Color(30, 100, 30));
        for (int i = 0; i < 4; i++) {
            int sx = x + 10 + i * 9;
            int[] spikeX = {sx, sx + 4, sx + 8};
            int[] spikeY = {y - 20, y - 28, y - 20};
            g2.fillPolygon(spikeX, spikeY, 3);
        }

        // Eye
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 56, y - 48, 2, 2);

        // Stat
        drawStats(g2);
    }
}
