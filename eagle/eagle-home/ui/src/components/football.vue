<script>
import nosayhia from '@/assets/nosayhia.svg';
import close from '@/assets/close.svg';
import backspace from '@/assets/backspace.svg';
import { service } from '../util/http';
import alert from './alert.vue';
export default {
    components: { alert },
    props: ['game'],
    data() {
        return {
            nosayhia: nosayhia,
            close: close,
            backspace: backspace,
            menus: ['滚球', '今日'],
            group: 2,
            tabs: ['主要玩法', '波胆'],
            tab: 0,
            list: [],
            change: {},
            timeouts: [],
            timeout: 0,
            interval: 0,
            sayhia: {
                show: false,
                game: {},
                type: '',
                shang: '',
                team: '',
                subitem: '',
                amount: '',
                rate: 0,
                win: '',
                nums: false,
                submiting: false,
            },
            alert: {
                show: false,
                content: '',
            },
        };
    },
    mounted() {
        document.title = this.game.name || '';
        this.load();
        this.interval = setInterval(this.load.bind(this), 1000);
    },
    beforeUnmount() {
        // for (let i = 0; i < this.timeouts.length; i++) {
        //     clearTimeout(this.timeouts[i]);
        // }
        clearInterval(this.interval);
    },
    methods: {
        load: function () {
            let group = this.group;
            service('/football/ons', { group }, data => {
                // this.timeouts[this.timeout] = setTimeout(this.load.bind(this), 1000);
                this.timeout = (this.timeout + 1) % 10;
                let second = Math.floor(new Date().getTime() / 1000) % 60;
                if (second < 10)
                    second = '0' + second;
                second = ':' + second
                let change = {};
                for (let i = 0; i < data.length; i++) {
                    let d = data[i];
                    d.deFenH = d.deFenH.replace(/ /g, '');
                    d.deFenH = d.deFenH.replace(/O/g, '大 ');
                    d.deFenC = d.deFenC.replace(/ /g, '');
                    d.deFenC = d.deFenC.replace(/U/g, '小 ');
                    d.deFenShangH = d.deFenShangH.replace(/ /g, '');
                    d.deFenShangH = d.deFenShangH.replace(/O/g, '大 ');
                    d.deFenShangC = d.deFenShangC.replace(/ /g, '');
                    d.deFenShangC = d.deFenShangC.replace(/U/g, '小 ');
                    let timer = d.timer;
                    if (timer) {
                        timer = timer.trim();
                        if (timer === '半场')
                            d.datetime = timer;
                        else {
                            try {
                                let t = parseInt(timer);
                                if (t && t > 0) {
                                    if (t <= 45)
                                        d.datetime = this.tab === 1 ? ['上半场', t + second] : ('上半场 ' + t + second);
                                    else
                                        d.datetime = this.tab === 1 ? ['下半场', t + second] : ('下半场 ' + t + second);
                                }
                            } catch (error) {
                            }
                        }
                    }
                    if (this.tab === 1 && d.boDan) {
                        d.boDan = JSON.parse(d.boDan);
                        d.bodan = [];
                        for (let bd of Object.keys(d.boDan)) {
                            if (bd === 'gid')
                                continue;

                            d.bodan.push({
                                score: bd.substring(1).replace(/c/g, '-'),
                                rate: d.boDan[bd]
                            });
                        }
                    }
                }
                for (let i = 0; i < data.length; i++) {
                    let d = data[i];
                    let l = this.list[i];
                    let keys = Object.keys(d);
                    for (let j = 0; j < keys.length; j++) {
                        let key = keys[j];
                        let value = d[key];
                        if (!value)
                            continue;

                        value = '' + value;
                        if (/\d+\.\d{2}0/.test(value)) {
                            value = value.replace(/0$/g, '');
                            d[key] = value;
                        }
                        if (l && value === '' + l[key])
                            continue;

                        change[d.gid + ':' + key] = true;
                    }
                    if (this.sayhia.show && d.gid === this.sayhia.game.gid) {
                        this.sayhia.game = d;
                        this.sayhiaAmount();
                    }
                }
                if (group === this.group) {
                    this.list = data;
                    this.change = change;
                }
            });
        },
        menu(index) {
            this.group = index;
            this.list = [];
            this.change = {};
        },
        toSayhia(game, type, team) {
            this.sayhia.game = game;
            this.sayhia.type = type;
            this.sayhia.shang = this.sayhia.type.indexOf('-shang') === -1 ? '' : ' - 上半场';
            this.sayhia.team = team;
            this.sayhiaAmount();
            this.sayhia.show = true;
        },
        sayhiaNum(n) {
            this.sayhia.amount += '' + n % 10;
            this.sayhiaAmount();
        },
        sayhiaBackspace() {
            if (!this.sayhia.amount) return;

            this.sayhia.amount = this.sayhia.amount.substring(0, this.sayhia.amount.length - 1);
            this.sayhiaAmount();
        },
        sayhiaPlus(n) {
            this.sayhia.amount = (parseFloat(this.sayhia.amount) || 0) + n;
            this.sayhiaAmount();
        },
        sayhiaAmount() {
            this.sayhia.rate = 0;
            if (this.sayhia.type === 'rang-qiu') {
                this.sayhia.rate = this.sayhia.team === 'H' ? this.sayhia.game.rangQiuH : this.sayhia.game.rangQiuC;
                this.sayhia.subitem = this.sayhia.game.rangQiu.replace(/ /g, '');
                this.sayhia.win = (this.sayhia.amount * this.sayhia.rate).toFixed(2);
            } else if (this.sayhia.type === 'de-fen') {
                this.sayhia.rate = this.sayhia.team === 'H' ? this.sayhia.game.deFenRateH : this.sayhia.game.deFenRateC;
                this.sayhia.subitem = this.sayhia.team === 'H' ? this.sayhia.game.deFenH : this.sayhia.game.deFenC;
                this.sayhia.win = (this.sayhia.amount * this.sayhia.rate).toFixed(2);
            } else if (this.sayhia.type === 'du-ying') {
                this.sayhia.rate = this.sayhia.team === 'He' ? this.sayhia.game.duYingHe : (this.sayhia.team === 'H' ? this.sayhia.game.duYingH : this.sayhia.game.duYingC);
                this.sayhia.win = (this.sayhia.amount * (this.sayhia.rate - 1)).toFixed(2);
            } else if (this.sayhia.type === 'rang-qiu-shang') {
                this.sayhia.rate = this.sayhia.team === 'H' ? this.sayhia.game.rangQiuShangH : this.sayhia.game.rangQiuShangC;
                this.sayhia.subitem = this.sayhia.game.rangQiuShang.replace(/ /g, '');
                this.sayhia.win = (this.sayhia.amount * this.sayhia.rate).toFixed(2);
            } else if (this.sayhia.type === 'de-fen-shang') {
                this.sayhia.rate = this.sayhia.team === 'H' ? this.sayhia.game.deFenRateShangH : this.sayhia.game.deFenRateShangC;
                this.sayhia.subitem = this.sayhia.team === 'H' ? this.sayhia.game.deFenShangH : this.sayhia.game.deFenShangC;
                this.sayhia.win = (this.sayhia.amount * this.sayhia.rate).toFixed(2);
            } else if (this.sayhia.type === 'du-ying-shang') {
                this.sayhia.rate = this.sayhia.team === 'He' ? this.sayhia.game.duYingShangHe : (this.sayhia.team === 'H' ? this.sayhia.game.duYingShangH : this.sayhia.game.duYingShangC);
                this.sayhia.win = (this.sayhia.amount * (this.sayhia.rate - 1)).toFixed(2);
            } else if (this.sayhia.type === 'bo-dan') {
                this.sayhia.rate = this.sayhia.game.boDan['h' + this.sayhia.team.replace(/-/g, 'c')];
                this.sayhia.win = (this.sayhia.amount * (this.sayhia.rate - 1)).toFixed(2);
            }
        },
        sayhiaSubmit() {
            let amount = parseInt(this.sayhia.amount);
            if (!amount) {
                this.showAlert('请输入下注金额');

                return;
            }

            let items = [{
                group: this.group,
                gid: this.sayhia.game.gid,
                type: this.sayhia.type,
                item: this.sayhia.team,
                subitem: this.sayhia.subitem,
                rate: this.sayhia.rate,
                amount
            }];
            this.sayhia.submiting = true;
            service('/sayhia/save', { game: this.game.id, items: JSON.stringify(items) }, data => {
                this.sayhia.submiting = false;
                if (data === 0) {
                    this.showAlert('您已成功投注');
                    this.sayhia.show = false;
                } else if (data === 2)
                    this.showAlert('已封盘');
                else if (data === 3)
                    this.showAlert('赔率已发生变化');
                else if (data === 5 || data === 6)
                    this.showAlert('超过最大限额');
                else if (data === 7)
                    this.showAlert('余额不足');
                else
                    this.showAlert('参数错误' + data);
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
    <div id="football-games">
        <div class="football-menus">
            <div v-for="(label, index) in menus" :class="index === group ? 'football-selected-menu' : ''"
                @click="menu(index)">{{ label }}</div>
        </div>
        <div class="football-tabs">
            <div v-for="(label, index) in tabs" :class="index === tab ? 'football-selected-tab' : ''" @click="tab = index">
                {{ label }}</div>
        </div>
        <div v-if="tab === 0">
            <div class="football-game" v-for="game in list">
                <div class="football-league">{{ game.league }}</div>
                <div class="football-body">
                    <div class="football-left">
                        <div class="football-datetime">{{ game.datetime }}</div>
                        <div>
                            <div class="football-team">
                                <div> {{ game.scoreH }}</div>
                                <div :class="'fontball-team-name' + (game.strong === 'H' ? ' football-team-strong' : '')">
                                    {{ game.teamH }}</div>
                            </div>
                        </div>
                        <div>
                            <div class="football-team">
                                <div> {{ game.scoreC }}</div>
                                <div :class="'fontball-team-name' + (game.strong === 'C' ? ' football-team-strong' : '')">
                                    {{ game.teamC }}</div>
                            </div>
                        </div>
                    </div>
                    <div class="football-content">
                        <div class="football-rates">
                            <div>
                                <div class="football-title-single">让球</div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':rangQiuH'] ? ' football-rate-change' : '')">
                                    <div v-if="game.rangQiuH" @click="toSayhia(game, 'rang-qiu', 'H')">
                                        <div v-if="game.strong === 'H'">{{ game.rangQiu }}</div>
                                        <div class="football-rate-rate">{{ game.rangQiuH }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':rangQiuC'] ? ' football-rate-change' : '')">
                                    <div v-if="game.rangQiuC" @click="toSayhia(game, 'rang-qiu', 'C')">
                                        <div v-if="game.strong === 'C'">{{ game.rangQiu }}</div>
                                        <div class="football-rate-rate">{{ game.rangQiuC }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                            </div>
                            <div>
                                <div class="football-title-single">得分大小</div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':deFenH'] || change[game.gid + ':deFenRateH'] ? ' football-rate-change' : '')">
                                    <div v-if="game.deFenH" @click="toSayhia(game, 'de-fen', 'H')">
                                        <div>{{ game.deFenH }}</div>
                                        <div class="football-rate-rate">{{ game.deFenRateH }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':deFenC'] || change[game.gid + ':deFenRateC'] ? ' football-rate-change' : '')">
                                    <div v-if="game.deFenC" @click="toSayhia(game, 'de-fen', 'C')">
                                        <div>{{ game.deFenC }}</div>
                                        <div class="football-rate-rate">{{ game.deFenRateC }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                            </div>
                            <div>
                                <div class="football-title-single">独赢</div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':duYingH'] ? ' football-rate-change' : '')">
                                    <div v-if="game.duYingH" class="football-rate-rate"
                                        @click="toSayhia(game, 'du-ying', 'H')">{{ game.duYingH }}</div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':duYingC'] ? ' football-rate-change' : '')">
                                    <div v-if="game.duYingC" class="football-rate-rate"
                                        @click="toSayhia(game, 'du-ying', 'C')">{{ game.duYingC }}</div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':duYingHe'] ? ' football-rate-change' : '')">
                                    <div v-if="game.duYingHe" @click="toSayhia(game, 'du-ying', 'He')">
                                        <div>和</div>
                                        <div class="football-rate-rate">{{ game.duYingHe }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                            </div>
                            <div>
                                <div class="football-title-shang">
                                    <div>独赢</div>
                                    <div>上半场</div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':duYingShangH'] ? ' football-rate-change' : '')">
                                    <div v-if="game.duYingShangH" class="football-rate-rate"
                                        @click="toSayhia(game, 'du-ying-shang', 'H')">{{ game.duYingShangH }}</div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':duYingShangC'] ? ' football-rate-change' : '')">
                                    <div v-if="game.duYingShangC" class="football-rate-rate"
                                        @click="toSayhia(game, 'du-ying-shang', 'C')">{{ game.duYingShangC }}</div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':duYingShangHe'] ? ' football-rate-change' : '')">
                                    <div v-if="game.duYingShangHe" @click="toSayhia(game, 'du-ying-shang', 'He')">
                                        <div>和</div>
                                        <div class="football-rate-rate">{{ game.duYingShangHe }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                            </div>
                            <div>
                                <div class="football-title-shang">
                                    <div>让球</div>
                                    <div>上半场</div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':rangQiuShangH'] ? ' football-rate-change' : '')">
                                    <div v-if="game.rangQiuShangH" @click="toSayhia(game, 'rang-qiu-shang', 'H')">
                                        <div v-if="game.strong === 'H'">{{ game.rangQiuShang }}</div>
                                        <div class="football-rate-rate">{{ game.rangQiuShangH }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':rangQiuShangC'] ? ' football-rate-change' : '')">
                                    <div v-if="game.rangQiuShangC" @click="toSayhia(game, 'rang-qiu-shang', 'C')">
                                        <div v-if="game.strong === 'C'">{{ game.rangQiuShang }}</div>
                                        <div class="football-rate-rate">{{ game.rangQiuShangC }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                            </div>
                            <div>
                                <div class="football-title-shang">
                                    <div>得分大小</div>
                                    <div>上半场</div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':deFenShangH'] || change[game.gid + ':deFenRateShangH'] ? ' football-rate-change' : '')">
                                    <div v-if="game.deFenShangH" @click="toSayhia(game, 'de-fen-shang', 'H')">
                                        <div>{{ game.deFenShangH }}</div>
                                        <div class="football-rate-rate">{{ game.deFenRateShangH }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                                <div
                                    :class="'football-rate' + (change[game.gid + ':deFenShangC'] || change[game.gid + ':deFenRateShangC'] ? ' football-rate-change' : '')">
                                    <div v-if="game.deFenShangC" @click="toSayhia(game, 'de-fen-shang', 'C')">
                                        <div>{{ game.deFenShangC }}</div>
                                        <div class="football-rate-rate">{{ game.deFenRateShangC }}</div>
                                    </div>
                                    <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                                </div>
                            </div>
                        </div>
                        <div class="football-scroller">
                            <div></div>
                            <div></div>
                        </div>
                    </div>
                </div>
                <div class="football-rangqiu-daxiao">让球&大/小</div>
            </div>
        </div>
        <div v-else>
            <div class="football-bodan" v-for="game in list">
                <div class="football-league">{{ game.league }}</div>
                <div class="football-bodan-teams">
                    <div>
                        <div class="football-bodan-team">{{ game.teamH }}</div>
                        <div class="football-bodan-team">{{ game.teamC }}</div>
                    </div>
                    <div v-if="game.datetime.length === 2">
                        <div class="football-bodan-datetime">{{ game.datetime[0] }}</div>
                        <div class="football-bodan-datetime">{{ game.datetime[1] }}</div>
                    </div>
                    <div v-else class="football-bodan-datetime">{{ game.datetime }}</div>
                </div>
                <div class="football-bodan-score-rates">
                    <div v-for="sr in game.bodan" :class="'football-rate'">
                        <div v-if="sr.rate" @click="toSayhia(game, 'bo-dan', sr.score)">
                            <div>{{ sr.score }}</div>
                            <div class="football-rate-rate">{{ sr.rate }}</div>
                        </div>
                        <div v-else><img class="football-nosayhia" :src="nosayhia" /></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="football-sayhia-mark" v-if="sayhia.show" @click="sayhia.show = false">
        <div class="football-sayhia" @click.stop="">
            <div class="football-sayhia-close" @click="sayhia.show = false"><img :src="close" /></div>
            <div class="football-sayhia-content">
                <div class="football-sayhia-title" v-if="sayhia.type.indexOf('rang-qiu') > -1">足球(滚球)让球{{ sayhia.shang
                }}<span class="football-sayhia-score">({{ sayhia.game.scoreH }} - {{ sayhia.game.scoreC }})</span></div>
                <div class="football-sayhia-title" v-else-if="sayhia.type.indexOf('du-ying') > -1">足球(滚球)独赢{{ sayhia.shang
                }}
                </div>
                <div class="football-sayhia-title" v-else-if="sayhia.type.indexOf('de-fen') > -1">足球(滚球)大 / 小{{ sayhia.shang
                }}<span class="football-sayhia-score">({{ sayhia.game.scoreH }} - {{ sayhia.game.scoreC }})</span></div>
                <div class="football-sayhia-title" v-else-if="sayhia.type === 'bo-dan'">足球(滚球)波胆</div>
                <div class="football-sayhia-league">{{ sayhia.game.league }}</div>
                <div class="football-sayhia-vs" v-if="sayhia.type === 'rang-qiu'">{{ sayhia.game.teamH }}<span
                        v-if="sayhia.game.strong === 'H'">{{ sayhia.game.rangQiu }}</span> vs {{ sayhia.game.teamC }}<span
                        v-if="sayhia.game.strong === 'C'">{{ sayhia.game.rangQiu }}</span></div>
                <div class="football-sayhia-vs"
                    v-else-if="sayhia.type === 'du-ying' || sayhia.type === 'de-fen' || sayhia.type === 'bo-dan'">{{
                        sayhia.game.teamH
                    }} vs {{ sayhia.game.teamC }}</div>
                <div class="football-sayhia-team-rate">
                    <b v-if="sayhia.team === 'He'">和局</b>
                    <b v-else-if="sayhia.type === 'de-fen'">{{ sayhia.subitem }}</b>
                    <b v-else-if="sayhia.type === 'bo-dan'">{{ sayhia.team }}</b>
                    <b v-else>{{ sayhia.team === 'H' ? sayhia.game.teamH : sayhia.game.teamC }}</b>
                    <span> @ </span>
                    <span class="football-sayhia-team-rate-rate">{{ sayhia.rate }}</span>
                </div>
                <div class="football-sayhia-amount">
                    <div><input v-model="sayhia.amount" placeholder="输入下注金额" @focus="sayhia.nums = true"
                            @keyup="sayhiaAmount" />
                    </div>
                    <div v-if="!sayhia.amount" class="football-sayhia-amount-max">最大投注金额</div>
                    <div v-else class="football-sayhia-amount-win">
                        <div class="football-sayhia-amount-win-label">可赢额</div>
                        <div class="football-sayhia-amount-win-value">{{ sayhia.win }}</div>
                    </div>
                </div>
            </div>
            <div class="football-sayhia-nums" v-if="sayhia.nums">
                <div v-for="i in 10" class="football-sayhia-nums-10" @click="sayhiaNum(i)">{{ i % 10 }}</div>
                <div class="football-sayhia-nums-backspace" @click="sayhiaBackspace"><img :src="backspace" /></div>
                <div class="football-sayhia-nums-plus" @click="sayhiaPlus(10)">+10</div>
                <div class="football-sayhia-nums-plus" @click="sayhiaPlus(100)">+100</div>
                <div class="football-sayhia-nums-plus" @click="sayhiaPlus(500)">+500</div>
                <div class="football-sayhia-nums-done" @click="sayhia.nums = false">完成</div>
            </div>
            <div class="football-sayhia-submit" v-if="sayhia.submiting">正在下注</div>
            <div class="football-sayhia-submit" v-else @click="sayhiaSubmit">
                <div>下注</div>
                <div>{{ sayhia.amount }}RMB</div>
            </div>
        </div>
    </div>
    <alert v-if="alert.show" :content="alert.content" @cancel="alert.show = false" @ok="alert.show = false"></alert>
</template>
<style>
#football-games {
    background-color: #fff;
    height: 100%;
    overflow: auto;
}

.football-menus {
    background-color: #503f32;
    color: rgba(255, 255, 255, 0.64);
    font-size: 14px;
    padding-left: 16px;
}

.football-menus div {
    display: inline-block;
    padding: 16px 16px 14px 16px;
}

.football-selected-menu {
    color: #debb69;
}

.football-tabs {
    text-align: center;
}

.football-tabs div {
    display: inline-block;
    margin: 16px;
    padding: 4px 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.football-selected-tab {
    color: rgb(168, 130, 40);
    border: 1px solid rgb(168, 130, 40);
}

.football-league {
    background-color: rgb(237, 237, 237);
    font-size: 16px;
    line-height: 36px;
    padding: 6px 0 0 8px;
}

.football-body {
    padding: 4px 0;
    border-bottom: 1px solid rgb(230, 230, 230);
    margin-bottom: 8px;
}

.football-left {
    display: inline-block;
    width: 45vw;
    vertical-align: top;
    font-size: 12px;
    line-height: 36px;
}

.football-datetime {
    height: 40px;
    display: table-cell;
    vertical-align: middle;
    padding: 0 8px;
    color: rgb(32, 32, 32);
}

.football-team {
    height: 40px;
    margin: 4px 0;
    display: table-cell;
    vertical-align: middle;
}

.football-team div {
    display: inline-block;
}

.football-team div:first-child {
    padding: 0 8px;
    color: rgb(167, 128, 37);
}

.fontball-team-name {
    width: 35vw;
}

.football-team-strong {
    font-weight: bold;
}

.football-content {
    display: inline-block;
    width: 55vw;
    vertical-align: top;
    overflow: auto;
    font-size: 12px;
}

.football-rates {
    min-width: 450px;
    display: flex;
    justify-content: space-around;
}

.football-title {
    height: 36px;
    text-align: center;
}

.football-title-single {
    line-height: 36px;
    color: rgb(112, 112, 112);
    text-align: center;
}

.football-title-shang {
    line-height: 18px;
    text-align: center;
}

.football-title-shang div:first-child {
    color: rgb(112, 112, 112);
}

.football-title-shang div:last-child {
    color: rgb(32, 32, 32);
}

.football-bodan-teams {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: rgb(249, 249, 249);
    padding: 4px 8px;
}

.football-bodan-team {
    font-size: 14px;
}

.football-bodan-datetime {
    color: rgb(170, 133, 46);
    font-size: 12px;
    line-height: 150%;
    text-align: center;
}

.football-bodan-score-rates {
    text-align: center;
    padding: 8px 0;
}

.football-bodan-score-rates>div {
    display: inline-block;
    width: 64px;
    vertical-align: middle;
    margin: 4px;
}

.football-rate {
    margin: 4px 0;
    width: 64px;
    height: 40px;
    border: 1px solid rgb(187, 187, 187);
    border-radius: 4px;
    text-align: center;
}

.football-rate>div {
    position: relative;
    top: 20px;
    transform: translateY(-50%);
    line-height: 12px;
}

.football-rate-rate {
    color: rgb(200, 37, 10);
    font-weight: bold;
    font-size: 14px;
}

.football-rate-change {
    background-color: rgb(255, 243, 195);
}

.football-nosayhia {
    width: 16px;
}

.football-scroller {
    padding: 4px 32px;
}

.football-scroller div {
    display: inline-block;
    margin: 0 8px;
    height: 3px;
    width: 24px;
    border-radius: 1px;
}

.football-scroller div:first-child {
    background-color: rgb(103, 211, 221);
}

.football-scroller div:last-child {
    background-color: #e0e0e0;
}

.football-rangqiu-daxiao {
    display: inline-block;
    border: 1px solid rgb(230, 230, 230);
    font-size: 12px;
    line-height: 24px;
    border-radius: 24px;
    padding: 0 8px;
    margin: 0 0 16px 8px;
}

.football-sayhia-mark {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: calc(50px + env(safe-area-inset-bottom));
    bottom: calc(50px + constant(safe-area-inset-bottom));
    z-index: 8;
}

.football-sayhia {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #fff;
    box-shadow: 0 -5px 3px rgb(230, 230, 230);
}

.football-sayhia-close {
    padding: 16px;
    float: right;
}

.football-sayhia-content {
    padding: 16px;
    font-size: 14px;
    line-height: 24px;
}

.football-sayhia-title .football-sayhia-score {
    padding: 0 8px;
    color: rgb(167, 128, 37);
}

.football-sayhia-league {
    color: rgba(0, 0, 0, 0.64);
}

.football-sayhia-vs span {
    padding: 4px;
    color: #ce3636;
}

.football-sayhia-team-rate-rate {
    color: rgb(206, 54, 54);
    font-weight: bold;
}

.football-sayhia-amount {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 8px;
}

.football-sayhia-amount input {
    width: 25vw;
    border: 1px solid rgb(187, 187, 187);
    font-size: 16px;
    line-height: 200%;
}

.football-sayhia-amount-max {
    color: rgb(0, 102, 204);
}

.football-sayhia-amount-win {
    text-align: right;
    font-size: 12px;
    line-height: 16px;
}

.football-sayhia-amount-win-label {
    color: rgba(0, 0, 0, 0.64);
}

.football-sayhia-amount-win-value {
    color: rgb(25, 128, 92);
}

.football-sayhia-nums {
    color: #fff;
    font-size: 14px;
    line-height: 32px;
    background-color: #454545;
}

.football-sayhia-nums div {
    display: inline-block;
    width: 20vw;
    text-align: center;
}

.football-sayhia-nums-backspace,
.football-sayhia-nums-plus,
.football-sayhia-nums-done {
    background-color: rgba(0, 0, 0, 0.24);
}

.football-sayhia-submit {
    background-color: #19805c;
    color: #fff;
    padding: 8px 0 5px 0;
    text-align: center;
    font-size: 14px;
    line-height: 150%;
}
</style>