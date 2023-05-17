<script>
import cm100 from "@/assets/cm100.png";
import cm200 from "@/assets/cm200.png";
import cm500 from "@/assets/cm500.png";
import cm1k from "@/assets/cm1k.png";
import cm3k from "@/assets/cm3k.png";
import cm5k from "@/assets/cm5k.png";
import cm10k from "@/assets/cm10k.png";
import check from "@/assets/check.png";
import { service, url } from "../util/http";
import { sign } from "../util/user";
import { countDown } from "../util/datetime";
import confirm from "./confirm.vue";
import alert from "./alert.vue";

export default {
  components: { confirm, alert },
  emits: ["rule"],
  props: ["game"],
  data() {
    return {
      marquee: "",
      next: "",
      close: "",
      open: "",
      issue: "",
      num1: 0,
      num2: 0,
      num3: 0,
      num4: 0,
      num5: 0,
      num6: 0,
      num7: 0,
      num8: 0,
      num9: 0,
      num10: 0,
      cover: {
        on: false,
        show: false,
        select: false,
        drawing: false,
        canvas: null,
      },
      player: {},
      message: {
        list: [],
        time: 0,
      },
      amount: "",
      quick: {
        show: false,
        tabs: [
          "冠军",
          "亚军",
          "第三名",
          "第四名",
          "第五名",
          "第六名",
          "第七名",
          "第八名",
          "第九名",
          "第十名",
          "冠亚和",
        ],
        tab: 0,
        sayhias: [],
        sayhia: [],
        sayhiaTabs: [],
        chouma: [
          {
            amount: 100,
            icon: cm100,
          },
          {
            amount: 200,
            icon: cm200,
          },
          {
            amount: 500,
            icon: cm500,
          },
          {
            amount: 1000,
            icon: cm1k,
          },
          {
            amount: 3000,
            icon: cm3k,
          },
          {
            amount: 5000,
            icon: cm5k,
          },
          {
            amount: 10000,
            icon: cm10k,
          },
        ],
        amount: "",
      },
      submit: {
        show: false,
        sayhias: [],
        count: 0,
        sum: 0,
        sayhiaing: false,
      },
      zhuihao: {
        show: false,
        type: "",
        name: "",
        amount: 10,
        count: 1,
        multiple: 1,
        items: [],
        stop: 0,
        check: check,
      },
      timeout: true,
      history: {
        show: false,
        list: [],
      },
      video: {
        show: false,
        urls: [
          "https://kj.kjose122.com/view/video/jisuft_video/index.html?10057?1680233.com",
          "",
          "https://kj.kjose122.com/view/video/PK10/video.html?10012?1680233.com",
          "https://kj.kjose122.com/view/video/PK10/video.html?10037?1682018.co",
        ],
        url: "",
      },
      alert: {
        show: false,
        content: "",
      },
      cancel: false,
    };
  },
  mounted() {
    document.title = this.game.name || "";
    service("/keyvalue/object", { key: "setting.home." }, (data) => {
      this.marquee = data["setting.home.gun-don-gong-gao"] || "";
    });
    service("/rate/list", { game: this.game.id }, (data) => {
      this.quick.sayhias = [];
      for (let i = 0; i < this.quick.tabs.length; i++) {
        this.quick.sayhias.push(data[this.quick.tabs[i]]);
      }
    });
    this.video.url = this.video.urls[this.game.type - 6];
    if (this.game.type === 7) {
      service("/domain/video", {}, (data) => {
        this.video.url =
          "http://" + data + "/view/video/PK10/video.html?10037?1680233.com";
      });
    }
    this.timer();
  },
  beforeUnmount() {
    this.timeout = false;
  },
  methods: {
    color(number) {
      switch (number) {
        case 1:
          return "rgb(255, 255, 0)";

        case 2:
          return "rgb(0, 137, 255)";

        case 3:
          return "rgb(77, 77, 77)";

        case 4:
          return "rgb(255, 115, 0)";

        case 5:
          return "rgb(129, 255, 255)";

        case 6:
          return "rgb(82, 0, 255)";

        case 7:
          return "rgb(186, 185, 185)";

        case 8:
          return "rgb(255, 0, 0)";

        case 9:
          return "rgb(118, 0, 0)";

        case 10:
          return "rgb(40, 195, 0)";

        default:
          break;
      }
    },
    timer() {
      if (!this.timeout) return;

      setTimeout(this.timer.bind(this), 1000);
      this.latest();
      this.sign();
      this.msg();
    },
    latest() {
      service("/scnum/latest", { game: this.game.id }, (data) => {
        if (!data.id) {
          this.next = "已停止";
          this.issue = "已停止";

          return;
        }

        if (data.close === 0 && this.close != "已封盘") {
          this.coverShow();
        }

        this.next = data.next;
        this.close = data.close === 0 ? "已封盘" : countDown(data.close);
        this.open = data.open === 0 ? "开奖中" : countDown(data.open);
        this.issue = data.issue;
        this.num1 = data.num1;
        this.num2 = data.num2;
        this.num3 = data.num3;
        this.num4 = data.num4;
        this.num5 = data.num5;
        this.num6 = data.num6;
        this.num7 = data.num7;
        this.num8 = data.num8;
        this.num9 = data.num9;
        this.num10 = data.num10;
      });
    },
    sign() {
      if (this.cover.show) {
        this.player.balance = "***";
        this.player.profit = "***";

        return;
      }

      sign((player) => {
        this.player.avatar = player.avatar;
        this.player.nick = player.nick;
        this.player.balance = Math.floor(player.balance / 100);
        if (player.profit) {
          let profit = Math.floor(player.profit / 100);
          this.player.profit = (profit > 0 ? "+" : "") + profit;
        } else {
          this.player.profit = 0;
        }
      });
    },
    msg() {
      service(
        "/message/query",
        { game: this.game.id, time: this.message.time },
        (data) => {
          if (data.list.length === 0) return;

          this.message.time = data.list[0].time;
          while (data.list.length > 0) {
            let msg = data.list.pop();
            if (msg.type >= 2) {
              if (msg.type === 2) msg.player.avatar = url(msg.player.avatar);
              msg.content = JSON.parse(msg.content);
              if (msg.type === 3) {
                for (let i = 0; i < msg.content.sayhias.length; i++) {
                  let sayhia = msg.content.sayhias[i];
                  sayhia.player.avatar = url(sayhia.player.avatar);
                }
              }
            }
            this.message.list.push(msg);
          }
          this.$nextTick(() => {
            this.$refs.scChat.scrollTop = this.$refs.scChat.scrollHeight;
          });
        }
      );
    },
    quickSayhia(index) {
      let sayhia = this.quick.tab + "," + index;
      let indexOf = this.quick.sayhia.indexOf(sayhia);
      if (indexOf === -1) {
        this.quick.sayhia.push(sayhia);
      } else {
        this.quick.sayhia[indexOf] = "";
      }
      let tabs = [];
      for (let i = 0; i < this.quick.sayhia.length; i++) {
        let b = this.quick.sayhia[i];
        if (!b || b.indexOf(",") === -1) continue;

        let indexOf = b.indexOf(",");
        tabs[parseInt(b.substring(0, indexOf))] = true;
      }
      this.quick.sayhiaTabs = tabs;
    },
    quickSayhiaTo() {
      let amount = parseInt(this.quick.amount);
      if (!amount) {
        this.showAlert("请输入元宝");

        return;
      }

      this.submit.sayhias = [];
      for (let i = 0; i < this.quick.sayhia.length; i++) {
        let sayhia = this.quick.sayhia[i];
        if (sayhia === "") continue;

        sayhia = sayhia.split(",");
        this.submit.sayhias.push({
          type: this.quick.tabs[sayhia[0]],
          name: this.quick.sayhias[sayhia[0]][sayhia[1]].name,
          amount: amount,
        });
      }
      if (this.submit.sayhias.length === 0) {
        this.showAlert("请选择下注项");

        return;
      }

      this.submit.count = this.submit.sayhias.length;
      this.submit.sum = this.submit.count * amount;
      this.submit.show = true;
    },
    amountFocus(e) {
      e.target.blur();
      this.quick.show = true;
    },
    send() {
      if (!this.amount) {
        this.showAlert("格式错误");

        return;
      }

      let indexOf = this.amount.indexOf("/");
      let lastIndexOf = this.amount.lastIndexOf("/");
      if (indexOf === -1 || indexOf === this.amount.lastIndexOf("/")) {
        this.showAlert("格式错误");

        return;
      }

      let sayhia = {
        type: this.amount.substring(0, indexOf),
        name: this.amount.substring(indexOf + 1, lastIndexOf),
        amount: this.amount.substring(lastIndexOf + 1),
      };

      this.submit.sayhias = [sayhia];
      this.submit.count = 1;
      this.submit.sum = sayhia.amount;
      this.submit.show = true;
    },
    sayhia() {
      if (this.submit.sayhiaing) return;

      this.submit.sayhiaing = true;
      service(
        "/sayhia/save",
        {
          game: this.game.id,
          issue: this.next,
          items: JSON.stringify(this.submit.sayhias),
        },
        (data) => {
          this.submit.sayhiaing = false;
          if (data === 0) {
            this.sign();
            this.reset();
            this.submit.show = false;
            this.quick.show = false;
          } else {
            if (data === 2) this.showAlert("已封盘");
            else if (data % 10 === 4)
              this.showAlert("投注额不能低于" + Math.floor(data / 10));
            else if (data % 10 === 5)
              this.showAlert("投注额不能高于" + Math.floor(data / 10));
            else if (data === 6) this.showAlert("当期下注已满额");
            else if (data === 7) this.showAlert("余额不足");
            else this.showAlert("参数错误" + data);
          }
        }
      );
    },
    reset() {
      this.amount = "";
      this.quick.amount = "";
      this.quick.sayhia = [];
    },
    showHistory() {
      this.history.show = true;
      service(
        "/scnum/list",
        { type: Math.floor(this.game.type - 6) },
        (data) => {
          this.history.list = data;
        }
      );
    },
    showVideo() {
      this.video.show = true;
    },
    hideVideo() {
      this.video.show = false;
    },
    showAlert(content) {
      this.alert.content = content;
      this.alert.show = true;
    },
    cancelOk() {
      this.cancel = false;
      service("/sayhia/cancel", { game: this.game.id }, (data) => {
        if (data === 0) this.showAlert("撤单成功");
        else if (data === 1) this.showAlert("投注期数不正确，不能撤单");
        else if (data === 2) this.showAlert("已封盘无法撤单");
        else if (data === 3) this.showAlert("本期无投注");
      });
    },
    coverOnOff() {
      this.cover.on = !this.cover.on;
      this.coverShow();
    },
    coverShow() {
      this.cover.show = this.cover.on;
      if (this.cover.show) {
        this.cover.show = false;
        setTimeout(() => {
          this.cover.show = true;
        }, 50);
        setTimeout(() => {
          let canvas = this.$refs.canvas.getContext("2d");
          canvas.globalCompositeOperation = "";
          canvas.fillStyle = "#999";
          canvas.fillRect(0, 0, 100, 100);
          canvas.strokeStyle = "#333";
          canvas.lineWidth = 5;
          canvas.globalCompositeOperation = "destination-out";
          this.cover.canvas = canvas;
        }, 100);
      }
    },
    coverMove(e) {
      if (this.close === "已封盘") return;

      let px = e.touches[0].pageX;
      let py = e.touches[0].pageY;
      let left = this.$refs.canvas.offsetLeft;
      let top = this.$refs.canvas.offsetTop;
      let width = this.$refs.canvas.clientWidth;
      let height = this.$refs.canvas.clientHeight;
      if (px < left || px > left + width || py < top || py > top + height)
        return;

      let x = ((px - left) / width) * 100;
      let y = ((py - top) / height) * 100;
      this.cover.canvas.lineTo(x, y);
      this.cover.canvas.stroke();
    },
    showZhuihao() {
      let sayhia = "";
      for (let i = 0; i < this.quick.sayhia.length; i++) {
        let b = this.quick.sayhia[i];
        if (b === "") continue;

        if (sayhia != "") return;

        sayhia = b;
      }
      if (sayhia === "") return;

      sayhia = sayhia.split(",");
      let type = parseInt(sayhia[0]);
      this.zhuihao.type = this.quick.tabs[type];
      this.zhuihao.name = this.quick.sayhias[type][parseInt(sayhia[1])].name;
      this.zhuihao.items = [];
      this.zhuihao.show = true;
    },
    zhuihaoCreate() {
      this.zhuihao.items = [];
      for (let i = 0; i < this.zhuihao.count; i++) {
        this.zhuihao.items.push({
          issue: "+1",
          amount: this.zhuihao.amount * Math.pow(this.zhuihao.multiple, i),
          multiple: Math.pow(this.zhuihao.multiple, i),
        });
      }
      this.zhuihao.items[0].issue = this.next;
    },
    zhuihaoSubmit() {
      service(
        "/sayhia/zhuihao",
        {
          game: this.game.id,
          issue: this.next,
          type: this.zhuihao.type,
          name: this.zhuihao.name,
          amount: this.zhuihao.amount,
          count: this.zhuihao.count,
          multiple: this.zhuihao.multiple,
          stop: this.zhuihao.stop ? 1 : 0,
        },
        (data) => {
          if (data === 0) {
            this.sign();
            this.reset();
            this.submit.show = false;
            this.quick.show = false;
            this.zhuihao.show = false;
          } else {
            if (data === 2) this.showAlert("已封盘");
            else if (data === 4)
              this.showAlert("投注额不能低于" + this.game.min);
            else if (data === 5)
              this.showAlert("投注额不能高于" + this.game.max);
            else if (data === 6) this.showAlert("当期下注已满额");
            else if (data === 7) this.showAlert("余额不足");
            else this.showAlert("参数错误" + data);
          }
        }
      );
    },
  },
};
</script>
<template>
  <van-notice-bar
    class="sc-marque-notice-bar"
    color="#73541F"
    background="#E3C084"
    :text="marquee"
  />
  <div id="next">
    <div>
      当前<span class="issue">{{ next }}</span
      >期　距封盘：<span class="state">{{ close }}</span
      >　距开奖：<span>{{ open }}</span>
    </div>
  </div>
  <div class="sc-mine">
    <div class="sc-mine-avatar-nick">
      <div class="sc-mine-avatar">
        <img v-if="player.avatar" :src="player.avatar" />
      </div>
      <div class="sc-mine-nick">{{ player.nick }}</div>
    </div>
    <div class="sc-mine-balance">
      元宝<span>{{ player.balance }}</span>
    </div>
    <div class="sc-mine-balance">
      盈亏<span>{{ player.profit }}</span>
    </div>
  </div>
  <div id="latest" @click="showHistory">
    <div class="issue">
      第<span>{{ issue }}</span
      >期
    </div>
    <div class="num" :style="{ background: this.color(num1) }">{{ num1 }}</div>
    <div class="num" :style="{ background: this.color(num2) }">{{ num2 }}</div>
    <div class="num" :style="{ background: this.color(num3) }">{{ num3 }}</div>
    <div class="num" :style="{ background: this.color(num4) }">{{ num4 }}</div>
    <div class="num" :style="{ background: this.color(num5) }">
      {{ num5 }}
    </div>
    <div class="num" :style="{ background: this.color(num6) }">{{ num6 }}</div>
    <div class="num" :style="{ background: this.color(num7) }">
      {{ num7 }}
    </div>
    <div class="num" :style="{ background: this.color(num8) }">{{ num8 }}</div>
    <div class="num" :style="{ background: this.color(num9) }">{{ num9 }}</div>
    <div class="num" :style="{ background: this.color(num10) }">
      {{ num10 }}
    </div>
  </div>
  <canvas
    ref="canvas"
    class="sc-open-cover"
    v-if="cover.show"
    width="100"
    height="100"
    @touchmove="coverMove"
  ></canvas>
  <div :class="cover.show ? 'sc-body-cover' : 'sc-body'">
    <div class="sc-chat" ref="scChat">
      <div v-for="msg in message.list" class="sc-chat-message">
        <div v-if="msg.type === 0" class="sc-chat-open">
          第{{ msg.content }}期开盘
        </div>
        <div v-else-if="msg.type === 1" class="sc-chat-close">
          第{{ msg.content }}期封盘
        </div>
        <div v-else-if="msg.type === 2" class="sc-chat-sayhia">
          <div class="sc-chat-sayhia-avatar">
            <img v-if="msg.player.avatar" :src="msg.player.avatar" />
          </div>
          <div>
            <div class="sc-chat-sayhia-nick">
              {{ msg.player.nick }}<span>{{ msg.timestr }}</span>
            </div>
            <div class="sc-chat-sayhia-content">
              <div class="sc-chat-sayhia-issue-sum">
                <div>{{ msg.content.issue }}期</div>
                <div>总计：￥{{ Math.floor(msg.content.sum / 100) }}</div>
              </div>
              <div
                v-for="item in msg.content.items"
                class="sc-chat-sayhia-item"
              >
                <div>投注内容：{{ item.type }}/{{ item.name }}</div>
                <div>￥{{ item.amount }}</div>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="msg.type === 3" class="sc-chat-sayhias">
          <div class="sc-chat-sayhias-title">
            {{ msg.content.issue }}期投注详情
          </div>
          <div
            class="sc-chat-sayhias-line"
            v-for="sayhia in msg.content.sayhias"
          >
            <div class="sc-chat-sayhias-avatar-nick">
              <div class="sc-chat-sayhias-avatar">
                <img v-if="sayhia.player.avatar" :src="sayhia.player.avatar" />
              </div>
              <div class="sc-chat-sayhias-nick">{{ sayhia.player.nick }}</div>
            </div>
            <div class="sc-chat-sayhias-item">
              {{ sayhia.type }}/{{ sayhia.item }}/{{
                Math.floor(sayhia.amount / 100)
              }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="sc-sayhia">
      <div class="sc-sayhia-input">
        <div class="input">
          <input
            placeholder="如：冠军/大/100"
            v-model="amount"
            @focus="amountFocus"
          />
        </div>
        <div class="send-btn" @click="send">发送</div>
        <div class="quick-btn" @click="quick.show = true">快捷</div>
        <div class="scratch-btn" @click="cover.select = !cover.select">
          刮奖
        </div>
      </div>
    </div>
  </div>
  <div class="sc-float-btns">
    <div @click="showVideo">视频</div>
    <div @click="$emit('rule')">规则</div>
    <!-- <div @click="showHistory">走势</div> -->
    <div @click="cancel = true">撤单</div>
  </div>
  <div class="sc-quick-mark" v-if="this.quick.show" @click="quick.show = false">
    <div class="sc-quick" @click.stop="quick.show = true">
      <div class="sc-haoma">
        <div class="sc-haoma-tabs">
          <div
            v-for="(tab, index) in quick.tabs"
            :class="index === quick.tab ? 'selected' : ''"
            @click="quick.tab = index"
          >
            <span class="sc-haoma-tab-label">{{ tab }}</span>
            <span
              :class="
                quick.sayhiaTabs[index]
                  ? 'sc-haoma-tab-dot-has'
                  : 'sc-haoma-tab-dot'
              "
            ></span>
          </div>
        </div>
        <div class="sc-haoma-body">
          <div
            v-for="(sayhia, index) in quick.sayhias[quick.tab]"
            :class="
              'sc-haoma-sayhia' +
              (quick.sayhia.indexOf(quick.tab + ',' + index) === -1
                ? ''
                : ' sc-haoma-sayhia-selected')
            "
            @click="quickSayhia(index)"
          >
            <div class="sc-haoma-name">{{ sayhia.name }}</div>
            <div class="sc-haoma-rate">{{ sayhia.rate }}</div>
          </div>
        </div>
      </div>
      <div class="sc-sayhia">
        <div class="sc-sayhia-chouma">
          <div>筹码</div>
          <img
            v-for="cm in quick.chouma"
            key="amount"
            :src="cm.icon"
            @click="quick.amount = cm.amount"
          />
        </div>
        <div class="sc-sayhia-input">
          <div class="scratch-btn" @click="showZhuihao">追号</div>
          <div class="input">
            <input placeholder="请输入元宝" v-model="quick.amount" />
          </div>
          <div class="send-btn" @click="quickSayhiaTo">投注</div>
          <div class="quick-btn" @click="reset">重置</div>
        </div>
      </div>
    </div>
  </div>
  <div v-if="submit.show" class="sc-submit-mark" @click="submit.show = false">
    <div class="sc-submit" @click.stop="submit.show = true">
      <div class="sc-submit-body">
        <div v-for="sayhia in submit.sayhias">
          {{ sayhia.type }}/{{ sayhia.name }}/{{ sayhia.amount }}
        </div>
      </div>
      <div class="sc-submit-summary">
        总注：{{ submit.count }} 总额：{{ submit.sum }}
      </div>
      <div class="sc-submit-btns">
        <div class="sc-submit-cancel" @click.stop="submit.show = false">
          取消
        </div>
        <div v-if="submit.sayhiaing" class="sc-submit-ok">投注中</div>
        <div v-else class="sc-submit-ok" @click="sayhia">投注</div>
      </div>
    </div>
  </div>
  <div
    v-if="history.show"
    class="sc-history-mark"
    @click="history.show = false"
  >
    <div class="sc-history">
      <div class="sc-history-title">游戏走势图</div>
      <div class="sc-history-content">
        <table cellspacing="2">
          <thead>
            <tr>
              <th>期数</th>
              <th>开奖号码</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pcnum in history.list">
              <td>{{ pcnum.issue % 1000 }}</td>
              <td>
                <div
                  :style="{
                    background: this.color(pcnum.num1),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num1 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num2),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num2 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num3),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num3 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num4),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num4 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num5),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num5 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num6),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num6 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num7),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num7 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num8),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num8 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num9),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num9 }}
                </div>
                <div
                  :style="{
                    background: this.color(pcnum.num10),
                    textShadow: ' 1px 1px black',
                  }"
                >
                  {{ pcnum.num10 }}
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  <div class="sc-video-mark" v-if="video.show" @click="hideVideo">
    <div class="sc-video">
      <iframe
        :src="video.url"
        width="100%"
        scrolling="no"
        height="100%"
        frameborder="0"
      ></iframe>
    </div>
  </div>
  <div
    class="sc-cover-select-mark"
    v-if="cover.select"
    @click="cover.select = false"
  >
    <div class="sc-cover-select" @click="coverOnOff">
      {{ cover.on ? "关闭" : "开启" }}
    </div>
  </div>
  <div
    class="sc-zhuihao-mark"
    v-if="zhuihao.show"
    @click="zhuihao.show = false"
  >
    <div class="sc-zhuihao" @click.stop="">
      <div class="sc-zhuihao-border">
        <div class="sc-zhuihao-name-amount">
          <div class="sc-zhuihao-name">
            {{ zhuihao.type }}/{{ zhuihao.name }}
          </div>
          <div class="sc-zhuihao-amount">
            <div class="sc-zhuihao-amount-label">追号金额：</div>
            <div class="sc-zhuihao-amount-input">
              <input v-model="zhuihao.amount" />
            </div>
          </div>
        </div>
        <div class="sc-zhuihao-count-multiple">
          <div>
            <div class="sc-zhuihao-count-label">追号期数：</div>
            <div class="sc-zhuihao-count-input">
              <input v-model="zhuihao.count" />
            </div>
            <div class="sc-zhuihao-multiple-label">翻倍：</div>
            <div class="sc-zhuihao-multiple-input">
              <input v-model="zhuihao.multiple" />
            </div>
          </div>
          <div class="sc-zhuihao-button" @click="zhuihaoCreate">创建</div>
        </div>
        <div class="sc-zhuihao-items">
          <table>
            <thead>
              <tr>
                <th>期号</th>
                <th>投注金额</th>
                <th>翻倍</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in zhuihao.items">
                <td>{{ item.issue }}</td>
                <td>{{ item.amount }}</td>
                <td>{{ item.multiple }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="sc-zhuihao-stop-ok">
          <div class="sc-zhuihao-stop" @click="zhuihao.stop = !zhuihao.stop">
            <div class="sc-zhuihao-stop-check" v-if="zhuihao.stop">
              <img :src="zhuihao.check" />
            </div>
            <div class="sc-zhuihao-stop-icon" v-else></div>
            <div class="sc-zhuihao-stop-label">中奖后是否停追</div>
          </div>
          <div class="sc-zhuihao-button" @click="zhuihaoSubmit">确认追号</div>
        </div>
      </div>
    </div>
  </div>
  <confirm
    v-if="cancel"
    content="是否进行撤单操作"
    @cancel="cancel = false"
    @ok="cancelOk"
  ></confirm>
  <alert
    v-if="alert.show"
    :content="alert.content"
    @cancel="alert.show = false"
    @ok="alert.show = false"
  ></alert>
</template>
<style scoped>
.sc-marque-notice-bar {
  height: 24px;
  padding: 0 4px;
}

#next,
#latest {
  padding-top: 10px;
  text-align: center;
  font-size: 14px;
  line-height: 20px;
}

#next div,
#latest div {
  display: inline-block;
  color: rgba(255, 255, 255, 0.74);
}

#next div {
  padding: 0 4px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.37);
}

#next .issue,
#latest .issue span {
  color: #fff;
}

#latest .issue {
  padding-right: 20px;
}

#latest .num,
#latest .sum {
  width: 18px;
  height: 18px;
  line-height: 18px;
  border-radius: 36px;
  margin: 0 2px;
  font-size: 9px;
  color: #fff;
  text-align: center;
}

#latest .num {
  background-color: #509dde;
  text-shadow: 1px 1px black;
}

#latest .sum {
  background-color: #e22d2d;
}

.sc-mine {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding-top: 10px;
}

.sc-mine-avatar-nick {
  display: flex;
  align-items: center;
}

.sc-mine-avatar {
  display: inline-block;
  width: 24px;
  height: 24px;
  border-radius: 24px;
  overflow: hidden;
  border: 2px solid #fff;
}

.sc-mine-avatar img {
  display: block;
  width: 24px;
  height: 24px;
}

.sc-mine-nick {
  display: inline-block;
  width: 20vw;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  overflow: hidden;
  margin-left: 8px;
}

.sc-mine-balance {
  display: inline-block;
  color: #fff;
  font-size: 11px;
}

.sc-mine-balance span {
  margin-left: 4px;
  font-size: 18px;
}

.sc-open-cover {
  position: relative;
  height: 28px;
  width: 62vw;
  left: 35vw;
  top: -24px;
}

.sc-body,
.sc-body-cover {
  background-color: #fff;
  border-radius: 21px 21px 0 0;
  overflow: hidden;
}

.sc-body {
  margin-top: 9px;
}

.sc-body-cover {
  margin-top: -24px;
}

.sc-chat {
  height: calc(100vh - 250px - env(safe-area-inset-bottom));
  height: calc(100vh - 250px - constant(safe-area-inset-bottom));
  overflow: auto;
}

.sc-chat-message {
  padding: 8px 16px;
}

.sc-chat-open,
.sc-chat-close {
  text-align: center;
  background-color: #ccc;
  color: #fff;
  padding: 8px 0;
}

.sc-chat-sayhia {
  display: flex;
}

.sc-chat-sayhia-avatar {
  width: 32px;
  height: 32px;
  border-radius: 32px;
  border: 2px solid #ead5b7;
  margin-right: 8px;
  overflow: hidden;
}

.sc-chat-sayhia-avatar img {
  display: block;
  width: 32px;
  height: 32px;
}

.sc-chat-sayhia-nick span {
  padding-left: 16px;
}

.sc-chat-sayhia-content {
  width: 70vw;
  border-radius: 4px;
  overflow: hidden;
}

.sc-chat-sayhia-issue-sum {
  display: flex;
  justify-content: space-between;
  background-color: #ead5b7;
  color: #fff;
  padding: 4px 8px;
}

.sc-chat-sayhia-item {
  display: flex;
  justify-content: space-between;
  background-color: #e0e0e0;
  padding: 4px 8px;
  align-items: center;
  border-top: 1px solid #ccc;
}

.sc-chat-sayhias {
  border-radius: 4px;
  overflow: hidden;
}

.sc-chat-sayhias-title {
  text-align: center;
  background-color: #dab576;
  color: #fff;
  font-size: 14px;
  line-height: 42px;
}

.sc-chat-sayhias-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #e0e0e0;
  border-top: 1px solid #ccc;
  padding: 4px 8px;
}

.sc-chat-sayhias-avatar-nick {
  display: flex;
  align-items: center;
}

.sc-chat-sayhias-avatar {
  width: 32px;
  height: 32px;
  border-radius: 32px;
  border: 2px solid #dab576;
  overflow: hidden;
  margin-right: 8px;
}

.sc-chat-sayhias-avatar img {
  display: block;
  width: 32px;
  height: 32px;
}

.sc-sayhia {
  background-color: #fff;
  box-shadow: 0 -2px 4px 0 rgba(0, 0, 0, 0.08);
  border-radius: 20px 20px 0 0;
}

.sc-sayhia-input {
  height: 68px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
}

.sc-sayhia-input .input {
  height: 38px;
  background-color: #f6f6f6;
  border: 1px solid #e4e8eb;
  border-radius: 6px;
}

.sc-sayhia-input .input input {
  height: 32px;
  width: 40vw;
  line-height: 32px;
  font-size: 14px;
  border: none;
  background: none;
  padding-left: 8px;
}

.sc-sayhia-input .send-btn,
.sc-sayhia-input .quick-btn,
.sc-sayhia-input .scratch-btn {
  height: 38px;
  line-height: 38px;
  border-radius: 6px;
  color: #fff;
  font-size: 14px;
  padding: 0 13px;
}

.sc-sayhia-input .send-btn {
  background-color: #000;
}

.sc-sayhia-input .quick-btn {
  background-color: #957ffa;
}

.sc-sayhia-input .scratch-btn {
  background-color: #f3b23b;
}

.sc-float-btns {
  position: absolute;
  right: 0;
  bottom: 18vh;
  padding: 16px;
}

.sc-float-btns div {
  width: 35px;
  height: 35px;
  line-height: 35px;
  border-radius: 35px;
  color: #fff;
  font-size: 11px;
  background-color: rgba(0, 0, 0, 0.66);
  text-align: center;
  margin: 16px 0;
  z-index: 9;
}

.sc-quick-mark,
.sc-submit-mark,
.sc-history-mark,
.sc-cover-select-mark,
.sc-zhuihao-mark {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: calc(50px + env(safe-area-inset-bottom));
  bottom: calc(50px + constant(safe-area-inset-bottom));
  background-color: rgba(0, 0, 0, 0.63);
}

.sc-quick-mark,
.sc-history-mark,
.sc-cover-select-mark {
  z-index: 8;
}

.sc-submit-mark,
.sc-zhuihao-mark {
  z-index: 9;
}

.sc-quick,
.sc-history {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0px;
  background-color: #fff;
  margin-top: 9px;
  border-radius: 21px 21px 0 0;
  overflow: hidden;
}

.sc-haoma-tabs {
  display: inline-block;
  width: 25vw;
  height: 300px;
  vertical-align: top;
  background-color: #f6f6f6;
  overflow: auto;
}

.sc-haoma-tabs div {
  line-height: 50px;
  font-size: 14px;
  border-bottom: 1px solid #e4e8eb;
  padding-left: 18px;
}

.sc-haoma-tabs .selected {
  background-color: #e9c890;
}

.sc-haoma-tabs span {
  display: inline-block;
}

.sc-haoma-tab-label {
  width: 15vw;
}

.sc-haoma-tab-dot,
.sc-haoma-tab-dot-has {
  width: 7px;
  height: 7px;
  border-radius: 7px;
  background-color: #7e7e7e;
}

.sc-haoma-tab-dot {
  background-color: #7e7e7e;
}

.sc-haoma-tab-dot-has {
  background-color: rgb(235, 51, 35);
}

.sc-haoma-body {
  display: inline-block;
  width: calc(75vw - 1px);
  height: 300px;
  vertical-align: top;
  border-left: 1px solid #e4e8eb;
  overflow: auto;
}

.sc-haoma-sayhia {
  display: inline-block;
  width: calc(50% - 1px);
  padding: 18px 0 8px 0;
  text-align: center;
  border-bottom: 1px solid #e4e8eb;
}

.sc-haoma-sayhia-selected {
  background-color: #e9c890;
}

.sc-haoma-sayhia:nth-child(odd) {
  border-right: 1px solid #e4e8eb;
}

.sc-haoma-name {
  display: inline-block;
  background-image: linear-gradient(90deg, #ebca93 2%, #dab576 100%);
  border-radius: 8.5px;
  font-size: 12px;
  line-height: 17px;
  padding: 3px 8px 0 8px;
}

.sc-haoma-rate {
  height: 28px;
  line-height: 28px;
  font-weight: 500;
  font-size: 20px;
  color: #09172f;
}

.sc-sayhia-chouma {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 15px 5px 0 5px;
}

.sc-sayhia-chouma div {
  font-size: 14px;
  color: #9ba6ba;
}

.sc-sayhia-chouma img {
  display: inline-block;
  width: 38px;
  height: 38px;
}

.sc-submit {
  position: fixed;
  left: 10vw;
  top: 20vh;
  right: 10vw;
  background-color: #fff;
  border-radius: 4px;
}

.sc-submit-body {
  max-height: 40vh;
  overflow: auto;
  padding: 8px;
  text-align: center;
}

.sc-submit-body div {
  font-size: 12px;
  line-height: 36px;
  border-top: 1px solid #e9c890;
}

.sc-submit-body div:first-child {
  border-top-width: 0;
}

.sc-submit-summary {
  background-color: #e9c890;
  color: #fff;
  line-height: 36px;
  font-size: 12px;
  text-align: center;
}

.sc-submit-btns div {
  display: inline-block;
  width: calc(50% - 1px);
  text-align: center;
  font-size: 12px;
  line-height: 48px;
}

.sc-submit-cancel {
  border-right: 1px solid #e9c890;
}

.sc-history-title {
  font-weight: bold;
  text-align: center;
  padding: 8px 0 5px 0;
}

.sc-history-content {
  max-height: 50vh;
  overflow: auto;
}

.sc-history-content table {
  width: 100%;
  background-color: rgb(232, 232, 232);
}

.sc-history-content thead {
  background-color: rgb(221, 221, 221);
}

.sc-history-content th {
  padding: 5px 0 4px 0;
}

.sc-history-content tbody tr:nth-child(odd) {
  background-color: #fff;
}

.sc-history-content td {
  text-align: center;
}

.sc-history-content div {
  display: inline-block;
  width: 25px;
  height: 25px;
  line-height: 25px;
  color: #fff;
  background-color: rgb(62, 135, 250);
  text-align: center;
  border-radius: 25px;
  margin: 4px;
}

.sc-history-sum {
  color: rgb(191, 44, 28);
  font-weight: bold;
}

.sc-history-da,
.sc-history-shuang {
  color: rgb(217, 46, 122);
}

.sc-history-xiao,
.sc-history-dan {
  color: rgb(53, 104, 213);
}

.sc-video-mark {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 50px;
  z-index: 5;
}

.sc-video {
  height: 65vw;
}

.sc-cover-select {
  position: absolute;
  right: 20px;
  bottom: 70px;
  background-color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
}

.sc-zhuihao {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 94vw;
  background-color: #fff;
}

.sc-zhuihao-border {
  margin: 14px;
  box-shadow: 0 -0 4px 4px rgba(0, 0, 0, 0.08);
}

.sc-zhuihao-name-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
}

.sc-zhuihao-name {
  color: rgb(214, 87, 72);
}

.sc-zhuihao-amount-label {
  display: inline-block;
}

.sc-zhuihao-amount-input,
.sc-zhuihao-count-input,
.sc-zhuihao-multiple-input {
  display: inline-block;
  border: 1px solid rgb(206, 206, 206);
  padding: 4px;
  border-radius: 4px;
}

.sc-zhuihao-count-multiple {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgb(240, 239, 244);
}

.sc-zhuihao-count-multiple div {
  display: inline-block;
}

.sc-zhuihao-items {
  background-color: rgb(240, 239, 244);
}

.sc-zhuihao-items table {
  width: 100%;
}

.sc-zhuihao-items td {
  text-align: center;
}

.sc-zhuihao-stop-ok {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sc-zhuihao input {
  display: inline-block;
  border: none;
  outline: none;
  width: 12vw;
  background: none;
}

.sc-zhuihao-stop div {
  display: inline-block;
  vertical-align: middle;
}

.sc-zhuihao-stop-check,
.sc-zhuihao-stop-icon {
  margin: 0 8px 0 16px;
}

.sc-zhuihao-stop-check img {
  width: 20px;
  height: 20px;
}

.sc-zhuihao-stop-icon {
  width: 18px;
  height: 18px;
  border-radius: 20px;
  border: 1px solid #ccc;
}

.sc-zhuihao-button {
  background-color: rgb(221, 84, 78);
  padding: 8px 16px;
  color: #fff;
  border-radius: 4px;
}
</style>
