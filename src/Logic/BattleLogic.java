package Logic;

import base.Droid;
import java.util.List;

/**
 * Клас BattleLogic відповідає за хід бою між двома гравцями.
 */
public class BattleLogic {

    private Player player1;
    private Player player2;
    private int currentPlayer;

    public BattleLogic(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = 1;
    }

    // Геттери
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    public Player getCurrentPlayer() {
        return (currentPlayer == 1) ? player1 : player2;
    }
    public Player getEnemyPlayer() {
        return (currentPlayer == 1) ? player2 : player1;
    }
    public List<Droid> getCurrentEnemyDroids() {
        return getEnemyPlayer().getDroids();
    }
    public void nextTurn() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    // Атака
    public String attack(Droid attacker, Droid target) {
        if (attacker == null || target == null) return "Немає дроїда або цілі!\n";
        if (!attacker.isAlive()) return attacker.getName() + " не може атакувати (мертвий).\n";

        String destroyedMsg = attacker.attack(target);
        String msg = attacker.getName() + " атакує " + target.getName() + " і завдає " + attacker.getDamage() + " шкоди.\n";

        if (!destroyedMsg.isEmpty()) {
            msg += destroyedMsg;
        }
        return msg;
    }

    // Використання здібності
    public String useAbility(Droid attacker, Droid target) {
        if (attacker == null) return "Немає дроїда!\n";
        if (target == null && !(attacker instanceof types.EngineerDroid)) return "Немає цілі!\n"; // Інженеру ціль не потрібна
        if (!attacker.hasAbility()) return attacker.getName() + " не має здібності!\n";
        if (!attacker.isAlive()) return attacker.getName() + " не може використовувати здібність (мертвий).\n";

        // Отримуємо команди
        Player allies = getCurrentPlayer();
        Player enemies = getEnemyPlayer();

        // Викликаємо useAbility з новою сигнатурою.
        // Java автоматично викличе правильну версію (BattleDroid, SniperDroid, або Droid)
        String abilityResult = attacker.useAbility(target, allies, enemies);

        // Формуємо фінальне повідомлення
        String msg = attacker.getName() + " використав здібність! \n" + abilityResult;

        return msg;
    }

    // Перевірка переможця
    public int checkWinner() {
        if (player1.allDead()) return 2;
        if (player2.allDead()) return 1;
        return 0;
    }
}