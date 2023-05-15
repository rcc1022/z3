<script>
import { service } from '../util/http';
import Appbar from './appbar.vue';
export default {
    components: { Appbar },
    emits: ['back'],
    data() {
        return {
            list: []
        };
    },
    mounted() {
        service('/player/ledger/user', {}, data => {
            for (let i = 0; i < data.list.length; i++) {
                let ledger = data.list[i];
                ledger.amount = (ledger.amount / 100).toFixed(2).replace(/\.0+$/g, '');
            }
            this.list = data.list;
        });
    }
}
</script>
<template>
    <Appbar @back="$emit('back')" label="交易明细" />
    <table cellspacing="1" class="ledger">
        <thead>
            <tr>
                <th>金额</th>
                <th>类型</th>
                <th>备注</th>
                <th>时间</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="ledger in list">
                <td>{{ ledger.amount }}</td>
                <td>{{ ledger.type }}</td>
                <td>{{ ledger.memo }}</td>
                <td>{{ ledger.time}}</td>
            </tr>
        </tbody>
    </table>
</template>
<style>
.ledger {
    width: 100%;
    background-color: #333;
}

.ledger th,
.ledger td {
    background-color: #111111;
    color: #fff;
    text-align: center;
}
</style>