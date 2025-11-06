package Logic;

import base.Droid;
import types.*;
import javax.swing.*;
import java.awt.Image;

public class DroidFactory {

    public static Droid creation(String DroidType){
        switch(DroidType){
            case "BattleDroid":
                BattleDroid battleDroid = new BattleDroid("Silver Johnny",120, 120, 35, true,"Rifle",true,getIcon("BattleDroid",150,150));
                return battleDroid;
            case "EngineerDroid":
                EngineerDroid engineerDroid = new EngineerDroid("S.A.M.",100, 100, 50, true,"Grenade Launcher",true,getIcon("EngineerDroid",150,150));
                return engineerDroid;
            case "GiantDroid":
                GiantDroid giantDroid = new GiantDroid("HULK",250, 250, 15, true,"Iron Fist",true,getIcon("GiantDroid",150,150));
                return giantDroid;
            case "SniperDroid":
                SniperDroid sniperDroid = new SniperDroid("Irishman",50, 50, 99, true,"Sniper Rifle",true,getIcon("SniperDroid",150,150));
                return sniperDroid;
            default:
                // Не можна викликати DroidFactory.creation() знову.
                // Це нескінченна рекурсія, яка зламає програму.
                System.out.println("Invalid Droid Type: " + DroidType);
                return null; // Повертаємо null, якщо тип невірний
        }
        // 'return DroidFactory.creation(DroidType);' звідси прибрано (це була помилка)
    }


    public static ImageIcon getIcon(String type, int width, int height) {
        try {
            // Використовуємо ресурси, а не жорсткий шлях.
            // Тепер іконки будуть працювати на будь-якому комп'ютері,
            // і навіть після компіляції в .jar файл.
            // Переконайся, що папка 'Assets' лежить в 'src'.

            String resourcePath = "/Assets/" + type + ".png";
            java.net.URL imgURL = DroidFactory.class.getResource(resourcePath);

            if (imgURL == null) {
                System.err.println("Не вдалося знайти ресурс: " + resourcePath);
                return new ImageIcon(); // Повертаємо порожню іконку
            }

            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);

        } catch (Exception e) {
            System.out.println("Помилка завантаження іконки для " + type + ": " + e.getMessage());
            return new ImageIcon(); // Повертаємо порожню іконку
        }
    }
}