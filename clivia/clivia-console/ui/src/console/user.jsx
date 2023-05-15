import React from 'react';
import { Avatar } from 'antd';
import { url } from '../http';
import './user.css'

class User extends React.Component {
    render = () => {
        if (!this.props.data)
            return null;

        let uids = this.uids();

        return (
            <div className="user">
                {this.avatar()}
                <div className="user-info">
                    {uids.map(uid => <div key={uid} className="user-uid">{uid}</div>)}
                    {this.line(uids, 'nick')}
                    {this.memo()}
                </div>
            </div>
        );
    }

    avatar = () => {
        if (!this.props.data.avatar)
            return null;

        return <div className="user-avatar"><Avatar src={url(this.props.data.avatar)} /></div>;
    }

    uids = () => {
        if (!this.props.data.auth || this.props.data.auth.length <= 0)
            return [];

        let uids = [];
        for (let i = 0; i < this.props.data.auth.length; i++)
            if (!this.props.data.auth[i].type)
                uids.push(this.props.data.auth[i].uid);

        return uids;
    }

    line = (uids, name) => {
        let value = this.props.data[name];
        if (!value)
            return null;

        for (let uid of uids)
            if (uid === value)
                return null;

        return <div className={'user-' + name}>{value}</div>;
    }

    memo = () => {
        let value = this.props.data['memo'];
        if (!value)
            return null;

        return <div className={'user-memo'}>({value})</div>;
    }
}

export default User;