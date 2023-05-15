import React from 'react';
import { Space, Alert } from 'antd';
import Grid from './grid';

class Setting extends React.Component {
    render = () => (
        <Space direction="vertical" style={{ width: '100%' }}>
            <Alert type="info" message={'接口地址：' + this.props.url + '/keyvalue/object'} />
            <Grid header={true} data={[{
                name: 'photon-session-id',
                type: 'string',
                require: true,
                description: '用户SESSION ID值，如：' + localStorage.getItem('photon-session-id') + '。'
            }]} />
            <Grid header={false} data={[{ name: 'key', type: 'string', require: true, description: '固定为：【' + this.props.meta.prefix + '】。' }]} />
            <div>返回结果</div>
            <pre>{'{\n' + this.props.meta.keys.map(kd => '    "' + kd.key + '":"' + kd.description + '",\n').join('') + '}'}</pre>
        </Space>
    );
}

export default Setting;