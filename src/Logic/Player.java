package Logic;

import java.util.ArrayList;
import java.util.List;
import base.Droid;

public class Player {
    private String name;
    private List<Droid> droids;  // всі дроїди цього гравця

    public Player(String name) {
        this.name = name;
        this.droids = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Droid> getDroids() {
        return droids;
    }

    public void addDroid(Droid droid) {
        if (droid != null) {
            droids.add(droid);
        }
    }

    // Повертає першого живого дроїда (для атаки)
    public Droid getFirstAliveDroid() {
        for (Droid d : droids) {
            if (d.isAlive()) {
                return d;
            }
        }
        return null;
    }

    // Перевіряє, чи всі дроїди загинули
    public boolean allDead() {
        for (Droid d : droids) {
            if (d.isAlive()) return false;
        }
        return true;
    }

    // Друкує стан усіх дроїдів
    public void showDroids() {
        System.out.println("Player " + name + " droids:");
        for (Droid d : droids) {
            System.out.println("- " + d.getName() + " | HP: " + d.getCurrentHealthPoint() +
                    " | Alive: " + d.isAlive());
        }
    }
}
