import React from 'react';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import { service, loader } from '../http';
import Console from '../console';
import SignIn from '../sign-in';

class Main extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      logo: '',
      signUpEnable: false,
      user: {}
    };
    loader(null);
  }

  componentDidMount = () => {
    service('/keyvalue/object', { key: 'setting.global.' }).then(data => {
      if (data === null) return;

      document.title = data['setting.global.console.title'] || 'Clivia Console';
      this.setState({
        logo: data['setting.global.console.logo']
      });
    });
    service('/keyvalue/object', { key: 'setting.user.' }).then(data => {
      if (data === null) return;

      this.setState({
        signUpEnable: data['setting.user.sign-up.enable'] === '1'
      });
    });
    service('/user/sign').then(data => this.setState({ user: data }));
  }

  render = () => (
    <ConfigProvider locale={zhCN}>
      {this.state.user.id && this.state.user.id.length === 36 ? <Console logo={this.state.logo} user={this.state.user} /> : <SignIn logo={this.state.logo} signUpEnable={this.state.signUpEnable} />}
    </ConfigProvider>
  );
}

export default Main;