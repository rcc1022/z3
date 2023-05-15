package com.desert.eagle.player.brokerage;

import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Service(BrokerageModel.NAME + ".service")
public class BrokerageServiceImpl implements BrokerageService {
    @Inject
    private DateTime dateTime;
    @Inject
    private BrokerageDao brokerageDao;

    @Override
    public Map<String, Integer> query(Date date) {
        Map<String, Integer> map = new HashMap<>();
        brokerageDao.query(date).getList().forEach(brokerage -> map.put(brokerage.getPlayer(), brokerage.getAmount()));

        return map;
    }

    @Override
    public void save(String player, int amount) {
        Date today = dateTime.today();
        BrokerageModel brokerage = brokerageDao.find(player, today);
        if (brokerage == null) {
            brokerage = new BrokerageModel();
            brokerage.setPlayer(player);
            brokerage.setDate(today);
        }
        brokerage.setAmount(brokerage.getAmount() + amount);
        brokerageDao.save(brokerage);
    }
}
