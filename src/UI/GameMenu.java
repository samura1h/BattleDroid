package UI;

import javax.swing.*;
import java.awt.*;
import Logic.*;
import base.Droid;

// === НОВІ ІМПОРТИ ===
import java.io.File; // Для роботи з файлами
import java.io.BufferedReader; // Для буферизованого читання з файлу
import java.io.FileReader; // Для читання текстових файлів
import java.io.IOException; // Для обробки помилок вводу/виводу
import javax.swing.JFileChooser; // Для діалогу вибору файлу
import javax.swing.JTextArea; // Для відображення багаторядкового тексту
import javax.swing.JScrollPane; // Для додавання прокрутки до JTextArea
import javax.swing.JOptionPane; // Для показу діалогових вікон (наприклад, з помилками)
// ======================

/**
 * Головний клас меню гри.
 * Створює головне вікно (JFrame) з опціями для початку нової гри (1х1, 2х2, 3х3),
 * відтворення гри з файлу та виходу з програми.
 */
public class GameMenu extends JFrame {

    /**
     * Конструктор класу GameMenu.
     * Ініціалізує та налаштовує всі компоненти головного меню.
     */
    public GameMenu() {

        // --- Налаштування головного вікна ---
        setTitle("Droid Battle Game"); // Заголовок вікна
        setSize(900, 1080); // Розмір вікна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Дія при закритті вікна (вихід)
        setLocationRelativeTo(null); // Центрування вікна на екрані

        // --- Налаштування фону та компонування ---
        setLayout(new BorderLayout()); // Використання BorderLayout для розміщення компонентів
        getContentPane().setBackground(Color.WHITE); // Встановлення білого фону
        // =============================

        // --- Створення заголовку ---
        JLabel title = new JLabel("DROID BATTLE", SwingConstants.CENTER); // Текст з вирівнюванням по центру
        title.setFont(new Font("Arial", Font.BOLD, 28)); // Встановлення шрифту
        add(title, BorderLayout.NORTH); // Додавання заголовку у верхню частину вікна

        // --- Створення панелі для кнопок ---
        // GridLayout: 5 рядків, 1 стовпець, вертикальні та горизонтальні відступи по 15px
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        // Встановлення внутрішніх відступів (top, left, bottom, right)
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
        // Робимо панель прозорою, щоб було видно білий фон вікна
        buttonPanel.setOpaque(false);

        // --- Створення кнопок ---
        JButton soloButton = new JButton("Solo (1x1)");
        JButton battle2x2Button = new JButton("Battle (2x2)");
        JButton battle3x3Button = new JButton("Battle (3x3)");
        // === НОВА КНОПКА ===
        JButton replayButton = new JButton("Відтворити з файлу");
        // ===================
        JButton exitButton = new JButton("Exit");

        // --- Налаштування шрифту для кнопок ---
        Font buttonFont = new Font("Arial", Font.PLAIN, 20);
        soloButton.setFont(buttonFont);
        battle2x2Button.setFont(buttonFont);
        battle3x3Button.setFont(buttonFont);
        replayButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        // --- Додавання кнопок на панель ---
        buttonPanel.add(soloButton);
        buttonPanel.add(battle2x2Button);
        buttonPanel.add(battle3x3Button);
        buttonPanel.add(replayButton);
        buttonPanel.add(exitButton);

        // Додаємо панель кнопок у центр вікна
        add(buttonPanel, BorderLayout.CENTER);

        // --- Додавання обробників подій (слухачів) для кнопок ---
        // Використовуємо лямбда-вирази для короткого запису
        soloButton.addActionListener(e -> openDroidSelection(1)); // Запуск вибору для 1 дроїда
        battle2x2Button.addActionListener(e -> openDroidSelection(2)); // Запуск вибору для 2 дроїдів
        battle3x3Button.addActionListener(e -> openDroidSelection(3)); // Запуск вибору для 3 дроїдів
        // === НОВЕ ===: Обробник подій для нової кнопки
        replayButton.addActionListener(e -> showReplayFromFile()); // Виклик методу показу реплею
        // ============
        exitButton.addActionListener(e -> System.exit(0)); // Завершення роботи програми

        // Робимо вікно видимим
        setVisible(true);
    }

    // === НОВИЙ МЕТОД ===
    /**
     * Відкриває JFileChooser для вибору файлу, зчитує його вміст
     * і відображає у новому вікні JTextArea.
     */
    private void showReplayFromFile() {
        // Створюємо діалог вибору файлу
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Оберіть файл для відтворення");

        // Показуємо діалог і чекаємо на вибір користувача
        // 'this' прив'язує діалог до головного вікна GameMenu
        int result = fileChooser.showOpenDialog(this);

        // Перевіряємо, чи користувач натиснув "Open" (або "OK")
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile(); // Отримуємо обраний файл
            StringBuilder content = new StringBuilder(); // Створюємо StringBuilder для накопичення тексту

            // Використовуємо try-with-resources для автоматичного закриття BufferedReader
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                // Читаємо файл по рядках, доки не досягнемо кінця (readLine() поверне null)
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n"); // Додаємо рядок і символ нового рядка
                }
            } catch (IOException ex) {
                // У випадку помилки читання (напр. файл не знайдено або немає доступу)
                // показуємо діалогове вікно з помилкою
                JOptionPane.showMessageDialog(this,
                        "Помилка читання файлу: " + ex.getMessage(),
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
                return; // Виходимо з методу, якщо сталася помилка
            }

            // --- Створення нового вікна для відображення реплею ---

            // Створюємо текстову область і передаємо їй весь зчитаний вміст
            JTextArea textArea = new JTextArea(content.toString());
            textArea.setEditable(false); // Забороняємо редагування тексту
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Моноширинний шрифт (краще для логів)

            // Додаємо текстову область у JScrollPane, щоб забезпечити прокрутку
            JScrollPane scrollPane = new JScrollPane(textArea);

            // Створюємо нове вікно (JFrame)
            JFrame replayFrame = new JFrame("Перегляд: " + selectedFile.getName());
            replayFrame.setSize(600, 800); // Встановлюємо розмір
            replayFrame.setLocationRelativeTo(this); // Центруємо відносно головного меню
            replayFrame.add(scrollPane); // Додаємо панель прокрутки (з текстом) у вікно
            replayFrame.setVisible(true); // Показуємо вікно
        }
    }
    // ======================

    /**
     * Відкриває нове вікно для вибору дроїдів для обох гравців.
     * @param droidsPerPlayer Кількість дроїдів, яку має обрати кожен гравець (1, 2 або 3).
     */
    private void openDroidSelection(int droidsPerPlayer) {
        // Створюємо нове вікно для вибору
        JFrame selectionFrame = new JFrame("Вибір дроїдів (" + droidsPerPlayer + ")");
        selectionFrame.setSize(950, 650);
        selectionFrame.setLocationRelativeTo(null); // Центруємо
        selectionFrame.setLayout(new BorderLayout());

        // Також робимо фон білим у вікні вибору
        selectionFrame.getContentPane().setBackground(Color.WHITE);

        // Заголовок вікна вибору
        JLabel title = new JLabel("Оберіть дроїдів і задайте імена", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        selectionFrame.add(title, BorderLayout.NORTH);

        // Головна панель, що містить панелі для гравця 1 і гравця 2
        // GridLayout (1 рядок, 2 стовпці) з горизонтальним відступом 30
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setOpaque(false); // Робимо прозорим

        // Масив доступних типів дроїдів (для випадаючих списків)
        String[] droidTypes = {"BattleDroid", "EngineerDroid", "GiantDroid","SniperDroid"};

        // === Player 1 ===
        // Панель для гравця 1, GridLayout (N рядків, 1 стовпець)
        JPanel player1Panel = new JPanel(new GridLayout(droidsPerPlayer, 1, 10, 10));
        player1Panel.setBorder(BorderFactory.createTitledBorder("Player 1")); // Рамка з заголовком
        player1Panel.setOpaque(false); // Прозорий фон

        // Масиви для зберігання посилань на елементи керування (списки та поля)
        JComboBox<String>[] player1Choices = new JComboBox[droidsPerPlayer];
        JTextField[] player1Names = new JTextField[droidsPerPlayer];

        // Створюємо N пар (випадаючий список + текстове поле) для гравця 1
        for (int i = 0; i < droidsPerPlayer; i++) {
            JPanel p = new JPanel(new GridLayout(1, 2, 5, 5)); // Міні-панель для однієї пари
            p.setOpaque(false);
            player1Choices[i] = new JComboBox<>(droidTypes); // Створюємо список
            player1Names[i] = new JTextField("Droid" + (i + 1)); // Створюємо поле з іменем за замовчуванням
            p.add(player1Choices[i]);
            p.add(player1Names[i]);
            player1Panel.add(p); // Додаємо міні-панель до панелі гравця 1
        }

        // === Player 2 ===
        // (Аналогічний код для гравця 2)
        JPanel player2Panel = new JPanel(new GridLayout(droidsPerPlayer, 1, 10, 10));
        player2Panel.setBorder(BorderFactory.createTitledBorder("Player 2"));
        player2Panel.setOpaque(false);
        JComboBox<String>[] player2Choices = new JComboBox[droidsPerPlayer];
        JTextField[] player2Names = new JTextField[droidsPerPlayer];

        for (int i = 0; i < droidsPerPlayer; i++) {
            JPanel p = new JPanel(new GridLayout(1, 2, 5, 5));
            p.setOpaque(false);
            player2Choices[i] = new JComboBox<>(droidTypes);
            player2Names[i] = new JTextField("Droid" + (i + 1));
            p.add(player2Choices[i]);
            p.add(player2Names[i]);
            player2Panel.add(p);
        }

        // Додаємо панелі гравців на головну панель
        mainPanel.add(player1Panel);
        mainPanel.add(player2Panel);
        selectionFrame.add(mainPanel, BorderLayout.CENTER); // Додаємо головну панель у центр вікна

        // --- Кнопка "Start Battle" ---
        JButton startButton = new JButton("Start Battle");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        selectionFrame.add(startButton, BorderLayout.SOUTH); // Додаємо кнопку внизу

        // --- Обробник події для кнопки "Start Battle" ---
        startButton.addActionListener(e -> {
            // Створюємо об'єкти гравців
            Player player1 = new Player("Player 1");
            Player player2 = new Player("Player 2");

            // Проходимо по всіх обраних дроїдах
            for (int i = 0; i < droidsPerPlayer; i++) {
                // Створюємо дроїдів за допомогою DroidFactory на основі вибору зі списків
                Droid d1 = DroidFactory.creation((String) player1Choices[i].getSelectedItem());
                Droid d2 = DroidFactory.creation((String) player2Choices[i].getSelectedItem());

                // Встановлюємо імена дроїдам з текстових полів (видаляємо зайві пробіли)
                d1.setName(player1Names[i].getText().trim());
                d2.setName(player2Names[i].getText().trim());

                // Додаємо дроїдів до відповідних гравців
                player1.addDroid(d1);
                player2.addDroid(d2);
            }

            selectionFrame.dispose(); // Закриваємо вікно вибору дроїдів
            new BattleUI(player1, player2); // Створюємо і запускаємо вікно бою
        });

        // Робимо вікно вибору видимим
        selectionFrame.setVisible(true);
    }

    /**
     * Головний метод (точка входу) програми.
     * @param args Аргументи командного рядка (не використовуються).
     */
    public static void main(String[] args) {
        // Запускає створення GUI у потоці обробки подій (Event Dispatch Thread - EDT).
        // Це є правильною практикою для Swing, щоб уникнути проблем з потокобезпечністю.
        // GameMenu::new - це посилання на конструктор GameMenu.
        SwingUtilities.invokeLater(GameMenu::new);
    }
}