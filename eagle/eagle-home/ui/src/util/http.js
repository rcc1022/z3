// const root = 'http://mobile';
const root = 'http://43.154.106.190:8080';


var illegalUrl = '';

const service = (uri, data, success) => {
    post(uri, data, json => {
        if (json.code === 0 && success) {
            success(json.data);
        } else if (json.code === 151901) {
            location.href = illegalUrl;
        }
    });
}

const post = (uri, body, success) => {
    let header = {};
    psid(header);
    fetch(root + uri, {
        method: 'POST',
        headers: header,
        body: JSON.stringify(body)
    }).then(response => {
        if (uri === '/user/sign-out')
            localStorage.removeItem('photon-session-id');

        if (response.ok && success)
            response.json().then(success);
    });
}

const upload = (name, file, success) => {
    let header = {};
    psid(header);
    let body = new FormData();
    body.append(name, file);

    fetch(root + '/photon/ctrl-http/upload', {
        method: 'POST',
        headers: header,
        body: body
    }).then(response => {
        if (response.ok) {
            response.json().then(json => {
                if (success)
                    success(json);
            });
        }
    });
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
}

const url = uri => {
    if (!uri)
        return null;

    if (uri.indexOf('://') > -1)
        return uri;

    return root + uri;
}

const illegal = (url) => {
    if (url)
        illegalUrl = url;
    else if (illegalUrl)
        location.href = illegalUrl;
    else
        location.reload();
}

export {
    post,
    service,
    upload,
    url,
    illegal
}