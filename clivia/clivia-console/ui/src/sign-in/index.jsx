import React from 'react';
import { Layout, Form, Input, Radio, Button, message } from 'antd';
import { UserOutlined, LockOutlined, FieldNumberOutlined, GithubOutlined, WechatOutlined, WeiboOutlined, AlipayOutlined } from '@ant-design/icons';
import { service } from '../http';
import { toArray } from '../json';
import './index.css';

class SignIn extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            up: false,
            invitecode: false,
            grades: []
        };
        service('/keyvalue/object', { key: 'setting.agreement.user.sign-up' }).then(data => {
            if (data === null) return;

            let array = toArray(data['setting.agreement.user.sign-up']);
            if (array.length === 0) return;

            let agreement = array[0];
            agreement.label = agreement.name;
            let index = agreement.name.lastIndexOf('.');
            if (index > -1)
                agreement.label = agreement.name.substring(0, index);
            this.setState({ agreement: agreement });
        });
        service('/keyvalue/object', { key: 'setting.user.sign-up.invite-code' }).then(data => {
            if (data === null) return;

            if (data['setting.user.sign-up.invite-code'] === '1') {
                this.setState({ invitecode: true });
            }
        });
        service('/user/crosier/sign-up-grades').then(data => {
            if (data === null || data.length <= 1)
                return;

            this.setState({ grades: data });
        });
    }

    finish = values => {
        if (this.state.up && values.repeat !== values.password) {
            message.warn('重复密码与密码必须相同！');

            return;
        }

        values.type = '';
        service('/user/sign-' + (this.state.up ? 'up' : 'in'), values).then(data => {
            if (data != null) {
                window.location.reload();
            }
        });
    };

    change = () => {
        this.setState({ up: !this.state.up });
    }

    render() {
        return (
            <Layout className="sign-in-layout">
                <Layout.Content>
                    <div className="sign-in-header">{document.title}</div>
                    <div className="sign-in-form">
                        <Form onFinish={this.finish}>
                            <Form.Item name="uid"><Input prefix={<UserOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="用户名" autoFocus={true} /></Form.Item>
                            <Form.Item name="password"><Input.Password prefix={<LockOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="密码" /></Form.Item>
                            {this.state.up ? <Form.Item name="repeat"><Input.Password prefix={<LockOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="重复密码" /></Form.Item> : null}
                            {this.state.up && this.state.invitecode ? <Form.Item name="invitecode"><Input prefix={<FieldNumberOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="邀请码" /></Form.Item> : null}
                            {this.state.up && this.state.grades.length > 0 ? <Form.Item label="我是" name="grade"><Radio.Group options={this.state.grades} optionType="button" buttonStyle="solid" /></Form.Item> : null}
                            {this.state.up && this.state.agreement ? <Form.Item><a href={this.state.agreement.uri + '?filename=' + this.state.agreement.name} target="_blank" rel="noopener noreferrer">{this.state.agreement.label}</a></Form.Item> : null}
                            <Form.Item><Button type="primary" htmlType="submit" className="sign-in-up-button">{this.state.up ? '提交注册' : '登录'}</Button></Form.Item>
                            {this.props.signUpEnable ? <Form.Item>
                                <Button type="link" className="sign-in-up-link" onClick={this.change}>{this.state.up ? '使用已有账户登录' : '注册新账户'}</Button>
                                <span>其他登录方式</span>
                                <WechatOutlined className="sign-in-third" />
                                <AlipayOutlined className="sign-in-third" />
                                <WeiboOutlined className="sign-in-third" />
                                <GithubOutlined className="sign-in-third" />
                            </Form.Item> : null}
                        </Form>
                    </div>
                </Layout.Content>
                <Layout.Footer className="sign-in-footer">clivia-console &copy; {new Date().getFullYear()}</Layout.Footer>
            </Layout>
        );
    }
}

export default SignIn;