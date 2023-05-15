import React from 'react';
import { Select, Tree, Button } from 'antd';
import { service } from '../http';
import './crosier.css';

class Crosier extends React.Component {
    constructor(props) {
        super(props);

        this.state = {};
        service('/user/crosier/grades').then(data => {
            if (data == null) return;

            this.setState({
                grades: data,
                grade: 0
            }, () => this.grade(0));
        });
        service('/console/menu', { all: true }).then(data => {
            if (data === null) return;

            this.setState({ menu: data });
        });
    }

    grade = grade => {
        this.setState({ show: false });
        service('/user/crosier/pathes', { grade: grade }).then(data => {
            if (data === null) return;

            this.setState({
                show: true,
                grade: grade,
                pathes: data
            });
        });
    }

    check = e => {
        this.setState({
            pathes: e.checked
        });
    }

    save = () => {
        this.setState({ show: false }, () => service('/user/crosier/save', { grade: this.state.grade, pathes: this.state.pathes.join(',,') }).then(data => this.setState({ show: true })));
    }

    expands = () => {
        let map = {};
        for (let path of this.state.pathes) {
            while (true) {
                map[path] = true;
                let index = path.lastIndexOf(';');
                if (index === -1)
                    break;

                path = path.substring(0, index);
            }
        }

        let expands = [];
        for (let key in map) {
            expands.push(key);
        }

        return expands;
    }

    render = () => {
        let elements = [];
        if (!this.state.grades || this.state.grades.length === 0) return elements;

        elements.push(this.grades());
        if (this.state.show && this.state.menu && this.state.menu.length > 0)
            elements.push(<Tree key="menu" checkable={true} selectable={false} showIcon={true} checkStrictly={true}
                defaultExpandedKeys={this.expands()} defaultCheckedKeys={this.state.pathes} onCheck={this.check} treeData={this.nodes(this.state.menu, '')} />);
        if (this.props.meta.toolbar && this.props.meta.toolbar.length > 0)
            elements.push(<div key="toolbar" className="console-crosier-toolbar"><Button type="primary" onClick={this.save}>保存</Button></div>);

        return elements;
    }

    grades = () => {
        let options = [];
        for (let grade of this.state.grades) {
            options.push(<Select.Option key={grade.grade} value={grade.grade}>{grade.name}</Select.Option>);
        }

        return <Select key="grades" defaultValue={this.state.grade || 0} style={{ width: '100%' }} onChange={this.grade}>{options}</Select>;
    }

    nodes = (menus, parentKey) => {
        let nodes = [];
        if (!menus || menus.length === 0) return nodes;

        let keys = {};
        for (let menu of menus) {
            let key = parentKey + (menu.service || menu.type || menu.label);
            if (menu.parameter)
                key += JSON.stringify(menu.parameter);
            if (key in keys) continue;

            keys[key] = true;
            nodes.push({
                key: key,
                title: menu.label,
                children: this.nodes(menu.items, key + ';')
            });
        }

        return nodes;
    }
}

export default Crosier;