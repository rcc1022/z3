package org.lpw.clivia.notification;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository(NotificationModel.NAME + ".dao")
class NotificationDaoImpl implements NotificationDao {
    @Inject
    private Validator validator;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<NotificationModel> query(String user, String genre, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_user", DaoOperation.Equals, user)
                .where("c_genre", DaoOperation.Equals, genre)
                .order("c_time desc")
                .query(NotificationModel.class, pageSize, pageNum);
    }

    @Override
    public NotificationModel unread(String user, Set<String> genre, Timestamp expiration) {
        StringBuilder where = new StringBuilder("c_user=? and c_read is null");
        List<Object> args = new ArrayList<>();
        args.add(user);
        if (!validator.isEmpty(genre)) {
            where.append(" and c_genre in(");
            for (String g : genre) {
                if (args.size() > 1)
                    where.append(',');
                where.append('?');
                args.add(g);
            }
            where.append(')');
        }
        where.append(" and (c_expiration is null or c_expiration>=?)");
        args.add(expiration);

        return liteOrm.findOne(new LiteQuery(NotificationModel.class).where(where.toString()).order("c_time"), args.toArray());
    }

    @Override
    public void save(NotificationModel notification) {
        liteOrm.save(notification);
    }
}