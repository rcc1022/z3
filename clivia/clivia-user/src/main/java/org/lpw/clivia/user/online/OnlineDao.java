package org.lpw.clivia.user.online;

import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Set;

interface OnlineDao {
    PageList<OnlineModel> query(Set<String> user, String ip, int pageSize, int pageNum);

    PageList<OnlineModel> query(Timestamp visit);

    PageList<OnlineModel> query(String user);

    OnlineModel findById(String id);

    OnlineModel findBySid(String sid);

    SqlTable user(Timestamp[] lastVisit, int grade);

    SqlTable user(Timestamp lastVisit, int grade);

    void save(OnlineModel online);

    void lastVisit(String sid, Timestamp lastVisit);

    void delete(OnlineModel online);
}
