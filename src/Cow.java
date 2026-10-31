import java.awt.*;
import java.util.Random;
import java.awt.image.BufferedImage;

public class Cow extends Animal implements AnimalBehaviours {
    private static float defaultHealth = 8f;
    private static float defaultSpeed = 3f;
    private static int defaultHunger = 10;
    private static int defaultDamage = 0; //purely prey

private static final BufferedImage sprite =
        SpriteLoader.load("src/assets/cow.png");

    private static Random rand = new Random();
    private int wanderTimer = 0;

    Cow() //default spawn
    {
        setStats();
        x = 0;
        y = 0;
    }

    Cow(int x, int y) //position specific spawn
    {
        setStats();
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        wanderTimer--;
        if (wanderTimer <= 0) {
            dx = rand.nextInt(3) - 1; // -1, 0, or 1
            dy = rand.nextInt(3) - 1; // -1, 0, or 1
            wanderTimer = 30 + rand.nextInt(60); // new direction roughly every 0.5-1.5s at 60fps
        }
        super.update(screenWidth, screenHeight); // let Animal's existing movement logic actually move x/y
    }
    @Override
    public void setStats(){
        this.health = defaultHealth;
        this.speed = defaultSpeed;
        this.hunger = defaultHunger;
        this.damage = defaultDamage;
    }

    @Override
    public void createNest(){
    }
    

   @Override
    public void draw(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, 100, 100, null);
        } else {
            g2.setColor(new Color(180, 130, 90));
            g2.fillRoundRect(x, y, 35, 30, 8, 8);
        }

        drawStats(g2);
    }
    
}