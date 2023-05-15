package org.lpw.clivia.user.password;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(PasswordModel.NAME + ".dao")
class PasswordDaoImpl implements PasswordDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<PasswordModel> query(String user) {
        return liteOrm.query(new LiteQuery(PasswordModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public PasswordModel find(String user, String type) {
        return liteOrm.findOne(new LiteQuery(PasswordModel.class).where("c_user=? and c_type=?"), new Object[]{user, type});
    }

    @Override
    public void save(PasswordModel password) {
        liteOrm.save(password);
    }

    @Override
    public void delete(PasswordModel password) {
        liteOrm.delete(password);
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(PasswordModel.class).where("c_user=?"), new Object[]{user});
    }
}