package org.lpw.clivia.editor;

import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Table;

@Component(EditorModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = EditorModel.NAME)
@Table(name = "t_editor")
public class EditorModel extends ModelSupport {
    static final String NAME = "clivia.editor";

//    private String key; // 引用KEY
//    private int order; // 顺序
//    private String content; // 内容
//    private long time; // 时间
//
//    @Jsonable
//    @Column(name = "c_key")
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    @Jsonable
//    @Column(name = "c_order")
//    public int getOrder() {
//        return order;
//    }
//
//    public void setOrder(int order) {
//        this.order = order;
//    }
//
//    @Jsonable
//    @Column(name = "c_content")
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    @Jsonable
//    @Column(name = "c_time")
//    public long getTime() {
//        return time;
//    }
//
//    public void setTime(long time) {
//        this.time = time;
//    }
}