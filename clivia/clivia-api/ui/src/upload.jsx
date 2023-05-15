import React from 'react';
import { Space, Alert, Divider } from 'antd';
import Grid from './grid';

class Upload extends React.Component {
    responseFile = `{
    "path": "文件URI地址",
    "fileName": "原文件名",
    "fileSize": "文件大小",
    "success": "true-成功；false-失败"
}`;

    responseBase64 = `{
    "code": 0,
    "data": {
        "path": "文件URI地址",
        "fileName": "原文件名",
        "fileSize": "文件大小",
        "success": "true-成功；false-失败"
    }
}`;

    render = () => (
        <Space direction="vertical" style={{ width: '100%' }}>
            <Divider dashed={true}>文件方式上传</Divider>
            <Alert type="info" message={'接口地址：' + this.props.url + '/photon/ctrl-http/upload'} />
            <Grid header={true} data={this.props.meta.headers} />
            <Grid header={false} data={[{ name: this.props.meta.upload, type: 'file', require: true, description: '上传文件。' }]} />
            <div>返回结果</div>
            <pre>{this.responseFile}</pre>
            <Divider dashed={true}>Base64方式上传</Divider>
            <Alert type="info" message={'接口地址：' + this.props.url + '/photon/ctrl/upload'} />
            <Grid header={true} data={this.props.meta.headers} />
            <Grid header={false} data={[{ name: 'name', type: 'string', require: true, description: '固定为：' + this.props.meta.upload + '。' },
            { name: 'contentType', type: 'string', require: true, description: '文件格式，如：image/jpeg。' },
            { name: 'fileName', type: 'string', require: true, description: '原文件名。' },
            { name: 'base64', type: 'string', require: true, description: 'BASE64编码后的文件内容，不包含Content-Type。' }]} />
            <div>返回结果</div>
            <pre>{this.responseBase64}</pre>
            <Divider dashed={true}>cURL方式上传</Divider>
            <Alert type="info" message={'接口地址：' + this.props.url + '/photon/ctrl-http/upload'} />
            <div>发送</div>
            <pre>curl -H 'photon-session-id: {localStorage.getItem('photon-session-id')}' -F '{this.props.meta.upload}=@/path/to/file' {this.props.url}/photon/ctrl-http/upload</pre>
            <div>返回结果</div>
            <pre>{this.responseFile}</pre>
        </Space>
    );
}

export default Upload;