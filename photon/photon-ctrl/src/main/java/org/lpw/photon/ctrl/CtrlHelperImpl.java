package org.lpw.photon.ctrl;

import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("photon.ctrl.helper")
public class CtrlHelperImpl implements CtrlHelper {
    @Inject
    private Validator validator;
    @Value("${photon.ctrl.root:}")
    private String root;

    @Override
    public String url(String uri) {
        return validator.isEmpty(root) ? null : (root + uri);
    }
}
