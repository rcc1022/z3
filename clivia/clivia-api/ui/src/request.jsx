import React from 'react';
import { Space } from 'antd';

class Request extends React.Component {
    request = `const post = (url, body) => fetch(url, {
    method: 'POST',
    headers: header(),
    body: JSON.stringify(body)
}).then(response => {
    if (response.ok) return response.json();

    message.warn('[' + response.status + ']' + response.statusText);

    return null;
});

const header = () => {
    let header = {
        'Content-Type': 'application/json'
    };
    psid(header, true);

    return header;
}

const psid = (header) => {
    let psid = localStorage.getItem('photon-session-id');
    if (!psid) {
        psid = '';
        while (psid.length < 64) psid += Math.random().toString(36).substring(2);
        psid = psid.substring(0, 64);
        localStorage.setItem('photon-session-id', psid);
    }
    header['photon-session-id'] = psid;
}`;

    response = `{
    "code": "编码，0表示成功，大于0表示失败。",
    "data": "数据，code===0时返回。",
    "message": "失败信息说明，code>0时返回。"
}`;

    render = () => {
        return (
            <Space direction="vertical" style={{ width: '100%' }}>
                <div>参考示例</div>
                <pre>{this.request}</pre>
                <div>返回结果</div>
                <pre>{this.response}</pre>
            </Space>
        );
    }
}

export default Request;