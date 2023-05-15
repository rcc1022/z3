package org.lpw.clivia.transfer;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Service(TransferModel.NAME + ".service")
public class TransferServiceImpl implements TransferService, UserListener, ContextRefreshedListener, SecondsJob {
    private static final String LOCK_ORDER_NO = TransferModel.NAME + ".service.order-no:";

    @Inject
    private DateTime dateTime;
    @Inject
    private Generator generator;
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private Optional<Set<TransferNotice>> notices;
    @Inject
    private TransferDao transferDao;
    private Set<String> ignores;
    private Map<String, TransferListener> listeners;

    @Override
    public JSONObject query(String type, String appId, String user, String orderNo, String billNo, String tradeNo, int state, String start) {
        JSONObject object = transferDao.query(type, appId, authService.findUser(user, user), orderNo, billNo, tradeNo,
                state, start, pagination.getPageSize(20), pagination.getPageNum()).toJson();
        userService.fill(object.getJSONArray("list"), new String[]{"user"});

        return object;
    }

    @Override
    public JSONObject success(String id, Map<String, String> map) {
        TransferModel transfer = find(id);
        complete(transfer, 1, "success", map);

        return modelHelper.toJson(transfer);
    }

    @Override
    public JSONObject notice(String id) {
        TransferModel transfer = find(id);
        notice(transfer);

        return modelHelper.toJson(transfer);
    }

    @Override
    public TransferModel find(String uk) {
        TransferModel transfer = transferDao.findById(uk);

        return transfer == null ? transferDao.findByOrderNo(uk) : transfer;
    }

    @Override
    public JSONObject create(String type, String appId, String user, String account, int amount, String billNo,
                             String notice, Map<String, String> map) {
        TransferModel transfer = new TransferModel();
        transfer.setType(type);
        transfer.setAppId(appId);
        transfer.setUser(validator.isEmpty(user) ? userService.id() : user);
        transfer.setAccount(account);
        transfer.setAmount(amount);
        transfer.setStart(dateTime.now());
        transfer.setOrderNo(newOrderNo(transfer.getStart()));
        transfer.setBillNo(billNo == null ? "" : billNo);
        transfer.setTradeNo("");
        transfer.setNotice(notice);
        JSONObject json = new JSONObject();
        json.put("create", putToJson(new JSONObject(), map));
        transfer.setJson(json.toJSONString());
        transferDao.save(transfer);

        return modelHelper.toJson(transfer);
    }

    private String newOrderNo(Timestamp now) {
        String time = dateTime.toString(now, "yyyyMMddHHmmssSSS");
        while (true) {
            String orderNo = time + generator.random(4);
            if (transferDao.findByOrderNo(orderNo) == null)
                return orderNo;
        }
    }

    @Override
    public JSONObject complete(String orderNo, int amount, String tradeNo, int state, Map<String, String> map) {
        String lockId = lockHelper.lock(LOCK_ORDER_NO + orderNo, 1L, 0);
        if (lockId == null)
            return new JSONObject();

        TransferModel transfer = find(orderNo);
        if (transfer.getState() == 0) {
            transfer.setAmount(amount);
            transfer.setTradeNo(tradeNo);
            complete(transfer, state, "complete", map);
        }
        lockHelper.unlock(lockId);

        return modelHelper.toJson(transfer);
    }

    private void complete(TransferModel transfer, int state, String name, Map<String, String> map) {
        transfer.setState(state);
        transfer.setEnd(dateTime.now());
        JSONObject json = this.json.toObject(transfer.getJson());
        json.put(name, putToJson(new JSONObject(), map));
        transfer.setJson(json.toJSONString());
        transferDao.save(transfer);
        notice(transfer);
    }

    private JSONObject putToJson(JSONObject object, Map<String, String> map) {
        map.forEach((key, value) -> {
            if (ignores.contains(key))
                return;

            object.put(key, value);
        });

        return object;
    }

    private void notice(TransferModel transfer) {
        if (validator.isEmpty(transfer.getNotice()))
            return;

        JSONObject notice = json.toObject(transfer.getNotice());
        if (notice != null)
            notices.ifPresent(set -> set.forEach(pn -> pn.transferDone(transfer, notice)));
    }

    @Override
    public void userDelete(UserModel user) {
        transferDao.delete(user.getId());
    }

    @Override
    public void executeSecondsJob() {
        if (validator.isEmpty(listeners) || Calendar.getInstance().get(Calendar.SECOND) % 30 > 0)
            return;

        String lockId = lockHelper.lock(TransferModel.NAME + ".seconds", 1L, 10);
        if (lockId == null)
            return;

        transferDao.query(0, dateTime.getStart(new Date(System.currentTimeMillis() - TimeUnit.Day.getTime()))).getList().forEach(transfer -> {
            if (listeners.containsKey(transfer.getType()))
                listeners.get(transfer.getType()).resetTransferState(transfer);
        });
        lockHelper.unlock(lockId);
    }

    @Override
    public int getContextRefreshedSort() {
        return 154;
    }

    @Override
    public void onContextRefreshed() {
        ignores = new HashSet<>();
        ignores.add("id");
        ignores.add("type");
        ignores.add("appId");
        ignores.add("user");
        ignores.add("account");
        ignores.add("amount");
        ignores.add("orderNo");
        ignores.add("billNo");
        ignores.add("tradeNo");
        ignores.add("notice");
        ignores.add("state");
        ignores.add("sign");
        ignores.add("sign-time");

        listeners = new HashMap<>();
        BeanFactory.getBeans(TransferListener.class).forEach(listener -> listeners.put(listener.getTransferType(), listener));
    }
}
