<script>
import signbg from '@/assets/sign.png';
import mineDeposit from '@/assets/mine-deposit.png';
import mineWithdraw from '@/assets/mine-withdraw.png';
import mineWallet from '@/assets/mine-wallet.png';
import mineBet from '@/assets/mine-bet.png';
import mineReport from '@/assets/mine-report.png';
import mineReportJunior from '@/assets/mine-report-junior.png';
import mineLedger from '@/assets/mine-ledger.png';
import mineTransfer from '@/assets/mine-transfer.png';
import mineAccount from '@/assets/mine-account.png';
import mineTemplate from '@/assets/mine-template.png';
import mineExit from '@/assets/mine-exit.png';
import { sign, signOut } from '../util/user';
import Report from './report.vue';
import Wallet from './wallet.vue';
import Account from './account.vue';
import Ledger from './ledger.vue';
import Commission from './commission.vue';
export default {
    components: { Report, Wallet, Account, Ledger, Commission },
    emits: ['navigate'],
    props: ['illegal'],
    data() {
        return {
            page: '',
            signbg: signbg,
            player: {
                nick: '',
                code: '',
                uid: '',
                mobile: '',
                balance: 0,
                bet: 0,
                profit: 0,
                commission: 0,
                commissionBalance: 0,
            },
            icon: {
                deposit: mineDeposit,
                withdraw: mineWithdraw,
                wallet: mineWallet,
                bet: mineBet,
                report: mineReport,
                reportJunior: mineReportJunior,
                ledger: mineLedger,
                transfer: mineTransfer,
                account: mineAccount,
                template: mineTemplate,
                exit: mineExit,
            },
            timeouts: [],
            timeout: 0,
            interval: 0,
        };
    },
    mounted() {
        document.title = '个人中心';
        this.load();
        this.interval = setInterval(this.load.bind(this), 1000);
    },
    beforeUnmount() {
        // for (let i = 0; i < this.timeouts.length; i++)
        //     clearTimeout(this.timeouts[i]);
        clearInterval(this.interval);
    },
    methods: {
        load() {
            // this.timeouts[this.timeout] = setTimeout(this.load.bind(this), 1000);
            this.timeout = (this.timeout + 1) % 10;

            sign(player => {
                this.player.nick = player.nick;
                this.player.code = player.code;
                this.player.uid = player.uid;
                this.player.avatar = player.avatar;
                this.player.mobile = player.mobile;
                this.player.balance = Math.floor(player.balance / 100);
                this.player.bet = Math.floor(player.bet / 100);
                this.player.profit = Math.floor(player.profit / 100);
                this.player.commission = player.commission;
                this.player.commissionBalance = player.commissionBalance;
            });
        },
        signout() {
            signOut(() => {
                location.href = this.illegal;
            });
        },
    }
}
</script>
<template>
    <Wallet v-if="page === 'wallet'" @back="page = ''" />
    <Report v-else-if="page === 'report'" @back="page = ''" />
    <Report v-else-if="page === 'report-junior'" :junior="true" @back="page = ''" />
    <Account v-else-if="page === 'account'" @back="page = ''" :player="player" />
    <Ledger v-else-if="page === 'ledger'" @back="page = ''" />
    <Commission v-else-if="page === 'commission'" @back="page = ''" :player="player" />
    <div v-else>
        <div id="sign" :style="'background-image:url(' + signbg + ')'">
            <div class="avatar">
                <div><img v-if="player.avatar" :src="player.avatar" /></div>
            </div>
            <div class="info">
                <div class="nick">{{ player.nick || '' }}</div>
                <div class="code">账号：{{ player.code || '' }}</div>
                <div class="uid">UID：{{ player.uid || '' }}</div>
            </div>
            <div class="summary">
                <div class="item">
                    <div class="amount">{{ player.balance }}</div>
                    <div class="label">余额</div>
                </div>
                <div class="separator"></div>
                <div class="item">
                    <div class="amount">{{ player.bet }}</div>
                    <div class="label">今日流水</div>
                </div>
                <div class="separator"></div>
                <div class="item">
                    <div class="amount">{{ player.profit }}</div>
                    <div class="label">今日输赢</div>
                </div>
            </div>
        </div>
        <div class="mine-operations">
            <div class="mine-operation" @click="$emit('navigate', 4)">
                <img :src="icon.deposit" />
                <div>在线充值</div>
            </div>
            <div class="mine-operation" @click="$emit('navigate', 5)">
                <img :src="icon.withdraw" />
                <div>快速提现</div>
            </div>
            <div class="mine-operation" @click="page = 'wallet'">
                <img :src="icon.wallet" />
                <div>钱包记录</div>
            </div>
            <div class="mine-operation" @click="$emit('navigate', 1)">
                <img :src="icon.bet" />
                <div>投注记录</div>
            </div>
            <div class="mine-operation" @click="page = 'report'">
                <img :src="icon.report" />
                <div>个人报表</div>
            </div>
            <div class="mine-operation" @click="page = 'report-junior'">
                <img :src="icon.reportJunior" />
                <div>下级报表</div>
            </div>
            <div class="mine-operation" @click="page = 'ledger'">
                <img :src="icon.ledger" />
                <div>个人帐变</div>
            </div>
            <div class="mine-operation" @click="page = 'commission'">
                <img :src="icon.transfer" />
                <div>佣金转换</div>
            </div>
            <div class="mine-operation" @click="page = 'account'">
                <img :src="icon.account" />
                <div>账号管理</div>
            </div>
            <!-- <div class="mine-operation">
                <img :src="icon.template" />
                <div>切换模板</div>
            </div> -->
            <div class="mine-operation" @click="signout">
                <img :src="icon.exit" />
                <div>安全退出</div>
            </div>
            <div class="mine-operation"></div>
            <div class="mine-operation"></div>
        </div>
    </div>
</template>
<style>
#sign {
    width: 96vw;
    height: 58vw;
    margin: 0 auto;
    background-size: 100% 100%;
}

#sign .avatar {
    display: inline-block;
    padding: 46px 0 0 46px;
}

#sign .avatar div {
    width: 54px;
    height: 54px;
    border: 3px solid #FFFFFF;
    border-radius: 54px;
    overflow: hidden;
}

#sign .avatar img {
    width: 54px;
    height: 54px;
    display: block;
}

#sign .info {
    display: inline-block;
    padding-left: 12px;
}

#sign .info .nick {
    font-size: 17px;
    font-weight: 500;
    line-height: 24px;
    color: #09172F;
}

#sign .info .code,
#sign .info .uid {
    color: #624C32;
    font-size: 14px;
    line-height: 18px;
}

#sign .summary {
    display: flex;
    width: 80vw;
    justify-content: space-around;
    align-items: center;
    margin: 0 auto;
    padding-top: 12vw;
}

#sign .summary .item {
    text-align: center;
}

#sign .summary .item .amount {
    font-size: 20px;
    line-height: 28px;
    font-weight: 500;
    color: #09172F;
}

#sign .summary .item .label {
    font-size: 11px;
    line-height: 16px;
    font-weight: 400;
    color: #624C32;
}

#sign .summary .separator {
    width: 1px;
    height: 27px;
    background-color: #E3C185;
}

.mine-operations {
    padding: 5vw 0;
    text-align: center;
}

.mine-operation {
    display: inline-block;
    width: 23vw;
    font-size: 12px;
    color: #EAD5B7;
    padding-top: 3vw;
    line-height: 12px;
}

.mine-operation img {
    display: inline-block;
    width: 39px;
    height: 39px;
}
</style>