<script>
import {service} from '../util/http';
import Appbar from './appbar.vue';

export default {
    components: {Appbar},
    emits: ['back'],
    data() {
        return {
            tabs: ['充值记录', '提现记录'],
            tab: 0,
            list: [],
            type: [
                ['微信支付', '支付宝', '管理员上分'],
                ['微信', '支付宝', '银行卡'],
            ],
            status: ['待处理', '已通过', '已拒绝']
        };
    },
    mounted() {
        this.load();
    },
    methods: {
        load() {
            service('/player/' + (this.tab === 0 ? 'deposit' : 'withdraw') + '/user', {}, data => {
                for (let model of data.list) {
                    model.type = this.type[this.tab][model.type];
                    if (this.tab === 0)
                        model.amount = (model.amount * 0.01).toFixed((2)).replace(/\.0+$/g, '');
                    model.status = this.status[model.status];
                }
                this.list = data.list;
            });
        },
        select(index) {
            this.tab = index;
            this.list = [];
            this.load();
        },
    },
}
</script>
<template>
    <Appbar @back="$emit('back')" label="钱包记录"/>
    <div v-for="(label, index) in tabs" :class="'wallet-tab' + (index == tab ? '-select' : '')" @click="select(index)">
        {{ label }}
    </div>
    <table cellspacing="1" class="wallet">
        <thead>
        <tr>
            <th>金额</th>
            <th>类型</th>
            <th>时间</th>
            <th>状态</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="model in list">
            <td>{{ model.amount }}</td>
            <td>{{ model.type }}</td>
            <td>{{ model.submit }}</td>
            <td>{{ model.status }}</td>
        </tr>
        </tbody>
    </table>
</template>
<style>
.wallet-tab,
.wallet-tab-select {
    display: inline-block;
    width: 50vw;
    text-align: center;
    font-size: 14px;
    padding: 6px 0;
    color: #fff;
}

.wallet-tab {
    background-color: #333;
}

.wallet-tab-select {
    background-color: #EAD5B7;
}

.wallet {
    width: 100vw;
    color: #fff;
    font-size: 12px;
    text-align: center;
    background-color: #ccc;
}

.wallet th,
.wallet td {
    background-color: #111111;
    padding: 3px 0;
}
</style>