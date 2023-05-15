import React from 'react';
import './index.css';

class Page extends React.Component {
    render = () => {
        switch (this.props.page) {
            default:
                return <div className="no-page">no page</div>;
        }
    }
}

export default Page;