import React from "react";
import { Spin, Dropdown, Menu, Avatar } from "antd";
import {
  LockOutlined,
  UserOutlined,
  LogoutOutlined,
  WhatsAppOutlined,
} from "@ant-design/icons";
import { post, url, loader } from "../http";
import "./sign.css";
import img from "../assets/客服.png";

class Sign extends React.Component {
  constructor(props) {
    super(props);

    this.state = { loading: false };
    loader(this);
  }

  sign = () => {
    this.props.body.load("/user/sign", {}, null);
  };

  password = () => {
    this.props.body.load("/user/password", {}, {});
  };

  signOut = () => {
    post("/user/sign-out").then((json) => window.location.reload());
  };

  render = () => {
    let nick = this.props.user.nick || "Clivia UI";
    let menu = (
      <Menu>
        <Menu.Item onClick={this.sign}>
          <UserOutlined />
          <span>个人信息</span>
        </Menu.Item>
        <Menu.Item onClick={this.password}>
          <LockOutlined />
          <span>修改密码</span>
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item onClick={this.signOut}>
          <LogoutOutlined />
          <span>退出</span>
        </Menu.Item>
      </Menu>
    );

    return (
      <div className="console-sign">
        <div className="console-sign-loading">
          <Spin spinning={this.state.loading} />
        </div>
        <img
          src={img}
          style={{ cursor: "pointer" }}
          onClick={() => {
            // window.open(window.location.href + "?id=true");
            window.location.href =
              "http://124.221.75.140:39461/index/index/home?visiter_id=&visiter_name=&avatar=&business_id=1&groupid=1&special=2";
          }}
        />
        <Dropdown overlay={menu}>
          <div className="console-sign-avatar">
            {this.props.user.avatar ? (
              <Avatar src={url(this.props.user.avatar)} />
            ) : (
              <Avatar>{nick.substring(0, 1)}</Avatar>
            )}
            <span>{nick}</span>
          </div>
        </Dropdown>
      </div>
    );
  };
}

export default Sign;
