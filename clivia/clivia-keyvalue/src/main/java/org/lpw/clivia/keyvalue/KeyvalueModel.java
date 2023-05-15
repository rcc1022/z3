package org.lpw.clivia.keyvalue;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.Memory;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(KeyvalueModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = KeyvalueModel.NAME)
@Table(name = "t_keyvalue")
@Memory(name = "m_keyvalue")
public class KeyvalueModel extends ModelSupport {
    static final String NAME = "clivia.keyvalue";

    private String key; // 键
    private String value; // 值

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @Column(name = "c_value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
