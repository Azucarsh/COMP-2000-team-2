import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

public class Simulator extends JPanel {
    private List<Animal> animals = new ArrayList<>(); // will contain all the animals in the scene
    private List<Zone> environment = new ArrayList<>(); // contains the environment
    private final Timer timer;

    Stream<Animal> animalStream = animals.parallelStream();
    Stream<Zone> enviromentStream = environment.parallelStream(); // parallel Streams derived from Animal and zones to improve efficiency by running them on different threads.

    public Simulator() {

        // add some initial dinosaurs and cavemen
        createDinosaur(100, 200);
        createDinosaur(100, 50);
        createCaveman(210, 200);
        createCaveman(220, 200);
        createCaveman(500, 500);
        createCaveman(500, 400);
        createVolcano(400, 300, 150, 0);
        createCave(1500,1000);
        createCow(200, 300);
        createCow(100, 250);
        createCow(250, 100);

        timer = new Timer(16, e -> {
            updateSimulation();
            repaint();

        });
        timer.start();
    } // timer is the engine for the simulation, repaint() calls paintComponent below
      // to draw everything.

    private void updateSimulation() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        Volcano volcano = (Volcano) environment.get(0);

        for (int j = 0; j < environment.size(); j++) { // updates the environment
            environment.get(j).update();
        }

        for (int i = 0; i < animals.size(); i++) { // checks to see if there are any animals within the volcano's lava
                                                   // range and kills them
            if (volcano.contains(animals.get(i).x, animals.get(i).y)) {
                animals.get(i).kill();
            }
        }

        for (int i = 0; i < animals.size(); i++) {
            Animal a = animals.get(i);
            a.update(screenWidth, screenHeight); // update animal position

            // if it's a dinosaur => track and follow caveman
            if (a instanceof Dinosaur) {
                ((Dinosaur) a).trackNearestCaveman(animals);
            }
        }

        for (int i = 0; i < animals.size(); i++) {
            for (int j = i + 1; j < animals.size(); j++) {
                Animal a = animals.get(i);
                Animal b = animals.get(j);

                // check if animals collide with each other
                if (a.getBounds().intersects(b.getBounds()) == true) {
                    CombatSystem.handleCollision(a, b, animals);
                }
            }
        }

        for (int i = animals.size() - 1; i >= 0; i--) {
            if (animals.get(i).isDead() == true) {
                animals.remove(i); // remove dead animal
            }
        }
    }

    @Override // this sets up the graphics to be able to draw stuff, can draw stuff in other
              // classes with g.rectangle() etc
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < environment.size(); i++) { // draws all elements in the environment
            environment.get(i).draw(g2);
        }
        for (int i = 0; i < animals.size(); i++) {
            animals.get(i).draw(g2);
        }
        // draws all of the dinosaurs and humans into the scene from animals List()

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
