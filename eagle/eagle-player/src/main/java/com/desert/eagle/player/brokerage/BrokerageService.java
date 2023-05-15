package com.desert.eagle.player.brokerage;

import java.sql.Date;
import java.util.Map;

public interface BrokerageService {
    Map<String, Integer> query(Date date);

    void save(String player, int amount);
}
