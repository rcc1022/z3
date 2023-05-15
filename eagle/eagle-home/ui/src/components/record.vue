<script>
import { ref } from 'vue';
import { service } from '../util/http';

export default {
    setup() {
        const today = new Date();

        return {
            min: new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000),
            max: today,
        };
    },
    data() {
        let today = new Date();
        return {
            time: {
                show: false,
                label: '',
                value: '',
                min: new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000),
                max: today,
            },
            game: {
                show: false,
                list: [],
                id: '',
                name: ''
            },
            loading: false,
            list: [],
            page: 0,
            finished: false,
            football: {
                type: {
                    'rang-qiu': '让',
                    'de-fen': '得分',
                    'du-ying': '独赢',
                    'rang-qiu-shang': '让',
                    'de-fen-shang': '得分',
                    'du-ying-shang': '独赢',
                    'bo-dan': '波胆'
                }
            }
        };
    },
    mounted() {
        document.title = '投注记录';
        service('/game/query', { on: 1 }, data => {
            for (let game of data.list) {
                this.game.list.push({ text: game.name, value: game.id });
            }
        });
        // this.load();
    },
    methods: {
        load() {
            this.loading = true;
            service('/sayhia/user', { time: this.time.value, game: this.game.id, pageNum: this.page + 1 }, data => {
                this.finished = data.page === data.number;
                this.page = data.number;
                for (let i = 0; i < data.list.length; i++) {
                    let bet = data.list[i];
                    bet.amount = Math.round(bet.amount / 100);
                    if (bet.status === 1) {
                        bet.profit = Math.round(bet.profit / 100 - bet.amount);
                        if (bet.profit > 0)
                            bet.profit = '+' + bet.profit;
                        if (bet.pc) {
                            bet.open = bet.pc.num1 + '+' + bet.pc.num2 + '+' + bet.pc.num3 + '=' + bet.pc.sum;
                        } else if (bet.sc) {
                            bet.open = bet.sc.num1 + '+' + bet.sc.num2 + '+' + bet.sc.num3 + '+' + bet.sc.num4 + '+' + bet.sc.num5 + '+' + bet.sc.num6
                                + '+' + bet.sc.num7 + '+' + bet.sc.num8 + '+' + bet.sc.num9 + '+' + bet.sc.num10;
                        } else if (bet.wu) {
                            bet.open = bet.wu.num1 + '+' + bet.wu.num2 + '+' + bet.wu.num3 + '+' + bet.wu.num4 + '+' + bet.wu.num5 + '=' + bet.wu.sum;
                        } else if (bet.football) {
                            bet.open = '已结束';
                        }
                    } else {
                        bet.open = '进行中';
                    }
                    if (bet.football) {
                        if (bet.type === 'de-fen')
                            bet.item = bet.subitem;
                        else if (bet.item === 'H')
                            bet.item = bet.football.teamH;
                        else if (bet.item === 'C')
                            bet.item = bet.football.teamC;
                        else if (bet.item === 'He')
                            bet.item = '和';
                        if (bet.type.startsWith('rang-qiu'))
                            bet.type = bet.football['team' + bet.football.strong] + this.football.type[bet.type] + bet.subitem;
                        else
                            bet.type = this.football.type[bet.type] || '';
                    }
                    this.list.push(bet);
                }
                this.loading = false;
            });
        },
        timeConfirm(range) {
            const [start, end] = range;
            this.time.show = false;
            this.time.label = `${this.formatDate(start)} 到 ${this.formatDate(end)}`;
            this.time.value = `${this.formatDate(start)},${this.formatDate(end)}`;
            this.reload();
        },
        formatDate(date) {
            let month = date.getMonth() + 1;
            if (month < 10)
                month = '0' + month;
            let d = date.getDate();
            if (d < 10)
                d = '0' + d;
            return `${date.getFullYear()}-${month}-${d}`;
        },
        gameConfirm(e) {
            this.game.show = false;
            if (!e.selectedIndexes || e.selectedIndexes.length === 0)
                return;

            let game = this.game.list[e.selectedIndexes[0]];
            this.game.id = game.value;
            this.game.name = game.text;
            this.reload();
        },
        gameCancel() {
            this.game.show = false;
            this.game.id = '';
            this.game.name = '';
            this.reload();
        },
        reload() {
            this.page = 0;
            this.list = [];
            this.load();
        },
    },
}
</script>
<template>
    <div class="record-time">
        <van-cell title="选择日期区间" :value="time.label" @click="time.show = true" />
        <van-calendar v-model:show="time.show" :min-date="min" :max-date="max" type="range" @confirm="timeConfirm" />
    </div>
    <div class="record-game">
        <van-cell title="选择彩种" :value="game.name" @click="game.show = true" />
        <van-popup v-model:show="game.show" round position="bottom">
            <van-picker :columns="game.list" @cancel="gameCancel" @confirm="gameConfirm" />
        </van-popup>
    </div>
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="load">
        <div class="record" v-for="bet in list">
            <div v-if="bet.football">
                <div class="record-line">
                    <div>{{ bet.football.league }}</div>
                    <div class="record-item">{{ bet.type }}/{{ bet.item }}/{{
                        bet.amount
                    }}@{{ (bet.rate / 100).toFixed(2) }}
                    </div>
                </div>
                <div class="record-line">
                    <div>{{ bet.football.teamH }} vs {{ bet.football.teamC }}</div>
                    <div>{{ bet.open }}</div>
                </div>
                <div class="record-line">
                    <div>总比分：{{ bet.football.scoreH }} vs {{ bet.football.scoreC }}</div>
                    <div :class="bet.profit === 0 ? '' : ('record-' + (bet.profit < 0 ? 'lose' : 'win'))">{{
                        bet.profit
                    }}
                    </div>
                </div>
                <div class="record-line">
                    <div>上半场：{{ bet.football.scoreShangH }} vs {{ bet.football.scoreShangC }}</div>
                    <div class="record-time">{{ bet.time }}</div>
                </div>
            </div>
            <div v-else>
                <div class="record-line">
                    <div>{{ bet.game.name }} / {{ bet.issue }}</div>
                    <div class="record-item">{{ bet.type }}/{{ bet.item }}/{{ bet.amount }}</div>
                </div>
                <div class="record-line">
                    <div>开奖：{{ bet.open }}</div>
                    <div :class="bet.profit === 0 ? '' : ('record-' + (bet.profit < 0 ? 'lose' : 'win'))">{{
                        bet.profit
                    }}
                    </div>
                </div>
                <div class="record-line">
                    <div></div>
                    <div class="record-time">{{ bet.time }}</div>
                </div>
            </div>
        </div>
    </van-list>
</template>
<style>
.record {
    border-bottom: 1px solid #333;
    padding: 8px 16px;
}

.record-line {
    display: flex;
    justify-content: space-between;
    color: #fff;
    font-size: 12px;
    line-height: 24px;
}

.record-item {
    color: #EAD5B7;
}

.record-win {
    color: green;
}

.record-lose {
    color: red;
}

.record-time {
    color: #666;
}
</style>