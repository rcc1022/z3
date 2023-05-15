package com.desert.eagle.player.deposit;

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
import java.sql.Timestamp;
import java.util.Set;

@Repository(DepositModel.NAME + ".dao")
class DepositDaoImpl implements DepositDao {
    @Inject
    private Numeric numeric;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<DepositModel> query(Set<String> player, int status, String submit, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .in("c_player", player)
                .where("c_status", DaoOperation.Equals, status)
                .between("c_submit", ColumnType.Timestamp, submit)
//                .order("c_check,c_audit desc")
                .order("c_check ,c_status ,c_audit desc")
                .query(DepositModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<DepositModel> query(String player, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(DepositModel.class).where("c_player=?")
                .order("c_submit desc").size(pageSize).page(pageNum), new Object[]{player});
    }

    @Override
    public DepositModel findById(String id) {
        return liteOrm.findById(DepositModel.class, id);
    }

    @Override
    public int count(int status) {
        return liteOrm.count(new LiteQuery(DepositModel.class).where("c_status=?"), new Object[]{status});
    }

    @Override
    public DepositModel find(String player, int status) {
        return liteOrm.findOne(new LiteQuery(DepositModel.class).where("c_player=? and c_status=?"), new Object[]{player, status});
    }

    @Override
    public int sum(boolean gift, Timestamp start, Timestamp end, int status) {
        return numeric.toInt(sql.query("select sum(c_amount) from t_player_deposit where c_status=? and c_audit between ? and ? and c_type"
                + (gift ? "=" : "<>") + "?", new Object[]{status, start, end, "补单充值"}).get(0, 0));
    }

    @Override
    public int sum(String player, boolean gift, Timestamp start, Timestamp end, int status) {
        return numeric.toInt(sql.query("select sum(c_amount) from t_player_deposit where c_player=? and c_status=? and c_audit between ? and ? and c_type"
                        + (gift ? "=" : "<>") + "?",
                new Object[]{player, status, start, end, "补单充值"}).get(0, 0));
    }

    @Override
    public void save(DepositModel deposit) {
        liteOrm.save(deposit);
    }

    @Override
    public void delete(Timestamp submit) {
        liteOrm.delete(new LiteQuery(DepositModel.class).where("c_submit<?"), new Object[]{submit});
        liteOrm.close();
    }
}