package com.desert.eagle.player.profit;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.jdbc.Sql;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository(ProfitModel.NAME + ".dao")
class ProfitDaoImpl implements ProfitDao {
    @Inject
    private Numeric numeric;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<ProfitModel> query(Set<String> player, String game, String date, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .in("c_player", player)
                .where("c_game", DaoOperation.Equals, game)
                .between("c_date", ColumnType.Date, date)
                .order("c_date desc")
                .query(ProfitModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<ProfitModel> query(String player, String game, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ProfitModel.class).where("c_player=? and c_game=?").order("c_date desc").size(pageSize).page(pageNum), new Object[]{player, game});
    }

    @Override
    public PageList<ProfitModel> query(String player, Date date) {
        return liteOrm.query(new LiteQuery(ProfitModel.class).where("c_player=? and c_date=?"), new Object[]{player, date});
    }

    @Override
    public PageList<ProfitModel> query(Date date) {
        return liteOrm.query(new LiteQuery(ProfitModel.class).where("c_date=?"), new Object[]{date});
    }

    @Override
    public PageList<ProfitModel> queryAllGame(String game, Date date, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ProfitModel.class).where("c_game=? and c_date=?").order("c_amount desc").size(pageSize).page(pageNum), new Object[]{game, date});
    }

    @Override
    public ProfitModel find(String player, String game, Date date) {
        return liteOrm.findOne(new LiteQuery(ProfitModel.class).where("c_player=? and c_game=? and c_date=?"), new Object[]{player, game, date});
    }

    @Override
    public ProfitModel findById(String id) {
        return liteOrm.findById(ProfitModel.class, id);
    }

    @Override
    public PageList<ProfitModel> water(String game) {
        return liteOrm.query(new LiteQuery(ProfitModel.class).where("c_game=? and c_water0>0").order("c_water0 desc"), new Object[]{game});
    }

    @Override
    public Map<String, int[]> water(String game, Date start, Date end) {
        Map<String, int[]> map = new HashMap<>();
        sql.query("select c_player,sum(c_water),sum(c_water0) from t_player_profit where c_game=? and c_date between ? and ? group by c_player",
                new Object[]{game, start, end}).forEach(list -> map.put((String) list.get(0), new int[]{numeric.toInt(list.get(1)), numeric.toInt(list.get(2))}));

        return map;
    }

    @Override
    public void save(ProfitModel profit) {
        liteOrm.save(profit);
        liteOrm.close();
    }

    @Override
    public void delete(Date date) {
        liteOrm.delete(new LiteQuery(ProfitModel.class).where("c_date<?"), new Object[]{date});
        liteOrm.close();
    }
}