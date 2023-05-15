package org.lpw.clivia.user.inviter;

import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.util.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;

@Service(InviterModel.NAME + ".service")
public class InviterServiceImpl implements InviterService, HourJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Session session;
    @Inject
    private UserService userService;
    @Inject
    private Optional<Set<InviterListener>> listeners;
    @Inject
    private InviterDao inviterDao;
    @Value("${" + InviterModel.NAME + ".expiry:7}")
    private int expiry;

    @Override
    public void set(String code) {
        if (!userService.isCode(code))
            return;

        String string = session.get(InviterModel.NAME);
        if (code.equals(string))
            return;

        InviterModel inviter = inviterDao.findByPsid(session.getId());
        if (inviter == null) {
            inviter = new InviterModel();
            inviter.setPsid(session.getId());
            inviter.setCode(code);
            inviter.setTime(dateTime.now());
            inviterDao.save(inviter);
            notice(code);
        } else if (!inviter.getCode().equals(code)) {
            inviter.setCode(code);
            inviter.setTime(dateTime.now());
            inviterDao.save(inviter);
            notice(code);
        }
        session.set(InviterModel.NAME, code);
    }

    private void notice(String code) {
        UserModel user = userService.findByCode(code);
        if (user == null)
            return;

        String id = user.getId();
        listeners.ifPresent(set -> set.forEach(listener -> listener.invite(id)));
    }

    @Override
    public String get() {
        String code = session.get(InviterModel.NAME);
        if (userService.isCode(code))
            return code;

        InviterModel inviter = inviterDao.findByPsid(session.getId());

        return inviter != null && userService.isCode(inviter.getCode()) ? inviter.getCode() : null;
    }

    @Override
    public void executeHourJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -expiry);
        inviterDao.clean(new Timestamp(calendar.getTimeInMillis()));
    }
}
