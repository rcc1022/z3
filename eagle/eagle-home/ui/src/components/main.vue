<script>
import { service, url, illegal } from "../util/http";
import { sign } from "../util/user";
import { keyvalue } from "../util/keyvalue";
import { randomMax } from "../util/random";
import home from "@/assets/home.png";
import record from "@/assets/record.png";
import invite from "@/assets/invite.png";
import kefu from "@/assets/kefu.png";
import deposit from "@/assets/deposit.png";
import withdraw from "@/assets/withdraw.png";
import mine from "@/assets/mine.png";
import Sign from "./sign.vue";
import Home from "./home.vue";
import Record from "./record.vue";
import Invite from "./invite.vue";
import Kefu from "./kefu.vue";
import Deposit from "./deposit.vue";
import Withdraw from "./withdraw.vue";
import Mine from "./mine.vue";
import Rule from "./rule.vue";

export default {
  components: {
    Sign,
    Home,
    Record,
    Invite,
    Kefu,
    Deposit,
    Withdraw,
    Mine,
    Rule,
  },
  data() {
    return {
      page: -2,
      splash: {
        uri: [],
        url: "",
      },
      navs: [
        {
          icon: home,
          label: "首页",
        },
        {
          icon: record,
          label: "记录",
        },
        {
          icon: invite,
          label: "推广",
        },
        {
          icon: kefu,
          label: "客服",
        },
        {
          icon: deposit,
          label: "充值",
        },
        {
          icon: withdraw,
          label: "提现",
        },
        {
          icon: mine,
          label: "我的",
        },
      ],
      navigator: 0,
      rule: "",
      player: {},
      redirect: [],
      enter: 5,
      timeout: 0,
      illegal: "",
    };
  },
  mounted() {
    keyvalue("setting.splash.", (data) => {
      if (
        data["setting.splash.wei-xin"] === "1" &&
        navigator.userAgent.toLowerCase().indexOf("micromessenger") === -1
      ) {
        location.href = data["setting.splash.illegal"];

        return;
      }

      this.splash.url = url(data["setting.splash.image"]);
    });

    if (location.search && location.search.indexOf("invite=") > -1) {
      let code = location.search.substring(
        location.search.indexOf("invite=") + 7
      );
      if (code.indexOf("&") > -1) code = code.substring(0, code.indexOf("&"));
      service("/player/inviter", { code }, (data) => this.start());
    } else {
      this.start();
    }
  },
  methods: {
    start: function () {
      keyvalue("setting.home.", (data) => {
        document.title = data["setting.home.title"] || "";
        // try {
        //     this.splash.uri = (data['setting.home.jin-ru'] || '').split(',');
        //     this.redirect = (data['setting.home.redirect'] || '').split(',');
        //     this.enter = parseInt(data['setting.home.auto'] || '0') || 5;
        // } catch (e) { }
        this.illegal = data["setting.home.illegal"];
        if (this.illegal) illegal(this.illegal);
        this.signin();
      });
    },
    signin: function () {
      sign((player) => {
        if (player.id) {
          this.player = player;
          // if (this.splash.uri && this.splash.uri.length > 0) {
          //     let time = 0;
          //     try {
          //         time = parseInt(localStorage.getItem('redirect-time') || '0');
          //     } catch (e) { }
          //     if (new Date().getTime() - time > 60 * 1000) {
          //         this.splash.url = url(this.splash.uri[randomMax(this.splash.uri.length)]);
          //         this.page = 3;
          //         if (this.enter > 0)
          //             this.autoEnter();
          //         localStorage.setItem('redirect-time', '' + new Date().getTime());

          //         return;
          //     }
          // }

          this.page = -1;
          this.ip();
          setInterval(() => {
            service("/olcs/member/user", { uid: this.player.uid }, (data) => {
              this.navs[3].badge = data.unread;
            });
          }, 1000);
        } else this.page = 1;
      });
    },
    splashTo: function () {
      this.page = 0;
      this.autoEnter();
    },
    autoEnter: function () {
      if (this.enter <= 0) {
        this.jumpNow();
      } else {
        this.enter--;
        this.timeout = setTimeout(this.autoEnter.bind(this), 1000);
      }
    },
    jumpNow: function () {
      // if (this.redirect && this.redirect.length > 0)
      //     location.href = this.redirect[randomMax(this.redirect.length)];
      // else
      this.page = 2;
    },
    stopJump: function () {
      clearTimeout(this.timeout);
    },
    backHome: function () {
      location.href = this.illegal;
    },
    navigate: function (index) {
      this.navigator = 99;
      setTimeout(() => {
        this.navigator = index;
      }, 10);
    },
    showRule: function (game) {
      this.rule = game;
      this.navigator = 7;
    },
    ip: function () {
      service("/player/ip", {});
      // setTimeout(() => {
      //     if (this.page < 2)
      //         this.page = 2;
      // }, 1000 + Math.round(Math.random() * 2000));
    },
  },
};
</script>

<template>
  <!-- <div class="main-loading" v-if="(page === 0)">
                                    <van-loading :vertical="true" size="50vw">正在加载</van-loading>
                                </div> -->
  <div v-if="page === -1">
    <img class="splash-image" :src="splash.url" @click="splashTo" />
  </div>
  <div v-if="page === 0" id="sign-in">
    <div>
      <!-- <div v-if="!player.nick">正在登录</div>
                                            <div v-else-if="player.nick.indexOf('微信') > -1">请授权微信认证</div>
                                            <div v-else> -->
      <div class="sign-in-success">登录成功</div>
      <div class="sign-in-jump">
        页面自动<span class="jump" @click="jumpNow">跳转</span>等待时间：<span
          class="second"
          >{{ enter }}</span
        >秒
      </div>
      <div class="sign-in-jumps">
        <div class="sign-in-jump-now" @click="jumpNow">立即跳转</div>
        <div class="sign-in-jump-stop" @click="stopJump">禁止跳转</div>
        <div class="sign-in-jump-home" @click="backHome">返回首页</div>
      </div>
      <!-- </div> -->
    </div>
  </div>
  <Sign v-if="page === 1" />
  <div v-if="page === 3">
    <img :src="splash.url" class="splash" @click="jumpNow" />
  </div>
  <div v-if="page === 2" id="body">
    <Home v-if="navigator === 0" @rule="(game) => showRule(game)" />
    <Record v-else-if="navigator === 1" />
    <Invite v-else-if="navigator === 2" />
    <Kefu v-else-if="navigator === 3" />
    <Deposit
      v-else-if="navigator === 4"
      @navigate="(index) => (navigator = index)"
    />
    <Withdraw
      v-else-if="navigator === 5"
      @navigate="(index) => (navigator = index)"
    />
    <Mine
      v-else-if="navigator === 6"
      @navigate="(index) => (navigator = index)"
      :illegal="illegal"
    />
    <Rule v-else-if="navigator == 7" :game="rule" />
    <div v-else></div>
  </div>
  <div v-if="page === 2" id="navigation">
    <div v-for="(nav, index) in navs" class="item" @click="navigate(index)">
      <van-badge v-if="nav.badge" :content="nav.badge">
        <img :src="nav.icon" />
      </van-badge>
      <img v-else :src="nav.icon" />
      <div>{{ nav.label }}</div>
    </div>
  </div>
</template>

<style scoped>
.main-loading {
  padding-top: 10vh;
}

.splash-image {
  display: block;
  width: 100vw;
}

#sign-in {
  height: 100vh;
  background-color: rgb(246, 246, 246);
  text-align: center;
  padding-top: 32px;
}

#sign-in > div {
  background-color: #fff;
  padding: 16px;
}

.sign-in-success {
  color: rgb(107, 191, 129);
  font-size: 32px;
  line-height: 64px;
  font-weight: bold;
}

.sign-in-jump {
  color: rgb(106, 106, 106);
}

.sign-in-jump .jump {
  padding: 0 4px;
  color: rgb(119, 152, 187);
}

.sign-in-jump .second {
  font-weight: bold;
  color: #000;
}

.sign-in-jumps {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 16px 32px;
}

.sign-in-jumps div {
  padding: 10px 16px 6px 16px;
  font-size: 14px;
  line-height: 14px;
  border-radius: 32px;
}

.sign-in-jump-now {
  background-color: rgb(106, 192, 129);
  border: 1px solid rgb(93, 154, 110);
  color: #fff;
}

.sign-in-jump-stop {
  background-color: rgb(234, 182, 111);
  border: 1px solid rgb(220, 166, 92);
  color: #fff;
}

.sign-in-jump-home {
  background-color: rgb(244, 244, 244);
  border: 1px solid rgb(228, 228, 228);
  color: rgb(87, 87, 87);
}

.splash {
  display: block;
  width: 100vw;
}

#body {
  height: calc(100vh - 50px - env(safe-area-inset-bottom));
  height: calc(100vh - 50px - constant(safe-area-inset-bottom));
  overflow: auto;
  background-color: #111111;
}

#navigation {
  height: 50px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  background-color: #353333;
}

#navigation .item {
  text-align: center;
}

#navigation .item img {
  width: 17.92px;
}

#navigation .item div {
  line-height: 12px;
  font-size: 11px;
  color: #ead5b7;
  font-weight: 500;
}
</style>
