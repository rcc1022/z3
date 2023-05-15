package com.desert.eagle.player.withdraw;

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

@Repository(WithdrawModel.NAME + ".dao")
class WithdrawDaoImpl implements WithdrawDao {
    @Inject
    private Numeric numeric;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<WithdrawModel> query(String player, int type, int status, String submit, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_player", DaoOperation.Equals, player)
                .where("c_type", DaoOperation.Equals, type)
                .where("c_status", DaoOperation.Equals, status)
                .between("c_submit", ColumnType.Timestamp, submit)
//                .order("c_check,c_audit desc")
                .order("c_check ,c_status ,c_audit desc")
                .query(WithdrawModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<WithdrawModel> query(String player, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(WithdrawModel.class).where("c_player=?")
                .order("c_submit desc").size(pageSize).page(pageNum), new Object[]{player});
    }

    @Override
    public int count(String player, Timestamp start, Timestamp end) {
        return liteOrm.count(new LiteQuery(WithdrawModel.class).where("c_player=? and c_submit between ? and ? and c_type<=2"),
                new Object[]{player, start, end});
    }

    @Override
    public WithdrawModel findById(String id) {
        return liteOrm.findById(WithdrawModel.class, id);
    }

    @Override
    public WithdrawModel find(String player, int status) {
        return liteOrm.findOne(new LiteQuery(WithdrawModel.class).where("c_player=? and c_status=?"), new Object[]{player, status});
    }

    @Override
    public int count(int status) {
        return liteOrm.count(new LiteQuery(WithdrawModel.class).where("c_status=?"), new Object[]{status});
    }

    @Override
    public int sum(Timestamp start, Timestamp end, int status) {
        return numeric.toInt(sql.query("select sum(c_amount) from t_player_withdraw where c_status=? and c_audit between ? and ?",
                new Object[]{status, start, end}).get(0, 0));
    }

    @Override
    public int sum(String player, Timestamp start, Timestamp end, int status) {
        return numeric.toInt(sql.query("select sum(c_amount) from t_player_withdraw where c_player=? and c_status=? and c_audit between ? and ?",
                new Object[]{player, status, start, end}).get(0, 0));
    }

    @Override
    public void save(WithdrawModel withdraw) {
        liteOrm.save(withdraw);
    }

    @Override
    public void delete(Timestamp submit) {
        liteOrm.delete(new LiteQuery(WithdrawModel.class).where("c_submit<?"), new Object[]{submit});
        liteOrm.close();
    }
}