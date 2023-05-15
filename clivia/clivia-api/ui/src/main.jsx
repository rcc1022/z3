import React from 'react';
import { ConfigProvider, Layout, Menu, Space, Alert } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import { service, url } from './http';
import Request from './request';
import Sign from './sign';
import Grid from './grid';
import Upload from './upload';
import Setting from './setting';
import Dict from './dict';
import './main.css';

class Main extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            logo: '',
            user: {},
            data: [],
            item: {}
        };
        this.map = {};
        this.url = window.location.href;
        this.url = this.url.substring(0, this.url.length - window.location.pathname.length);
    }

    componentDidMount = () => {
        service('/keyvalue/object', { key: 'setting.global.' }).then(data => {
            if (data === null) return;

            document.title = data['setting.global.console.title'] || 'Clivia API';
            this.setState({ logo: data['setting.global.console.logo'] });
        });
        service('/user/sign').then(data => this.setState({ user: data }));
        service('/api/get').then(data => {
            if (data === null) return;

            for (let module of data) {
                for (let service of module.services) {
                    if (service.psid) {
                        if (!service.headers) service.headers = [];
                        service.headers.push({
                            name: 'photon-session-id',
                            type: 'string',
                            require: true,
                            description: '用户SESSION ID值，如：' + localStorage.getItem('photon-session-id') + '。'
                        });
                    }
                    if (service.sign) {
                        if (!service.parameters) service.parameters = [];
                        service.parameters.push({ name: 'sign-time', type: 'long', require: true, description: '签名时间戳，精确到毫秒。' });
                        service.parameters.push({ name: 'sign', type: 'string', require: true, description: '参数签名，规则参考【通用->参数签名】页。' });
                    }
                    if (module.model) {
                        if (service.response === 'model')
                            service.response = module.model;
                        else if (service.response.length === 1 && service.response[0] === 'model')
                            service.response = '[' + module.model + ']';
                        else if (service.response === 'pagination') {
                            if (!service.parameters) service.parameters = [];
                            service.parameters.push({ name: 'pageSize', type: 'int', description: '每页显示记录数，默认：20。' });
                            service.parameters.push({ name: 'pageNum', type: 'int', description: '当前显示页数。' });
                            service.response = `{
    "count":"记录总数。",
    "size":"每页最大显示记录数。",
    "number":"当前显示页数。",
    "page":"总页数。",
    "list":[
        `+ module.model.replace(/\n {4}/g, '\n            ').replace('\n}', '\n        }') + `
    ]
}`;
                        }
                    }
                }
            }
            let list = [{
                name: '通用',
                services: [{
                    name: 'HTTP请求',
                    page: 'request'
                }, {
                    name: '参数签名',
                    page: 'sign'
                }]
            }];
            for (let d of data) {
                if (d.type === 'config') {
                    if (d.dict) {
                        let dicts = [];
                        for (let dict of d.dict) {
                            if (dict.key && dict.key.length > 0) {
                                dicts.push({
                                    key: dict.key,
                                    name: dict.name,
                                    page: 'dict'
                                });
                            }
                        }
                        if (dicts.length > 0) {
                            list.push({
                                name: '字典',
                                services: dicts
                            });
                        }
                    }
                } else
                    list.push(d);
            }
            this.setState({ data: list }, () => this.show({ key: '0-0-0' }));
        });
    }

    render = () => (
        <ConfigProvider locale={zhCN}>
            <Layout style={{ minHeight: '100vh' }}>
                <Layout.Sider>
                    <div className="api-logo">{this.props.logo ? [<img key="img" src={url(this.props.logo)} alt="" />, <div key="div"></div>] : null}</div>
                    <div className="api-menu"><Menu onClick={this.show} mode="inline" theme="dark" defaultOpenKeys={['0-0']} defaultSelectedKeys={[this.state.item.uri ? '0-0' : '0-0-0']}>{this.menu(this.state.data, '0')}</Menu></div>
                    <div className="api-copyright">clivia-api &copy; {new Date().getFullYear()}</div>
                </Layout.Sider>
                <Layout>
                    <Layout.Header className="api-header">
                    </Layout.Header>
                    <Layout.Content>
                        <div className="api-body">{this.body()}</div>
                    </Layout.Content>
                </Layout>
            </Layout>
        </ConfigProvider>
    );

    menu = (items, parent) => {
        let menus = [];
        if (items.length === 0) return menus;

        for (let i = 0; i < items.length; i++) {
            let key = parent + '-' + i;
            let item = items[i];
            this.map[key] = item;
            if (item.services)
                menus.push(<Menu.SubMenu key={key} title={<span>{item.name}</span>} >{this.menu(item.services, key)}</Menu.SubMenu>);
            else
                menus.push(<Menu.Item key={key}>{item.name}</Menu.Item>);
        }

        return menus;
    };

    show = e => {
        this.setState({
            item: this.map[e.key]
        });
    }

    body = () => {
        if (this.state.item.page) {
            switch (this.state.item.page) {
                case 'request':
                    return <Request />;
                case 'sign':
                    return <Sign />;
                case 'upload':
                    return <Upload url={this.url} meta={this.state.item} />;
                case 'setting':
                    return <Setting url={this.url} meta={this.state.item} />;
                case 'dict':
                    return <Dict url={this.url} meta={this.state.item} />;
                default:
                    return <div />;
            }
        }

        return (
            <Space direction="vertical" style={{ width: '100%' }}>
                <Alert type="info" message={'接口地址：' + this.url + this.state.item.uri} />
                <Grid header={true} data={this.state.item.headers} />
                <Grid header={false} data={this.state.item.parameters} />
                <div className="api-response-title">返回</div>
                <pre>{this.state.item.response}</pre>
            </Space>
        );
    }
}

export default Main;