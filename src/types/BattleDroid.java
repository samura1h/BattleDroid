package types;
import base.Droid;
import Logic.Player; // <-- Додано імпорт
import javax.swing.*;

public class BattleDroid extends Droid {

    private int BombAbility;

    public BattleDroid(String name, int maxHealthPoint, int currentHealthPoint, int damage, boolean alive, String weaponType, boolean hasAbility, ImageIcon icon){
        super(name, maxHealthPoint,currentHealthPoint,damage,alive,weaponType,hasAbility,icon);
        this.BombAbility = 25;
    }

    public int getBombAbility() {
        return BombAbility;
    }

    /**
     * Перевизначаємо базовий метод useAbility
     * Здібність BattleDroid-а: завдає 25 шкоди обраній цілі.
     */
    @Override
    public String useAbility(Droid target, Player allies, Player enemies) {
        int abilityDamage = getBombAbility(); // 25
        String msg = "  Завдає " + abilityDamage + " шкоди цілі " + target.getName() + "!\n";
        msg += target.takeDamage(abilityDamage); // Додаємо повідомлення про смерть
        return msg;
    }

    // Старий метод 'public void BombAbility(...)' видалено, бо він більше не потрібен.
}