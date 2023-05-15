import React from 'react';
import { Space, Alert } from 'antd';
import Grid from './grid';

class Dict extends React.Component {
    render = () => (
        <Space direction="vertical" style={{ width: '100%' }}>
            <Alert type="info" message={'接口地址：' + this.props.url + '/dict/list'} />
            <Grid header={true} data={[{
                name: 'photon-session-id',
                type: 'string',
                require: true,
                description: '用户SESSION ID值，如：' + localStorage.getItem('photon-session-id') + '。'
            }]} />
            <Grid header={false} data={[{ name: 'key', type: 'string', require: true, description: '固定为：【' + this.props.meta.key + '】。' }]} />
            <div>返回结果</div>
            <pre>{'[{\n    "value": "值",\n    "name": "名称"\n}]'}</pre>
        </Space>
    );
}

export default Dict;