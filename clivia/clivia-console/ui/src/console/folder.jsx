import React from 'react';
import { Input } from 'antd';
import { FolderOutlined } from '@ant-design/icons';

class Folder extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: props.value
        };
    }

    render = () => [
        <div key={this.props.name + ':file'} className="console-form-folder"><input type="file" id={this.props.name + ':file'} multiple={false} onChange={this.change} /></div>,
        <Input key={this.props.name + ':input'} addonAfter={<FolderOutlined onClick={this.select} />} value={this.state.value} />
    ];

    select = () => {
        document.getElementById(this.props.name + ':file').click();
    }

    change = (e) => {
        if (!e || !e.target || !e.target.files || e.target.files.length === 0) return;

        let path = e.target.files[0].path;
        if (!path) return;

        path = path.substring(0, path.lastIndexOf(e.target.files[0].name));
        this.props.form.value(this.props.name, path);
        this.setState({
            value: path
        });
    }
}

export default Folder;