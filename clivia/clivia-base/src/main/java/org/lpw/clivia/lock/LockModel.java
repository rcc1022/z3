package org.lpw.clivia.lock;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(LockModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = LockModel.NAME)
@Table(name = "m_lock")
public class LockModel extends ModelSupport {
    static final String NAME = "clivia.lock";

    private String md5; // MD5值
    private String key; // 锁key
    private long create; // 创建时间
    private long expire; // 过期时间

    @Jsonable
    @Column(name = "c_md5")
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @Column(name = "c_create")
    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    @Jsonable
    @Column(name = "c_expire")
    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
