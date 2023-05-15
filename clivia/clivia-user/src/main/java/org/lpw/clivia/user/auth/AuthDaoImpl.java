package org.lpw.clivia.user.auth;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(AuthModel.NAME + ".dao")
class AuthDaoImpl implements AuthDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<AuthModel> query(String user) {
        return liteOrm.query(new LiteQuery(AuthModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public PageList<AuthModel> search(String uid) {
        return liteOrm.query(new LiteQuery(AuthModel.class).where("c_uid like ?"), new Object[]{"%" + uid + "%"});
    }

    @Override
    public AuthModel findByUid(String uid) {
        return liteOrm.findOne(new LiteQuery(AuthModel.class).where("c_uid=?"), new Object[]{uid});
    }

    @Override
    public AuthModel findByAvatar(String avatar) {
        return liteOrm.findOne(new LiteQuery(AuthModel.class).where("c_avatar=?"), new Object[]{avatar});
    }

    @Override
    public void save(AuthModel auth) {
        liteOrm.save(auth);
    }

    @Override
    public void delete(AuthModel auth) {
        liteOrm.delete(auth);
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(AuthModel.class).where("c_user=?"), new Object[]{user});
    }
}
