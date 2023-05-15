package com.desert.eagle.player.commission;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.player.PlayerModel;
import com.desert.eagle.player.PlayerService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(CommissionModel.NAME + ".service")
public class CommissionServiceImpl implements CommissionService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private PlayerService playerService;
    @Inject
    private CommissionDao commissionDao;

    @Override
    public JSONObject query() {
        return commissionDao.query(pagination.getPageSize(20), pagination.getPageNum())
                .toJson((commission, object) -> object.put("player", playerService.get(commission.getPlayer())));
    }

    @Override
    public void save(String player, int amount) {
        CommissionModel commission = new CommissionModel();
        commission.setPlayer(player);
        commission.setAmount(amount);
        commission.setSubmit(dateTime.now());
        commissionDao.save(commission);
    }

    @Override
    public void pass(String id) {
        CommissionModel commission = commissionDao.findById(id);
        if (id == null)
            return;

        commission.setStatus(1);
        commission.setAudit(dateTime.now());
        commissionDao.save(commission);
        playerService.transfer(commission.getPlayer(), commission.getAmount(), true);
    }

    @Override
    public void reject(String id) {
        CommissionModel commission = commissionDao.findById(id);
        if (id == null)
            return;

        commission.setStatus(2);
        commission.setAudit(dateTime.now());
        commissionDao.save(commission);
        playerService.transfer(commission.getPlayer(), commission.getAmount(), false);
    }
}
