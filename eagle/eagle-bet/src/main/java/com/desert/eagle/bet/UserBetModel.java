package com.desert.eagle.bet;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname UserBetModel
 * @Description TODO
 * @Date 2023/5/16 22:23
 * @Created by yiren
 */
public class UserBetModel implements Serializable {

    private String type;
    private String ratio = "赔率";
    private String val = "注额";

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<UserBets> getUserBetsList() {
        return userBetsList;
    }

    public void setUserBetsList(List<UserBets> userBetsList) {
        this.userBetsList = userBetsList;
    }

    private List<UserBets> userBetsList;

    public static class UserBets {
        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        private String item;
        private String rate;
        private String amount;
    }

}
