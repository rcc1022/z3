package com.desert.eagle.channel;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(ChannelModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ChannelModel.NAME)
@Table(name = "t_channel")
public class ChannelModel extends ModelSupport {
    static final String NAME = "eagle.channel";

    private int sort; // 顺序
    private String name; // 名称
    private String icon; // 图标
    private String qrcode; // 二维码
    private int min; // 最小金额
    private int max; // 最大金额

    @Jsonable
    @Column(name = "c_sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
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
    @Column(name = "c_icon")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Jsonable
    @Column(name = "c_qrcode")
    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Jsonable
    @Column(name = "c_min")
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Jsonable
    @Column(name = "c_max")
    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}