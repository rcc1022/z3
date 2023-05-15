<script>
import { service } from '../util/http';
import Appbar from './appbar.vue';
import alert from './alert.vue';
export default {
    components: { Appbar, alert },
    emits: ['back'],
    props: ['player'],
    data() {
        return {
            commission: 0,
            commissionBalance: 0,
            subs: [],
            alert: {
                show: false,
                content: '',
            },
        };
    },
    mounted() {
        this.commission = this.player.commission;
        this.commissionBalance = this.player.commissionBalance;
        this.load();
    },
    methods: {
        load() {
            service('/player/junior', {}, data => {
                this.subs = data;
            });
        },
        transfer() {
            if (this.commissionBalance <= 0) {
                this.showAlert('您的余额不足');

                return;
            }

            service('/player/transfer', {}, data => {
                this.commissionBalance = 0;
                this.showAlert('请等待管理员审核');
            });
        },
        showAlert(content) {
            this.alert.content = content;
            this.alert.show = true;
        },
    },
}
</script>
<template>
    <Appbar @back="$emit('back')" label="佣金转换" />
    <div class="commission-total-balance">总佣金:<span>{{ (commission * 0.01).toFixed(2).replace(/\.00$/g, '') }}</span> 佣金余额:<span>{{ (commissionBalance * 0.01).toFixed(2).replace(/\.00$/g, '') }}</span></div>
    <div class="commission-transfer" @click="transfer">转出所有佣金至余额</div>
    <div class="commission-mine">我的下线会员</div>
    <table class="commission-subs" cellspacing="1">
        <thead>
            <tr>
                <th>昵称</th>
                <th>头像</th>
                <th>余额</th>
                <th>产生佣金</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="sub in subs">
                <td>{{ sub.user.nick }}</td>
                <td><img :src="sub.user.avatar" /></td>
                <td>{{ (sub.balance * 0.01).toFixed(2) }}</td>
                <td>{{ (sub.commissionGenerate * 0.01).toFixed(2) }}</td>
            </tr>
        </tbody>
    </table>
    <alert v-if="alert.show" :content="alert.content" @cancel="alert.show = false" @ok="alert.show = false"></alert>
</template>
<style>
.commission-total-balance {
    text-align: center;
    color: #fff;
}

.commission-total-balance span {
    color: #EAD5B7;
}

.commission-transfer {
    margin: 8px;
    padding: 8px 0;
    background-color: #EAD5B7;
    color: #fff;
    text-align: center;
}

.commission-mine {
    padding: 16px 0 4px 0;
    text-align: center;
    color: #fff;
}

.commission-subs {
    width: 100%;
    background-color: #EAD5B7;
}

.commission-subs th,
.commission-subs td {
    text-align: center;
    background-color: #111111;
    color: #fff;
}
</style>