import React from 'react';
import body from './body';
import './dialog.css';

class Dialog extends React.Component {
    constructor(props) {
        super(props);
        this.state = { body: <div /> };
        body.setDialog(this);
    }

    render = () => this.state.body;
}

export default Dialog;