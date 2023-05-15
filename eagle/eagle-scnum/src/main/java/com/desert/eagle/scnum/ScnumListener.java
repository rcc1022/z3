package com.desert.eagle.scnum;

import com.desert.eagle.game.GameModel;

import java.util.Map;

public interface ScnumListener {
    int newer(ScnumModel scnum);

    void close(GameModel game, ScnumModel scnum);

    Map<String, Long> sumScAmountProfit(long issue);
}
