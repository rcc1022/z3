package org.lpw.clivia.api;

import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(ApiModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApiModel extends ModelSupport {
    static final String NAME = "clivia.api";
}
