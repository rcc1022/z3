<script>
import { service } from '../util/http';
import { keyvalue } from '../util/keyvalue';
import { sign } from '../util/user';
import alert from './alert.vue';

export default {
    components: { alert },
    emits: ['navigate'],
    data() {
        return {
            balance: '0.00',
            nick: '',
            surplus: 0,
            amount: '',
            count: 5,
            min: 50,
            max: 0,
            time: '',
            types: [
                { value: 0, text: '微信' },
                { value: 1, text: '支付宝' },
                { value: 2, text: '银行卡' },
            ],
            type: 0,
            select: false,
            alert: {
                show: false,
                content: '',
            },
        }
    },
    mounted() {
        document.title = '快速提现';
        sign(player => {
            this.nick = player.nick;
            this.balance = (player.balance / 100).toFixed(2);
        });
        service('/player/withdraw/surplus', {}, data => {
            this.surplus = data;
        });
        keyvalue('setting.withdraw.', data => {
            this.min = data['setting.withdraw.min'] || 50;
            this.max = data['setting.withdraw.max'] || 0;
            this.count = data['setting.withdraw.count'] || 5;
            this.time = data['setting.withdraw.time'] || '';
            if (this.time && this.time.indexOf(',') > -1)
                this.time = this.time.replace(/,/g, ' - ');
        });
    },
    methods: {
        selectType(index) {
            this.type = index;
            this.select = false;
        },
        submit() {
            let amount = parseInt(this.amount) || 0;
            if (amount < this.min) {
                this.showAlert('最低提现金额为：' + this.min + '元');

                return;
            }

            if (this.max > 0 && amount > this.max) {
                this.showAlert('最高提现金额为：' + this.max + '元');

                return;
            }

            if (this.time && this.time.indexOf('-')) {
                let date = new Date();
                let time = this.num2(date.getHours()) + ':' + this.num2(date.getMinutes()) + ':' + this.num2(date.getSeconds());
                let range = this.time.split(' - ');
                if (time < range[0] || time > range[1]) {
                    this.showAlert('提现处理时间：' + this.time);

                    return;
                }
            }

            service('/player/withdraw/submit', { type: this.type, amount: amount }, data => {
                if (data === 1) {
                    this.showAlert('最低提现金额为：' + this.min + '元');

                    return;
                }

                if (data === 2) {
                    this.showAlert('每天最多能提现：' + this.count + '次');

                    return;
                }

                if (data === 3) {
                    this.showAlert('余额不足');

                    return;
                }

                if (data === 4) {
                    this.showAlert('请等待上一笔审核后再申请');

                    return;
                }

                sign(player => {
                    this.balance = (player.balance / 100).toFixed(2);
                });
                this.surplus--;
                this.showAlert('请等待财务人员处理');
            });
        },
        num2(n) {
            return n > 9 ? n : ('0' + n);
        },
        showAlert(content) {
            this.alert.content = content;
            this.alert.show = true;
        },
    }
}
</script>
<template>
    <div class="withdraw-card">
        <div class="withdraw-title">
            <div class="withdraw-title-label">账户余额</div>
            <div class="withdraw-title-balance">{{ balance }}</div>
        </div>
        <div class="withdraw-line">
            <div class="withdraw-label">用户名</div>
            <div class="withdraw-value">{{ nick }}</div>
        </div>
        <div class="withdraw-line">
            <div class="withdraw-label">剩余次数</div>
            <div class="withdraw-value">{{ surplus }}</div>
        </div>
        <div v-if="time" class="withdraw-line">
            <div class="withdraw-label">提现时间</div>
            <div class="withdraw-value">{{ time }}</div>
        </div>
        <div class="withdraw-line">
            <div class="withdraw-label">提现金额</div>
            <div class="withdraw-value"><input class="withdraw-input" v-model="amount" placeholder="请输入提现金额" />
            </div>
        </div>
        <div class="withdraw-line">
            <div class="withdraw-label">收款类型</div>
            <div class="withdraw-value">
                <van-dropdown-menu>
                    <van-dropdown-item v-model="type" :options="types" />
                </van-dropdown-menu>
            </div>
        </div>
        <div class="withdraw-types" v-if="select">
            <div v-for="(label, index) in types" @click="selectType(index)">{{ label }}</div>
        </div>
        <div class="withdraw-btns">
            <div class="withdraw-submit" @click="submit">提交</div>
            <div class="withdraw-kefu" @click="$emit('navigate', 3)">联系客服</div>
        </div>
    </div>
    <alert v-if="alert.show" :content="alert.content" @cancel="alert.show = false" @ok="alert.show = false"></alert>
</template>
<style>
.withdraw-card {
    margin: 8px;
    background-color: #fff;
    border-radius: 4px;
    overflow: hidden;
    font-size: 15px;
    line-height: 30px;
}

.withdraw-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #fff;
    padding: 0 8px;
    border-bottom: 1px solid #ccc;
}

.withdraw-title-label {
    color: #666;
}

.withdraw-title-balance {
    background-color: green;
    color: #fff;
    border-radius: 12px;
    line-height: 24px;
    padding: 0 4px;
}

.withdraw-line {
    margin: 8px 16px;
    border-bottom: 1px solid #ccc;
}

.withdraw-label {
    display: inline-block;
    width: 30vw;
}

.withdraw-value {
    display: inline-block;
    width: 50vw;
}

.withdraw-input {
    border: none;
    outline: none;
    font-size: 15px;
    line-height: 30px;
    background-color: #fff;
}

.van-dropdown-menu__bar {
    box-shadow: none;
}

.van-dropdown-menu__item {
    justify-content: start;
}

.van-ellipsis {
    width: 45vw;
}

.withdraw-types {
    position: absolute;
    left: 35vw;
    z-index: 2;
    background-color: #fff;
    border-radius: 8px;
}

.withdraw-types div {
    padding: 4px 40vw 4px 16px;
    border-bottom: 1px solid #e0e0e0;
}

.withdraw-btns {
    padding: 32px 0 16px 0;
    text-align: center;
}

.withdraw-submit {
    display: inline-block;
    background-color: blue;
    color: #fff;
    border-radius: 4px;
    padding: 0 10px;
}

.withdraw-kefu {
    padding-top: 8px;
    color: blue;
}
</style>