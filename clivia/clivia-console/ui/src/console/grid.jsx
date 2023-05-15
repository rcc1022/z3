import React from 'react';
import {
    Form,
    Row,
    Col,
    Radio,
    Select,
    DatePicker,
    Input,
    InputNumber,
    Button,
    Table,
    Pagination,
    Divider,
    Menu,
    Dropdown,
    Modal,
    Switch
} from 'antd';
import { MinusOutlined, PaperClipOutlined, DashOutlined } from '@ant-design/icons';
import 'moment/locale/zh-cn';
import locale from 'antd/es/date-picker/locale/zh_CN';
import { service, url } from '../http';
import meta from './meta';
import { toDecimal, toPercent } from './numeric';
import { toArray } from '../json';
import Dialog from './dialog';
import Icon from './icon';
import Category from './category';
import User from './user';
import './grid.css';

const { RangePicker } = DatePicker;

class Grid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            list: [],
            dselect: {},
            pagination: false,
            preview: null,
            more: null,
            delete: null,
            edit: null,
            dialog: null,
        };

        let columns = meta.props(props.props, props.meta.props);
        if (props.meta.search && props.meta.search.length > 0) {
            this.form = React.createRef();
            this.searchProps = meta.props(columns, props.meta.search);
            this.search = true;
            for (let prop of this.searchProps)
                if (prop.type === 'dselect')
                    this.loadDselect(props, prop);
        } else if (props.meta.toolbar && props.meta.toolbar.length > 0) {
            this.toolbar = [];
            for (let toolbar of props.meta.toolbar) {
                if (!toolbar.hidden) {
                    this.toolbar.push(this.button(toolbar));
                }
            }
        }

        this.columns = [];
        for (let prop of columns) {
            let column = { key: prop.name, title: prop.label };
            if (prop.labels)
                column.render = model => this.format(prop, model, prop.multiple ? this.multiple(prop.labels, this.value(model, prop.name)) : prop.labels[this.value(model, prop.name)]);
            else if (prop.values) {
                column.render = model => {
                    let value = this.value(model, prop.name);
                    if (typeof (prop.values) !== 'string') {
                        if (prop.multiple) {
                            value = '';
                            for (let v of value.split(',')) {
                                if (prop.values instanceof Object)
                                    value += ',' + prop.values[value];
                                else if (prop.values instanceof Array)
                                    for (let vs of prop.values)
                                        if (vs.value === v)
                                            value += ',' + vs.label;
                            }
                            if (value.length > 0) value = value.substring(1);
                        } else {
                            if (prop.values instanceof Array) {
                                for (let v of prop.values) {
                                    if (v.value === value) {
                                        value = v.label;

                                        break;
                                    }
                                }
                            } else if (prop.values instanceof Object)
                                value = prop.values[value];
                        }
                    }
                    return this.format(prop, model, value);
                };
            } else if (prop.type === 'money' || prop.type === 'read-only:money')
                column.render = model => this.format(prop, model, toDecimal(this.value(model, prop.name), 2, prop.empty));
            else if (prop.type === 'decimal' || prop.type === 'read-only:decimal')
                column.render = model => this.format(prop, model, toDecimal(this.value(model, prop.name), prop.size, prop.empty));
            else if (prop.type === 'percent' || prop.type === 'read-only:percent')
                column.render = model => this.format(prop, model, toPercent(this.value(model, prop.name)));
            else if (prop.type === 'image' || prop.type === 'read-only:image') {
                column.render = model => {
                    let value = this.value(model, prop.name);
                    if (value === '') return this.format(prop, model, '');

                    if (value.indexOf(',') === -1) return this.format(prop, model, <img src={url(value)} alt=""
                        onClick={this.preview} />);

                    let imgs = [];
                    for (let img of value.split(','))
                        imgs.push(<img key={prop.name + imgs.length} src={url(img)} alt="" onClick={this.preview} />);

                    return this.format(prop, model, imgs);
                }
            } else if (prop.type === 'file' || prop.type === 'read-only:file') {
                column.render = model => {
                    let files = [];
                    for (let file of toArray(this.value(model, prop.name))) {
                        files.push(<div key={'file-' + files.length} className="file">
                            <PaperClipOutlined />
                            <a href={url(file.uri)} target="_blank" rel="noopener noreferrer">{file.name}</a>
                        </div>);
                    }

                    return this.format(prop, model, files);
                }
            } else if (prop.type === 'switch') {
                column.render = model => {
                    let s = { defaultChecked: this.value(model, prop.name) === 1 };
                    if (prop.service)
                        s.onChange = this.switch.bind(this, prop, model);
                    else
                        s.disabled = true;

                    return this.format(prop, model, <Switch {...s} />);
                }
            } else if (prop.type === 'dselect') {
                this.loadDselect(props, prop);
                column.render = model => this.format(prop, model, this.dselect(prop, model));
            } else if (prop.type === 'password')
                column.render = model => this.format(prop, model, '***');
            else if (prop.type === 'editor' || prop.type === 'html')
                column.render = model => this.format(prop, model, <div
                    dangerouslySetInnerHTML={{ __html: this.value(model, prop.name) }} />);
            else if (prop.type === 'user')
                column.render = model => this.format(prop, model, <User data={this.value(model, prop.name)} />);
            else if (prop.type === 'multi-line') {
                column.render = model => {
                    let lines = [];
                    for (let line of meta.props(props.props, prop.lines)) {
                        let value = this.value(model, line.name);
                        if (!value && value !== 0)
                            continue;

                        if (line.labels)
                            value = line.labels[value];
                        else if (line.values && !(line.values instanceof Array))
                            value = line.values[value];
                        else if (line.type === 'money' || line.type === 'read-only:money')
                            value = toDecimal(value, 2, prop.empty);
                        else if (line.type === 'decimal' || line.type === 'read-only:decimal')
                            value = toDecimal(value, prop.size, prop.empty);
                        else if (line.type === 'percent' || line.type === 'read-only:percent')
                            value = toPercent(value);
                        lines.push(<div
                            key={'line:' + model.id + ':' + line.name}>{line.label} : {this.format(line, model, value)}</div>);
                    }

                    return lines;
                }
            } else if (prop.type === 'edit') {
                column.render = model => this.format(prop, model, <div style={{ color: '#1890ff' }} onClick={this.operate.bind(this, prop, model)}>{prop.edit}</div>);
            } else
                column.render = model => this.format(prop, model, this.value(model, prop.name));
            if (prop.sort)
                column.sorter = true;
            this.columns.push(column);
        }
        if (props.meta.ops && props.meta.ops.length > 0) {
            this.columns.push({
                title: '',
                render: model => {
                    let mops = [];
                    for (let op of props.meta.ops)
                        // eslint-disable-next-line
                        if (!op.when || eval(op.when))
                            mops.push(op);

                    let ops = [];
                    let opsize = [99, 99];
                    // let opsize = [2];
                    // if (props.meta.opsize) {
                    //     if (props.meta.opsize.length > 0)
                    //         opsize[0] = props.meta.opsize[0];
                    //     if (props.meta.opsize.length > 1)
                    //         opsize[1] = props.meta.opsize[1];
                    // }
                    // if (opsize.length < 2)
                    //     opsize[1] = 2;
                    if (mops.length <= opsize[0]) {
                        for (let i in mops) {
                            if (i > 0) ops.push(i % opsize[1] === 0 ? <br key={'br-' + i} /> : (mops[i].icon ? null : <Divider key={'divider-' + i} type="vertical" />));
                            ops.push(this.action(mops[i], model));
                        }
                    } else {
                        opsize[0]--;
                        for (let i = 0; i < opsize[0]; i++) {
                            ops.push(this.action(mops[i], model));
                            ops.push(i > 0 && (i % opsize[1] === 0) ? <br key={'br-' + i} /> :
                                <Divider key={'divider-' + i} type="vertical" />);
                        }
                        let items = [];
                        for (let i = opsize[0]; i < mops.length; i++)
                            items.push(<Menu.Item key={mops[i].label}>{this.action(mops[i], model)}</Menu.Item>);
                        ops.push(<Dropdown key="more" overlay={<Menu>{items}</Menu>}><span
                            className="console-grid-op">更多</span></Dropdown>);
                    }

                    return <div className="console-grid-ops">{ops}</div>;
                }
            });
        }

        this.load(null);
    }

    loadDselect = (props, prop) => {
        service(props.body.uri(props.uri, prop.service), prop.parameter).then(data => {
            if (data === null) return;

            let options = {};
            let vname = prop.vname || 'id';
            let lname = prop.lname || 'name';
            for (let d of data.list || data) {
                let option = d;
                if (lname.indexOf('+') > -1)
                    // eslint-disable-next-line
                    eval('option.label=' + lname);
                else
                    option.label = d[lname];
                options[d[vname]] = option.label;
            }
            let dselect = this.state.dselect;
            dselect[prop.name] = options;
            this.setState({
                dselect: dselect
            });
        });
    }

    value = (model, name) => {
        let m = model;
        for (let n of name.split('.')) {
            if (n in m)
                m = m[n];
            else
                return '';
        }

        if (m === 0)
            return 0;

        return m || '';
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

    dselect = (prop, model) => {
        let value = this.value(model, prop.name);
        if (!this.state.dselect || !this.state.dselect[prop.name]) return 'value';

        return this.state.dselect[prop.name][value];
    }

    format = (prop, model, element) => {
        let maxlen = prop.maxlen || 64;
        if (element && typeof (element) === 'string' && element.length && element.length > maxlen)
            element = <div>{element.substring(0, maxlen)}<span className="console-grid-more"
                onClick={this.more.bind(this, {
                    label: prop.label,
                    value: element
                })}><DashOutlined /></span></div>;

        if (prop.style) {
            for (let style of prop.style) {
                try {
                    // eslint-disable-next-line
                    if (eval(style.condition))
                        return <div style={style.value}>{element}</div>;
                } catch (e) {
                }
            }
        }

        return element;
    }

    button = op => <Button key={op.label} onClick={this.operate.bind(this, op, null)}>{op.label}</Button>;

    action = (op, model) => <span key={op.label} className="console-grid-op" style={op.style || {}}
        onClick={this.operate.bind(this, op, model)}>{op.icon ? <Icon name={op.icon} label={op.label} /> : <div className="console-grid-op-label">{op.label}</div>}</span>;

    operate = (op, model) => {
        if (op.type === 'create' || op.type === 'ucreate') {
            let data = model && model.id ? { parent: model.id } : {};
            if (op.parameter) data = { ...data, ...op.parameter };
            if (op.search) data = { ...data, ...this.searches() };
            this.setState({ dialog: { title: op.label } });
            this.props.body.setMode(1);
            this.props.body.load(this.props.body.uri(this.props.uri, op.service || op.type), this.props.parameter, data, this);

            return;
        }

        if (op.type === 'delete') {
            this.setState({
                delete: {
                    op: op,
                    model: model,
                }
            });

            return;
        }

        if (op.type === 'edit') {
            this.setState({
                edit: {
                    op: op,
                    model: model,
                }
            });

            return;
        }

        if (op.reload) {
            this.reload(op, model, {});

            return;
        }

        if (op.search) {
            this.reload(op, model, this.searches());

            return;
        }

        if (op.type === 'ids') {
            let ids = '';
            for (let m of this.state.list)
                ids += ',' + m.id;
            if (ids.length > 0)
                this.reload(op, model, { ids: ids.substring(1) });

            return;
        }

        if (op.type === 'upload') {
            let input = document.createElement("input");
            input.type = 'file';
            input.style.display = 'none';
            input.onchange = e => {
                if (!e.target.files || e.target.files.length === 0) return;

                let reader = new FileReader();
                let file = e.target.files[0];
                reader.onload = () => {
                    if (!reader.result || typeof reader.result !== 'string') {
                        return;
                    }

                    service('/photon/ctrl/upload', {
                        name: op.upload,
                        fileName: file.name,
                        contentType: file.type,
                        base64: reader.result.substring(reader.result.indexOf(',') + 1)
                    }).then(data => {
                        document.body.removeChild(input);
                        if (data === null) return;

                        this.load();
                    });
                };
                reader.readAsDataURL(file);
            };
            document.body.appendChild(input);
            input.click();

            return;
        }

        if (op.type === 'download') {
            let href = this.props.body.uri(this.props.uri, op.service || op.type);
            if (model && model.id) {
                href += '?id=' + model.id;
            } else {
                let values = this.searches();
                for (let key in values) {
                    let value = values[key];
                    if (!value)
                        continue;

                    href += href.indexOf('?') === -1 ? '?' : '&';
                    href += key + '=' + encodeURIComponent(value);
                }
            }
            window.open('about:blank').location.href = href;

            return;
        }

        if (model && model.children)
            delete model.children;

        if (op.type === 'page')
            this.props.body.page(op.page, this.props.parameter, model);
        else if (op.type === 'back')
            this.props.body.back();
        else {
            this.setState({ dialog: { title: op.label } });
            this.props.body.setMode(1);
            this.props.body.load(this.props.body.uri(this.props.uri, op.service || op.type), this.props.parameter, { ...this.searches(), ...model }, this);
        }
    }

    preview = e => this.setState({ preview: e.currentTarget.src });

    cancelPreview = () => this.setState({ preview: null });

    more = lv => this.setState({ more: lv });

    cancelMore = () => this.setState({ more: null });

    cancelDelete = () => this.setState({ delete: null });

    cancelEdit = () => this.setState({ edit: null });

    closeDialog = () => {
        this.setState({ dialog: null });
        this.props.body.setMode(0);
    }

    okDelete = () => {
        this.reload(this.state.delete.op, this.state.delete.model, {});
        this.setState({ delete: null });
    }

    okEdit = () => {
        this.reload(this.state.edit.op, this.state.edit.model, {});
        this.setState({ edit: null });
    }

    switch = (op, model, check) => {
        let parameter = {};
        parameter[op.name] = check ? 1 : 0;
        this.reload(op, model, parameter);
    }

    reload = (op, model, parameter) => {
        let param = { ...model, ...parameter }
        if (op.parameter)
            param = { ...param, ...op.parameter };
        if (this.props.parameter)
            param = { ...param, ...this.props.parameter };
        service(this.props.body.uri(this.props.uri, op.service || op.type), param).then(data => {
            if (data === null) return;

            this.load();
        });
    }

    load = (pagination, search, sorter) => {
        if (this.timeout)
            window.clearTimeout(this.timeout);

        let parameter = this.searches();
        if (pagination) {
            parameter.pageSize = pagination.pageSize;
            parameter.pageNum = pagination.current;
        }
        if (!parameter.pageSize && this.state.pagination && this.state.pagination.pageSize)
            parameter.pageSize = this.state.pagination.pageSize;
        if (!parameter.pageNum && this.state.pagination && this.state.pagination.pageNum)
            parameter.pageNum = this.state.pagination.pageNum;

        if (this.props.parameter)
            parameter = { ...parameter, ...this.props.parameter };
        if (this.props.data)
            parameter = { ...parameter, ...this.props.data };
        if (search)
            parameter['console-grid-search'] = true;
        if (sorter && sorter.order)
            parameter['console-grid-sort'] = sorter.columnKey + ' ' + sorter.order;
        service(this.props.uri, parameter).then(data => {
            if (this.props.meta.interval)
                this.timeout = window.setTimeout(this.load.bind(this, pagination, search, sorter), this.props.meta.interval * 1000);
            if (data === null) return;

            if (data instanceof Array) {
                this.setState({
                    list: data
                });
            } else {
                let state = {
                    list: data.list
                };
                if (data.count <= data.size)
                    state.pagination = false;
                else {
                    state.pagination = {
                        total: data.count,
                        pageSize: data.size,
                        current: data.number,
                        showSizeChanger: false,
                        showTotal: (total, range) => {
                            return range[0] + '-' + range[1] + ' 共' + total + '条';
                        },
                        onChange: this.onPage,
                    };
                }
                if (this.props.meta.info)
                    state[this.props.meta.info] = data[this.props.meta.info];
                this.setState(state);
            }
        });
    }

    onPage = (page, pageSize) => {
        this.load({
            current: page,
            pageSize,
        });
    }

    onPageSize = (e) => {
        let pageSize = e.target.value;
        if (pageSize === this.state.pagination.pageSize)
            return;

        this.load({
            pageSize,
            current: this.state.pagination.current,
        });
    }

    searches = () => {
        if (!this.form || !this.form.current || !this.searchProps || this.searchProps.length === 0) return {};

        let values = this.form.current.getFieldsValue();
        for (let column of this.searchProps) {
            if (column.type === 'range') {
                values[column.name] = (values[column.name + "Start"] || '') + ',' + (values[column.name + "End"] || '');
                delete values[column.name + "Start"];
                delete values[column.name + "End"];

                continue;
            }

            let value = values[column.name];
            if (!value) continue;

            if (column.type === 'date') {
                values[column.name] = value.format('YYYY-MM-DD');
            } else if (column.type === 'date-range') {
                if (value.length === 0)
                    values[column.name] = '';
                else {
                    let format = column.time ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD';
                    values[column.name] = value[0].format(format) + ',' + value[1].format(format);
                }
            }
        }

        return values;
    }

    render = () => {
        let elements = [];
        if (this.props.meta.info)
            elements.push(<div key={'info:' + this.props.meta.info} className="console-info"
                dangerouslySetInnerHTML={{ __html: this.state[this.props.meta.info] }} />);
        if (this.search)
            elements.push(<Search key="search" props={this.searchProps} toolbar={this.props.meta.toolbar}
                grid={this} form={this.form} dselect={this.state.dselect} />);
        else if (this.toolbar)
            elements.push(<div key="toolbar" className="console-grid-toolbar">{this.toolbar}</div>);
        elements.push(<Table key="table" columns={this.columns} dataSource={this.state.list} rowKey="id" pagination={false}
            className="console-grid" scroll={this.scroll()} onChange={this.load} />);
        if (this.state.pagination) {
            elements.push(
                <div key="pagination" className="console-grid-pagination">
                    <Pagination {...this.state.pagination} />
                    <div className="console-grid-pagination-size">
                        <InputNumber controls={false} defaultValue={this.state.pagination.pageSize} onBlur={this.onPageSize} onPressEnter={this.onPageSize} />
                    </div>
                    <div>条/页</div>
                </div>
            );
        }
        elements.push(
            <Modal key="preview" open={this.state.preview != null} footer={null} onCancel={this.cancelPreview}>
                <img style={{ width: '100%' }} src={this.state.preview} alt="" />
            </Modal>
        );
        elements.push(
            <Modal key="more" open={this.state.more} title={this.state.more ? this.state.more.label : ''}
                onCancel={this.cancelMore} footer={null}>
                {this.moreContent()}
            </Modal>
        );
        elements.push(
            <Modal key="delete" open={this.state.delete} title={this.state.delete ? this.state.delete.op.label : ''}
                onCancel={this.cancelDelete} onOk={this.okDelete}>
                {this.deleteContent()}
            </Modal>
        );
        elements.push(
            <Modal key="edit" open={this.state.edit} title={this.state.edit ? this.state.edit.op.edit : ''}
                onCancel={this.cancelEdit} onOk={this.okEdit}>
                {this.editContent()}
            </Modal>
        );
        elements.push(
            <Modal key="dialog" open={this.state.dialog} width="80vw" title={this.state.dialog ? this.state.dialog.title : ''} footer={null} maskClosable={false}
                onCancel={this.closeDialog}>
                <Dialog />
            </Modal >
        );

        return elements;
    }

    scroll = () => {
        if (!this.props.meta.freeze)
            return { x: true };

        let height = document.querySelector('.console-body').clientHeight;
        let form = document.querySelector('.console-grid-search-form');
        if (form)
            height -= form.clientHeight;
        let rows = Math.floor(height / 55 - 2.5);
        let len = this.state.list ? this.state.list.length : 0;

        if (len < rows)
            return { x: true };

        return { x: true, y: 55 * rows };
    }

    moreContent = () => {
        if (!this.state.more || !this.state.more.value) return '';

        if (this.state.more.value.indexOf('\n') === -1) return this.state.more.value;

        let values = [];
        let key = 0;
        for (let v of this.state.more.value.split('\n'))
            values.push(<div key={'more:' + (key++)}>{v}</div>);

        return values;
    }

    deleteContent = () => {
        if (!this.state.delete) return null;

        let items = [];
        for (let prop of this.props.props) {
            let value = this.state.delete.model[prop.name];
            if (prop.labels) value = prop.labels[value];
            else if (prop.type === 'dselect')
                value = this.dselect(prop, this.state.delete.model);
            items.push(<Row key={prop.name}>
                <Col span={6}>{prop.label}</Col>
                <Col span={18}>{value}</Col>
            </Row>);
        }

        return items;
    }

    editContent = () => {
        if (!this.state.edit) return null;

        return <Input value={this.state.edit.model[this.state.edit.op.name]} onChange={this.editContentChange} />;
    }

    editContentChange = (e) => {
        let edit = this.state.edit;
        edit.model[edit.op.name] = e.target.value;
        this.setState({ edit });
    }

    componentWillUnmount = () => {
        if (this.timeout)
            window.clearTimeout(this.timeout);
    }
}

class Search extends React.Component {
    render = () => {
        let cols = [];
        let initialValues = {};
        for (let column of this.props.props) {
            if (column.labels || column.values || column.type === 'dselect') initialValues[column.name] = column.value || '';

            let item = { label: column.label };
            if (column.type !== 'range')
                item.name = column.name;
            cols.push(
                <Col span={6} key={column.name}>
                    <Form.Item {...item}>{this.input(column)}</Form.Item>
                </Col>
            );
        }
        let toolbar = [];
        toolbar.push(<Button key="search" type="primary" htmlType="submit">搜索</Button>);
        if (this.props.toolbar && this.props.toolbar.length > 0) {
            for (let button of this.props.toolbar) {
                if (!button.hidden) {
                    toolbar.push(this.props.grid.button(button));
                }
            }
        }
        cols.push(<span key="toolbar" className="console-grid-search-toolbar">{toolbar}</span>);

        return (
            <Form ref={this.props.form} className="console-grid-search-form" initialValues={initialValues}
                onFinish={this.finish}>
                <Row gutter={24}>{cols}</Row>
            </Form>
        );
    }

    input = column => {
        if (column.labels) {
            let options = [{ label: '全部', value: '' }];
            for (let index in column.labels)
                options.push({ label: column.labels[index], value: index });

            return options.length <= 3 ? <Radio.Group options={options} /> : <Select options={options} />;
        }

        if (column.values) {
            let options = [{ label: '全部', value: '' }];
            if (column.values instanceof Array) {
                for (let value of column.values)
                    options.push(value);
            } else if (column.values instanceof Object) {
                let keys = Object.keys(column.values);
                for (let index in keys) {
                    let key = keys[index];
                    options.push({ label: column.values[key] || key, value: key });
                }
            } else if (typeof (column.values) === 'string') {
                for (let value of column.values.split(','))
                    options.push({ label: value, value: value });
            }

            return options.length <= 3 ? <Radio.Group options={options} /> : <Select options={options} />;
        }

        if (column.type === 'dselect') {
            let options = [{ label: '全部', value: '' }];
            let values = this.props.dselect[column.name];
            if (values) {
                for (let key in values)
                    options.push({ label: values[key], value: key });
            }

            return <Select showSearch={true} options={options} filterOption={this.filter} />;
        }

        if (column.type === 'date')
            return <DatePicker locale={locale} />;

        if (column.type === 'date-range')
            return <RangePicker locale={locale} showTime={column.time} />;

        if (column.type === 'range') {
            return (
                <Input.Group className="console-grid-search-range" compact>
                    <Form.Item name={column.name + 'Start'} noStyle><Input /></Form.Item>
                    <span className="range-minus"><MinusOutlined /></span>
                    <Form.Item name={column.name + 'End'} noStyle><Input /></Form.Item>
                </Input.Group>
            );
        }

        if (column.type === 'switch') {
            return (
                <Radio.Group initValue={''}>
                    <Radio value={''}>全部</Radio>
                    <Radio value={'0'}>否</Radio>
                    <Radio value={'1'}>是</Radio>
                </Radio.Group>
            );
        }

        if (column.type === 'category')
            return <Category list={column.category} pointTo={column.pointTo} name={column.name} form={this} />;

        return <Input />
    }

    filter = (input, option) => {
        if (!option) return false;

        if (input === '') return true;

        return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 || option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }

    value = (name, value) => {
    }

    finish = () => {
        this.props.grid.load(null, true);
    }
}

export default Grid;