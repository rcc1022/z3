package com.desert.eagle.message;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.player.PlayerService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;

@Service(MessageModel.NAME + ".service")
public class MessageServiceImpl implements MessageService {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private PlayerService playerService;
    @Inject
    private MessageDao messageDao;

    @Override
    public JSONObject query(String game, long time) {
        return messageDao.query(game, time, pagination.getPageSize(20), pagination.getPageNum()).toJson((message, object) -> {
            if (validator.isId(message.getPlayer()))
                object.put("player", playerService.getNickAvatar(message.getPlayer()));
            object.put("timestr", dateTime.toString(new Timestamp(message.getTime())));
        });
    }

    @Override
    public void save(String game, String player, int type, String content) {
        MessageModel message = new MessageModel();
        message.setGame(game);
        message.setPlayer(player);
        message.setType(type);
        message.setContent(content);
        message.setTime(System.currentTimeMillis());
        messageDao.save(message);
    }
}
