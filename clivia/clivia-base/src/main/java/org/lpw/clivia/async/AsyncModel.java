package org.lpw.clivia.async;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(AsyncModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = AsyncModel.NAME)
@Table(name = "t_async")
public class AsyncModel extends ModelSupport {
    static final String NAME = "clivia.async";

    private String key; // 引用KEY
    private String parameter; // 参数
    private String result; // 结果
    private int state; // 状态：0-进行中；1-已完成；2-异常；3-超时
    private Timestamp begin; // 开始时间
    private Timestamp finish; // 结束时间
    private Timestamp timeout; // 超时时间

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @Column(name = "c_parameter")
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Jsonable
    @Column(name = "c_result")
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Jsonable
    @Column(name = "c_state")
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Jsonable
    @Column(name = "c_begin")
    public Timestamp getBegin() {
        return begin;
    }

    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    @Jsonable
    @Column(name = "c_finish")
    public Timestamp getFinish() {
        return finish;
    }

    public void setFinish(Timestamp finish) {
        this.finish = finish;
    }

    @Jsonable
    @Column(name = "c_timeout")
    public Timestamp getTimeout() {
        return timeout;
    }

    public void setTimeout(Timestamp timeout) {
        this.timeout = timeout;
    }
}
