package types;
import base.Droid;

import javax.swing.*;

public class GiantDroid extends Droid {

    public GiantDroid(String name, int maxHealthPoint, int currentHealthPoint, int damage, boolean alive, String weaponType, boolean hasAbility, ImageIcon icon){
        super(name, maxHealthPoint,currentHealthPoint,damage,alive,weaponType,hasAbility,icon);
    }
}
