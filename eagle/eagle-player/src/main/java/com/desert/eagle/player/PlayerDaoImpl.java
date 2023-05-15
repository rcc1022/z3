package com.desert.eagle.player;

import org.lpw.clivia.console.ConsoleHelper;
import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.clivia.dao.QueryBuilder;
import org.lpw.photon.dao.jdbc.Sql;
import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Set;

@Repository(PlayerModel.NAME + ".dao")
class PlayerDaoImpl implements PlayerDao {
    @Inject
    private Numeric numeric;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;
    @Inject
    private ConsoleHelper consoleHelper;

    @Override
    public PageList<PlayerModel> query(Set<String> player, String uid, String memo, int ban, String time, boolean junior, int pageSize, int pageNum) {
        QueryBuilder qb = daoHelper.newQueryBuilder()
                .where("c_uid", DaoOperation.Equals, uid)
                .in("c_id", player)
                .where("c_ban", DaoOperation.Equals, ban)
                .between("c_time", ColumnType.Timestamp, time)
                .like(null, "c_memo", memo)
                .order(consoleHelper.sort("c_time desc"));
        if (junior)
            qb.where("c_junior", DaoOperation.Greater, 0);

        return qb.query(PlayerModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<PlayerModel> query(String invitor, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(PlayerModel.class).where("c_invitor=?").order(consoleHelper.sort("c_uid"))
                .size(pageSize).page(pageNum), new Object[]{invitor});
    }

    @Override
    public PageList<PlayerModel> query(String invitor) {
        return liteOrm.query(new LiteQuery(PlayerModel.class).where("c_invitor=?"), new Object[]{invitor});
    }

    @Override
    public PlayerModel findById(String id) {
        return liteOrm.findById(PlayerModel.class, id);
    }

    @Override
    public PlayerModel find(String uid) {
        return liteOrm.findOne(new LiteQuery(PlayerModel.class).where("c_uid=?"), new Object[]{uid});
    }

    @Override
    public PlayerModel maxUid() {
        return liteOrm.findOne(new LiteQuery(PlayerModel.class).order("c_uid desc"), null);
    }

    @Override
    public int balance() {
        return numeric.toInt(sql.query("select sum(c_balance) from t_player;", null).get(0, 0));
    }

    @Override
    public int count(Timestamp start, Timestamp end) {
        return liteOrm.count(new LiteQuery(PlayerModel.class).where("c_time between ? and ?"), new Object[]{start, end});
    }

    @Override
    public void insert(PlayerModel player) {
        liteOrm.insert(player);
    }

    @Override
    public void save(PlayerModel player) {
        liteOrm.save(player);
        liteOrm.close();
    }

    @Override
    public void ban(String id, int ban) {
        liteOrm.update(new LiteQuery(PlayerModel.class).set("c_ban=?").where("c_id=?"), new Object[]{ban, id});
    }

    @Override
    public void zero() {
        liteOrm.update(new LiteQuery(PlayerModel.class).set("c_bet=?,c_profit=?"), new Object[]{0, 0});
        liteOrm.close();
    }

    @Override
    public int junior(String invter) {
        return liteOrm.count(new LiteQuery(PlayerModel.class).where("c_junior=?"), new Object[]{invter});
    }

    @Override
    public SqlTable countInvite() {
        return sql.query("select c_invitor,count(*) from t_player where c_invitor is not null group by c_invitor", null);
    }

    @Override
    public void clearQrcode() {
        liteOrm.update(new LiteQuery(PlayerModel.class).set("c_qrcode=?"), new Object[]{""});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(PlayerModel.class, id);
    }
}