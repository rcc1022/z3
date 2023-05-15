package com.desert.eagle.football;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(FootballModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = FootballModel.NAME)
@Table(name = "t_football")
public class FootballModel extends ModelSupport {
    static final String NAME = "eagle.football";

    private int group; // 组
    private String gid; // 比赛ID
    private int sort; // 排序
    private String league; // 联赛
    private String teamH; // 主队名
    private String teamC; // 客队名
    private String strong; // 强队
    private int scoreH; // 主队得分
    private int scoreC; // 客队得分
    private int scoreShangH; // 主队上半场得分
    private int scoreShangC; // 客队上半场得分
    private String rangQiu; // 让球
    private String rangQiuH; // 主队让球
    private String rangQiuC; // 客队让球
    private String rangQiuShang; // 上半场让球
    private String rangQiuShangH; // 上半场主队让球
    private String rangQiuShangC; // 上半场客队让球
    private String deFenH; // 主队得分
    private String deFenC; // 客队得分
    private String deFenRateH; // 主队得分赔率
    private String deFenRateC; // 客队得分赔率
    private String deFenShangH; // 主队上半场得分
    private String deFenShangC; // 客队上半场得分
    private String deFenRateShangH; // 主队上半场得分赔率
    private String deFenRateShangC; // 客队上半场得分赔率
    private String duYing; // 独赢
    private String duYingH; // 主队独赢
    private String duYingC; // 客队独赢
    private String duYingHe; // 独赢和
    private String duYingShang; // 上半场独赢
    private String duYingShangH; // 上半场主队独赢
    private String duYingShangC; // 上半场客队独赢
    private String duYingShangHe; // 上半场独赢和
    private String boDan; // 波胆赔率
    private String datetime; // 时间
    private String timer; // 计时器
    private int on; // 开启：0-否；1-是
    private long timestamp; // 更新时间戳
    private int zero; // 空数据：0-否；1-是

    @Jsonable
    @Column(name = "c_group")
    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Jsonable
    @Column(name = "c_gid")
    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    @Jsonable
    @Column(name = "c_sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Jsonable
    @Column(name = "c_league")
    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    @Jsonable
    @Column(name = "c_team_h")
    public String getTeamH() {
        return teamH;
    }

    public void setTeamH(String teamH) {
        this.teamH = teamH;
    }

    @Jsonable
    @Column(name = "c_team_c")
    public String getTeamC() {
        return teamC;
    }

    public void setTeamC(String teamC) {
        this.teamC = teamC;
    }

    @Jsonable
    @Column(name = "c_strong")
    public String getStrong() {
        return strong;
    }

    public void setStrong(String strong) {
        this.strong = strong;
    }

    @Jsonable
    @Column(name = "c_score_h")
    public int getScoreH() {
        return scoreH;
    }

    public void setScoreH(int scoreH) {
        this.scoreH = scoreH;
    }

    @Jsonable
    @Column(name = "c_score_c")
    public int getScoreC() {
        return scoreC;
    }

    public void setScoreC(int scoreC) {
        this.scoreC = scoreC;
    }

    @Jsonable
    @Column(name = "c_score_shang_h")
    public int getScoreShangH() {
        return scoreShangH;
    }

    public void setScoreShangH(int scoreShangH) {
        this.scoreShangH = scoreShangH;
    }

    @Jsonable
    @Column(name = "c_score_shang_c")
    public int getScoreShangC() {
        return scoreShangC;
    }

    public void setScoreShangC(int scoreShangC) {
        this.scoreShangC = scoreShangC;
    }

    @Jsonable
    @Column(name = "c_rang_qiu")
    public String getRangQiu() {
        return rangQiu;
    }

    public void setRangQiu(String rangQiu) {
        this.rangQiu = rangQiu;
    }

    @Jsonable
    @Column(name = "c_rang_qiu_h")
    public String getRangQiuH() {
        return rangQiuH;
    }

    public void setRangQiuH(String rangQiuH) {
        this.rangQiuH = rangQiuH;
    }

    @Jsonable
    @Column(name = "c_rang_qiu_c")
    public String getRangQiuC() {
        return rangQiuC;
    }

    public void setRangQiuC(String rangQiuC) {
        this.rangQiuC = rangQiuC;
    }

    @Jsonable
    @Column(name = "c_rang_qiu_shang")
    public String getRangQiuShang() {
        return rangQiuShang;
    }

    public void setRangQiuShang(String rangQiuShang) {
        this.rangQiuShang = rangQiuShang;
    }

    @Jsonable
    @Column(name = "c_rang_qiu_shang_h")
    public String getRangQiuShangH() {
        return rangQiuShangH;
    }

    public void setRangQiuShangH(String rangQiuShangH) {
        this.rangQiuShangH = rangQiuShangH;
    }

    @Jsonable
    @Column(name = "c_rang_qiu_shang_c")
    public String getRangQiuShangC() {
        return rangQiuShangC;
    }

    public void setRangQiuShangC(String rangQiuShangC) {
        this.rangQiuShangC = rangQiuShangC;
    }

    @Jsonable
    @Column(name = "c_de_fen_h")
    public String getDeFenH() {
        return deFenH;
    }

    public void setDeFenH(String deFenH) {
        this.deFenH = deFenH;
    }

    @Jsonable
    @Column(name = "c_de_fen_c")
    public String getDeFenC() {
        return deFenC;
    }

    public void setDeFenC(String deFenC) {
        this.deFenC = deFenC;
    }

    @Jsonable
    @Column(name = "c_de_fen_rate_h")
    public String getDeFenRateH() {
        return deFenRateH;
    }

    public void setDeFenRateH(String deFenRateH) {
        this.deFenRateH = deFenRateH;
    }

    @Jsonable
    @Column(name = "c_de_fen_rate_c")
    public String getDeFenRateC() {
        return deFenRateC;
    }

    public void setDeFenRateC(String deFenRateC) {
        this.deFenRateC = deFenRateC;
    }

    @Jsonable
    @Column(name = "c_de_fen_shang_h")
    public String getDeFenShangH() {
        return deFenShangH;
    }

    public void setDeFenShangH(String deFenShangH) {
        this.deFenShangH = deFenShangH;
    }

    @Jsonable
    @Column(name = "c_de_fen_shang_c")
    public String getDeFenShangC() {
        return deFenShangC;
    }

    public void setDeFenShangC(String deFenShangC) {
        this.deFenShangC = deFenShangC;
    }

    @Jsonable
    @Column(name = "c_de_fen_rate_shang_h")
    public String getDeFenRateShangH() {
        return deFenRateShangH;
    }

    public void setDeFenRateShangH(String deFenRateShangH) {
        this.deFenRateShangH = deFenRateShangH;
    }

    @Jsonable
    @Column(name = "c_de_fen_rate_shang_c")
    public String getDeFenRateShangC() {
        return deFenRateShangC;
    }

    public void setDeFenRateShangC(String deFenRateShangC) {
        this.deFenRateShangC = deFenRateShangC;
    }

    @Jsonable
    @Column(name = "c_du_ying")
    public String getDuYing() {
        return duYing;
    }

    public void setDuYing(String duYing) {
        this.duYing = duYing;
    }

    @Jsonable
    @Column(name = "c_du_ying_h")
    public String getDuYingH() {
        return duYingH;
    }

    public void setDuYingH(String duYingH) {
        this.duYingH = duYingH;
    }

    @Jsonable
    @Column(name = "c_du_ying_c")
    public String getDuYingC() {
        return duYingC;
    }

    public void setDuYingC(String duYingC) {
        this.duYingC = duYingC;
    }

    @Jsonable
    @Column(name = "c_du_ying_he")
    public String getDuYingHe() {
        return duYingHe;
    }

    public void setDuYingHe(String duYingHe) {
        this.duYingHe = duYingHe;
    }

    @Jsonable
    @Column(name = "c_du_ying_shang")
    public String getDuYingShang() {
        return duYingShang;
    }

    public void setDuYingShang(String duYingShang) {
        this.duYingShang = duYingShang;
    }

    @Jsonable
    @Column(name = "c_du_ying_shang_h")
    public String getDuYingShangH() {
        return duYingShangH;
    }

    public void setDuYingShangH(String duYingShangH) {
        this.duYingShangH = duYingShangH;
    }

    @Jsonable
    @Column(name = "c_du_ying_shang_c")
    public String getDuYingShangC() {
        return duYingShangC;
    }

    public void setDuYingShangC(String duYingShangC) {
        this.duYingShangC = duYingShangC;
    }

    @Jsonable
    @Column(name = "c_du_ying_shang_he")
    public String getDuYingShangHe() {
        return duYingShangHe;
    }

    public void setDuYingShangHe(String duYingShangHe) {
        this.duYingShangHe = duYingShangHe;
    }

    @Jsonable
    @Column(name = "c_bo_dan")
    public String getBoDan() {
        return boDan;
    }

    public void setBoDan(String boDan) {
        this.boDan = boDan;
    }

    @Jsonable
    @Column(name = "c_datetime")
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Jsonable
    @Column(name = "c_timer")
    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    @Jsonable
    @Column(name = "c_on")
    public int getOn() {
        return on;
    }

    public void setOn(int on) {
        this.on = on;
    }

    @Jsonable
    @Column(name = "c_timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Jsonable
    @Column(name = "c_zero")
    public int getZero() {
        return zero;
    }

    public void setZero(int zero) {
        this.zero = zero;
    }
}