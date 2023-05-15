package org.lpw.photon.ctrl.validate;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.ExecutorHelper;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;

import javax.inject.Inject;

public abstract class ValidatorSupport implements Validator {
    @Inject
    protected org.lpw.photon.util.Validator validator;
    @Inject
    protected Converter converter;
    @Inject
    protected Numeric numeric;
    @Inject
    protected Message message;
    @Inject
    protected Request request;
    @Inject
    protected ExecutorHelper executorHelper;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return false;
    }

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        return false;
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 0;
    }

    @Override
    public String getFailureMessage(ValidateWrapper validate) {
        return message.get(validator.isEmpty(validate.getFailureKey()) ? getDefaultFailureMessageKey() : validate.getFailureKey(),
                getFailureMessageArgs(validate));
    }

    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        if (validator.isEmpty(validate.getFailureArgKeys()))
            return new Object[]{message.get(executorHelper.get().getKey() + "." + validate.getParameter())};

        Object[] args = new Object[validate.getFailureArgKeys().length];
        for (int i = 0; i < args.length; i++)
            args[i] = message.get(validate.getFailureArgKeys()[i]);

        return args;
    }

    protected abstract String getDefaultFailureMessageKey();
}
