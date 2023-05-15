package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.jdbc.Sql;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(MemberModel.NAME + ".dao")
class MemberDaoImpl implements MemberDao {
    @Inject
    private Numeric numeric;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<MemberModel> query() {
        return liteOrm.query(new LiteQuery(MemberModel.class).order("c_time desc"), null);
    }

    @Override
    public PageList<MemberModel> unread() {
        return liteOrm.query(new LiteQuery(MemberModel.class).where("c_replier_unread>?"), new Object[]{0});
    }

    @Override
    public MemberModel findById(String id) {
        return liteOrm.findById(MemberModel.class, id);
    }

    @Override
    public int sum() {
        return numeric.toInt(sql.query("select sum(c_replier_unread) from t_olcs_member", null).get(0, 0));
    }

    @Override
    public void insert(MemberModel member) {
        liteOrm.insert(member);
    }

    @Override
    public void save(MemberModel member) {
        liteOrm.save(member);
    }

    @Override
    public void userRead(String id, Timestamp time) {
        liteOrm.update(new LiteQuery(MemberModel.class).set("c_user_unread=?,c_user_read=?").where("c_id=?"), new Object[]{0, time, id});
    }

    @Override
    public void replierRead(String id, Timestamp time) {
        liteOrm.update(new LiteQuery(MemberModel.class).set("c_replier_unread=?,c_replier_read=?").where("c_id=?"), new Object[]{0, time, id});
    }

    @Override
    public void content(String content, Timestamp time) {
        liteOrm.update(new LiteQuery(MemberModel.class).set("c_content=?").where("c_time<?"), new Object[]{content, time});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(MemberModel.class, id);
    }
}