package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMAtkMod extends AbstractFunction {
    private static final FuncMAtkMod _fma_instance = new FuncMAtkMod();

    public static AbstractFunction getInstance() {
        return _fma_instance;
    }

    private FuncMAtkMod() {
        super(Stats.MAGIC_ATTACK, 1, null, 0, null);
    }

    @Override
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        // Level Modifier^2 * INT Modifier^2
        double lvlMod = Formulas.INTbonus[effector.getINT()];
        double intMod = effector.isPlayer() ? effector.getLevelMod() : effector.getLevelMod();
        return initVal * Math.pow(lvlMod, 2) * Math.pow(intMod, 2);
    }
}
