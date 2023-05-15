package org.lpw.clivia.chat;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.function.BiConsumer;

@Service(ChatModel.NAME + ".service")
public class ChatServiceImpl implements ChatService, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private ChatDao chatDao;

    @Override
    public JSONObject query(String group, long time) {
        return query(group, time, (chat, object) -> object.put("sender", userService.get(chat.getSender())));
    }

    @Override
    public JSONObject query(String group, long time, BiConsumer<ChatModel, JSONObject> consumer) {
        return chatDao.query(group, time, pagination.getPageSize(20), pagination.getPageNum()).toJson(consumer);
    }

    @Override
    public void save(String group, String sender, String genre, String body) {
        ChatModel chat = new ChatModel();
        chat.setGroup(group);
        chat.setSender(sender);
        chat.setGenre(genre);
        chat.setBody(body);
        chat.setTime(System.currentTimeMillis());
        chatDao.save(chat);
    }

    @Override
    public void executeHourJob() {
        chatDao.delete(System.currentTimeMillis() - TimeUnit.Day.getTime(keyvalueService.valueAsInt("setting.chat.overdue", 7)));
    }
}
