package com.desert.eagle.bet;

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
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository(BetModel.NAME + ".dao")
class BetDaoImpl implements BetDao {
    @Inject
    private Numeric numeric;
    @Inject
    private Validator validator;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<BetModel> query(String game, String player, String issue, int win, String time, int pageSize, int pageNum) {
        QueryBuilder qb = daoHelper.newQueryBuilder()
                .where("c_game", DaoOperation.Equals, game)
                .where("c_player", DaoOperation.Equals, player)
                .where("c_issue", DaoOperation.Equals, issue)
                .where("c_robot", DaoOperation.Equals, 0)
                .between("c_time", ColumnType.Timestamp, time)
                .order("c_time desc");
        if (win > -1) {
            qb.where("c_status", DaoOperation.Equals, win == 0 ? 0 : 1);
            if (win == 1)
                qb.where("c_profit", DaoOperation.LessEquals, 0);
            else if (win == 2)
                qb.where("c_profit", DaoOperation.Greater, 0);
        }

        return qb.query(BetModel.class, pageSize, pageNum);
    }

    @Override
    public long sumAmount(int status, int robot, Map<String, String> map) {
        StringBuilder sb = new StringBuilder("select sum(c_amount) from t_bet where c_status=? and c_robot=?");
        List<Object> args = new ArrayList<>();
        args.add(status);
        args.add(robot);
        if (map.size() == 1) {
            sb.append(" and c_game=? and c_issue=?");
            String key = map.keySet().iterator().next();
            args.add(key);
            args.add(map.get(key));
        } else {
            sb.append(" and (");
            boolean or = false;
            for (String key : map.keySet()) {
                if (or)
                    sb.append(" or ");
                or = true;
                sb.append("(c_game=? and c_issue=?)");
                args.add(key);
                args.add(map.get(key));
            }
            sb.append(')');
        }

        return numeric.toLong(sql.query(sb.toString(), args.toArray()).get(0, 0));
    }

    @Override
    public PageList<BetModel> query(String game, String issue, int status) {
        return liteOrm.query(new LiteQuery(BetModel.class).where("c_game=? and c_issue=? and c_status=?"), new Object[]{game, issue, status});
    }

    @Override
    public PageList<BetModel> queryUserBetList(String game, String issue) {
        return liteOrm.query(new LiteQuery(BetModel.class).select("select c_type,c_item,c_rate,sum(c_amount) c_amount from t_bet ")
                        .where("c_robot=0 and c_game=? and c_issue=?")
                        .group("c_type,c_item"),
                new Object[]{game, issue});
    }

    @Override
    public PageList<BetModel> query(String issue, int status, int robot) {
        return liteOrm.query(new LiteQuery(BetModel.class).where("c_issue=? and c_status=? and c_robot=?"), new Object[]{issue, status, robot});
    }

    @Override
    public PageList<BetModel> query(String game, String player, String issue, int status) {
        return liteOrm.query(new LiteQuery(BetModel.class).where("c_game=? and c_player=? and c_issue=? and c_status=?"), new Object[]{game, player, issue, status});
    }

    @Override
    public PageList<BetModel> query(int status, String zhuihao) {
        return liteOrm.query(new LiteQuery(BetModel.class).where("c_status=? and c_zhuihao=?"), new Object[]{status, zhuihao});
    }

    @Override
    public BetModel findById(String id) {
        return liteOrm.findById(BetModel.class, id);
    }

    @Override
    public int sum(String game, String issue, int status, int robot) {
        return numeric.toInt(sql.query("select sum(c_amount) from t_bet where c_game=? and c_issue=? and c_status=? and c_robot=?",
                new Object[]{game, issue, status, robot}).get(0, 0));
    }

    @Override
    public SqlTable sum(int status, int robot, Timestamp start, Timestamp end) {
        return sql.query("select c_game,c_player,count(*),sum(c_amount),sum(c_water),sum(c_profit),sum(c_commission) from t_bet where c_time between ? and ? and c_status=? and c_robot=? group by c_game,c_player",
                new Object[]{start, end, status, robot});
    }

    @Override
    public SqlTable sum(Set<String> player, String game, int status, int robot, Timestamp start, Timestamp end) {
        StringBuilder sb = new StringBuilder("select c_player,c_issue,sum(c_amount),sum(c_profit),sum(c_water),sum(c_commission) from t_bet where c_time between ? and ? and c_status=? and c_robot=?");
        List<Object> args = new ArrayList<>();
        args.add(start);
        args.add(end);
        args.add(status);
        args.add(robot);
        if (!validator.isEmpty(player)) {
            sb.append(" and c_player in(");
            for (int i = 0, size = player.size(); i < size; i++) {
                if (i > 0)
                    sb.append(',');
                sb.append('?');
            }
            sb.append(')');
            args.addAll(player);
        }
        if (!validator.isEmpty(game)) {
            sb.append(" and c_game=?");
            args.add(game);
        }
        sb.append(" group by c_player,c_issue");

        return sql.query(sb.toString(), args.toArray());
    }

    @Override
    public SqlTable sum(String game, String issue, int robot) {
        return sql.query("select c_type,c_item,c_rate,sum(c_amount),sum(c_profit) from t_bet where c_game=? and c_issue=? and c_robot=? group by c_type,c_item,c_rate",
                new Object[]{game, issue, robot});
    }

    @Override
    public int count(String zhuihao) {
        return liteOrm.count(new LiteQuery(BetModel.class).where("c_zhuihao=?"), new Object[]{zhuihao});
    }

    @Override
    public void save(BetModel bet) {
        liteOrm.save(bet);
    }

    @Override
    public void delete(BetModel bet) {
        liteOrm.delete(bet);
    }

    @Override
    public void delete(int status, Timestamp settle) {
        liteOrm.delete(new LiteQuery(BetModel.class).where("c_status=? and c_settle<?"), new Object[]{status, settle});
        liteOrm.close();
    }

    @Override
    public void deleteRobot(int robot, Timestamp time) {
        liteOrm.delete(new LiteQuery(BetModel.class).where("c_time<? and c_robot=?"), new Object[]{time, robot});
    }

    @Override
    public void clear(String game, String issue, int status) {
        liteOrm.delete(new LiteQuery(BetModel.class).where("c_game=? and c_issue=? and c_status=?"), new Object[]{game, issue, status});
    }

    @Override
    public void close() {
        liteOrm.close();
    }
}