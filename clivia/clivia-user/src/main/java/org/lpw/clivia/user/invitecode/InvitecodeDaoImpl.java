package org.lpw.clivia.user.invitecode;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(InvitecodeModel.NAME + ".dao")
class InvitecodeDaoImpl implements InvitecodeDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<InvitecodeModel> query(String batch, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_batch", DaoOperation.Equals, batch)
                .order("c_batch desc")
                .query(InvitecodeModel.class, pageSize, pageNum);
    }

    @Override
    public InvitecodeModel find(String code) {
        return liteOrm.findOne(new LiteQuery(InvitecodeModel.class).where("c_code=?"), new Object[]{code});
    }

    @Override
    public void save(InvitecodeModel invitecode) {
        liteOrm.save(invitecode);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(InvitecodeModel.class, id);
    }
}