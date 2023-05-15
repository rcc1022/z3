package org.lpw.clivia.payment;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.account.AccountService;
import org.lpw.clivia.account.log.LogService;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service(PaymentModel.NAME + ".service")
public class PaymentServiceImpl implements PaymentService, UserListener {
    private static final String LOCK_ORDER_NO = PaymentModel.NAME + ".service.order-no:";

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
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private AccountService accountService;
    @Inject
    private LogService logService;
    @Inject
    private Optional<Set<PaymentNotice>> notices;
    @Inject
    private PaymentDao paymentDao;
    private final Set<String> ignores = Set.of("id", "type", "user", "appId", "amount", "orderNo", "billNo", "tradeNo", "notice", "state", "sign", "sign-time");

    @Override
    public JSONObject query(String type, String appId, String user, String orderNo, String billNo, String tradeNo,
                            int state, String start) {
        JSONObject object = paymentDao.query(type, appId, authService.findUser(user, user), orderNo, billNo, tradeNo,
                state, start, pagination.getPageSize(20), pagination.getPageNum()).toJson();
        userService.fill(object.getJSONArray("list"), new String[]{"user"});

        return object;
    }

    @Override
    public JSONObject success(String id, Map<String, String> map) {
        PaymentModel payment = find(id);
        complete(payment, 1, "success", map);

        return modelHelper.toJson(payment);
    }

    @Override
    public JSONObject notice(String id) {
        PaymentModel payment = find(id);
        notice(payment);

        return modelHelper.toJson(payment);
    }

    @Override
    public PaymentModel find(String uk) {
        PaymentModel payment = paymentDao.findById(uk);

        return payment == null ? paymentDao.findByOrderNo(uk) : payment;
    }

    @Override
    public JSONObject create(String type, String appId, String user, int amount, String billNo, String notice, Map<String, String> map) {
        PaymentModel payment = new PaymentModel();
        payment.setType(type);
        payment.setAppId(appId);
        payment.setUser(validator.isEmpty(user) ? userService.id() : user);
        payment.setAmount(amount);
        payment.setStart(dateTime.now());
        payment.setOrderNo(newOrderNo(payment.getStart()));
        payment.setBillNo(billNo == null ? "" : billNo);
        payment.setTradeNo("");
        payment.setNotice(notice);
        JSONObject json = new JSONObject();
        json.put("create", putToJson(new JSONObject(), map));
        payment.setJson(json.toJSONString());
        paymentDao.save(payment);

        JSONObject object = modelHelper.toJson(payment);
        int amt = keyvalueService.valueAsInt("setting.global.payment.amount", -1);
        if (amt > 0)
            object.put("amount", amt);

        return object;
    }

    private String newOrderNo(Timestamp now) {
        String time = dateTime.toString(now, "yyyyMMddHHmmssSSS");
        while (true) {
            String orderNo = time + generator.random(4);
            if (paymentDao.findByOrderNo(orderNo) == null)
                return orderNo;
        }
    }

    @Override
    public JSONObject complete(String orderNo, int amount, String tradeNo, int state, Map<String, String> map) {
        String lockId = lockHelper.lock(LOCK_ORDER_NO + orderNo, 1L, 0);
        if (lockId == null)
            return new JSONObject();

        PaymentModel payment = find(orderNo);
        if (payment.getState() == 0) {
            payment.setAmount(amount);
            payment.setTradeNo(tradeNo);
            complete(payment, state, "complete", map);
        }
        lockHelper.unlock(lockId);

        return modelHelper.toJson(payment);
    }

    private void complete(PaymentModel payment, int state, String name, Map<String, String> map) {
        payment.setState(state);
        payment.setEnd(dateTime.now());
        JSONObject json = this.json.toObject(payment.getJson());
        json.put(name, putToJson(new JSONObject(), map));
        payment.setJson(json.toJSONString());
        if (state == 1) {
            JSONObject object = accountService.deposit(payment.getUser(), "", 0, payment.getType(), payment.getAmount(), merge(json.getJSONObject("create"), map));
            if (this.json.containsKey(object, "logId"))
                logService.pass(new String[]{object.getString("logId")});
        }
        paymentDao.save(payment);
        notice(payment);
    }

    private JSONObject putToJson(JSONObject object, Map<String, String> map) {
        if (map != null) {
            map.forEach((key, value) -> {
                if (ignores.contains(key))
                    return;

                object.put(key, value);
            });
        }

        return object;
    }

    private Map<String, String> merge(JSONObject object, Map<String, String> map) {
        Map<String, String> m = new HashMap<>();
        if (!validator.isEmpty(object))
            for (String key : object.keySet())
                m.put(key, object.getString(key));
        if (!validator.isEmpty(map))
            m.putAll(map);

        return m;
    }

    private void notice(PaymentModel payment) {
        if (validator.isEmpty(payment.getNotice()))
            return;

        JSONObject notice = json.toObject(payment.getNotice());
        if (notice != null)
            notices.ifPresent(set -> set.forEach(pn -> pn.paymentDone(payment, notice)));
    }

    @Override
    public void userDelete(UserModel user) {
        paymentDao.delete(user.getId());
    }
}
