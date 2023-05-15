import {
    message
} from 'antd';

// const root = 'http://43.154.106.190:8080';
const root = 'http://console';

const service = (uri, body) => post(uri, body).then(json => {
    if (json === null) return null;

    if (json.code === 0) {
        if (json.message)
            message.success(json.message);

        return json.data;
    }

    message.warn('[' + json.code + ']' + json.message);

    return null;
});

const post = (uri, body) => fetch(root + uri, {
    method: 'POST',
    headers: header(),
    body: JSON.stringify(body)
}).then(response => {
    if (post.loader) {
        post.loader.setState({
            loading: false
        });
    }

    if (uri === '/user/sign-out')
        localStorage.removeItem('photon-session-id');

    if (response.ok) return response.json();

    message.warn('[' + response.status + ']' + response.statusText);

    return null;
});

const upload = (name, file) => {
    let header = {};
    psid(header, true);
    let body = new FormData();
    body.append(name, file);

    return fetch(root + '/photon/ctrl-http/upload', {
        method: 'POST',
        headers: header,
        body: body
    }).then(response => {
        if (post.loader) {
            post.loader.setState({
                loading: false
            });
        }

        if (response.ok) {
            return response.json().then(json => {
                if (json.success)
                    return json;

                message.warn(json.message);

                return null;
            });
        }

        message.warn('[' + response.status + ']' + response.statusText);

        return null;
    });
}

const header = () => {
    let header = {
        'Content-Type': 'application/json'
    };
    psid(header, true);

    return header;
}

const url = uri => {
    if (uri.indexOf('://') > -1)
        return uri;

    return root + uri;
}

const psid = (header, loading) => {
    if (loading && post.loader) {
        post.loader.setState({
            loading: true
        });
    }

    let psid = localStorage.getItem('photon-session-id');
    if (!psid) {
        psid = '';
        while (psid.length < 64) psid += Math.random().toString(36).substring(2);
        psid = psid.substring(0, 64);
        localStorage.setItem('photon-session-id', psid);
    }
    header['photon-session-id'] = psid;
}

const loader = loader => post.loader = loader;

export {
    service,
    post,
    upload,
    url,
    psid,
    loader
};