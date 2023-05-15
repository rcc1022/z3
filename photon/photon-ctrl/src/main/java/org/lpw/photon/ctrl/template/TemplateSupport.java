package org.lpw.photon.ctrl.template;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.Failure;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

/**
 * 模板支持类。
 */
public abstract class TemplateSupport implements Template {
    @Inject
    protected Validator validator;
    @Inject
    protected Message message;
    @Value("${photon.ctrl.exception:999999}")
    protected int exception;
    @Value("${photon.ctrl.busy:999998}")
    protected int busy;
    @Value("${photon.ctrl.danger:999997}")
    protected int danger;
    @Value("${photon.ctrl.not-permit:999996}")
    protected int notPermit;

    @Override
    public Object failure(int code, String message, String parameter, String value) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        JSONObject object = new JSONObject();
        object.put("name", parameter);
        object.put("value", value);
        json.put("parameter", object);

        return json;
    }

    @Override
    public Object success(Object data, String key, Object... args) {
        JSONObject object = new JSONObject();
        object.put("code", 0);
        if (data != null)
            object.put("data", data);
        if (!validator.isEmpty(key))
            object.put("message", message.get(key, args));

        return object;
    }

    protected JSONObject getFailure(Failure failure) {
        JSONObject object = new JSONObject();
        object.put("code", getFailureCode(failure));
        object.put("message", message.get(failure.getMessageKey()));

        return object;
    }

    protected int getFailureCode(Failure failure) {
        if (failure == Failure.NotPermit)
            return notPermit;

        if (failure == Failure.Danger)
            return danger;

        if (failure == Failure.Busy)
            return busy;

        return exception;
    }
}
