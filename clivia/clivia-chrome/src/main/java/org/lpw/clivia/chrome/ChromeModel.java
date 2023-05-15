package org.lpw.clivia.chrome;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(ChromeModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ChromeModel.NAME)
@Table(name = "t_chrome")
public class ChromeModel extends ModelSupport {
    static final String NAME = "clivia.chrome";

    private String key; // 引用key
    private String name; // 名称
    private int x; // 图片X位置
    private int y; // 图片Y位置
    private int width; // 宽度
    private int height; // 高度
    private String pages; // 页面集
    private int wait; // 等待时长，单位：秒
    private String filename; // 文件名

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @Column(name = "c_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Jsonable
    @Column(name = "c_x")
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Jsonable
    @Column(name = "c_y")
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Jsonable
    @Column(name = "c_width")
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Jsonable
    @Column(name = "c_height")
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Jsonable
    @Column(name = "c_pages")
    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    @Jsonable
    @Column(name = "c_wait")
    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    @Jsonable
    @Column(name = "c_filename")
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
