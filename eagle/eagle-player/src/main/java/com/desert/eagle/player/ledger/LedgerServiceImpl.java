package com.desert.eagle.player.ledger;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.player.PlayerModel;
import com.desert.eagle.player.PlayerService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Service(LedgerModel.NAME + ".service")
public class LedgerServiceImpl implements LedgerService, OverdueListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private PlayerService playerService;
    @Inject
    private LedgerDao ledgerDao;

    @Override
    public JSONObject query(String uid, String nick, String invitor, String type, String time) {
        Set<String> player = new HashSet<>();
        if (!validator.isEmpty(uid)) {
            PlayerModel model = playerService.find(uid);
            if (model == null)
                return new JSONObject();

            player.add(model.getId());
        } else {
            if (!validator.isEmpty(nick)) {
                player.addAll(userService.ids(null, null, null, nick, null, null, null, null, null));
                if (player.isEmpty())
                    return new JSONObject();
            }

            if (!validator.isEmpty(invitor)) {
                Set<String> set = playerService.subids(invitor);
                if (set.isEmpty())
                    return new JSONObject();

                if (player.isEmpty())
                    player.addAll(set);
                else
                    player.retainAll(set);
            }
        }

        return ledgerDao.query(player, type, time, pagination.getPageSize(20), pagination.getPageNum()).toJson((ledger, object) -> {
            JSONObject p = playerService.get(ledger.getPlayer());
            object.put("uid", p.getString("uid"));
            object.put("player", p);
        });
    }

    @Override
    public JSONObject query(String player, String type, String time) {
        JSONObject p = playerService.get(player);
        String uid = p.getString("uid");

        return ledgerDao.query(Set.of(player), type, time, pagination.getPageSize(20), pagination.getPageNum()).toJson((ledger, object) -> {
            object.put("uid", uid);
            object.put("player", p);
        });
    }

    @Override
    public JSONObject water(String time) {
        return ledgerDao.query(null, "返水", time, pagination.getPageSize(20), pagination.getPageNum()).toJson((ledger, object) -> {
            JSONObject p = playerService.get(ledger.getPlayer());
            object.put("uid", p.getString("uid"));
            object.put("player", p);
        });
    }

    @Override
    public JSONObject user() {
        return ledgerDao.query(Set.of(userService.id()), null, null, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(String player, String type, int balance0, int amount, int balance, String memo) {
        LedgerModel ledger = new LedgerModel();
        ledger.setPlayer(player);
        ledger.setType(type);
        ledger.setBalance0(0);
        ledger.setBalance0(balance0);
        ledger.setAmount(amount);
        ledger.setBalance(balance);
        ledger.setMemo(memo);
        ledger.setTime(dateTime.now());
        ledger.setTimestamp(System.currentTimeMillis());
        ledgerDao.save(ledger);
    }

    @Override
    public void overdue(Timestamp time) {
        ledgerDao.delete(time);
    }
}
