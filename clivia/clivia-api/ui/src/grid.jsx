import React from 'react';
import { Table, Empty } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';

class Grid extends React.Component {
    render = () => (
        <Table title={() => <div className="api-title">{this.props.header ? '头信息' : '参数'}</div>} columns={[{
            title: this.props.header ? '头名称' : '参数名',
            dataIndex: 'name',
            key: 'name',
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
        }, {
            title: '必须',
            dataIndex: 'require',
            key: 'require',
            render: require => require ? <CheckOutlined /> : <CloseOutlined />
        }, {
            title: '说明',
            dataIndex: 'description',
            key: 'description',
        }]} dataSource={this.props.data} rowKey="name" pagination={false}
            locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={this.props.header ? '无需头信息' : '无需参数'} /> }} />
    );
}

export default Grid;