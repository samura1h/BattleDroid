package types;
import base.Droid;
import Logic.Player; // <-- Додано імпорт
import javax.swing.*;

public class SniperDroid extends Droid {

    private int Granade;

    public SniperDroid(String name, int maxHealthPoint, int currentHealthPoint, int damage, boolean alive, String weaponType, boolean hasAbility,ImageIcon icon){
        super(name, maxHealthPoint,currentHealthPoint,damage,alive,weaponType,hasAbility,icon);
        this.Granade = 15;
    }

    public int getGranade() {
        return Granade;
    }

    /**
     * Перевизначаємо базовий метод useAbility
     * Здібність Снайпера: кидає гранату і завдає 15 шкоди ВСІМ ворогам.
     * 'target' і 'allies' ігноруються.
     */

    @Override
    public String useAbility(Droid target, Player allies, Player enemies) {
        String msg = "  Кидає гранату в команду ворога!\n";
        String destroyedMsgs = ""; // Збираємо повідомлення про смерті

        for (Droid enemy : enemies.getDroids()) {
            if (enemy.isAlive()) {
                msg += "  " + enemy.getName() + " отримує " + getGranade() + " шкоди.\n";
                destroyedMsgs += enemy.takeDamage(getGranade());
            }
        }

        if (!destroyedMsgs.isEmpty()) {
            msg += "\n" + destroyedMsgs; // Додаємо всі повідомлення про смерті
        }
        return msg;
    }
}