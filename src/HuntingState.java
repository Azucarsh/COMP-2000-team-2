import java.util.List;

public class HuntingState implements AnimalState {
    @Override
    public void update(Animal animal, int screenWidth, int screenHeight, List<Animal> animals, List<Grass> grassList) {
        if (!animal.isHungry) {
            animal.setState(new WanderingState());
            return;
        }

        // Tùy theo loài mà định nghĩa mục tiêu tìm kiếm khi đói
        if (animal instanceof Dinosaur) {
            ((Dinosaur) animal).trackNearestCaveman(animals);
        } else if (animal instanceof Caveman) {
            trackNearestCow((Caveman) animal, animals);
        } else if (animal instanceof Cow && grassList != null) {
            trackNearestGrass((Cow) animal, grassList); // Cow tìm cỏ ở đây!
        }

        // Gọi logic vật lý cốt lõi (di chuyển, đếm ngược starvationTimer)
        animal.updateCore(screenWidth, screenHeight);

        // Kiểm tra nếu quá thời gian starvation mà chết -> Chuyển sang DeadState
        if (animal.isDead()) {
            animal.setState(new DeadState());
        }
    }

    // Logic Caveman tìm Cow
    private void trackNearestCow(Caveman caveman, List<Animal> animals) {
        Cow nearestCow = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal a : animals) {
            if (a instanceof Cow && !a.isDead()) {
                double dist = caveman.getDistanceTo(a);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestCow = (Cow) a;
                }
            }
        }

        if (nearestCow != null) {
            caveman.dx = (caveman.x < nearestCow.x) ? Math.abs((int)caveman.speed) : -Math.abs((int)caveman.speed);
            caveman.dy = (caveman.y < nearestCow.y) ? Math.abs((int)caveman.speed) : -Math.abs((int)caveman.speed);
        }
    }

    // Logic Cow tìm Grass (Đã được kéo từ Cow.java vào đây để chuẩn State Pattern)
    private void trackNearestGrass(Cow cow, List<Grass> grassList) {
        Grass nearestGrass = null;
        double minDistance = Double.MAX_VALUE;

        for (Grass g : grassList) {
            if (!g.isEaten) {
                // Tính khoảng cách từ Cow tới đốm cỏ g
                double deltaX = cow.x - g.x;
                double deltaY = cow.y - g.y;
                double dist = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestGrass = g;
                }
            }
        }

        if (nearestGrass != null) {
            cow.dx = (cow.x < nearestGrass.x) ? Math.abs((int)cow.speed) : -Math.abs((int)cow.speed);
            cow.dy = (cow.y < nearestGrass.y) ? Math.abs((int)cow.speed) : -Math.abs((int)cow.speed);

            // Nếu bò chạm vào cỏ -> Ăn cỏ, hồi phục hunger và thoát trạng thái đói
            if (cow.getBounds().intersects(nearestGrass.getBounds())) {
                nearestGrass.isEaten = true;
                cow.hunger = Math.min(cow.maxHunger, cow.hunger + nearestGrass.foodValue);
                cow.isHungry = false;
                cow.starvationTimer = cow.defaultStarvationTime;
            }
        }
    }
}