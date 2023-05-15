<script>
import { showToast } from 'vant';
import Fingerprint2 from 'fingerprintjs2';
import md5 from 'md5';
import { post, service } from '../util/http';
import { keyvalue } from '../util/keyvalue';
export default {
    data() {
        return {
            type: 0,
            username: '',
            password: '',
            remember: false,
            repeat: '',
            invitor: '',
            weixinAuth: '',
        };
    },
    mounted() {
        this.username = localStorage.getItem('username') || '';
        this.password = localStorage.getItem('password') || '';
        this.remember = localStorage.getItem('remember') === 'true';
        if (location.search && location.search.indexOf('invite=') > -1) {
            this.invitor = location.search.substring(location.search.indexOf('invite=') + 7);
            if (this.invitor.indexOf('&') > -1)
                this.invitor = this.invitor.substring(0, this.invitor.indexOf('&'));
        }
        keyvalue('setting.weixin.auth', data => {
            this.weixinAuth = data['setting.weixin.auth'] || '';
        });
    },
    methods: {
        signIn() {
            post('/user/sign-in', { type: '', uid: this.username, password: this.password }, json => {
                if (json.code === 0) {
                    if (this.remember) {
                        localStorage.setItem('username', this.username);
                        localStorage.setItem('password', this.password);
                    } else {
                        localStorage.removeItem('username');
                        localStorage.removeItem('password');
                    }
                    localStorage.setItem('remember', this.remember + '');
                    location.reload();
                }
                else
                    this.warn(json.message);
            });
        },
        signUp() {
            if (this.username.length < 5 || this.username.length > 15) {
                this.warn('账号长度限制为5-15个字符')

                return;
            }

            if (this.password.length < 6 || this.password.length > 20) {
                this.warn('账号长度限制为6-20个字符')

                return;
            }

            if (this.repeat != this.password) {
                this.warn('两次密码不相同')

                return;
            }

            post('/user/sign-up', { type: '', uid: this.username, password: this.password }, json => {
                if (json.code === 0)
                    location.reload();
                else if (json.code === 151004)
                    this.warn('账号已存在');
                else
                    this.warn(json.message);
            });
        },
        forget() {
            this.warn('请联系客服找回密码');
        },
        wechat() {
            if (window.requestIdleCallback) {
                requestIdleCallback(this.wechatFingerprint);
            } else {
                setTimeout(this.wechatFingerprint, 500);
            }
        },
        wechatFingerprint() {
            Fingerprint2.get((components) => {
                service(this.weixinAuth === '1' ? '/home/wx-sign-in-url' : '/home/2auth', { host: location.host, fingerprint: md5(JSON.stringify(components)) }, data => {
                    location.href = data;
                });
            });
        },
        warn(message) {
            showToast({
                message: message,
                icon: 'warning',
            });
        },
    },
}
</script>
<template>
    <div class="sign-in-up-body">
        <div v-if="(type === 0)">
            <div class="sign-in-up-form">
                <van-field v-model="username" label="账号" placeholder="输入您的用户名" />
                <van-field v-model="password" type="password" label="密码" placeholder="输入您的密码" />
            </div>
            <div class="sign-in-remember" @click="(remember = !remember)">
                <div class="sign-in-remember-label">记住密码</div>
                <van-switch v-model="remember" @click.stop="" />
            </div>
            <div class="sign-in-up-submit">
                <van-button type="primary" :block="true" @click="signIn">登录</van-button>
            </div>
            <div class="sign-in-links">
                <div class="sign-in-link" @click="(type = 1)">注册账号</div>
                <div class="sign-in-link-separator"></div>
                <div class="sign-in-link" @click="forget">忘记密码</div>
            </div>
            <div class="sign-in-third">
                <van-divider>第三方账号登录</van-divider>
            </div>
            <div class="sign-in-thirds">
                <div class="sign-in-wechat-logo" @click="wechat">
                    <svg xmlns='http://www.w3.org/2000/svg' width='60' height='48' viewBox='0 0 60 48'>
                        <g fill='#FFF' fill-rule='evenodd' opacity='.9'>
                            <path
                                d='M52.629 42.213c3.73-2.706 6.113-6.709 6.113-11.157 0-8.152-7.921-14.76-17.694-14.76-9.77 0-17.693 6.608-17.693 14.76 0 8.153 7.922 14.761 17.693 14.761 2.02 0 3.969-.289 5.776-.809.165-.05.339-.078.52-.078.34 0 .648.104.94.272l3.872 2.24c.109.06.214.11.342.11a.59.59 0 0 0 .59-.59c0-.148-.059-.293-.095-.433l-.798-2.977a1.413 1.413 0 0 1-.062-.376c0-.397.196-.749.496-.963M35.15 28.544a2.21 2.21 0 1 1 0-4.422 2.21 2.21 0 0 1 0 4.422m11.795 0c-1.22 0-2.208-.99-2.208-2.21a2.21 2.21 0 1 1 4.418 0 2.21 2.21 0 0 1-2.21 2.21' />
                            <path
                                d='M21.232 0C9.506 0 0 7.93 0 17.712c0 5.338 2.86 10.142 7.336 13.389.36.257.595.678.595 1.155 0 .157-.034.3-.075.451l-.957 3.573c-.044.168-.114.342-.114.518 0 .391.317.708.708.708.154 0 .28-.057.408-.132l4.65-2.687c.349-.2.718-.325 1.126-.325.218 0 .427.032.624.092a25.087 25.087 0 0 0 8.097.944 13.658 13.658 0 0 1-.713-4.341c0-8.921 8.669-16.155 19.364-16.155.387 0 .486.013.867.031C40.317 6.471 31.824 0 21.232 0m-7.077 14.557a2.512 2.512 0 0 1 0-5.025 2.511 2.511 0 0 1 2.51 2.513 2.512 2.512 0 0 1-2.51 2.512m14.154 0a2.511 2.511 0 0 1-2.51-2.512 2.511 2.511 0 0 1 2.51-2.513 2.511 2.511 0 0 1 2.51 2.513 2.511 2.511 0 0 1-2.51 2.512' />
                        </g>
                    </svg>
                </div>
                <div class="sign-in-wechat-label"><span @click="wechat">微信登录</span></div>
            </div>
        </div>
        <div v-if="(type === 1)">
            <div class="sign-in-up-form">
                <van-field v-model="username" label="账号" placeholder="输入注册用户名" />
                <van-field v-model="password" type="password" label="密码" placeholder="输入登录密码" />
                <van-field v-model="repeat" type="password" label="确认密码" placeholder="请再次输入登录密码" />
                <van-field v-model="invitor" label="推荐人" readonly />
            </div>
            <div class="sign-in-up-submit">
                <van-button type="primary" :block="true" @click="signUp">注册</van-button>
            </div>
            <div class="sign-in-links">
                <div class="sign-in-link" @click="(type = 0)">已有账户，立即登录</div>
            </div>
        </div>
    </div>
</template>
<style>
.sign-in-up-body {
    height: 100vh;
    background-color: rgb(248, 248, 248);
}

.sign-in-up-form,
.sign-in-remember {
    margin-top: 20px;
    border-top: 1px solid rgb(208, 208, 208);
    border-bottom: 1px solid rgb(208, 208, 208);
}

.sign-in-remember {
    display: flex;
    padding: 16px;
    align-items: center;
    justify-content: space-between;
}

.sign-in-up-submit {
    margin-top: 20px;
    padding: 0 16px;
}

.sign-in-links {
    padding: 32px 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

.sign-in-link-separator {
    border: 1px solid rgb(192, 193, 193);
    height: 16px;
    margin: 0 16px;
}

.sign-in-link {
    color: rgb(60, 108, 185);
}

.sign-in-third {
    padding: 0 20vw;
}

.sign-in-wechat-logo {
    width: 48px;
    height: 48px;
    background-color: rgb(81, 182, 112);
    border-radius: 48px;
    text-align: center;
    display: inline-block;
}

.sign-in-wechat-logo svg {
    width: 32px;
}

.sign-in-thirds {
    text-align: center;
}
</style>