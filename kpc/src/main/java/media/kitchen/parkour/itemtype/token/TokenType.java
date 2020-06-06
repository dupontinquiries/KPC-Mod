package media.kitchen.parkour.itemtype.token;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;

import java.util.HashMap;


public enum TokenType {
    WARRIOR("kwarr"),
    TANK("ktank"),
    SCOUT("kscou");

    private HashMap<IAttribute, Double> modifiers;
    private String id = "kwarr";

    private TokenType(String s) {
        this.modifiers = new HashMap<>();
        this.id = s;
        setup();
    }

    private void setup() {
        if ( id == "kwarr" ) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE, 0.25D);
            modifiers.put(SharedMonsterAttributes.MAX_HEALTH, -0.15D);
        } else if ( id == "ktank" ) {
            modifiers.put(SharedMonsterAttributes.MAX_HEALTH, 1.0D);
            modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED, -0.0005D);
        } else if ( id == "kscou" ) {
            modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED, 0.0005D);
            modifiers.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, -0.0002D);
        }

    }
    public String getId() {
        return this.id;
    }

    public HashMap<IAttribute, Double> getModifiers() {

        return modifiers;
    }
}
