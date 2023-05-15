package org.lpw.clivia.account;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(AccountModel.NAME + ".dao")
class AccountDaoImpl implements AccountDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<AccountModel> query(String user, String owner, int type, String balance, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_user", DaoOperation.Equals, user)
                .where("c_owner", DaoOperation.Equals, owner)
                .where("c_type", DaoOperation.Equals, type)
                .between("c_balance", ColumnType.Money, balance)
                .order("c_user,c_owner,c_type")
                .query(AccountModel.class, pageSize, pageNum);
    }

    @Override
    public AccountModel findById(String id) {
        return liteOrm.findById(AccountModel.class, id);
    }

    @Override
    public AccountModel find(String user, String owner, int type) {
        return liteOrm.findOne(new LiteQuery(AccountModel.class).where("c_user=? and c_owner=? and c_type=?"),
                new Object[]{user, owner, type});
    }

    @Override
    public void save(AccountModel account) {
        liteOrm.save(account);
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(AccountModel.class).where("c_user=?"), new Object[]{user});
    }
}
