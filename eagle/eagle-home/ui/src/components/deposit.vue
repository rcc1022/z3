<script>
import { service, url } from '../util/http';
import { sign } from '../util/user';
import { randomMax } from '../util/random';
import wepay from '@/assets/wepay.png';
import alipay from '@/assets/alipay.png';
import arrow from '@/assets/arrow.png';
import Appbar from './appbar.vue';
import alert from './alert.vue';

export default {
    components: { Appbar, alert },
    emits: ['navigate'],
    props: ['player'],
    data() {
        return {
            list: [],
            channel: {},
            alipay: [],
            alipayUrl: "",
            alipayIcon: alipay,
            weixin: [],
            weixinUrl: "",
            weixinIcon: wepay,
            arrow: arrow,
            show: 0,
            selects: ['微信支付', '支付宝'],
            select: 0,
            label: '',
            qrcode: '',
            player: {
                nick: ''
            },
            min: 0,
            max: 0,
            amount: '',
            alert: {
                show: false,
                content: '',
            },
        };
    },
    mounted() {
        document.title = "在线充值";
        service('/channel/query', { pageSize: 1024 }, data => {
            for (let channel of data.list) {
                channel.icon = url(channel.icon);
                channel.qrcode = url(channel.qrcode);
            }
            this.list = data.list;
        });
        sign(data => {
            this.player = data;
        });
    },
    methods: {
        toAmount: function (e) {
            let index = e.target.parentNode.dataset.channel;
            if (!index)
                return;

            this.channel = this.list[index];
            this.show = 1;
        },
        submit() {
            let amount = parseInt(this.amount);
            if (!amount) {
                this.showAlert('请输入转账金额');

                return;
            }

            if (amount < this.channel.min) {
                this.showAlert('转账金额不能小于' + this.channel.min);

                return;
            }

            if (this.channel.max > 0 && amount > this.channel.max) {
                this.showAlert('转账金额不能大于' + this.channel.max);

                return;
            }

            service('/player/deposit/submit', { type: this.channel.name, amount: amount }, data => {
                if (data === 1) {
                    this.showAlert('请等待上一笔审核后再申请');

                    return;
                }
                
                this.showAlert('请等待财务人员处理');
            });
        },
        showAlert(content) {
            this.alert.content = content;
            this.alert.show = true;
        },
    }
}
</script>

<template>
    <div class="deposit" v-if="show === 0">
        <div class="title">二维码</div>
        <div v-for="(item, index) in list" class="tile tile-line" :data-channel="index" @click="toAmount">
            <span class="label">{{ item.name }}</span>
            <img :src="item.icon" class="pay" />
            <img :src="arrow" class="arrow" />
        </div>
    </div>
    <div class="desposit" v-if="show === 1">
        <Appbar label="提交充值金额" @back="show = 0" />
        <div class="deposit-qr">
            <div class="deposit-qr-select">{{ channel.name }}</div>
            <div class="deposit-qr-image"><img :src="channel.qrcode" /></div>
            <div class="deposit-qr-user">充值用户：{{ player.nick }}</div>
            <div class="divider"></div>
            <div class="deposit-qr-scan">[-]请长按识别二维码进行转账</div>
        </div>
        <div class="deposit-amount">
            <input placeholder="请输入转账金额" v-model="amount" />
        </div>
        <div class="deposit-note">
            <div>温馨提示</div>
            <div>识别二维码<span>付款后</span>再填写转账金额提交</div>
        </div>
        <div class="deposit-submit" @click="submit">开始充值</div>
        <div class="deposit-kefu" @click="$emit('navigate', 3)">联系客服</div>
    </div>
    <alert v-if="alert.show" :content="alert.content" @cancel="alert.show = false" @ok="alert.show = false"></alert>
</template>

<style>
.deposit {
    margin: 2vw;
    background-color: #353333;
    border-radius: 4px;
    color: #EAD5B7;
}

.deposit .title {
    padding: 4px 8px;
    border-bottom: 1px solid #111111;
    font-size: 22px;
}

.deposit .tile {
    margin-left: 8px;
    padding: 4px 8px 4px 0;
    font-size: 22px;
}

.deposit .tile>* {
    display: inline-block;
    vertical-align: middle;
}

.deposit .tile .label {
    width: 80vw;
}

.deposit .tile .pay {
    width: 18px;
}

.deposit .tile .arrow {
    width: 12px;
    margin-left: 8px;
}

.deposit .tile-line {
    border-bottom: 1px solid #111111;
}

.deposit-qr {
    margin: 8px;
    border: 1px solid #353333;
    text-align: center;
    font-size: 14px;
    line-height: 36px;
}

.deposit-qr-select {
    color: #888;
}

.deposit-qr-image img {
    display: inline-block;
    width: 90vw;
}

.deposit-qr-user {
    color: green;
}

.deposit-qr-scan {
    color: red;
}

.deposit-amount {
    margin: 8px;
    border: 1px solid #353333;
}

.deposit-amount input {
    width: calc(100vw - 26px);
    border: none;
    outline: none;
    font-size: 18px;
    line-height: 36px;
    padding: 0 4px;
}

.deposit-note {
    margin: 8px;
    font-size: 14px;
    line-height: 24px;
    color: #888;
}

.deposit-note div:first-child,
.deposit-note span {
    color: #f00;
}

.deposit-note span {
    padding: 0 4px;
}

.deposit-submit {
    margin: 8px;
    background-color: #F3B23B;
    color: #fff;
    font-size: 14px;
    line-height: 36px;
    text-align: center;
}

.deposit-kefu {
    font-size: 14px;
    line-height: 24px;
    text-align: center;
    color: blue;
}
</style>