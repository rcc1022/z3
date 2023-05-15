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
            password: '',
            repeat: '',
            mobile: '',
            alert: {
                show: false,
                content: '',
            },
        };
    },
    methods: {
        showAlert(content) {
            this.alert.content = content;
            this.alert.show = true;
        },
        submit() {
            if (this.password) {
                if (this.password.length < 6 || this.password.length > 20) {
                    this.showAlert('密码长度不符合要求 6,20');

                    return;
                }
            }
            if (this.repeat) {
                if (this.repeat.length < 6 || this.repeat.length > 20) {
                    this.showAlert('确认密码长度不符合要求 6,20');

                    return;
                }

                if (this.repeat != this.password) {
                    this.showAlert('确认密码与确认字段不一致');

                    return;
                }
            }
            if (!this.mobile) {
                this.showAlert('请输入手机号');

                return;
            }
            service('/player/save', { password: this.password, repeat: this.repeat, mobile: this.mobile }, data => {
                this.showAlert('修改成功');
            });
        }
    },
}
</script>
<template>
    <Appbar @back="$emit('back')" label="账号管理" />
    <div class="account">
        <div class="account-line">
            <div class="account-label">用户名</div>
            <div class="account-input">{{ player.code || '' }}</div>
        </div>
        <div class="account-line">
            <div class="account-label">登录密码</div>
            <div class="account-input"><input type="password" placeholder="留空不修改" v-model="password" /></div>
        </div>
        <div class="account-line">
            <div class="account-label">确认密码</div>
            <div class="account-input"><input type="password" placeholder="留空不修改" v-model="repeat" /></div>
        </div>
        <div class="account-line">
            <div class="account-label">手机号</div>
            <div class="account-disable" v-if="player.mobile">{{ player.mobile }}</div>
            <div class="account-input" v-else><input placeholder="填写之后，不能修改" v-model="mobile" /></div>
        </div>
        <div class="account-toolbar">
            <div @click="submit">确认</div>
        </div>
    </div>
    <alert v-if="alert.show" :content="alert.content" @cancel="alert.show = false" @ok="alert.show = false"></alert>
</template>
<style>
.account {
    font-size: 12px;
    color: #fff;
    background-color: #333;
}

.account-line {
    margin-left: 8px;
    border-bottom: 1px solid #ccc;
    padding: 4px 0;
}

.account-line div {
    display: inline-block;
}

.account-label {
    width: 100px;
}

.account-input input {
    background: none;
    border: none;
    outline: none;
}

.account-disable {
    color: #888;
}

.account-toolbar {
    text-align: center;
    padding: 6px 0;
}

.account-toolbar div {
    display: inline-block;
    padding: 4px 8px;
    background-color: #666;
}
</style>