import React from 'react';
import { Upload, Button } from 'antd';
import { PaperClipOutlined, UploadOutlined } from '@ant-design/icons';
import UploadSupport from './upload';
import { toArray } from '../json';
import { url, psid } from '../http';

class File extends UploadSupport {
    state = {
        list: null
    }

    render = () => {
        if (this.props.readonly) {
            let files = [];
            try {
                for (let file of toArray(this.props.value)) {
                    files.push(<div key={'file-' + files.length} className="console-form-file">
                        <PaperClipOutlined />
                        <a href={url(file.uri)} target="_blank" rel="noopener noreferrer">{file.name}</a>
                    </div>);
                }
            } catch (e) { }

            return files;
        }

        let props = {
            action: this.action,
            headers: {},
            name: this.props.upload,
            multiple: true,
            progress: {
                strokeColor: {
                    '0%': '#108ee9',
                    '100%': '#87d068',
                },
                strokeWidth: 3,
                format: percent => `${parseFloat(percent.toFixed(2))}%`,
            },
            fileList: this.list(),
            onChange: this.change
        };
        psid(props.headers, false);

        return (
            <Upload {...props}>
                <Button><UploadOutlined /> 上传</Button>
            </Upload>
        );
    }
}

export default File;