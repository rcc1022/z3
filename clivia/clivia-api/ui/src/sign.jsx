import React from 'react';
import { Space, Steps, Input } from 'antd';
import md5 from 'md5';
import './sign.css';

class Sign extends React.Component {
    constructor(props) {
        super(props);

        let secret = '';
        while (secret.length < 32) secret += Math.random().toString(36).substring(2);
        secret = secret.substring(0, 32);
        this.state = {
            time: '',
            secret: secret,
            plain: '',
            sign: ''
        };
    }

    componentDidMount = () => {
        this.change();
    }

    render = () => (
        <Space direction="vertical">
            <Steps direction="vertical" current={6}>
                <Steps.Step title="添加签名时间戳参数" description="获取当前时间戳，精确到毫秒，参数名为：sign-time。" icon={1} />
                <Steps.Step title="参数名排序" description="将所有参数根据参数名ASCII码升序排列。" icon={2} />
                <Steps.Step title="拼接参数" description="用等号'='拼接参数名与值，并用'&'符号连接，如：a=1&b=2&c=3&sign-time=1234567890123" icon={3} />
                <Steps.Step title="添加密钥" description="添加'&secret={secret value}'" icon={4} />
                <Steps.Step title="计算MD5值" description="计算字符串的MD5值，转化为小写字母，即为签名值，签名参数名为sign。" icon={5} />
            </Steps>
            <div>示例</div>
            <div className="sign-form">
                <div className="sign-form-line">
                    <div className="sign-form-label">参数名</div>
                    <div className="sign-form-value">参数值</div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label"><Input id="name-1" onChange={this.change} defaultValue="a" /></div>
                    <div className="sign-form-value"><Input id="value-1" onChange={this.change} defaultValue="1" /></div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label"><Input id="name-2" onChange={this.change} defaultValue="b" /></div>
                    <div className="sign-form-value"><Input id="value-2" onChange={this.change} defaultValue="2" /></div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label"><Input id="name-3" onChange={this.change} defaultValue="c" /></div>
                    <div className="sign-form-value"><Input id="value-3" onChange={this.change} defaultValue="3" /></div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label">sign-time</div>
                    <div className="sign-form-value">{this.state.time}</div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label">secret</div>
                    <div className="sign-form-value">{this.state.secret}</div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label">签名明文</div>
                    <div className="sign-form-value sign-form-plain">{this.state.plain}</div>
                </div>
                <div className="sign-form-line">
                    <div className="sign-form-label">sign</div>
                    <div className="sign-form-value">{this.state.sign}</div>
                </div>
            </div>
        </Space>
    );

    change = () => {
        let array = [];
        for (let i = 1; i <= 3; i++)
            array.push(document.querySelector('#name-' + i).value + '=' + document.querySelector('#value-' + i).value);
        let time = new Date().getTime();
        array.push('sign-time=' + time);
        array.sort();
        array.push('secret=' + this.state.secret);
        let plain = array.join('&');
        this.setState({
            time: time,
            plain: plain,
            sign: md5(plain)
        });
    }
}

export default Sign;