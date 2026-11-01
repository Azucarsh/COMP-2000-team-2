import java.awt.*;


public class Caveman extends Animal implements AnimalBehaviours{
    // stats can change later for balancing
    private static float defaultHealth = 10f;
    private static int defaultSpeed = 2;
    //private static int defaultHunger = 10; //seconds before dying?
    private static int defaultDamage = 2;
    private static int defaultMaxHunger = 800;
    private static int defaultFoodValue = 150;
    public static final int ALERT_RADIUS = 150;

    Caveman(){
        setStats();
        setStrategies();
        x = 0;
        y = 0;
    }

    Caveman(int spawnX, int spawnY) {
        setStats();
        setStrategies();
        x = spawnX;
        y = spawnY;
        width = 30;
        height = 50;
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
        setHuntStrategy(new HuntStrategy(a -> a instanceof Cow));
    }

    @Override
        public void draw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
 
        // Head
        g2.drawOval(x, y, 6, 6);

        // Body
        g2.drawLine(x + 3, y + 6, x + 3, y + 14);

        // Arms
        g2.drawLine(x + 3, y + 8, x - 2, y + 12);
        g2.drawLine(x + 3, y + 8, x + 8, y + 12);

        // Legs
        g2.drawLine(x + 3, y + 14, x - 1, y + 20);
        g2.drawLine(x + 3, y + 14, x + 7, y + 20);
        
        // Stat
        drawStats(g2);
    }

}
