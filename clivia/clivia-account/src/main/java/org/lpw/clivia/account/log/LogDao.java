package org.lpw.clivia.account.log;

import org.lpw.photon.dao.orm.PageList;

interface LogDao {
    PageList<LogModel> query(String user, String owner, String type, String channel, int state, String start, int pageSize, int pageNum);

    PageList<LogModel> query(int restate);

    LogModel findById(String id);

    void save(LogModel log);

    void delete(String user);
}
