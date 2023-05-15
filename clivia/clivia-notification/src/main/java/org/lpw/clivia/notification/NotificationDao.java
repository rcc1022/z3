package org.lpw.clivia.notification;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Set;

interface NotificationDao {
    PageList<NotificationModel> query(String user, String genre, int pageSize, int pageNum);

    NotificationModel unread(String user, Set<String> genre, Timestamp expiration);

    void save(NotificationModel notification);
}