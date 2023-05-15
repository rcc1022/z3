<script>
import { service } from '../util/http';
export default {
    props: ['game'],
    data() {
        return {
            rule: false,
            rules: [],
            rates: {},
        };
    },
    mounted() {
        document.title = '玩法说明';
        service('/game/rule', { id: this.game.id }, data => {
            if (data === '' || data.indexOf('\n') === -1)
                return;

            this.rules = data.split('\n');
            this.rule = true;
        });
        service('/rate/list', { game: this.game.id }, data => {
            for (let type in data) {
                for (let rate of data[type] || []) {
                    this.rates[type + ':' + rate.name] = rate;
                }
            }
        });
    },
    methods: {
        num2(n) {
            return (n <= 10 ? '0' : '') + (n - 1);
        },
        percent(n) {
            return (n * 0.01).toFixed(1);
        },
        max(key) {
            return (this.rates[key] || {})['max'] || 0;
        },
    }
}
</script>
<template>
    <div class="rule" v-if="rule">
        <div>【{{ game.name }}】赔率说明</div>
        <div v-for="r in rules">
            <div v-if="r === ''" class="rule-space"></div>
            <div v-else>{{ r }}</div>
        </div>
    </div>
    <div class="rule" v-else-if="Object.keys(rates).length > 0">
        <div>【{{ game.name }}】赔率说明</div>
        <div v-if="game.type <= 5">
            <div>大/小/单/双：{{ rates['双面:大'].rate }}倍</div>
            <div>小单/大双：{{ rates['双面:小单'].rate }}倍</div>
            <div>小双/大单：{{ rates['双面:小双'].rate }}倍</div>
            <div v-if="game.type % 3 === 0">极大/极小：{{ rates['双面:极大'].rate }}倍</div>
            <div v-else>
                <div v-if="game.type % 3 >= 1">对子：{{ rates['特殊:对子'].rate }}倍</div>
                <div v-if="game.type % 3 >= 1">顺子：{{ rates['特殊:顺子'].rate }}倍</div>
                <div v-if="game.type % 3 >= 1">豹子：{{ rates['特殊:豹子'].rate }}倍</div>
            </div>
            <div>【特殊赔率】</div>
            <div v-if="game.type % 3 === 0">
                <div>买小或单，开奖为13赔：{{ rates['双面:小'].param }}倍</div>
                <div>买大或双，开奖为14赔：{{ rates['双面:大'].param }}倍</div>
                <div>下注小单开奖为13中奖回本</div>
                <div>下注大双开奖为14中奖回本</div>
            </div>
            <div v-if="game.type % 3 === 1">开数字13/14/对子/顺子/豹子/中奖单注组合回本</div>
            <div v-if="game.type % 3 === 2">
                <div>大小单双正常赔率{{ rates['双面:大'].rate }}倍</div>
                <div>开奖结果遇开奖区三个数字其中一个带0或9与开奖结果13或14,中奖回本金。</div>
                <div>组合，正常赔率{{ rates['双面:小单'].rate }}倍</div>
                <div>开奖结果遇开奖区三个数字其中一个带0或9与开奖结果13或14,中奖回本金。</div>
            </div>
            <div class="rule-space"></div>
            <div v-for="n in 14">数字（{{ num2(n) }}/{{ 28 - n }}）：{{ rates['特码:' + (n - 1)].rate }}倍</div>
            <div class="rule-space"></div>
            <div>以上所有赔率都包含本金</div>
            <div class="rule-space"></div>
            <div>大/小/单/双：{{ max('双面:大') }}封顶</div>
            <div>组合：{{ max('双面:大单') }}封顶</div>
            <div v-if="game.type % 3 === 0">极大/极小：{{ max('双面:极大') }}封顶</div>
            <div>数字00/27：{{ max('特码:0') }}封顶</div>
            <div>数字01/26：{{ max('特码:1') }}封顶</div>
            <div>其他数字：{{ max('特码:2') }}封顶</div>
            <div v-if="game.type % 3 >= 1">
                <div>对子：{{ max('特殊:对子') }}封顶</div>
                <div>顺子：{{ max('特殊:顺子') }}封顶</div>
                <div>豹子：{{ max('特殊:豹子') }}封顶</div>
            </div>
        </div>
        <div v-else>
            <div>大/小/单/双：{{ rates['冠军:大'].rate }}倍（含本金）</div>
            <div>10车号：{{ rates['冠军:1'].rate }}倍（含本金）</div>
            <div>龙/虎：{{ rates['冠军:龙'].rate }}倍（含本金）</div>
            <div>冠亚和</div>
            <div>大/双：{{ rates['冠亚和:大'].rate }}倍（含本金）</div>
            <div>小/单：{{ rates['冠亚和:小'].rate }}倍（含本金）</div>
            <div>3/4/18/19：含本{{ rates['冠亚和:3'].rate }}倍</div>
            <div>5/6/16/17{{ rates['冠亚和:5'].rate }}倍</div>
            <div>7/8/14/15{{ rates['冠亚和:7'].rate }}倍</div>
            <div>9/10/12/13{{ rates['冠亚和:9'].rate }}倍</div>
            <div>11：含本{{ rates['冠亚和:11'].rate }}倍</div>
            <div>1~10名猜大小单双</div>
            <div>开出之号码：1/2/3/4/5为小，6/7/8/9/10为大。开出的号码偶数为双，号码奇数为单。</div>
            <div>猜号码</div>
            <div>每一号码为一竞猜组，开奖结果【竞猜号码】对应所猜【赛道】视为中奖，其余情形视为不中奖。</div>
            <div>猜龙虎</div>
            <div>第1名vs第10名，第2名vs第9名，第3名vs第8名，第4名vs第7名，第5名vs第6名，前比后大为龙，反之为虎。</div>
            <div>猜冠亚</div>
            <div>猜冠军及亚军号码，每次竞猜2个号码，顺序不限。</div>
            <div>冠亚和值（特码）猜大小单双：冠军号码+亚军号码=冠亚和值=特码=数字3-19,冠亚和值大于或等于12为大，小于或等于11为小。开出的号码偶数为双，号码奇数为单。</div>
            <div>冠亚和值（特码）猜数字：【冠亚和值】为【特码】可能出现的结果为3-19,竞猜中对应【冠亚和值】数字的视为中奖，其余视为不中奖。</div>
            <div class="rule-space"></div>
            <div>总下注：{{ game.max }}封顶</div>
            <div>大小单双：{{ max('冠军:大') }}封顶</div>
            <div>单号码：{{ max('冠军:1') }}封顶</div>
            <div>冠亚和：{{ max('冠亚和:大') }}封顶</div>
        </div>
        <div class="rule-space"></div>
        <div v-if="game.lose > 0">【亏损返水{{ percent(game.lose) }}%】</div>
        <div v-else>【亏损无返水】</div>
        <div v-if="game.commission > 0">【上级佣金{{ percent(game.commission) }}%】</div>
        <div v-if="game.water > 0">【流水福利{{ percent(game.water) }}%】</div>
        <div>【晚上00:00系统自动反入账单，个人帐变记录查询】</div>
    </div>
</template>
<style>
.rule {
    color: #fff;
    font-size: 12px;
    line-height: 24px;
    padding: 8px;
}

.rule-space {
    height: 24px;
}
</style>