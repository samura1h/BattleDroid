package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage; // Потрібен для віддзеркалення іконок
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter; // Для запису у файл
import java.io.PrintWriter; // Для зручного запису тексту у файл
import java.io.IOException; // Для обробки помилок при роботі з файлами

import base.Droid; // Базовий клас дроїда
import Logic.Player; // Клас гравця
import Logic.BattleLogic; // Клас, що керує логікою бою
import types.EngineerDroid; // для перевірки анімації

/**
 * BattleUI - це головний клас, що відповідає за вікно бою.
 * Він успадковує JFrame (тобто, сам є вікном) і відображає:
 * - Панелі гравців з їхніми дроїдами.
 * - Лог бою (JTextArea).
 * - Кнопки дій (Атака, Здібність).
 * - Інформацію про поточний раунд та селектори вибору.
 * Він також керує логікою анімації та оновленням стану гри.
 */
public class BattleUI extends JFrame {

    // --- Поля Класу ---
    private BattleLogic battleLogic; // Об'єкт, що керує всією логікою бою (ходи, атаки, переможці)
    private JTextArea logArea; // Текстове поле для виведення логів бою

    // Тепер є два селектори ===
    private JComboBox<String> attackerSelector; // Випадаючий список для вибору АТАКУЮЧОГО дроїда
    private JComboBox<String> targetSelector; // Випадаючий список для вибору ЦІЛІ (ворожого дроїда)
    // ==================================

    private JButton attackButton, abilityButton; // Кнопки для дій
    private JPanel player1Panel, player2Panel; // Панелі, що містять дроїдів гравця 1 та 2
    private JPanel centerPanel; // Головна панель, де розміщуються панелі гравців та анімації

    // Карта (словник) для зв'язування об'єкта Droid з його візуальною JPanel
    // Це потрібно, щоб знати, яку панель анімувати або оновити
    private Map<Droid, JPanel> droidPanelMap;

    private JLabel roundLabel; // Напис, що показує поточний раунд ("Round 1")
    private int roundCount = 1; // Лічильник раундів

    // Карта для зв'язування об'єкта Droid з його JLabel "статусу"
    // (де з'являється "Attacking...")
    private Map<Droid, JLabel> droidStatusLabelMap;

    // Об'єкт для запису логів бою у файл ("battle_log.txt")
    private PrintWriter logWriter;

    // --- Константи для розміщення ---
    // Використання констант робить код чистішим і легшим для редагування розмірів
    private static final int PLAYER_PANEL_WIDTH = 400; // Ширина зони одного гравця
    private static final int DROID_PANEL_WIDTH = 160;  // Ширина картки одного дроїда
    private static final int DROID_PANEL_HEIGHT = 180; // Висота картки одного дроїда

    // Розрахунок відступу дроїда по X, щоб він був по центру своєї панелі
    private static final int DROID_X_OFFSET = (PLAYER_PANEL_WIDTH - DROID_PANEL_WIDTH) / 2; // = 120

    // Координати X для написів "Player 1" та "Player 2"
    // 50 - відступ player1Panel, 450 - відступ player2Panel
    private static final int P1_LABEL_X = 50 + DROID_X_OFFSET; // 50 + 120 = 170
    private static final int P2_LABEL_X = 450 + DROID_X_OFFSET; // 450 + 120 = 570
    // Залишаємо 50px згори для написів "Player 1" / "Player 2"
    private static final int LABELS_Y_OFFSET = 50;

    // --- Конструктор Класу ---
    /**
     * Конструктор BattleUI.
     * Ініціалізує всі компоненти, налаштовує вікно та запускає логіку.
     * @param p1 Об'єкт гравця 1 (з його набором дроїдів)
     * @param p2 Об'єкт гравця 2 (з його набором дроїдів)
     */

    public BattleUI(Player p1, Player p2) {
        // Ініціалізація полів
        this.battleLogic = new BattleLogic(p1, p2);
        this.droidPanelMap = new HashMap<>();
        this.droidStatusLabelMap = new HashMap<>();

        // Ініціалізація логера файлу
        try {
            // Створюємо FileWriter (дозволяє запис) та PrintWriter (дає зручні методи як println)
            // `true` в FileWriter означає "autoflush" - дані записуються одразу, не чекаючи закриття
            logWriter = new PrintWriter(new FileWriter("battle_log.txt"), true);
            logWriter.println("=== Нова гра розпочата ===");
        } catch (IOException e) {
            // Обробка помилки, якщо файл не можна створити (напр. немає прав доступу)
            System.err.println("Помилка при створенні файлу логу: " + e.getMessage());
            logWriter = null; // Встановлюємо в null, щоб програма не впала при спробі запису
        }

        // --- 3. Налаштування головного вікна (JFrame) ---
        setTitle("Droid Battle");
        setSize(900, 800); // Розмір вікна

        // Спеціальна обробка закриття вікна (коли юзер тисне [X])
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Ми перехоплюємо закриття самі
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Цей код виконається, коли користувач натисне [X]
                if (logWriter != null) {
                    logWriter.println("\n--- Гра закрита достроково ---");
                    logWriter.close(); // ВАЖЛИВО: Закрити потік, щоб файл зберігся
                }
                dispose(); // Закрити це вікно
                System.exit(0); // Повністю зупинити програму (бо GameMenu вже закрите)
            }
        });

        setLocationRelativeTo(null); // Центрувати вікно на екрані
        setLayout(new BorderLayout()); // Використовуємо BorderLayout (NORTH, SOUTH, CENTER)

        // === 4. Верхня Панель (Раунд + Вибір цілі/атакера) ===
        // Ця панель сама використовує BorderLayout для розміщення напису раунду і панелі вибору
        JPanel topPanel = new JPanel(new BorderLayout());

        // Напис раунду (буде у BorderLayout.NORTH)
        roundLabel = new JLabel("Round " + roundCount, SwingConstants.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(roundLabel, BorderLayout.NORTH);

        // === ЗМІНЕНО: Панель вибору (буде у BorderLayout.CENTER) ===
        // Створюємо головну панель для обох селекторів
        // GridLayout(1, 2) - 1 рядок, 2 колонки. Горизонтальний відступ 20px.
        JPanel selectionPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // 4a. Панель вибору АТАКЕРА (ліва колонка)
        JPanel attackerPanel = new JPanel(new FlowLayout()); // FlowLayout просто розміщує елементи в ряд
        attackerPanel.add(new JLabel("Вибери дроїда:")); // Напис
        attackerSelector = new JComboBox<>(); // Ініціалізуємо порожній список
        attackerPanel.add(attackerSelector); // Додаємо список
        selectionPanel.add(attackerPanel); // Додаємо панель атакера в першу колонку (ліворуч)

        // 4b. Панель вибору ЦІЛІ (права колонка)
        JPanel targetPanel = new JPanel(new FlowLayout());
        targetPanel.add(new JLabel("Вибери ціль:")); // Напис
        targetSelector = new JComboBox<>(); // Ініціалізуємо порожній список
        targetPanel.add(targetSelector); // Додаємо список
        selectionPanel.add(targetPanel); // Додаємо панель цілі в другу колонку (праворуч)

        // Додаємо панель з обома селекторами у центр верхньої панелі
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        // =================================

        // === 5. Центральна Панель (Де стоять дроїди + НАПИСИ) ===
        centerPanel = new JPanel(null); // `null` layout означає ручне розміщення (Absolute Layout)
        centerPanel.setBackground(Color.WHITE); // Білий фон, на якому стоять дроїди

        Font playerLabelFont = new Font("Arial", Font.BOLD, 16);

        // Напис "Player 1"
        JLabel p1Label = new JLabel("Player 1", SwingConstants.CENTER);
        p1Label.setFont(playerLabelFont);
        p1Label.setForeground(Color.BLACK);
        // setBounds(x, y, width, height) - ручне позиціонування
        p1Label.setBounds(P1_LABEL_X, 10, DROID_PANEL_WIDTH, 30); // Використовуємо константи
        centerPanel.add(p1Label);

        // Напис "Player 2"
        JLabel p2Label = new JLabel("Player 2", SwingConstants.CENTER);
        p2Label.setFont(playerLabelFont);
        p2Label.setForeground(Color.BLACK);
        p2Label.setBounds(P2_LABEL_X, 10, DROID_PANEL_WIDTH, 30); // Використовуємо константи
        centerPanel.add(p2Label);

        // Панель для дроїдів Гравця 1 (також з `null` layout, бо дроїди на ній ставимо вручну)
        player1Panel = new JPanel(null);
        player1Panel.setOpaque(false); // Робимо прозорою, щоб було видно білий фон centerPanel
        player1Panel.setBounds(50, LABELS_Y_OFFSET, PLAYER_PANEL_WIDTH, 550); // Початкові розміри
        centerPanel.add(player1Panel);

        // Панель для дроїдів Гравця 2
        player2Panel = new JPanel(null);
        player2Panel.setOpaque(false);
        player2Panel.setBounds(450, LABELS_Y_OFFSET, PLAYER_PANEL_WIDTH, 550); // Початкові розміри
        centerPanel.add(player2Panel);

        // === 6. Нижня Панель (Кнопки + Лог) ===

        // Кнопки
        attackButton = new JButton("Attack");
        abilityButton = new JButton("Ability");

        // Панель для кнопок (FlowLayout - просто ставить їх в ряд)
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.add(attackButton);
        actionPanel.add(abilityButton);

        // Текстове поле логу
        logArea = new JTextArea(5, 40); // 5 рядків, 40 стовпців (приблизно)
        logArea.setEditable(false); // Користувач не може в ньому писати
        // Додаємо лог у JScrollPane, щоб з'явилась вертикальна прокрутка
        JScrollPane scrollPane = new JScrollPane(logArea);

        // Контейнер для нижньої частини, що містить і кнопки, і лог
        // Використовує BorderLayout
        JPanel bottomContainerPanel = new JPanel(new BorderLayout());
        bottomContainerPanel.add(actionPanel, BorderLayout.NORTH); // Кнопки зверху
        bottomContainerPanel.add(scrollPane, BorderLayout.CENTER); // Лог знизу

        // === 7. Додавання всіх головних панелей у вікно ===
        add(topPanel, BorderLayout.NORTH); // Верхня панель (раунд, селектори)
        add(centerPanel, BorderLayout.CENTER); // Центральна панель (дроїди)
        add(bottomContainerPanel, BorderLayout.SOUTH); // Нижня панель (кнопки, лог)

        // --- 8. Перший запуск гри ---
        updateDroidPanels(); // Розміщуємо дроїдів на панелях
        updateAttackerList(); // <-- НОВЕ: Заповнюємо список атакерів (дроїди гравця 1)
        updateTargetList(); // Заповнюємо список цілей (дроїди гравця 2)
        appendToLog("--- Хід гравця " + battleLogic.getCurrentPlayer().getName() + " ---\n");

        // === 9. Обробники подій (Listeners) ===
        // "Підключаємо" кнопки до методу doAction
        attackButton.addActionListener(e -> doAction("attack"));
        abilityButton.addActionListener(e -> doAction("ability"));

        // Робимо вікно видимим (завжди в кінці конструктора, після додавання всіх елементів)
        setVisible(true);
    }

    // === НОВИЙ МЕТОД ===
    /**
     * Допоміжний метод для логування.
     * Додає повідомлення одночасно і в JTextArea (лог на екрані),
     * і в PrintWriter (лог у файлі).
     * @param message Повідомлення для логування
     */
    private void appendToLog(String message) {
        logArea.append(message); // Додати в текстове поле на екрані
        if (logWriter != null) {
            // Якщо логер ініціалізований (не було помилки при створенні)
            logWriter.print(message); // Додати в файл "battle_log.txt"
        }
    }

    // --- Головні Методи ---

    /**
     * Головний метод, що запускається при натисканні "Attack" або "Ability".
     * Він збирає дані, запускає анімацію, запускає логіку і оновлює UI.
     * @param actionType Рядок ("attack" або "ability"), що вказує на тип дії.
     */
    private void doAction(String actionType) {

        // === ЗМІНЕНО: Отримуємо атакера і ціль з нових селекторів ===
        Droid attacker = getAttackerDroid(); // Хто атакує (визначаємо за attackerSelector)
        Droid target = getTargetDroid(); // По кому атакує (визначаємо за targetSelector)
        // =========================================

        // --- Перевірки вводу ---
        if (attacker == null) {
            // Це може статись, якщо список атакерів порожній (всі померли)
            appendToLog("Виберіть дроїда для дії!\n");
            return; // Вийти з методу, нічого не роблячи
        }

        // Перевірка на ціль. `!(attacker instanceof EngineerDroid && actionType.equals("ability"))`
        // Це складна умова, що означає: "Якщо ціль потрібна".
        // Ціль НЕ потрібна, лише якщо це Інженер (EngineerDroid) і він використовує здібність (лікує).
        if (target == null && !(attacker instanceof EngineerDroid && actionType.equals("ability"))) {
            appendToLog("Немає доступних цілей! (Або оберіть ціль)\n");
            return;
        }
        // --- Кінець перевірок ---

// Позначаємо, що дроїd діє (візуально)
        JLabel statusLabel = droidStatusLabelMap.get(attacker);
        if (statusLabel != null) {

            // === НОВА ПЕРЕВІРКА ===
            // Якщо це інженер і він використовує здібність (лікування)
            if (actionType.equals("ability") && attacker instanceof EngineerDroid) {

                statusLabel.setText("Healing..."); // Встановлюємо "Healing..."
                statusLabel.setForeground(new Color(0, 150, 0)); // Встановлюємо зелений колір

            } else {

                // Для всіх інших дій (атака, здібності інших дроїдів)
                statusLabel.setText("Attacking..."); // Встановлюємо "Attacking..."
                statusLabel.setForeground(Color.RED); // Встановлюємо червоний колір
            }
            // ======================
        }

        // === ЗМІНЕНО: Передаємо тип дії в анімацію ===
        // Тепер анімація знатиме, чи це атака (куля), чи здібність (лікування)
        animateAction(attacker, target, actionType);
        // ==========================================

        // Виконуємо саму логіку бою (розрахунок шкоди, лікування тощо)
        // Використовуємо тернарний оператор для виклику потрібного методу
        String result = actionType.equals("attack")
                ? battleLogic.attack(attacker, target) // Якщо "attack", викликати це
                : battleLogic.useAbility(attacker, target); // Інакше (це "ability"), викликати це

        // (Для дебагу в консолі)
        System.out.println(result);

        // --- Таймер для затримки ---
        // Ми запускаємо логіку оновлення гри з затримкою в 400 мс.
        // Це потрібно, щоб користувач ВСТИГ побачити анімацію (яка триває ~300 мс).
        // Без таймера HP-бар оновиться миттєво, до анімації.
        Timer logicTimer = new Timer(400, e -> {

            // Цей код (всередині лямбди) виконається через 400 мс

            appendToLog(result + "\n"); // 1. Показати результат бою в лозі
            updateDroidPanels(); // 2. Оновити HP-бари (перемалювати панелі дроїдів)
            checkWinner(); // 3. Перевірити, чи хтось не переміг

            // 4. Якщо гра триває (переможця немає)...
            if (battleLogic.checkWinner() == 0) {
                battleLogic.nextTurn(); // 5. Передати хід наступному гравцю

                // 6. Перевірка на новий раунд
                // Якщо поточний гравець - це Гравець 1, значить, почався новий раунд
                if (battleLogic.getCurrentPlayer() == battleLogic.getPlayer1()) {
                    roundCount++;
                    roundLabel.setText("Round " + roundCount);
                }

                // === ЗМІНЕНО: Оновлюємо ОБИДВА списки ===
                updateAttackerList(); // 7. Оновити список атакерів (тепер це будуть дроїди іншого гравця)
                updateTargetList(); // 8. Оновити список цілей (тепер це будуть вороги для нового гравця)
                // =====================================

                // 9. Повідомити про початок нового ходу
                appendToLog("\n--- Хід гравця " + battleLogic.getCurrentPlayer().getName() + " ---\n");
            }
        });
        logicTimer.setRepeats(false); // Виконати лише один раз
        logicTimer.start(); // Запустити таймер
    }

    /**
     * (!!!) МЕТОД ПЕРЕЙМЕНОВАНО ТА ОНОВЛЕНО (!!!)
     * Тепер називається animateAction і обробляє різні анімації.
     * Запускає візуальну анімацію (куля або лікування).
     * @param attacker Хто атакує
     * @param target Ціль (може бути null для лікування)
     * @param actionType "attack" чи "ability"
     */
    private void animateAction(Droid attacker, Droid target, String actionType) {
        // 1. Знаходимо візуальну панель нападника
        JPanel attackerPanel = droidPanelMap.get(attacker);
        if (attackerPanel == null) { return; } // Якщо дроїда немає на карті (помилка), виходимо

        // 2. Перевіряємо, чи це інженер використовує здібність
        // (саме ця комбінація запускає анімацію лікування)
        if (actionType.equals("ability") && attacker instanceof EngineerDroid) {

            // --- Анімація ЛІКУВАННЯ (Зелений Плюс) ---

            // Отримуємо координати центру нападника (Інженера)
            // Важливо: SwingUtilities.convertPoint перераховує координати
            // з системи координат attackerPanel (напр. [80, 90])
            // в систему координат centerPanel (напр. [200, 300])
            Point healPoint = SwingUtilities.convertPoint(
                    attackerPanel, // Звідки
                    new Point(attackerPanel.getWidth() / 2, attackerPanel.getHeight() / 2), // Точка (центр)
                    centerPanel // Куди
            );

            // Створюємо зелений "+" (JLabel)
            JLabel healPlus = new JLabel("+");
            healPlus.setForeground(new Color(0, 150, 0)); // Темно-зелений
            healPlus.setFont(new Font("Arial", Font.BOLD, 28));
            // Ставимо "+" трохи вище центру дроїда
            healPlus.setBounds(healPoint.x - 10, healPoint.y - 30, 30, 30);

            centerPanel.add(healPlus); // Додаємо "+" на головну панель
            // setComponentZOrder(component, 0) - переміщує компонент на передній план
            centerPanel.setComponentZOrder(healPlus, 0);
            centerPanel.repaint(); // Оновлюємо панель, щоб показати "+"

            // Таймер, щоб прибрати "+" через 500 мс
            Timer healTimer = new Timer(500, e -> {
                centerPanel.remove(healPlus); // Видаляємо "+"
                centerPanel.repaint(); // Оновлюємо панель
            });
            healTimer.setRepeats(false); // Одноразовий
            healTimer.start();

        } else if (target != null) {
            // У всіх інших випадках (звичайна атака АБО здібність, що б'є)
            // і якщо ціль існує, запускаємо анімацію АТАКИ (червона куля)

            // --- Анімація АТАКИ (Червона Куля) ---

            // Знаходимо панель цілі
            JPanel targetPanel = droidPanelMap.get(target);
            if (targetPanel == null) { return; } // Якщо цілі немає, виходимо

            // 1. Визначаємо початкову (центр атакера) і кінцеву (центр цілі) точки
            Point startPoint = SwingUtilities.convertPoint(
                    attackerPanel, new Point(attackerPanel.getWidth() / 2, attackerPanel.getHeight() / 2), centerPanel);
            Point endPoint = SwingUtilities.convertPoint(
                    targetPanel, new Point(targetPanel.getWidth() / 2, targetPanel.getHeight() / 2), centerPanel);

            // 2. Створюємо "кулю" (просто червоний JLabel 10x10)
            JLabel projectile = new JLabel();
            projectile.setOpaque(true); // Дозволяємо фону бути видимим
            projectile.setBackground(Color.RED);
            projectile.setBounds(startPoint.x, startPoint.y, 10, 10); // Початкова позиція

            // 3. Додаємо кулю на панель
            centerPanel.add(projectile);
            centerPanel.setComponentZOrder(projectile, 0); // На передній план
            centerPanel.repaint();

            // 4. Налаштування анімації
            int duration = 300; // Загальна тривалість (300 мс)
            int steps = 20; // Кількість кроків (кадрів) анімації
            int delay = duration / steps; // Затримка між кроками (300 / 20 = 15 мс)

            // Розраховуємо, на скільки зміщувати кулю по X та Y за один крок
            double dx = (endPoint.x - startPoint.x) / (double)steps;
            double dy = (endPoint.y - startPoint.y) / (double)steps;

            // 5. Створюємо таймер анімації (Swing Timer)
            Timer timer = new Timer(delay, null); // `null` бо ми додамо listener нижче
            // Використовуємо масив {0}, щоб можна було змінити його з лямбди
            // (змінні в лямбді мають бути "effectively final")
            final int[] currentStep = {0};

            timer.addActionListener(e -> {
                currentStep[0]++; // Наступний крок анімації

                if (currentStep[0] >= steps) {
                    // Анімація завершена
                    centerPanel.remove(projectile); // Видаляємо кулю
                    timer.stop(); // Зупиняємо таймер
                    centerPanel.repaint();
                } else {
                    // Анімація в процесі
                    // Розраховуємо нову позицію кулі
                    int x = (int)(startPoint.x + currentStep[0] * dx);
                    int y = (int)(startPoint.y + currentStep[0] * dy);
                    projectile.setBounds(x, y, 10, 10); // Оновлюємо позицію кулі
                    centerPanel.repaint(); // Перемальовуємо панель
                }
            });
            timer.start(); // Запускаємо анімацію
        }
    }

    // --- Допоміжні Методи ---

    // === НОВИЙ МЕТОД ===
    /**
     * Оновлює випадаючий список АТАКЕРІВ (attackerSelector).
     * Отримує список дроїдів ПОТОЧНОГО гравця, що ходить, і додає живих у список.
     */
    private void updateAttackerList() {
        attackerSelector.removeAllItems(); // Повністю очищуємо старий список

        // Отримуємо дроїдів поточного гравця з логіки
        List<Droid> allies = battleLogic.getCurrentPlayer().getDroids();

        // Додаємо в список лише живих
        for (Droid d : allies) {
            if (d.isAlive()) {
                attackerSelector.addItem(d.getName()); // Додаємо ім'я
            }
        }
    }

    // === НОВИЙ МЕТОД ===
    /**
     * Повертає об'єкт Droid, який наразі обраний у attackerSelector.
     * @return Об'єкт Droid або null, якщо нічого не обрано.
     */
    private Droid getAttackerDroid() {
        // Отримуємо вибране ім'я (String) зі списку
        String selected = (String) attackerSelector.getSelectedItem();
        if (selected == null) return null; // Якщо список порожній або нічого не обрано

        // Шукаємо дроїда з таким іменем у поточного гравця
        for (Droid d : battleLogic.getCurrentPlayer().getDroids()) {
            if (d.getName().equals(selected)) {
                return d; // Знайшли дроїда за іменем
            }
        }
        return null; // На практиці не має статись, якщо список оновлюється вірно
    }

    /**
     * Оновлює випадаючий список ЦІЛЕЙ (targetSelector).
     * Отримує список дроїдів ВОРОЖОГО гравця і додає живих у список.
     */
    private void updateTargetList() {
        targetSelector.removeAllItems(); // Очищуємо список

        // Отримуємо живих ворогів з логіки
        List<Droid> enemies = battleLogic.getCurrentEnemyDroids();

        // Додаємо в список
        for (Droid d : enemies) {
            if (d.isAlive()) { // (Метод getCurrentEnemyDroids вже мав би повернути живих, але це дод. перевірка)
                targetSelector.addItem(d.getName()); // Додаємо ім'я в список
            }
        }
    }

    /**
     * Повертає об'єкт Droid, який наразі обраний у targetSelector.
     * @return Об'єкт Droid або null, якщо нічого не обрано.
     */
    private Droid getTargetDroid() {
        // Отримуємо вибране ім'я
        String selected = (String) targetSelector.getSelectedItem();
        if (selected == null) return null; // Якщо список порожній (напр. всі вороги мертві)

        // Шукаємо дроїда з таким іменем серед ворогів
        for (Droid d : battleLogic.getCurrentEnemyDroids()) {
            if (d.getName().equals(selected)) {
                return d; // Знайшли
            }
        }
        return null;
    }


    /**
     * Перевіряє, чи є переможець.
     * Якщо так, вимикає кнопки, показує діалогове вікно і закриває вікно бою.
     */
    private void checkWinner() {
        int winner = battleLogic.checkWinner(); // 0 = гра триває, 1 = P1, 2 = P2
        if (winner != 0) {
            // Гра закінчена
            attackButton.setEnabled(false); // Вимикаємо кнопки
            abilityButton.setEnabled(false);

            if (logWriter != null) {
                // Записуємо фінальне повідомлення в лог
                logWriter.println("\n--- Кінець гри. Переміг Player " + winner + " ---");
                logWriter.close(); // Закриваємо файл
                logWriter = null; // Обнуляємо, щоб уникнути подальших спроб запису
            }

            // Показуємо спливаюче вікно з поздоровленням
            JOptionPane.showMessageDialog(this, "🏆 Переміг Player " + winner + "!");
            // `this` прив'язує вікно до BattleUI

            dispose(); // Закриваємо вікно бою
            // (Програма завершить роботу, оскільки GameMenu було закрите)
        }
    }

    /**
     * Повністю оновлює (перемальовує) панелі з дроїдами.
     * Викликається на початку гри та після кожної дії (в таймері).
     */
    private void updateDroidPanels() {
        // 1. Очищуємо старі дані
        droidPanelMap.clear(); // Більше не посилаємось на старі JPanel
        droidStatusLabelMap.clear(); // І старі JLabel
        player1Panel.removeAll(); // Видаляємо всі компоненти з панелі
        player2Panel.removeAll();

        // 2. Динамічний розрахунок висоти (на випадок зміни розміру вікна)
        int availableHeight = centerPanel.getHeight();
        // Гарантуємо, що висота достатня хоча б для 3 дроїдів, навіть якщо вікно замале
        if (availableHeight < (DROID_PANEL_HEIGHT * 3 + LABELS_Y_OFFSET)) {
            availableHeight = DROID_PANEL_HEIGHT * 3 + LABELS_Y_OFFSET;
        }

        // 3. Оновлюємо розміри панелей гравців
        player1Panel.setBounds(50, LABELS_Y_OFFSET, PLAYER_PANEL_WIDTH, availableHeight - LABELS_Y_OFFSET);
        player2Panel.setBounds(450, LABELS_Y_OFFSET, PLAYER_PANEL_WIDTH, availableHeight - LABELS_Y_OFFSET);

        // 4. Розміщуємо дроїдів на панелях
        // `false` - не віддзеркалювати (Гравець 1)
        placeDroids(player1Panel, battleLogic.getPlayer1().getDroids(), false, player1Panel.getHeight());
        // `true` - віддзеркалити (Гравець 2)
        placeDroids(player2Panel, battleLogic.getPlayer2().getDroids(), true, player2Panel.getHeight());

        // 5. Оновлюємо Swing-компоненти
        player1Panel.revalidate(); // Перераховуємо layout
        player2Panel.revalidate();
        centerPanel.repaint(); // Перемальовуємо головну панель
    }

    /**
     * Допоміжний метод, що розміщує дроїдів на панелі (player1Panel або player2Panel).
     * @param panel Панель, куди додавати (p1Panel або p2Panel)
     * @param droids Список дроїдів
     * @param flip Чи потрібно віддзеркалити іконку (true для гравця 2)
     * @param panelHeight Висота панелі для розрахунку позицій
     */
    private void placeDroids(JPanel panel, List<Droid> droids, boolean flip, int panelHeight) {
        // Розрахунок позицій:
        // Дроїди "ростуть" знизу вгору.
        int startY = panelHeight - DROID_PANEL_HEIGHT; // Позиція Y для першого дроїда (знизу)
        int spacing = DROID_PANEL_HEIGHT; // Відстань між дроїдами (дорівнює висоті дроїда)

        int x_coord = DROID_X_OFFSET; // X-координата (однакова для всіх, по центру)

        for (int i = 0; i < droids.size(); i++) {
            Droid d = droids.get(i);
            // Створюємо візуальну панель для дроїда (з HP, іменем...)
            JPanel droidPanel = createDroidPanel(d, flip);

            // Встановлюємо позицію (x, y, width, height)
            // startY - i * spacing:
            // i=0: startY (низ)
            // i=1: startY - spacing (вище)
            // i=2: startY - 2*spacing (ще вище)
            droidPanel.setBounds(x_coord, startY - i * spacing, DROID_PANEL_WIDTH, DROID_PANEL_HEIGHT);
            panel.add(droidPanel); // Додаємо на панель гравця

            // Зберігаємо посилання на панель, щоб мати до неї доступ при анімації/оновленні
            droidPanelMap.put(d, droidPanel);
        }
    }

    /**
     * Створює індивідуальну JPanel для одного дроїда (з іконкою, HP-баром, іменем).
     * @param d Дроїд, для якого створюється панель
     * @param flip Чи потрібно віддзеркалити іконку
     * @return Готова JPanel
     */
    private JPanel createDroidPanel(Droid d, boolean flip) {
        // Панель використовує BorderLayout (NORTH, CENTER, SOUTH)
        JPanel panel = new JPanel(new BorderLayout(0, 2)); // 0_Hgap, 2_Vgap
        panel.setOpaque(false); // Прозорий фон

        // === NORTH: Інформація (Ім'я + Статус) ===
        JPanel topInfoPanel = new JPanel(new BorderLayout());
        topInfoPanel.setOpaque(false);

        // Ім'я (в центрі)
        JLabel nameLabel = new JLabel(d.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(Color.BLACK);
        topInfoPanel.add(nameLabel, BorderLayout.CENTER);

        // Статус (праворуч, напр. "Attacking...")
        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER); // Початково " " (пробіл для розміру)
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 10));
        topInfoPanel.add(statusLabel, BorderLayout.EAST);

        // Зберігаємо посилання на statusLabel, щоб оновити його пізніше
        droidStatusLabelMap.put(d, statusLabel);

        // === CENTER: Іконка ===
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (d.getIcon() != null) {
            // Якщо іконка є, використовуємо її
            // `flip ? flipIcon(...) : d.getIcon()` - тернарний оператор:
            // якщо flip=true, викликати flipIcon, інакше взяти звичайну іконку.
            iconLabel.setIcon(flip ? flipIcon(d.getIcon()) : d.getIcon());
        }

        // === SOUTH: HP-бар ===
        JProgressBar hpBar = new JProgressBar(0, d.getMaxHealthPoint()); // min=0, max=maxHP
        hpBar.setValue(d.getCurrentHealthPoint()); // Поточне здоров'я
        hpBar.setStringPainted(true); // Показувати текст (напр. "50/100")
        hpBar.setForeground(Color.GREEN); // Колір заповнення (здоров'я)
        hpBar.setBackground(Color.RED); // Колір фону (втрачене здоров'я)

        // Додаємо частини на головну панель дроїда
        panel.add(topInfoPanel, BorderLayout.NORTH);
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(hpBar, BorderLayout.SOUTH);

        // Якщо дроїд мертвий, робимо його "сірим"
        if (!d.isAlive()) {
            hpBar.setForeground(Color.DARK_GRAY); // HP-бар стає сірим
            nameLabel.setForeground(Color.GRAY); // Ім'я стає сірим
        }

        return panel;
    }

    /**
     * Допоміжний метод для графічного віддзеркалення іконки по горизонталі.
     * Використовується для дроїдів гравця 2, щоб вони "дивились" на гравця 1.
     * @param icon Оригінальна іконка
     * @return Віддзеркалена іконка
     */
    private ImageIcon flipIcon(ImageIcon icon) {
        Image img = icon.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        // Створюємо нове пусте зображення (BufferedImage) з підтримкою прозорості (ARGB)
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // Отримуємо "художника" (Graphics2D) для цього зображення
        Graphics2D g2 = flipped.createGraphics();

        // Малюємо оригінальне зображення на новому, але з трюком:
        // g2.drawImage(img, x, y, width, height, null);
        // x = w (починаємо малювати з правого краю)
        // width = -w (малюємо у зворотній бік, тобто віддзеркалюємо)
        g2.drawImage(img, w, 0, -w, h, null);

        g2.dispose(); // Звільняємо ресурси "художника"
        return new ImageIcon(flipped); // Повертаємо нову іконку, створену з віддзеркаленого зображення
    }
}