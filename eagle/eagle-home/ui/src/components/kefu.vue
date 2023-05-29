<script>
import EmojiPicker from "vue3-emoji-picker";
import "vue3-emoji-picker/css";
import { service, upload, url } from "../util/http";
import { keyvalue } from "../util/keyvalue";
import { sign } from "../util/user";
import { randomMax } from "../util/random";
import arrow from "@/assets/arrow.png";
export default {
  components: {
    EmojiPicker: EmojiPicker,
  },
  data() {
    return {
      kefu: "",
      avatar: "",
      uid: "",
      online: false,
      messages: [],
      time: "",
      read: "",
      faqs: {},
      unread: 0,
      unreadable: true,
      qq: [],
      qqUrl: "",
      wechat: [],
      wechatUrl: "",
      arrow: arrow,
      show: false,
      image: "",
      emoji: {
        show: false,
      },
    };
  },
  mounted() {
    document.title = "在线客服";
    keyvalue("setting.home.ke-fu.", (data) => {
      this.kefu = data["setting.home.ke-fu.online"] || "";
      if (this.kefu != "") this.kefu = url(this.kefu);
      this.qq = data["setting.home.ke-fu.qq"] || "";
      if (this.qq) {
        let qqs = this.qq.split(",");
        if (qqs.length > 0) this.qqUrl = url(qqs[randomMax(qqs.length)]);
      }
      this.wechat = data["setting.home.ke-fu.wechat"] || "";
      if (this.wechat) {
        let wechats = this.wechat.split(",");
        if (wechats.length > 0)
          this.wechatUrl = url(wechats[randomMax(wechats.length)]);
      }
    });
    sign((data) => {
      if (data.avatar) this.avatar = url(data.avatar);
      this.uid = data.uid;
    });
    this.getUnread();
  },
  beforeUnmount() {
    this.online = false;
    this.unreadable = false;
  },
  methods: {
    showOnline() {
      this.online = true;
      this.messages = [];
      this.time = "";
      this.timer();
    },
    showQQ() {
      this.image = this.qqUrl;
      this.show = true;
    },
    showWechat() {
      this.image = this.wechatUrl;
      this.show = true;
    },
    getUnread() {
      if (this.online || !this.unreadable) return;

      service("/olcs/member/user", { uid: this.uid }, (data) => {
        setTimeout(this.getUnread.bind(this), 1000);
        if (data === null) return;

        this.unread = data.unread;
      });
    },
    timer() {
      if (!this.online) return;

      service("/olcs/user", { time: this.time, uid: this.uid }, (data) => {
        setTimeout(this.timer.bind(this), 1000);
        if (data === null) return;

        if (data.list && data.list.length > 0) {
          for (let message of data.list) {
            if (message.genre === "delete") {
              for (let i = 0; i < this.messages.length; i++) {
                if (this.messages[i].id === message.id) {
                  this.messages.splice(i, 1);

                  break;
                }
              }
            } else {
              if (message.genre === "image")
                message.content = url(message.content);
              else if (message.genre === "text")
                message.content = message.content.split("\n");
              else if (message.genre === "faq") message.content = data.faq;
              this.messages.push(message);
            }
            this.time = message.time;
          }
          setTimeout(() => {
            this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight;
          }, 250);
        }
        this.read = data.read || "";
      });
    },
    askFaq(faq) {
      this.faqs[faq.subject] = true;
      service("/olcs/ask", { genre: "text", content: faq.subject });
    },
    onemoji(e) {
      if (this.$refs.textarea.value === "") {
        this.$refs.textarea.value = e.i;

        return;
      }

      this.$refs.textarea.value =
        this.$refs.textarea.value.substring(
          0,
          this.$refs.textarea.selectionStart
        ) +
        e.i +
        this.$refs.textarea.value.substring(this.$refs.textarea.selectionEnd);
    },
    picture() {
      this.$refs.uploader.click();
    },
    upload() {
      upload("clivia.olcs.image", this.$refs.uploader.files[0], (data) => {
        service("/olcs/ask", { genre: "image", content: data.path }, (d) => {
          this.$refs.uploader.value = "";
        });
        this.$refs.textarea.focus();
      });
    },
    send() {
      service(
        "/olcs/ask",
        { genre: "text", content: this.$refs.textarea.value },
        (data) => {
          if (data === null) return;

          this.$refs.textarea.value = "";
          this.$refs.textarea.focus();
        }
      );
    },
  },
};
</script>
<template>
  <iframe
    v-if="online"
    style="width: 100%; height: 100%"
    src="http://43.154.39.141/index/index/home?visiter_id=&visiter_name=&avatar=&business_id=1&groupid=1&special=2"
    frameborder="0"
  ></iframe>
  <!-- <div v-if="online" class="olcs">
    <div class="olcs-messages" ref="messages">
      <div v-for="message in messages" :key="message.id" class="olcs-message">
        <div v-if="message.replier" class="olcs-message-replier">
          <div class="olcs-message-avatar">
            <img v-if="kefu" :src="kefu" />
          </div>
          <div class="olcs-message-content">
            <div v-if="message.genre === 'image'" class="olcs-message-image">
              <a :href="message.content" target="_blank">
                <img :src="message.content" />
              </a>
            </div>
            <div v-else-if="message.genre === 'text'" class="olcs-message-text">
              <div v-for="(line, index) in message.content" :key="index">
                {{ line }}
              </div>
            </div>
            <div v-else-if="message.genre === 'faq'" class="olcs-message-faq">
              <div class="olcs-message-faq-title">常见问题</div>
              <div
                :class="
                  'olcs-message-faq-subject' +
                  (faqs[faq.subject] ? ' olcs-message-faq-subject-ask' : '')
                "
                v-for="(faq, index) in message.content"
                @click="askFaq(faq)"
              >
                {{ index + 1 }}.{{ faq.subject }}
              </div>
            </div>
            <div class="olcs-message-time">{{ message.time }}</div>
          </div>
        </div>
        <div v-else class="olcs-message-user">
          <div class="olcs-message-content">
            下面这两行需要注释
            <div v-if="message.time < read" class="olcs-message-read">已读</div>
            <div v-else class="olcs-message-unread">未读</div>
            <div v-if="message.genre === 'image'" class="olcs-message-image">
              <a :href="message.content" target="_blank">
                <img :src="message.content" />
              </a>
            </div>
            <div v-else class="olcs-message-text">
              <div v-for="(line, index) in message.content" :key="index">
                {{ line }}
              </div>
            </div>
            <div class="olcs-message-time">{{ message.time }}</div>
          </div>
          <div class="olcs-message-avatar">
            <img v-if="avatar" :src="avatar" />
          </div>
        </div>
      </div>
    </div>
    <div class="olcs-tools">
      <div class="olcs-tool-emoji" @click="emoji.show = true">
        <svg
          viewBox="64 64 896 896"
          focusable="false"
          data-icon="smile"
          width="1em"
          height="1em"
          fill="currentColor"
          aria-hidden="true"
        >
          <path
            d="M288 421a48 48 0 1096 0 48 48 0 10-96 0zm352 0a48 48 0 1096 0 48 48 0 10-96 0zM512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm263 711c-34.2 34.2-74 61-118.3 79.8C611 874.2 562.3 884 512 884c-50.3 0-99-9.8-144.8-29.2A370.4 370.4 0 01248.9 775c-34.2-34.2-61-74-79.8-118.3C149.8 611 140 562.3 140 512s9.8-99 29.2-144.8A370.4 370.4 0 01249 248.9c34.2-34.2 74-61 118.3-79.8C413 149.8 461.7 140 512 140c50.3 0 99 9.8 144.8 29.2A370.4 370.4 0 01775.1 249c34.2 34.2 61 74 79.8 118.3C874.2 413 884 461.7 884 512s-9.8 99-29.2 144.8A368.89 368.89 0 01775 775zM664 533h-48.1c-4.2 0-7.8 3.2-8.1 7.4C604 589.9 562.5 629 512 629s-92.1-39.1-95.8-88.6c-.3-4.2-3.9-7.4-8.1-7.4H360a8 8 0 00-8 8.4c4.4 84.3 74.5 151.6 160 151.6s155.6-67.3 160-151.6a8 8 0 00-8-8.4z"
          ></path>
        </svg>
      </div>
      <van-popup v-model:show="emoji.show">
        <EmojiPicker :native="true" @select="onemoji" />
      </van-popup>
      <div class="olcs-tool-image" @click="picture">
        <svg
          viewBox="64 64 896 896"
          focusable="false"
          data-icon="picture"
          width="1em"
          height="1em"
          fill="currentColor"
          aria-hidden="true"
        >
          <path
            d="M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zm-40 632H136v-39.9l138.5-164.3 150.1 178L658.1 489 888 761.6V792zm0-129.8L664.2 396.8c-3.2-3.8-9-3.8-12.2 0L424.6 666.4l-144-170.7c-3.2-3.8-9-3.8-12.2 0L136 652.7V232h752v430.2zM304 456a88 88 0 100-176 88 88 0 000 176zm0-116c15.5 0 28 12.5 28 28s-12.5 28-28 28-28-12.5-28-28 12.5-28 28-28z"
          ></path>
        </svg>
      </div>
      <div class="olcs-tool-file">
        <input type="file" @change="upload" ref="uploader" accept="image/*" />
      </div>
      <div class="olcs-tool-space"></div>
      <div class="olcs-tool-send" @click="send">
        <svg
          viewBox="64 64 896 896"
          focusable="false"
          data-icon="send"
          width="1em"
          height="1em"
          fill="currentColor"
          aria-hidden="true"
        >
          <path
            d="M931.4 498.9L94.9 79.5c-3.4-1.7-7.3-2.1-11-1.2a15.99 15.99 0 00-11.7 19.3l86.2 352.2c1.3 5.3 5.2 9.6 10.4 11.3l147.7 50.7-147.6 50.7c-5.2 1.8-9.1 6-10.3 11.3L72.2 926.5c-.9 3.7-.5 7.6 1.2 10.9 3.9 7.9 13.5 11.1 21.5 7.2l836.5-417c3.1-1.5 5.6-4.1 7.2-7.1 3.9-8 .7-17.6-7.2-21.6zM170.8 826.3l50.3-205.6 295.2-101.3c2.3-.8 4.2-2.6 5-5 1.4-4.2-.8-8.7-5-10.2L221.1 403 171 198.2l628 314.9-628.2 313.2z"
          ></path>
        </svg>
      </div>
    </div>
    <div class="olcs-input">
      <textarea ref="textarea"></textarea>
    </div>
  </div> -->
  <div v-else class="kefu-card">
    <div class="kefu-line" @click="showOnline">
      <div class="kefu-name">
        <span>在线客服</span>
        <span v-if="unread > 0" class="kefu-unread">{{ unread }}未读</span>
      </div>
      <div class="kefu-arrow"><img :src="arrow" /></div>
    </div>
    <div class="kefu-line" @click="showQQ">
      <div class="kefu-name">QQ客服</div>
      <div class="kefu-arrow"><img :src="arrow" /></div>
    </div>
    <div class="kefu-line" @click="showWechat">
      <div class="kefu-name">微信</div>
      <div class="kefu-arrow"><img :src="arrow" /></div>
    </div>
  </div>
  <div class="kefu-mark" v-if="show" @click="show = false">
    <div class="kefu-image" @click.stop=""><img :src="image" /></div>
  </div>
</template>
<style>
.kefu-card {
  margin: 8vh 4vw 0 4vw;
  border-radius: 4px;
  background-color: #353333;
  color: #ead5b7;
  padding: 8px 16px;
}

.kefu-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 22px;
  border-bottom: 1px solid #ccc;
  line-height: 48px;
}

.kefu-line:last-child {
  border-bottom-width: 0;
}

.kefu-arrow img {
  display: inline-block;
  width: 16px;
}

.kefu-mark {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 50px;
  background-color: rgba(0, 0, 0, 0.63);
  z-index: 8;
}

.kefu-image {
  margin-top: 10vh;
  text-align: center;
}

.kefu-image img {
  width: 96vw;
  display: inline-block;
}

.kefu-unread {
  padding: 4px;
  color: #ff4d4f;
}

.olcs-messages {
  height: calc(100vh - 164px - env(safe-area-inset-bottom));
  height: calc(100vh - 164px - constant(safe-area-inset-bottom));
  overflow: auto;
}

.olcs-message-user {
  padding: 8px;
  text-align: right;
}

.olcs-message-replier {
  padding: 8px;
}

.olcs-message-avatar {
  display: inline-block;
  width: 32px;
  height: 32px;
  border: 3px solid #ffffff;
  border-radius: 32px;
  overflow: hidden;
  vertical-align: top;
}

.olcs-message-avatar img {
  width: 32px;
  height: 32px;
  display: block;
}

.olcs-message-content {
  display: inline-block;
  vertical-align: top;
  padding: 0 8px;
}

.olcs-message-text,
.olcs-message-faq {
  display: inline-block;
  text-align: left;
  max-width: calc(75vw - 60px);
  padding: 4px 8px;
  margin-bottom: 4px;
}

.olcs-message-user .olcs-message-text {
  background-color: rgb(149, 236, 105);
  border-radius: 4px 0 4px 4px;
}

.olcs-message-replier .olcs-message-text,
.olcs-message-replier .olcs-message-faq {
  border-radius: 0 4px 4px 4px;
  background-color: #fff;
}

.olcs-message-replier .olcs-message-faq div {
  line-height: 1.5rem;
}

.olcs-message-replier .olcs-message-faq-subject {
  color: #73d13d;
  cursor: pointer;
}

.olcs-message-replier .olcs-message-faq-subject-ask {
  color: #888;
}

.olcs-message-read,
.olcs-message-unread {
  display: inline-block;
  vertical-align: bottom;
  padding-right: 4px;
  font-size: 0.5rem;
}

.olcs-message-read {
  color: #ccc;
}

.olcs-message-unread {
  color: rgb(46, 135, 255);
}

.olcs-message-time {
  color: #888;
}

.olcs-message-image a {
  text-decoration: none;
  display: inline-block;
}

.olcs-message-image img {
  max-width: 50vw;
  max-height: 25vh;
}

.olcs-tools {
  display: flex;
  height: 32px;
  align-items: center;
  justify-content: space-between;
  color: #ead5b7;
  padding: 0 8px;
  border-top: 1px solid #353333;
}

.olcs-tool-emoji,
.olcs-tool-image,
.olcs-tool-send {
  display: inline-block;
  text-align: center;
}

.olcs-tool-space {
  width: calc(100vw - 120px);
}

.olcs-tools svg {
  display: block;
  width: 24px;
  height: 24px;
}

.olcs-tool-file {
  position: absolute;
  top: -100vh;
}

.olcs-input textarea {
  height: 64px;
  width: calc(100vw - 16px);
  border: none;
  outline: none;
  display: block;
  margin: 0;
  background-color: #353333;
  padding: 8px;
  color: #fff;
}
</style>
