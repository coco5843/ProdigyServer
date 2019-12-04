package fr.cocoraid.prodigyserver.minigame.games.battleship;

import net.minecraft.server.v1_13_R2.*;

public class CustomDolphin extends EntityDolphin {


    public CustomDolphin(World w) {
        super(w);
        getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0D);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.3D);

        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
    }

    @Override
    protected void n() {

        this.goalSelector.a(0, new PathfinderGoalWater(this));
        this.goalSelector.a(4, new PathfinderGoalRandomSwim(this, 1.0, 10));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(5, new PathfinderGoalWaterJump(this, 10));
        this.goalSelector.a(6, new PathfinderGoalMeleeAttack(this, 2, true));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));

    }
}
