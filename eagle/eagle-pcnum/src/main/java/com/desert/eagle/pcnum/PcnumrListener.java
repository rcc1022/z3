package com.desert.eagle.pcnum;

import com.desert.eagle.game.GameModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PcnumrListener {
    int newer(PcnumModel pcnum);

    void close(GameModel game, PcnumModel pcnum);

    List<PcnumBet> sumPcAmountProfit(long issue);
}
