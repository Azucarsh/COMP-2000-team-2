import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Map;

public class Simulator extends JPanel {
    private List<Animal> animals = new ArrayList<>();
    private List<Zone> environment = new ArrayList<>();
    private List<Grass> grassList = new ArrayList<>();
    private final Timer timer;
    private int grassSpawnTimer = 0;
    private Random rand = new Random();
    private static final List<Class<? extends Animal>> SPECIES = List.of(Cow.class, Caveman.class, Dinosaur.class);

    public Simulator() {
        createDinosaur(150, 200);
        createDinosaur(150, 500);
        createDinosaur(400, 400);
        createDinosaur(400, 50);
        createCaveman(210, 200);
        createCaveman(220, 200);
        createCaveman(500, 500);
        createCaveman(500, 400);
        createCow(200, 300);
        createCow(100, 250);
        createCow(250, 100);
        createCow(400, 400);
        createCow(450, 400);
        createCow(300, 300);
        
        createVolcano(400, 300, 150, 0);
        createCave(300, 400);

        timer = new Timer(16, e -> {
            updateSimulation();
            repaint();
        });
        timer.start();
    }

    private void updateSimulation() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        for (Zone zone : environment) {
            zone.update();
        }

        Volcano volcano = getVolcano();
        boolean volcanoErupting = (volcano != null && volcano.errupting);

        // Spawning grass (stops when volcano is erupting)
        if (!volcanoErupting) {
            grassSpawnTimer++;
            if (grassSpawnTimer >= 30) {
                int randomX = 30 + rand.nextInt(Math.max(100, screenWidth - 60));
                int randomY = 30 + rand.nextInt(Math.max(100, screenHeight - 60));
                grassList.add(new Grass(randomX, randomY));
                grassSpawnTimer = 0;
            }
            PopulationStats stats = animals.stream().collect(PopulationStats.collector(SPECIES));
        }

        // Lava kills animal and grass inside
        if (volcanoErupting) {
            for (Animal a : animals) {
                if (volcano.contains(a.x, a.y)) {
                    a.kill();
                }
            }

            for (Grass g : grassList) {
                if (volcano.contains(g.x, g.y)) {
                    g.isEaten = true;
                }
            }
        }

        // Update animals state
        List<Animal> animalsCopy = new ArrayList<>(animals); // Make a copy of animal list
        for (Animal a : animalsCopy) {
            a.update(screenWidth, screenHeight, animals, grassList);
        }

        // Check animal collision (Combat System)
        /*for (int i = 0; i < animals.size(); i++) {
            for (int j = i + 1; j < animals.size(); j++) {
                Animal a = animals.get(i);
                Animal b = animals.get(j);

                if (a.getBounds().intersects(b.getBounds())) {
                    CombatSystem.handleCollision(a, b, animals);
                }
            }
        }*/

        // (read only) find every pair of animals collided
        List<Animal[]> collisions = IntStream.range(0, animals.size()) 
            .parallel()
            .boxed()
            .flatMap(i -> IntStream.range(i + 1, animals.size())
                .filter(j -> animals.get(i).getBounds().intersects(animals.get(j).getBounds()))
                .mapToObj(j -> new Animal[] { animals.get(i), animals.get(j) }))
            .collect(Collectors.toList());

        // resolve the fights one thread at a time -> fix race conditions
        for (Animal[] pair : collisions) {
            CombatSystem.handleCollision(pair[0], pair[1], animals);
        }

        // Remove dead animals
        animals.removeIf(Animal::isDead);
        grassList.removeIf(g -> g.isEaten);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Zone zone : environment) {
            zone.draw(g2);
        }

        for (Grass grass : grassList) {
            grass.draw(g2);
        }

        for (Animal animal : animals) {
            animal.draw(g2);
        }

        // ...
        PopulationStats stats = animals.stream().collect(PopulationStats.collector(SPECIES));

        int yPos = 20;
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        for (Map.Entry<Class<?>, Integer> e : stats.getCounts().entrySet()) {
            g2.drawString(e.getKey().getSimpleName() + ": " + e.getValue(), 10, yPos);
            yPos += 15;
        }
        g2.drawString("Grass: " + grassList.stream().filter(gr -> !gr.isEaten).count(), 10, yPos);
        yPos += 15;
        g2.drawString(String.format("Balance: %.1f / 100", stats.getBalance()), 10, yPos);
    }

    private Volcano getVolcano() {
        for (Zone zone : environment) {
            if (zone instanceof Volcano) {
                return (Volcano) zone;
            }
        }
        return null;
    }

    public void createVolcano(int spawnX, int spawnY, int radius, int lavaRad) {
        environment.add(new Volcano(spawnX, spawnY, radius, lavaRad));
    }

    public void createCave(int spawnX, int spawnY) {
        environment.add(new Cave(spawnX, spawnY));
    }

    public void createDinosaur(int spawnX, int spawnY) {
        animals.add(new Dinosaur(spawnX, spawnY));
    }

    public void createCaveman(int spawnX, int spawnY) {
        animals.add(new Caveman(spawnX, spawnY));
    }

    public void createCow(int spawnX, int spawnY) {
        animals.add(new Cow(spawnX, spawnY));
    }
}