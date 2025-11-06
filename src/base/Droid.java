package base;

import javax.swing.ImageIcon;
import Logic.Player;

/**
 * Клас Droid є базовим (батьківським) класом для всіх типів дроїдів у грі.
 * Він містить загальні властивості та методи, які є у кожного дроїда,
 * незалежно від його спеціалізації (BattleDroid, SniperDroid тощо).
 */
public class Droid {

    // (Тут твої поля name, maxHealthPoint, currentHealthPoint і т.д. - вони не змінились)
    private String name;
    private int maxHealthPoint;
    private int currentHealthPoint;
    private int damage;
    private boolean alive;
    private String weaponType;
    private boolean hasAbility;
    private ImageIcon icon;


    public Droid(String name, int maxHealthPoint,int currentHealthPoint, int damage,boolean alive, String weaponType, boolean hasAbility,ImageIcon icon) {
        this.name = name;
        this.maxHealthPoint = maxHealthPoint;
        this.currentHealthPoint = maxHealthPoint;
        this.damage = damage;
        this.alive = true;
        this.weaponType = weaponType;
        this.hasAbility = hasAbility;
        this.icon = icon;
    }

    // (Тут твої Геттери і Сеттери - вони не змінились)
    public String getName() { return name; }
    public int getMaxHealthPoint() { return maxHealthPoint; }
    public int getCurrentHealthPoint() { return currentHealthPoint; }
    public int getDamage() { return damage; }
    public boolean isAlive() { return alive; }
    public String getWeaponType() { return weaponType; }
    public boolean hasAbility() { return hasAbility; }
    public ImageIcon getIcon() { return icon; }

    public void setCurrentHealthPoint(int hp) { currentHealthPoint = hp; }
    public void setAlive(boolean status) { alive = status; }
    public void setIcon(ImageIcon icon) { this.icon = icon; }
    public void setName(String name) {this.name = name;}


    // ======= Методи бою (ОНОВЛЕНО) =======

    /**
     * Атака на ворога.
     * @return Повертає рядок "Ім'я знищено!", якщо ціль загинула.
     */
    public String attack(Droid target) {
        if (!alive || target == null || !target.isAlive()) return "";
        return target.takeDamage(damage);
    }

    /**
     * (ОНОВЛЕНО) Базовий метод використання здібності.
     * Більшість дроїдів (як GiantDroid) просто використають цю версію (подвійна шкода).
     * Інші дроїди (BattleDroid, Sniper) перевизначать (@Override) цей метод.
     *
     * @param target - Обрана ціль
     * @param allies - Твоя команда (для лікування)
     * @param enemies - Команда ворога (для AoE атак)
     * @return Рядок з описом того, що сталося (хто отримав шкоду/лікування)
     */
    public String useAbility(Droid target, Player allies, Player enemies) {
        if (!alive || !hasAbility || target == null || !target.isAlive()) return "";

        // Базова здібність - просто подвійна шкода по цілі
        int abilityDamage = damage * 2;
        String msg = " завдає " + abilityDamage + " подвійної шкоди цілі " + target.getName() + "!\n";
        msg += target.takeDamage(abilityDamage); // Додаємо повідомлення про смерть, якщо є
        return msg;
    }

    /**
     * Лікування дроїда.
     */
    public void heal(int amount) {
        if (!alive) return;
        this.currentHealthPoint += amount;
        // Перевіряємо, щоб лікування не перевищило максимальне здоров'я
        if (this.currentHealthPoint > this.maxHealthPoint) {
            this.currentHealthPoint = this.maxHealthPoint;
        }
    }

    /**
     * Метод отримання шкоди.
     * @return Повертає рядок "Ім'я знищено!", якщо дроїд загинув
     */
    public String takeDamage(int dmg) {
        currentHealthPoint -= dmg;
        if (currentHealthPoint <= 0) {
            currentHealthPoint = 0;
            alive = false;
            return "  " + name + " знищено!\n"; // Додаємо відступ для логу
        }
        return "";
    }
}