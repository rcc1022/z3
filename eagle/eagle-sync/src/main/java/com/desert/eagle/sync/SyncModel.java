package com.desert.eagle.sync;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(SyncModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = SyncModel.NAME)
@Table(name = "t_sync")
public class SyncModel extends ModelSupport {
    static final String NAME = "eagle.sync";
    private long issue;
    private Timestamp time;
    private long prevIssue;
    private int[] prevCode;
    private Timestamp prevTime;

    public long getIssue() {
        return issue;
    }

    public void setIssue(long issue) {
        this.issue = issue;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public long getPrevIssue() {
        return prevIssue;
    }

    public void setPrevIssue(long prevIssue) {
        this.prevIssue = prevIssue;
    }

    public int[] getPrevCode() {
        return prevCode;
    }

    public void setPrevCode(int[] prevCode) {
        this.prevCode = prevCode;
    }

    public Timestamp getPrevTime() {
        return prevTime;
    }

    public void setPrevTime(Timestamp prevTime) {
        this.prevTime = prevTime;
    }
}