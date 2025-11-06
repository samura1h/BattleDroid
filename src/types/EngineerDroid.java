package types;
import base.Droid;
import Logic.Player; // <-- Додано імпорт
import javax.swing.*;

public class EngineerDroid extends Droid {

    private int RepairAbility;

    public EngineerDroid(String name, int maxHealthPoint, int currentHealthPoint, int damage, boolean alive, String weaponType, boolean hasAbility, ImageIcon icon) {
        super(name, maxHealthPoint,currentHealthPoint,damage,alive,weaponType,hasAbility,icon);
        this.RepairAbility = 5;
    }

    public int getRepairAbility() {
        return RepairAbility;
    }

    /**
     * Перевизначаємо базовий метод useAbility
     * Здібність Інженера: лікує ВСІХ союзників (включно з собою) на 5 HP.
     * 'target' і 'enemies' ігноруються.
     */
    @Override
    public String useAbility(Droid target, Player allies, Player enemies) {
        String msg = "  Ремонтує свою команду!\n";
        for (Droid ally : allies.getDroids()) {
            if (ally.isAlive()) {
                ally.heal(getRepairAbility()); // Викликаємо новий метод heal()
                msg += "  " + ally.getName() + " відновлює " + getRepairAbility() + " HP.\n";
            }
        }
        return msg;
    }

    // Старий метод 'public void Repair(...)' видалено, бо він більше не потрібен.
}