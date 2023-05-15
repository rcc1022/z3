package org.lpw.clivia.upgrader;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(UpgraderModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = UpgraderModel.NAME)
@Table(name = "t_upgrader")
public class UpgraderModel extends ModelSupport {
    static final String NAME = "clivia.upgrader";

    private int version; // 版本号
    private String name; // 版本名
    private int forced; // 强制升级：0-否；1-是
    private String explain; // 说明
    private String android; // Android升级URL
    private String ios; // iOS升级URL
    private String windows; // Windows升级URL
    private String macos; // MacOS升级URL
    private String linux; // Linux升级URL
    private String file; // 升级包

    @Jsonable
    @Column(name = "c_version")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
    @Column(name = "c_forced")
    public int getForced() {
        return forced;
    }

    public void setForced(int forced) {
        this.forced = forced;
    }

    @Jsonable
    @Column(name = "c_explain")
    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @Jsonable
    @Column(name = "c_android")
    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    @Jsonable
    @Column(name = "c_ios")
    public String getIos() {
        return ios;
    }

    public void setIos(String ios) {
        this.ios = ios;
    }

    @Jsonable
    @Column(name = "c_windows")
    public String getWindows() {
        return windows;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    @Jsonable
    @Column(name = "c_macos")
    public String getMacos() {
        return macos;
    }

    public void setMacos(String macos) {
        this.macos = macos;
    }

    @Jsonable
    @Column(name = "c_linux")
    public String getLinux() {
        return linux;
    }

    public void setLinux(String linux) {
        this.linux = linux;
    }

    @Jsonable
    @Column(name = "c_file")
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
