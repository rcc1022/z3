<script>
import { service, url } from '../util/http';
export default {
    data() {
        return {
            qruri: '',
            qrurl: '',
            qrcode: ''
        };
    },
    mounted() {
        service('/player/qr', { host: location.host }, data => {
            this.qruri = data.qruri;
            this.qrurl = url(data.qruri);
            this.qrcode = data.qrcode;
        });
    }
}
</script>
<template>
    <div class="invite-qrcode">
        <img v-if="qrurl" :src="qrurl" />
    </div>
    <div class="invite-note">
        <div class="invite-title">推广链接</div>
        <div>{{ qrcode}}</div>
    </div>
    <div class="invite-note">
        <div class="invite-title">转发小提示</div>
        <div>1：长按图片，保存到手机分享给朋友！</div>
        <div>2：长按链接全选复制粘贴给朋友！</div>
        <div>3：从该图中的二维码进的都是你的下线！</div>
        <div>4：你的下线产生的流水都会产生千分之三的佣金！</div>
    </div>
</template>
<style>
.invite-qrcode {
    margin: 2vw 0;
    text-align: center;
}

.invite-qrcode img {
    width: 96vw;
    display: inline-block;
}

.invite-note {
    background-color: yellow;
    margin: 2vw;
    padding: 2vw;
    font-size: 12px;
    line-height: 24px;
    border: 1px dotted #ccc;
}

.invite-title {
    display: inline-block;
    background-color: #888;
    color: #fff;
    padding: 0 8px;
    border-radius: 4px;
}
</style>