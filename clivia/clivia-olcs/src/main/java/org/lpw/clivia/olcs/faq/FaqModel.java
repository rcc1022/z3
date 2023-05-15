package org.lpw.clivia.olcs.faq;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(FaqModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = FaqModel.NAME)
@Table(name = "t_olcs_faq")
public class FaqModel extends ModelSupport {
    static final String NAME = "clivia.olcs.faq";

    private int sort; // 顺序
    private String subject; // 标题
    private String content; // 内容
    private int frequently; // 常见问题

    @Jsonable
    @Column(name = "c_sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Jsonable
    @Column(name = "c_subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Jsonable
    @Column(name = "c_content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Jsonable
    @Column(name = "c_frequently")
    public int getFrequently() {
        return frequently;
    }

    public void setFrequently(int frequently) {
        this.frequently = frequently;
    }
}