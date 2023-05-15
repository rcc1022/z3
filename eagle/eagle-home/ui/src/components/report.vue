<script>
import { service } from '../util/http';
import Appbar from './appbar.vue';
export default {
    components: { Appbar },
    emits: ['back'],
    props: ['junior'],
    data() {
        return {
            title: '',
            juniors: [{
                user: {
                    nick: '选择下级'
                }
            }],
            player: {
                user: {
                    nick: '选择下级'
                }
            },
            show: false,
            list: [],
        };
    },
    mounted() {
        this.title = this.junior ? '下级报表' : '个人报表';
        if (this.junior) {
            service('/player/junior', {}, data => {
                if (data && data.length > 0)
                    this.juniors = data;
            });
        } else {
            this.load('');
        }
    },
    methods: {
        load(player) {
            service('/player/profit/' + (this.junior ? 'junior' : 'user'), { player: player || '' }, data => {
                if (!data.list) {
                    this.list = [];
                    return;
                }

                for (let i = 0; i < data.list.length; i++) {
                    let model = data.list[i];
                    model.deposit = (model.deposit / 100).toFixed(2);
                    model.withdraw = (model.withdraw / 100).toFixed(2);
                    model.win = ((model.amount + model.profit) / 100).toFixed(2);
                    model.amount = (model.amount / 100).toFixed(2);
                    model.profit = (model.profit / 100).toFixed(2);
                    model.water = (model.water / 100).toFixed(2);
                }
                this.list = data.list;
            });
        },
        toSelect(select) {
            if (select)
                this.show = !this.show;
            else
                this.show = false;
        },
        search() {
            if (this.player.id)
                this.load(this.player.id);
        },
    },
}
</script>
<template>
    <div class="report-body" @click="toSelect(false)">
        <Appbar @back="$emit('back')" :label="title" />
        <div v-if="junior" class="report-junior">
            <div class="report-junior-select" @click.stop="toSelect(true)">{{ player.user.nick }}</div>
            <div class="report-junior-players" :style="{ display: show ? '' : 'none' }">
                <div v-for="junior in juniors" @click="player = junior">{{ junior.user.nick }}</div>
            </div>
            <div class="report-junior-search" @click="search">搜索</div>
        </div>
        <table cellspacing="1" class="report">
            <thead>
                <tr>
                    <th>日期</th>
                    <th>充值</th>
                    <th>提现</th>
                    <th>投注</th>
                    <th>中奖</th>
                    <th>盈亏</th>
                    <th>返水</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="model in list">
                    <td>{{ model.date }}</td>
                    <td>{{ model.deposit }}</td>
                    <td>{{ model.withdraw }}</td>
                    <td>{{ model.amount }}</td>
                    <td>{{ model.win }}</td>
                    <td>{{ model.profit }}</td>
                    <td>{{ model.water }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>
<style>
.report-body {
    height: calc(100vh - 50px);
}

.report-junior {
    color: #fff;
    padding: 4px 0;
    font-size: 12px;
}

.report-junior-select,
.report-junior-search {
    display: inline-block;
    background-color: #666;
    padding: 4px;
}

.report-junior-select {
    width: 50vw;
}

.report-junior-players {
    position: absolute;
    background-color: #666;
    width: calc(50vw + 8px);
    max-height: 50vh;
    overflow: auto;
    border-top: 1px solid #333;
    z-index: 5;
}

.report-junior-players div {
    padding: 4px;
}

.report-junior-search {
    margin-left: 4px;
    text-align: center;
}

.report {
    width: 100%;
    color: #fff;
    font-size: 12px;
    line-height: 24px;
    background-color: #ccc;
}

.report th,
.report td {
    text-align: center;
    background-color: #000;
}
</style>