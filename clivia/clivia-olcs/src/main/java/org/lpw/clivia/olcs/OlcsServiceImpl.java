package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.olcs.faq.FaqModel;
import org.lpw.clivia.olcs.faq.FaqService;
import org.lpw.clivia.olcs.member.MemberModel;
import org.lpw.clivia.olcs.member.MemberService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.util.Thread;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service(OlcsModel.NAME + ".service")
public class OlcsServiceImpl implements OlcsService, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Generator generator;
    @Inject
    private Message message;
    @Inject
    private Json json;
    @Inject
    private Thread thread;
    @Inject
    private Session session;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private MemberService memberService;
    @Inject
    private FaqService faqService;
    @Inject
    private Optional<OlcsConfig> config;
    @Inject
    private OlcsDao olcsDao;
    private final String casual = "olcsolcs-";

    @Override
    public JSONObject query(String user, Timestamp time) {
        return query(user, time, true);
    }

    @Override
    public JSONObject user(Timestamp time) {
        return query(userService.id(), time, false);
    }

    private JSONObject query(String user, Timestamp time, boolean replier) {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<OlcsModel> list;
        if (time == null) {
            list = olcsDao.query(user, pagination.getPageSize(50), pagination.getPageNum()).getList();
            Collections.reverse(list);
        } else
            list = olcsDao.query(user, time).getList();
        list.forEach(olcs -> addToArray(object, array, olcs));
        if (!replier && time == null && (array.isEmpty() || !array.getJSONObject(array.size() - 1).get("genre").equals("faq"))) {
            addToArray(object, array, save(user, UserService.SYSTEM_ID, "faq", "", null, true));
        }
        if (json.hasTrue(object, "faq"))
            object.put("faq", faqService.frequently());
        if (!array.isEmpty()) {
            if (replier)
                memberService.replierRead(user, dateTime.now());
            else
                memberService.userRead(user, dateTime.now());
        }
        object.put("list", array);
        MemberModel member = memberService.findById(user);
        if (member != null)
            object.put("read", dateTime.toString(replier ? member.getUserRead() : member.getReplierRead()));

        return object;
    }

    private void addToArray(JSONObject object, JSONArray array, OlcsModel olcs) {
        JSONObject obj = new JSONObject();
        obj.put("id", olcs.getId());
        if (olcs.getReplier() != null)
            obj.put("replier", userService.getNickAvatar(olcs.getReplier()));
        obj.put("genre", olcs.getGenre());
        if (olcs.getGenre().equals("faq")) {
            obj.put("content", "");
            object.put("faq", true);
        } else
            obj.put("content", olcs.getContent());
        obj.put("time", dateTime.toString(olcs.getTime()));
        array.add(obj);
    }

    @Override
    public void ask(String genre, String content) {
        String user = userService.id();
        if (validator.isEmpty(user)) {
            user = session.get(OlcsModel.NAME);
            if (validator.isEmpty(user)) {
                user = casual + dateTime.toString(dateTime.now(), "yyyy-MMdd-HHmm-ssSSS") + generator.random(7);
                session.set(OlcsModel.NAME, user);
            }
        }
        OlcsModel olcs = save(user, null, genre, content, null, false);
        if (!genre.equals("text"))
            return;

        FaqModel faq = faqService.find(content);
        if (faq == null)
            return;

        save(user, UserService.SYSTEM_ID, "text", faq.getContent(), new Timestamp(olcs.getTime().getTime() + 1000), true);
    }

    @Override
    public void reply(String user, String genre, String content) {
        save(user, userService.id(), genre, content, null, true);
    }

    private OlcsModel save(String user, String replier, String genre, String content, Timestamp time, boolean reply) {
        OlcsModel olcs = new OlcsModel();
        olcs.setUser(user);
        olcs.setReplier(replier);
        olcs.setGenre(genre);
        olcs.setContent(content);
        olcs.setTime(time == null ? dateTime.now() : time);
        olcsDao.save(olcs);
        memberService.save(user, genre.equals("text") ? content : message.get(OlcsModel.NAME + ".genre." + genre), reply);

        return olcs;
    }

    @Override
    public void delete(String id) {
        OlcsModel olcs = olcsDao.findById(id);
        if (validator.isEmpty(olcs.getReplier()))
            return;

        olcs.setGenre("delete");
        olcs.setTime(dateTime.now());
        olcsDao.save(olcs);
    }

    @Override
    public void clean(String user) {
        olcsDao.delete(user);
        memberService.clean(user);
    }

    @Override
    public void executeHourJob() {
        int overdue = config.map(OlcsConfig::overdue).orElse(0);
        if (overdue <= 0)
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -overdue);
        Timestamp time = new Timestamp(calendar.getTimeInMillis());
        olcsDao.delete(time);
        memberService.empty(time);
    }
}
