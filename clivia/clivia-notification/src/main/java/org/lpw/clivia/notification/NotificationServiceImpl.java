package org.lpw.clivia.notification;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Service(NotificationModel.NAME + ".service")
public class NotificationServiceImpl implements NotificationService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private NotificationDao notificationDao;

    @Override
    public JSONObject user(String genre) {
        return notificationDao.query(userService.id(), genre, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject unread(String[] genre) {
        Set<String> set = new HashSet<>();
        for (String g : genre)
            if (!validator.isEmpty(g))
                set.add(g);
        NotificationModel notification = notificationDao.unread(userService.id(), set, dateTime.now());
        if (notification == null)
            return new JSONObject();

        notification.setRead(dateTime.now());
        notificationDao.save(notification);

        return modelHelper.toJson(notification);
    }

    @Override
    public void save(String user, String genre, String subject, String content, Timestamp expiration) {
        NotificationModel notification = new NotificationModel();
        notification.setUser(user);
        notification.setGenre(genre);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setExpiration(expiration);
        notification.setTime(dateTime.now());
        notificationDao.save(notification);
    }
}
