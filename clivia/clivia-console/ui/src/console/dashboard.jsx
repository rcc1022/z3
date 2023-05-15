import React from 'react';
import { Row, Col, Card, Statistic, Table } from 'antd';
import { Line, Column, Pie } from '@ant-design/charts';
import { service } from '../http';
import meta from './meta';
import './dashboard.css';

class Dashboard extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            columns: []
        };
        this.timeout = null;
        service('/console/dashboard').then(data => {
            if (data === null) return;

            let state = { columns: data };
            for (let i = 0; i < data.length; i++) {
                for (let j = 0; j < data[i].cards.length; j++) {
                    let card = data[i].cards[j];
                    card.key = i + '-' + j;
                    card.load = false;
                    state[card.key] = {};
                    meta.get(card.service).then(mt => {
                        let m = mt[card.service.substring(card.service.lastIndexOf('/') + 1)];
                        if (!m) return;

                        card.meta = m;
                        if (m.type === 'grid') {
                            card.columns = [];
                            for (let prop of meta.props(mt.props, m.props)) {
                                card.columns.push({
                                    title: prop.label,
                                    dataIndex: prop.name,
                                    key: prop.name
                                });
                            }
                        }
                    });
                }
            }
            this.setState(state, this.load);
        });
    }

    render = () => <Row gutter={[8, 8]}>{this.state.columns.map((column, index) => <Col key={index} span={column.span}>{this.cards(index, column.cards)}</Col>)}</Row>;

    cards = (index, cards) => {
        let elements = [];
        for (let i = 0; i < cards.length; i++) {
            if (i > 0)
                elements.push(<div key={'space:' + index + '-' + i} className="console-dashboard-space" />);
            elements.push(<Card key={'card:' + index + '-' + i} title={cards[i].title}>{this.card(cards[i])}</Card>);
        }

        return elements;
    }

    card = card => {
        if (!card.load || !card.meta) return null;

        switch (card.meta.type) {
            case 'statistic':
                return this.statistic(card);
            case 'grid':
                return this.grid(card);
            case 'line':
                return this.line(card);
            case 'column':
                return this.column(card);
            case 'pie':
                return this.pie(card);
            default:
                return null;
        }
    }

    statistic = card => {
        if (card.meta.props.length === 0) return null;

        let span = card.meta.props.length < 3 ? (24 / card.meta.props.length) : 8;

        return <Row gutter={[8, 8]}>{card.meta.props.map(prop => <Col key={prop.name} span={span}><Statistic title={prop.label} value={this.state[card.key][prop.name]} /></Col>)}</Row>;
    }

    grid = card => {
        let data = this.state[card.key] || {};

        return <Table columns={card.columns} dataSource={data.list || data} pagination={false} />;
    }

    line = card => {
        return <Line xField={card.meta.x || 'x'} yField={card.meta.y || 'y'} seriesField={card.meta.series || 'series'} data={this.state[card.key] || {}} />;
    }

    column = card => {
        return <Column xField={card.meta.x || 'x'} yField={card.meta.y || 'y'} seriesField={card.meta.series || 'series'} isGroup={true} data={this.state[card.key] || {}} />;
    }

    pie = card => {
        let config = {
            colorField: card.meta.name || 'name',
            angleField: card.meta.value || 'value',
            radius: 0.8,
            label: {
                type: 'outer',
                content: '{name} {percentage}'
            }
        };
        if (card.annulus)
            config.innerRadius = 0.6;

        return <Pie {...config} data={this.state[card.key] || {}} />;
    }

    load = () => {
        this.timeout = setTimeout(this.load, 10 * 1000);
        for (let column of this.state.columns) {
            for (let card of column.cards) {
                if (card.load && !card.reload)
                    continue;

                service(card.service, card.parameter).then(data => {
                    card.load = true;
                    if (data === null) return;

                    let state = {};
                    state[card.key] = data;
                    this.setState(state);
                });
            }
        }
    }

    componentWillUnmount = () => {
        if (this.timeout !== null)
            clearTimeout(this.timeout);
    }
}

export default Dashboard;