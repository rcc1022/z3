package com.desert.eagle.player;

import org.lpw.clivia.user.crosier.CrosierGrade;
import org.springframework.stereotype.Service;

@Service(PlayerModel.NAME + ".crosier-grade")
public class CrosierGradeImpl implements CrosierGrade {
    @Override
    public int[] grades() {
        return new int[]{0, 89, 90, 91};
    }

    @Override
    public String name() {
        return "eagle.player.grade.";
    }
}
