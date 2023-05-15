import React from 'react';
import { Form, Radio, Checkbox, Select, DatePicker, TimePicker, Switch, AutoComplete, Transfer, Input, Button, message } from 'antd';
import { SyncOutlined, VerticalAlignTopOutlined, ArrowUpOutlined, ArrowDownOutlined, VerticalAlignBottomOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import moment from 'moment';
import 'moment/locale/zh-cn';
import locale from 'antd/es/date-picker/locale/zh_CN';
import { service } from '../http';
import meta from './meta';
import { toDecimal, fromDecimal, toPercent, fromPercent, toInt } from './numeric';
import { toArray } from '../json';
import Image from './image';
import File from './file';
import Folder from './folder';
import DSelect from './dselect';
import Editor from './editor';
import Category from './category';
import User from './user';
import './form.css';

const layout = {
    labelCol: {
        xs: {
            span: 24
        },
        sm: {
            span: 6
        }
    },
    wrapperCol: {
        xs: {
            span: 24
        },
        sm: {
            span: 12
        }
    }
};

class Base extends React.Component {
    constructor(props) {
        super(props);

        this.form = React.createRef();
        this.state = props.data || {};
        this.format(this.state);
        this.values = {};
        this.binds = {};
        this.transfers = {};
        this.itemIndex = 0;
    }

    componentDidMount = () => {
        if (this.props.uri && !this.props.data) {
            this.load().then(data => {
                if (data === null) return;

                this.data(data);
            });
        }

        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            if (prop.type === 'auto-complete' && prop.service && prop.vname) {
                service(prop.service, prop.parameter).then(data => {
                    if (data === null) return;

                    let options = [];
                    for (let d of data.list || data)
                        options.push({ value: d[prop.vname] });
                    let state = {};
                    state['auto-complete:' + prop.name] = options;
                    this.setState(state);
                });
            }
            if (prop.type === 'agreement') {
                service('/keyvalue/object', { key: prop.agreement }).then(data => {
                    if (data === null) return;

                    let array = toArray(data[prop.agreement]);
                    if (array.length === 0) return;

                    data[prop.agreement] = array[0];
                    this.setState(data);
                });
            }
            if (prop.type === 'transfer' && prop.service) {
                service(prop.service).then(data => {
                    if (data === null) return;

                    this.transfers[prop.name] = data.list || data;
                    this.setState({});
                });
            }

            if (prop.bind && prop.service) {
                if (!this.binds[prop.bind])
                    this.binds[prop.bind] = [];
                this.binds[prop.bind].push({ name: prop.name, service: prop.service });
            }
        }

        for (let name in this.binds)
            this.change({ name: name }, { 'target': { 'value': this.state[name] } });
    }

    data = data => {
        let values = this.form.current.getFieldsValue();
        for (let key in values) {
            values[key] = data[key];
        }
        this.format(values);
        this.form.current.setFieldsValue(values);
        this.setState(data);
    }

    format = (values) => {
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let value = values[prop.name];
            if (prop.labels)
                values[prop.name] = '' + toInt(value, 0);
            else if (prop.type === 'money')
                values[prop.name] = toDecimal(value, 2, prop.empty);
            else if (prop.type === 'decimal')
                values[prop.name] = toDecimal(value, prop.size, prop.empty);
            else if (prop.type === 'percent')
                values[prop.name] = toPercent(value);
            else if (prop.type === 'switch')
                values[prop.name] = value === 1 || value === '1';
            else if (prop.type === 'array' || prop.type === 'read-only:array') {
                let array = toArray(value);
                for (let i = 0; i < array.length; i++)
                    for (let k in array[i])
                        values[prop.name + ':' + k + ':' + i] = '' + array[i][k];
            } else if (value) {
                if (prop.type === 'date')
                    values[prop.name] = moment(value, 'YYYY-MM-DD');
                else if (prop.type === 'datetime')
                    values[prop.name] = moment(value, 'YYYY-MM-DD HH:mm:ss');
                else if (prop.type === 'time-range') {
                    let array = value.split(',');
                    values[prop.name] = [moment(array[0], 'HH:mm:ss'), moment(array[1], 'HH:mm:ss')];
                }
                else if (prop.type === 'transfer')
                    values[prop.name] = value.split(',');
            }
            if (prop.multiple)
                values[prop.name] = values[prop.name] ? values[prop.name].split(',') : [];
        }
    }

    value = (name, value) => {
        if (value === null) {
            let values = this.form.current ? this.form.current.getFieldsValue() : {};

            return values[name] || this.values[name];
        }

        this.values[name] = value;
    }

    button = mt => {
        let values = this.form.current.getFieldsValue();
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let value = values[prop.name];
            if (prop.type === 'switch') {
                values[prop.name] = value ? 1 : 0;

                continue;
            }

            if (!value && value !== '') {
                delete values[prop.name];

                continue;
            };

            if (prop.labels)
                values[prop.name] = toInt(value);
            else if (prop.type === 'date')
                values[prop.name] = value.format("YYYY-MM-DD");
            else if (prop.type === 'datetime')
                values[prop.name] = value.format("YYYY-MM-DD HH:mm:ss");
            else if (prop.type === 'time-range')
                values[prop.name] = value[0].format("HH:mm:ss") + ',' + value[1].format("HH:mm:ss");
            else if (prop.type === 'money')
                values[prop.name] = fromDecimal(value, 2);
            else if (prop.type === 'decimal')
                values[prop.name] = fromDecimal(value, prop.size);
            else if (prop.type === 'percent')
                values[prop.name] = fromPercent(value);
            else if (prop.multiple || prop.type === 'transfer')
                values[prop.name] = values[prop.name].join(',');
        }
        if (this.props.data)
            for (let key in this.props.data)
                if (!(key in values) && this.props.data[key])
                    values[key] = this.props.data[key];
        this.submit(mt, { ...values, ...this.values }).then(data => {
            if (data === null) return;

            if (mt.reload)
                this.props.body.load(this.props.uri, this.props.parameter);
            else if (mt.success) {
                if (this.props.grid)
                    this.props.grid.closeDialog();
                this.props.body.load(this.props.body.uri(this.props.uri, mt.success), this.props.parameter);
            }
            else if (mt.data)
                this.data(data);
            else
                message.success('操作已完成！');
        });
    }

    cancel = mt => {
        if (this.props.grid)
            this.props.grid.closeDialog();
        else
            this.props.body.load(this.props.body.uri(this.props.uri, mt.success), this.props.parameter);
    }

    render = () => {
        this.itemIndex = 0;
        let items = [];
        if (this.props.meta.info)
            items.push(<div key={'info:' + this.props.meta.info} className="console-info" dangerouslySetInnerHTML={{ __html: this.state[this.props.meta.info] }} />);
        for (let prop of meta.props(this.props.props, this.props.meta.props))
            this.item(items, prop, '');

        return (
            <Form ref={this.form} {...layout} initialValues={this.state}>
                {items}
                <Form.Item className="console-form-toolbar" label="toolbar">{this.toolbar()}</Form.Item>
            </Form>
        );
    }

    item = (items, prop, key) => {
        if (prop.when && !eval(prop.when))
            return;

        if (prop.type === 'array' || prop.type === 'read-only:array') {
            let is = [];
            let array = toArray(this.state[prop.name]);
            for (let i = 0; i < array.length; i++) {
                if (array[i] === null)
                    continue;

                is.push(<div key={prop.name + ':divider:' + i} className="console-form-children-divider" />);
                for (let child of prop.children) {
                    let c = JSON.parse(JSON.stringify(child));
                    c.name = prop.name + ':' + c.name + ':' + i;
                    if (prop.type === 'read-only:array' || (prop.editable === 'last' && i < array.length - 1)) {
                        if (c.type) {
                            if (!c.type.startsWith('read-only'))
                                c.type = 'read-only:' + c.type;
                        } else
                            c.type = 'read-only';
                    }
                    this.item(is, c, prop.name + ':');
                }
                if (!prop.fix) {
                    is.push(
                        <div key={prop.name + ':toolbar:' + i} className="console-form-children-toolbar">
                            {i > 0 ? <div onClick={this.move.bind(this, prop, array.length, i, 0)}><VerticalAlignTopOutlined /></div> : null}
                            {i > 0 ? <div onClick={this.move.bind(this, prop, array.length, i, i - 1)}><ArrowUpOutlined /></div> : null}
                            {i < array.length - 1 ? <div onClick={this.move.bind(this, prop, array.length, i, i + 1)}><ArrowDownOutlined /></div> : null}
                            {i < array.length - 1 ? <div onClick={this.move.bind(this, prop, array.length, i, array.length - 1)}><VerticalAlignBottomOutlined /></div> : null}
                            <div onClick={this.remove.bind(this, prop, i)}><DeleteOutlined /></div>
                        </div>
                    );
                }
            }
            items.push(
                <div key={prop.name} className="console-form-children">
                    <div className="console-form-children-title">{prop.label}</div>
                    {is}
                    {prop.fix ? null : <div className="console-form-children-divider" />}
                    {prop.fix ? null : <div className="console-form-children-plus"><Button onClick={this.plus.bind(this, prop)}><PlusOutlined /></Button></div>}
                </div>
            );
            this.itemIndex++;

            return;
        }

        let item = {
            key: key + prop.name,
            className: 'console-form-item console-form-item-' + (this.itemIndex++ % 2 === 0 ? 'odd' : 'even'),
            label: prop.label
        };
        if (prop.type && prop.type.startsWith('read-only')) {
            items.push(<Form.Item {...item}>{this.readonly(prop)}</Form.Item>);
        } else if (prop.type === 'image') {
            items.push(<Form.Item {...item}><Image name={prop.name} upload={prop.upload} size={prop.size || 1} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
        } else if (prop.type === 'image-viewer') {
            items.push(<Form.Item {...item}><img src={this.state[prop.name] || ''} alt="" /></Form.Item>);
        } else if (prop.type === 'file') {
            items.push(<Form.Item {...item}><File name={prop.name} upload={prop.upload} size={prop.size || 1} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
        } else if (prop.type === 'folder') {
            items.push(<Form.Item {...item}><Folder name={prop.name} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
        } else if (prop.type === 'dselect') {
            items.push(<Form.Item {...item}><DSelect body={this.props.body} uri={this.props.uri} {...prop} value={this.state[prop.name]} data={this.props.data} form={this} /></Form.Item>);
        } else if (prop.type === 'refresh') {
            items.push(<Form.Item {...item}>{this.state[prop.name] || ''} {prop.service ? <Button icon={<SyncOutlined alt={prop.label} />} onClick={this.refresh.bind(this, prop)} /> : null}</Form.Item>);
        } else if (prop.type === 'editor') {
            items.push(<Form.Item {...item}><Editor name={prop.name} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
        } else if (prop.type === 'html') {
            items.push(<Form.Item {...item}><div dangerouslySetInnerHTML={{ __html: this.state[prop.name] || '' }} /></Form.Item>);
        } else if (prop.type === 'agreement') {
            let value = this.state[prop.agreement] || { uri: '', name: '' };
            if (value) {
                let label = value.name;
                let index = label.lastIndexOf('.');
                if (index > -1) label = label.substring(0, index);
                item.className += ' console-form-agreement';
                item.label = 'agreement';
                items.push(<Form.Item {...item}><a href={value.uri + '?filename=' + value.name} target="_blank" rel="noopener noreferrer">{label}</a></Form.Item>);
            }
        } else if (prop.type === 'category') {
            let list = prop.category;
            if (!list && this.props.parameter && this.props.parameter.key)
                list = this.props.parameter.key;
            items.push(<Form.Item {...item}><Category list={list} pointTo={prop.pointTo} name={prop.name} value={this.state[prop.name]} form={this} /></Form.Item>);
        } else if (prop.type === 'user') {
            items.push(<Form.Item {...item}><User data={this.state[prop.name]} /></Form.Item>);
        } else {
            if (prop.type === 'switch')
                item.valuePropName = 'checked';
            items.push(<Form.Item {...item} name={prop.name}>{this.input(prop)}</Form.Item>);
        }
    }

    plus = (prop) => {
        let obj = {};
        for (let child of prop.children)
            obj[child.name] = '';
        let data = toArray(this.state[prop.name]);
        data.push(obj);
        let state = {};
        state[prop.name] = data;
        this.setState(state);
    }

    move = (prop, length, from, to) => {
        let state = {};
        for (let child of prop.children) {
            if (from < to) {
                for (let i = 0; i < from; i++)
                    this.moveCopy(prop, child, state, i, i);
                for (let i = from; i < to; i++)
                    this.moveCopy(prop, child, state, i + 1, i);
                this.moveCopy(prop, child, state, from, to);
                for (let i = to + 1; i < length; i++)
                    this.moveCopy(prop, child, state, i, i);
            }
            else {
                for (let i = 0; i < to; i++)
                    this.moveCopy(prop, child, state, i, i);
                this.moveCopy(prop, child, state, from, to);
                for (let i = to; i < from; i++)
                    this.moveCopy(prop, child, state, i, i + 1);
                for (let i = from + 1; i < length; i++)
                    this.moveCopy(prop, child, state, i, i);
            }
        }
        this.setState(state, () => this.form.current.setFieldsValue(state));
    }

    moveCopy = (prop, child, state, source, target) => {
        let value = this.state[prop.name + ':' + child.name + ':' + source];
        if (value || value === 0)
            state[prop.name + ':' + child.name + ':' + target] = value;
    }

    remove = (prop, index) => {
        let array = toArray(this.state[prop.name]);
        array[index] = null;
        let state = {};
        state[prop.name] = array;
        this.setState(state);
    }

    readonly = prop => {
        let value = this.state[prop.name];
        if (prop.type === 'read-only:money')
            return toDecimal(value, 2, prop.empty);

        if (prop.type === 'read-only:decimal')
            return toDecimal(value, prop.size, prop.empty);

        if (prop.type === 'read-only:percent')
            return toPercent(value);

        if (prop.type === 'read-only:image')
            return <Image name={prop.name} upload={prop.upload} size={prop.size || 1} readonly={true} value={this.state[prop.name] || ''} form={this} />;

        if (prop.type === 'read-only:file')
            return <File name={prop.name} upload={prop.upload} readonly={true} value={this.state[prop.name] || ''} form={this} />;

        if (prop.labels)
            return prop.multiple ? this.multiple(prop.labels, value) : (prop.labels[value] || '');

        if (prop.values)
            return prop.multiple ? this.multiple(prop.values, value) : (prop.values[value] || '');

        if (value === 0)
            return 0;

        if (!value)
            return '';

        if (typeof value !== 'string' || value.indexOf('\n') === -1)
            return value;

        let values = [];
        let key = 0;
        for (let v of value.split('\n'))
            values.push(<div key={'readonly:' + prop.name + ':' + (key++)}>{v}</div>);

        return values;
    }

    multiple = (values, value) => {
        if (!value) return '';

        let labels = '';
        for (let v of value) {
            let label = values[v];
            if (label)
                labels += ',' + label;
        }

        return labels.length > 0 ? labels.substring(1) : '';
    }

    input = prop => {
        if (prop.labels) {
            let options = [];
            for (let index in prop.labels)
                options.push({ label: prop.labels[index], value: '' + toInt(index, 0) });

            if (prop.multiple)
                return options.length < 5 ? <Checkbox.Group options={options} /> : <Select options={options} mode="multiple" allowClear />;

            return options.length < 5 ? <Radio.Group options={options} onChange={this.change.bind(this, prop)} /> : <Select options={options} />;
        }

        if (prop.values) {
            let options = [];
            if (prop.values instanceof Array) {
                for (let value of prop.values) {
                    options.push(value);
                }
            } else if (prop.values instanceof Object) {
                let keys = Object.keys(prop.values);
                for (let index in keys) {
                    let key = keys[index];
                    options.push({ label: prop.values[key] || key, value: key });
                }
            } else if (typeof (prop.values) === 'string') {
                for (let value of prop.values.split(','))
                    options.push({ label: value, value: value });
            }

            if (prop.multiple)
                return options.length < 5 ? <Checkbox.Group options={options} /> : <Select options={options} mode="multiple" allowClear />;

            return options.length < 5 ? <Radio.Group options={options} /> : <Select options={options} />;
        }

        if (prop.type === 'date') return <DatePicker locale={locale} />;

        if (prop.type === 'datetime') return <DatePicker locale={locale} showTime={prop.time || true} />;

        if (prop.type === 'time-range') return <TimePicker.RangePicker />;

        if (prop.type === 'switch') return <Switch disabled={!prop.service && !prop.permit} onChange={this.switch.bind(this, prop)} />;

        if (prop.type === 'text-area') return <Input.TextArea autoSize={{ minRows: 2 }} />;

        if (prop.type === 'password') return <Input.Password />;

        if (prop.type === 'auto-complete') return <AutoComplete options={this.state['auto-complete:' + prop.name]} />;

        if (prop.type === 'transfer') {
            return <Transfer dataSource={this.transfers[prop.name]} showSearch={true} oneWay={true} render={item => item.title} targetKeys={this.state[prop.name]} onChange={keys => {
                let state = {};
                state[prop.name] = keys;
                this.setState(state);
            }} titles={['可选', '选中']} listStyle={{ direction: 'right', width: 8192 }} />;
        }

        return <Input />
    }

    switch = (prop, check) => {
        this.value(prop.name, check ? 1 : 0);
    }

    refresh = (prop) => {
        service(this.props.body.uri(this.props.uri, prop.service), { id: this.state.id }).then(data => {
            if (data === null) return;

            this.props.body.load(this.props.uri, this.props.parameter, data);
        });
    }

    change = (prop, e) => {
        let binds = this.binds[prop.name];
        if (!binds) return;

        for (let bind of binds) {
            let param = {};
            param[prop.name] = e.target.value;
            service(this.props.body.uri(this.props.uri, bind.service), param).then(data => {
                if (data === null) return;

                let state = {};
                state[bind.name] = data;
                this.setState(state);
            });
        }
    }

    toolbar = () => {
        let buttons = [];
        if (!this.props.meta.toolbar || this.props.meta.toolbar.length <= 0)
            return buttons;

        let one = this.props.meta.toolbar.length === 1;
        if (this.props.meta.toolbar) {
            for (let toolbar of this.props.meta.toolbar) {
                if (toolbar.hidden) continue;

                let button = {
                    key: toolbar.label
                };
                if (one) {
                    button.type = 'primary';
                    button.htmlType = 'submit';
                }
                buttons.push(<Button {...button} onClick={this.button.bind(this, toolbar)}>{toolbar.label}</Button>);
                if (toolbar.success && one)
                    buttons.push(<Button key="cancel" type="dashed" onClick={this.cancel.bind(this, toolbar)}>取消</Button>);
            }
        }

        return buttons;
    }
}

class Normal extends Base {
    load = () => service(this.props.body.uri(this.props.uri, this.props.meta.service), this.props.parameter);

    submit = (mt, values) => {
        let data = { ...this.state, ...values }
        for (let prop of this.props.meta.props) {
            if (prop.type === 'array') {
                let labels = {};
                for (let child of prop.children)
                    if (child.labels)
                        labels[child.name] = true;

                let array = [];
                let state = toArray(this.state[prop.name]);
                for (let key in data) {
                    if (data[key] === undefined) {
                        delete data[key];

                        continue;
                    }

                    if (key.indexOf(prop.name) > -1) {
                        let ks = key.split(':');
                        let index = toInt(ks[2]);
                        if (state[index] === null) {
                            delete data[key];

                            continue;
                        }

                        let obj = array[index] || {};
                        obj[ks[1]] = labels[ks[1]] ? toInt(data[key]) : data[key];
                        delete data[key];
                        array[index] = obj;
                    }
                }
                let arr = [];
                for (let obj of array)
                    if (obj)
                        arr.push(obj);
                data[prop.name] = JSON.stringify(arr);
            }
        }

        return service(this.props.body.uri(this.props.uri, mt.service || mt.type), { ...data, ...this.props.parameter });
    }
}

export default Normal;

export { Base };