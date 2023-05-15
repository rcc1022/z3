import React from 'react';
import { TreeSelect } from 'antd';
import { service } from '../http';

class Category extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            list: [],
            value: ''
        };
        props.form.value(props.name, props.value);
        service('/category/list', { key: props.list, pointTo: props.pointTo }).then(data => {
            if (data === null)
                return;

            let state = {
                list: [],
                value: props.value
            };
            this.format(state.list, data);
            this.setState(state);
        });
    }

    format = (target, source) => {
        if (source.length === 0)
            return;

        for (let category of source) {
            let element = {
                title: category.name,
                value: category.id
            };
            if (category.children) {
                element.children = [];
                this.format(element.children, category.children);
            }
            target.push(element);
        }
    }

    change = value => {
        this.setState({
            value: value
        });
        this.props.form.value(this.props.name, value);
    }

    render = () => <TreeSelect treeData={this.state.list} value={this.state.value} treeDefaultExpandedKeys={[this.state.value]} onChange={this.change} />;
}

export default Category;