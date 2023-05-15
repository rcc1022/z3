import React from 'react';
import { message } from 'antd';
import { url } from '../http';
import { toArray } from '../json';

class UploadSupport extends React.Component {
    action = url('/photon/ctrl-http/upload');

    change = ({ file }) => {
        if (file.status === 'uploading') {
            this.changed(file, true);

            return;
        }

        if (file.status === 'done') {
            if (file.response.success) {
                this.changed(file, true, this.value);

                return;
            }

            this.changed(file, false);
            message.warn(file.response.message);

            return;
        }

        if (file.status === 'removed') {
            this.changed(file, false, this.value);

            return;
        }
    }

    changed = (file, replace, callback) => {
        let list = [];
        let exists = false;
        for (let f of this.list()) {
            if (f.uid === file.uid) {
                exists = true;
                if (replace)
                    list.push(file);

                continue;
            }

            list.push(f);
        }
        if (!exists)
            list.push(file);
        this.setState({ list }, callback);
    }

    value = () => {
        let list = [];
        for (let file of this.state.list) {
            if (!file.uri) {
                file.uri = file.response.path;
                if (file.response.thumbnail)
                    file.thumbnail = file.response.thumbnail;
                file.url = url(file.uri);
            }
            let f = {
                name: file.name,
                uri: file.uri
            };
            if (file.thumbnail)
                f.thumbnail = file.thumbnail;
            list.push(f);
        }
        this.props.form.value(this.props.name, JSON.stringify(list));
    }

    list = () => {
        if (this.state.list !== null)
            return this.state.list;

        let list = this.props.value ? toArray(this.props.value) : [];
        for (let i = 0; i < list.length; i++) {
            list[i].uid = '' + i;
            list[i].url = url(list[i].uri);
            list[i].status = 'done';
        }

        return list;
    }
}

export default UploadSupport;