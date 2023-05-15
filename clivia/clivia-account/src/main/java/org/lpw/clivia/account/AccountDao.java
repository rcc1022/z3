package org.lpw.clivia.account;

import org.lpw.photon.dao.orm.PageList;

interface AccountDao {
    PageList<AccountModel> query(String user, String owner, int type, String balance, int pageSize, int pageNum);

    AccountModel findById(String id);

    AccountModel find(String user, String owner, int type);

    void save(AccountModel account);

    void delete(String user);
}
