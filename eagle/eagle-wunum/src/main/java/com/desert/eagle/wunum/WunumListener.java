package com.desert.eagle.wunum;

import com.desert.eagle.game.GameModel;

import java.util.Map;

public interface WunumListener {
    int newer(WunumModel wunum);

    void close(GameModel game, WunumModel wunum);

    Map<String, Long> sumWuAmountProfit(long issue);
}
