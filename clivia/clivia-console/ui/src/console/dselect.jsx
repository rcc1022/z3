import React from 'react';
import { Select } from 'antd';
import { service } from '../http';

class DSelect extends React.Component {
    constructor(props) {
        super(props);

        if (props.form)
            props.form.value(props.name, props.value);

        this.vname = props.vname || 'id';
        this.lname = props.lname || 'name';
        this.state = {
            options: [],
            value: props.value
        };
    }

    componentDidMount = () => {
        this.search('');
    }

    focus = () => {
        this.search('');
    }

    search = (value) => {
        let parameter = {};
        if (this.props.search) {
            for (let i = 0; i < this.props.search.length; i++) {
                if (i === this.props.search.length - 1)
                    parameter[this.props.search[i].name] = value;
                else if (this.props.search[i].form)
                    parameter[this.props.search[i].name || this.props.search[i].form] = this.props.form.value(this.props.search[i].form, null);
            }
        } else
            parameter.value = value;
        service(this.props.body.uri(this.props.uri, this.props.service), { ...parameter, ...this.props.parameter }).then(data => {
            if (data === null) return;

            let options = []
            for (let option of data.list || data) {
                let label = option[this.vname];
                if (this.lname.indexOf('+') > -1)
                    // eslint-disable-next-line
                    eval('label=' + this.lname);
                else
                    label = option[this.lname];
                options.push({
                    label: label,
                    value: option[this.vname]
                });
            }
            this.setState({ options });
        });
    }

    filter = (input, option) => {
        if (!option) return false;

        if (input === '') return true;

        return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 || option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }

    change = value => {
        this.setState({ value: value });
        this.props.form.value(this.props.name, value);
    }

    render = () => <Select showSearch={true} onFocus={this.focus} onSearch={this.search} filterOption={this.filter} onChange={this.change} value={this.state.value} options={this.state.options} />;
}

export default DSelect;