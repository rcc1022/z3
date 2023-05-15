package org.lpw.photon.script;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ScriptValidator.NAME)
public class ScriptValidatorImpl extends ValidatorSupport {
    @Inject
    private ScriptService scriptService;
    private ThreadLocal<JSONObject> json = new ThreadLocal<>();

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        json.remove();
        json.set(scriptService.validate(validate.getString(), parameter));

        return getCode() == 0;
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return getCode();
    }

    @Override
    public String getFailureMessage(ValidateWrapper validate) {
        int failureCode = getFailureCode(validate);
        if (failureCode == 999999)
            return message.get("photon.ctrl.exception");

        JSONObject json = this.json.get();
        if (failureCode == 999996)
            return message.get("photon.script.validate.name.not-exists", json.getString("name"));

        return json.getString("failure");
    }

    private int getCode() {
        JSONObject json = this.json.get();

        return json == null ? 0 : json.getIntValue("code");
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return null;
    }
}
