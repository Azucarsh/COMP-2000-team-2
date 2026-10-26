import java.util.ArrayList;
import java.util.List;

public class CombatSystem {

    public static void handleCollision(Animal a, Animal b, List<Animal> animals) { // check which animal collide with which
        if (a instanceof Dinosaur && b instanceof Caveman) {
            Dinosaur dino = (Dinosaur) a;
            Caveman caveman = (Caveman) b;
            dinoCavemanCombat(dino, caveman, animals);
        } else if (a instanceof Caveman && b instanceof Dinosaur) {
            Caveman caveman = (Caveman) a;
            Dinosaur dino = (Dinosaur) b;
            dinoCavemanCombat(dino, caveman, animals);
        } else if (a instanceof Dinosaur && b instanceof Dinosaur) {
            Dinosaur d1 = (Dinosaur) a;
            Dinosaur d2 = (Dinosaur) b;
            dinoDinoCombat(d1, d2);
        }
    }

    private static void dinoCavemanCombat(Dinosaur dino, Caveman caveman, List<Animal> animals) {
        if (dino.isHungry && dino.attackCooldown == 0 && caveman.attackCooldown == 0) {
            List<Caveman> fighters = new ArrayList<>(); // list of cavemen join combat
            fighters.add(caveman); // the attacked cavemen

            caveman.takeDamage(dino.damage); // dinosaur attack first
            dino.attackCooldown = 30; // wait 0.5s
            caveman.attackCooldown = 30;

            if (caveman.isDead() == false) { // if caveman alive => counter attack
                dino.takeDamage(caveman.damage);

                // find cavemen inside AlertRadius to join combat
              /*  for (int i = 0; i < animals.size(); i++) {
                    Animal other = animals.get(i);

                    if (other instanceof Caveman && other != caveman) {
                        Caveman ally = (Caveman) other;
                        double distance = caveman.getDistanceTo(ally);
 
                        if (distance <= Caveman.ALERT_RADIUS) {
                            dino.takeDamage(ally.damage);
                            fighters.add(ally); */

                            List<Caveman> nearbyAllies = animals.parallelStream()
                            .filter(other -> other instanceof Caveman && other != caveman)
                            .map(other -> (Caveman) other)
                            .filter(ally -> caveman.getDistanceTo(ally) <= Caveman.ALERT_RADIUS)
                            .collect(Collectors.toList());
            
                        fighters.addAll(nearbyAllies);

                        for (Caveman ally : nearbyAllies) {
                            dino.takeDamage(ally.damage);

                            // ally change direction and go to the dinosaur
                            if (ally.x < dino.x) {
                                ally.dx = Math.abs((int) ally.speed);
                            } else {
                                ally.dx = -Math.abs((int) ally.speed);
                            }

                            if (ally.y < dino.y) {
                                ally.dy = Math.abs((int) ally.speed);
                            } else {
                                ally.dy = -Math.abs((int) ally.speed);
                            }
                        }
                    }
                }
            }

            // if cavemen die => dinosaur eat caveman
            if (caveman.isDead() == true) {
                dino.hunger = Math.min(dino.maxHunger, dino.hunger + caveman.foodValue);
                dino.isHungry = false;
                dino.starvationTimer = dino.defaultStarvationTime;
            }

            // if dinosaur die => caveman eat dinosaur
            if (dino.isDead() == true) {
                for (int i = 0; i < fighters.size(); i++) {
                    Caveman fighter = fighters.get(i);

                    // if caveman didn't die => receive dinosaur meat
                    if (fighter.isDead() == false) {
                        fighter.hunger = Math.min(fighter.maxHunger, fighter.hunger + dino.foodValue);
                        fighter.isHungry = false;
                        fighter.starvationTimer = fighter.defaultStarvationTime;
                    }
                }
            }
        }
    }

    private static void dinoDinoCombat(Dinosaur d1, Dinosaur d2) {
        // if d1 is hungry and attack first
        if (d1.isHungry == true && d1.attackCooldown == 0 && d2.attackCooldown == 0) {
            executeDinoFight(d1, d2);
        }
        // if d2 is hungry and attack d1
        else if (d2.isHungry == true && d1.attackCooldown == 0 && d2.attackCooldown == 0) {
            executeDinoFight(d2, d1);
        }
    }

    private static void executeDinoFight(Dinosaur attacker, Dinosaur defender) {
        defender.takeDamage(attacker.damage); // d1/d2 attack first

        attacker.attackCooldown = 30;
        defender.attackCooldown = 30;

        if (defender.isDead() == false) {
            attacker.takeDamage(defender.damage); // if defender not dead => counter attack
        }

        // End combat: defender die => attacker eat
        if (defender.isDead() == true) {
            attacker.hunger = Math.min(attacker.maxHunger, attacker.hunger + defender.foodValue);
            attacker.isHungry = false;
            attacker.starvationTimer = attacker.defaultStarvationTime;
        }
        // attacker die => defender eat
        else if (attacker.isDead() == true) {
            defender.hunger = Math.min(defender.maxHunger, defender.hunger + attacker.foodValue);
            defender.isHungry = false;
            defender.starvationTimer = defender.defaultStarvationTime;
        }
    }
}