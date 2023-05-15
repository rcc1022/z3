package org.lpw.photon.ctrl.validate;

public abstract class IdValidatorSupport extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isId(parameter);
    }
}
