import React from 'react';
import { Badge, message } from 'antd';
import { service } from '../http';
import './shortcut.css';

class Shortcut extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            list: []
        };

        this.ringtone = null;
    }

    componentDidMount = () => {
        this.load();
        this.timer();
    }

    load = () => {
        service('/console/shortcut').then(data => {
            if (data === null)
                return;

            for (let i = 0; i < data.length; i++) {
                data[i].data = {};
                this.shortcut(i, data[i]);
            }
            this.setState({ list: data });
        });
    }

    timer = () => {
        setInterval(() => {
            let time = Math.floor(new Date().getTime() / 1000);
            for (let i = 0; i < this.state.list.length; i++) {
                let sc = this.state.list[i];
                if (!sc.interval || sc.interval <= 0 || time % sc.interval > 0)
                    continue;

                this.shortcut(i, sc);
            }
        }, 1000);
    }

    shortcut = (index, sc) => {
        if (!sc.service)
            return;

        service(sc.service, sc.parameter || {}).then(data => {
            if (data === null)
                return;

            let list = this.state.list;
            let sc = list[index];
            sc.data = data;
            this.setState({ list });
            if (!sc.ringtone || !sc.badge || !sc.data[sc.badge] || sc.data.mute)
                return;

            if (this.ringtone === null) {
                message.warn('提示铃声未被激活，请点击下鼠标');

                return;
            }

            this.ringtone.src = sc.ringtone;
            this.ringtone.muted = '';
            this.ringtone.play();
        });
    }

    ringtoneInit = () => {
        if (this.ringtone == null) {
            this.ringtone = document.getElementById('ringtone');
            this.ringtone.play();
        }
    }

    render = () => {
        let children = [];
        for (let i = 0; i < this.state.list.length; i++) {
            let sc = this.state.list[i];
            let item = <div key={i} className="console-shortcut-item" onClick={this.body.bind(this, sc)}>{sc.icon ? <img src={sc.icon} alt="" /> : sc.data[sc.name]}</div>;
            if (sc.badge && sc.data[sc.badge]) {
                children.push(<Badge key={i} count={sc.data[sc.badge]}>{item}</Badge>);
            } else {
                children.push(item);
            }
        }

        return (
            <div className="console-shortcut">
                {children}
                <div className="console-shortcut-ringtone"><audio id="ringtone" controls="controls" muted="muted" autoPlay="autoplay"></audio></div>
            </div>
        );
    }

    body = (sc) => {
        if (sc.load) {
            this.props.body.load(sc.load, sc.param || {});
        }
    }
}

export default Shortcut;