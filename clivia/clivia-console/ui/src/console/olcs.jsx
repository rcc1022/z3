import React from "react";
import {
  Col,
  Row,
  Collapse,
  List,
  Avatar,
  Badge,
  Popover,
  Input,
  Button,
  Space,
} from "antd";
import {
  CloseCircleOutlined,
  SmileOutlined,
  PictureOutlined,
  SendOutlined,
  CodeOutlined,
} from "@ant-design/icons";
import { LazyLoadImage } from "react-lazy-load-image-component";
import EmojiPicker from "emoji-picker-react";
import { service, upload, url } from "../http";
import "./olcs.css";

class Olcs extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      all: [],
      allSearch: [],
      nick: "",
      newer: [],
      unread: {},
      user: {},
      messages: [],
      time: "",
      read: "",
      faqs: [],
      searchFaqs: [],
    };
    this.timeout = true;
    this.timer();
  }

  componentWillUnmount = () => {
    this.timeout = false;
  };

  timer = () => {
    service("/olcs/member/query", { size: this.state.all.length }).then(
      (data) => {
        if (this.timeout) setTimeout(this.timer.bind(this), 1000);
        if (data === null) return;

        if (!data.all) data.all = this.state.all;
        if (!data.newer) data.newer = this.state.newer;
        if (this.state.nick === "") {
          this.setState({
            all: data.all,
            newer: data.newer,
            allSearch: data.all,
            unread: data.unread,
          });

          return;
        }

        let all = [];
        for (let a of data.all) {
          if (
            (a.uid && a.uid.indexOf(this.state.nick) > -1) ||
            (a.nick && a.nick.indexOf(this.state.nick) > -1) ||
            (a.memo && a.memo.indexOf(this.state.nick) > -1)
          ) {
            all.push(a);
          }
        }
        this.setState({
          all: data.all,
          newer: data.newer,
          allSearch: all,
          unread: data.unread,
        });
      }
    );
    if (this.state.user.id) {
      service("/olcs/query", {
        user: this.state.user.id,
        time: this.state.time,
      }).then((data) => {
        if (data === null) return;

        let state = {};
        let has = data.list && data.list.length > 0;
        if (has) {
          state.messages = this.state.messages;
          for (let message of data.list) {
            if (message.genre === "delete") {
              for (let i = 0; i < state.messages.length; i++) {
                if (state.messages[i].id === message.id) {
                  state.messages.splice(i, 1);

                  break;
                }
              }
            } else {
              state.messages.push(message);
            }
            state.time = message.time;
          }
        }
        state.read = data.read || "";
        this.setState(state);
        if (has) {
          setTimeout(() => {
            let msgs = document.querySelector(".olcs-messages");
            if (msgs) msgs.scrollTop = msgs.scrollHeight;
          }, 250);
        }
      });
    }
    if (this.state.faqs.length === 0) {
      service("/olcs/faq/query", { pageSize: 1024 }).then((data) => {
        if (data === null) return;

        this.setState({ faqs: data.list, searchFaqs: data.list });
      });
    }
  };

  searchNick = () => {
    let input = document.querySelector("#search-nick");
    if (input) this.setState({ nick: input.value.trim() });
  };

  chat = (item) => {
    this.setState({ user: item, messages: [], time: "" });
  };

  avatar = (user) =>
    user.avatar ? (
      <LazyLoadImage className="lazy-avatar" src={user.avatar} />
    ) : (
      <Avatar>{(user.nick || "C").substring(0, 1)}</Avatar>
    );

  name = (user) => {
    let unread = this.state.unread[user.id];
    let name = user.memo ? (
      <span>
        [{user.uid}]{user.nick}
        <span className="olcs-user-memo">({user.memo})</span>
      </span>
    ) : (
      <span>
        [{user.uid}]{user.nick}
      </span>
    );
    if (!unread || unread === 0) return name;

    return (
      <Badge count={unread} offset={[10]}>
        {name}
      </Badge>
    );
  };

  message = () => {
    if (!this.state.user.id) return <div></div>;

    let messages = [];
    for (let message of this.state.messages) {
      let content = null;
      if (message.genre === "image") {
        content = (
          <a href={url(message.content)} rel="noreferrer" target="_blank">
            <img src={url(message.content)} alt="" />
          </a>
        );
      } else if (message.genre === "text") {
        if (!message.content) message.content = "";
        if (message.content.indexOf("\n") === -1) {
          content = message.content;
        } else {
          content = [];
          for (let line of message.content.split("\n"))
            content.push(<div key={content.length}>{line}</div>);
        }
      } else if (message.genre === "faq") {
        content = "常见问题";
      }
      if (message.replier) {
        messages.push(
          <div className="olcs-message-replier" key={message.id}>
            <div className="olcs-message-content">
              {message.time < this.state.read ? (
                <div className="olcs-message-read">已读</div>
              ) : (
                <div className="olcs-message-unread">未读</div>
              )}
              <div className={"olcs-message-" + message.genre}>
                <div className="olcs-message-delete">
                  <CloseCircleOutlined
                    onClick={this.delete.bind(this, message.id)}
                  />
                </div>
                {content}
              </div>
              <div className="olcs-message-time">{message.time}</div>
            </div>
            {this.avatar(message.replier)}
          </div>
        );
      } else {
        messages.push(
          <div className="olcs-message-user" key={message.id}>
            {this.avatar(this.state.user)}
            <div className="olcs-message-content">
              <div className={"olcs-message-" + message.genre}>{content}</div>
              <div className="olcs-message-time">{message.time}</div>
            </div>
          </div>
        );
      }
    }

    let image = (
      <Space direction="vertical">
        <Button block onClick={this.picture}>
          上传
        </Button>
        <Input
          addonBefore="图片链接"
          id="piclink"
          addonAfter={<SendOutlined onClick={this.piclink} />}
        />
      </Space>
    );

    let faq = (
      <Space direction="vertical">
        <Input.Search onSearch={this.searchFaq} id="faq-search" />
        <List
          className="olcs-faqs"
          itemLayout="horizontal"
          dataSource={this.state.searchFaqs}
          renderItem={(item) => (
            <List.Item onClick={this.sendFaq.bind(this, item)}>
              <List.Item.Meta title={item.subject} description={item.content} />
            </List.Item>
          )}
        />
      </Space>
    );

    return (
      <Row>
        <Col span={24}>
          <div className="olcs-content">
            <div className="olcs-avatar-nick">
              {this.avatar(this.state.user)}
              <span className="nick">{this.state.user.nick || ""}</span>
            </div>
            <div className="olcs-messages">{messages}</div>
            <div className="olcs-tools">
              <Popover
                content={<EmojiPicker onEmojiClick={this.emoji} />}
                trigger="click"
              >
                <span className="olcs-tool">
                  <SmileOutlined />
                </span>
              </Popover>
              <Popover content={image} trigger="click">
                <span className="olcs-tool">
                  <PictureOutlined />
                </span>
              </Popover>
              <Popover content={faq} trigger="click">
                <span className="olcs-tool">
                  <CodeOutlined />
                </span>
              </Popover>
            </div>
            <div className="olcs-input">
              <textarea onKeyUp={this.keyup} onPaste={this.paste}></textarea>
            </div>
            <div className="olcs-send">
              <Button type="primary" onClick={this.send}>
                发送
              </Button>
            </div>
          </div>
        </Col>
      </Row>
    );
  };

  delete = (id) => {
    service("/olcs/delete", { id });
  };

  emoji = (e) => {
    let textarea = document.querySelector(".olcs-input textarea");
    let value = textarea.value;
    if (value === "") {
      textarea.value = e.emoji;

      return;
    }

    textarea.value =
      value.substring(0, textarea.selectionStart) +
      e.emoji +
      value.substring(textarea.selectionEnd);
  };

  picture = () => {
    let input = document.createElement("input");
    input.type = "file";
    input.style.position = "absolute";
    input.style.top = "-9999px";
    input.accept = "image/*";
    document.body.appendChild(input);
    input.onchange = (e) => {
      this.upload(e.target.files[0], (data) => {
        document.body.removeChild(input);
      });
    };
    input.click();
  };

  piclink = () => {
    let input = document.querySelector("#piclink");
    if (!input || input.value.trim() === "") return;

    service("/olcs/reply", {
      user: this.state.user.id,
      genre: "image",
      content: input.value.trim(),
    });
  };

  searchFaq = () => {
    let input = document.querySelector("#faq-search");
    if (!input) return;

    let value = input.value.trim();
    if (value === "") {
      this.setState({ searchFaqs: this.state.faqs });

      return;
    }

    let faqs = [];
    for (let faq of this.state.faqs) {
      if (faq.subject.indexOf(value) > -1 || faq.content.indexOf(value) > -1) {
        faqs.push(faq);
      }
    }
    this.setState({ searchFaqs: faqs });
  };

  sendFaq = (item) => {
    service("/olcs/reply", {
      user: this.state.user.id,
      genre: "text",
      content: item.content,
    });
  };

  clean = () => {
    service("/olcs/clean", { user: this.state.user.id }).then((data) => {
      if (data === null) return;

      this.setState({ messages: [], time: "" });
    });
  };

  keyup = (e) => {
    if (e.code === "Enter") {
      this.send();
    }
  };

  paste = (e) => {
    if (
      !e.clipboardData ||
      !e.clipboardData.items ||
      e.clipboardData.items.length === 0
    )
      return;

    let item = e.clipboardData.items[0];
    if (item.type.indexOf("image/") === -1) return;

    this.upload(e.clipboardData.files[0]);
  };

  upload = (file, success) => {
    upload("clivia.olcs.image", file).then((data) => {
      if (data === null) return;

      service("/olcs/reply", {
        user: this.state.user.id,
        genre: "image",
        content: data.path,
      }).then((d) => {
        if (success) success(d);
        if (d === null) return;

        document.querySelector(".olcs-input textarea").focus();
      });
    });
  };

  send = () => {
    let textarea = document.querySelector(".olcs-input textarea");
    let content = textarea.value.trim();
    if (!content) return;

    let image = false;
    if (content.indexOf("://")) {
      let indexOf = content.lastIndexOf(".");
      if (indexOf > -1) {
        let suffix = content.substring(indexOf + 1).toLowerCase();
        image =
          suffix === "jpg" ||
          suffix === "jpeg" ||
          suffix === "png" ||
          suffix === "gif" ||
          suffix === "bmp" ||
          suffix === "svg";
      }
    }
    service("/olcs/reply", {
      user: this.state.user.id,
      genre: image ? "image" : "text",
      content: content,
    }).then((data) => {
      if (data === null) return;

      textarea.value = "";
      textarea.focus();
    });
  };

  render = () => {
    console.log(window.location.pathname)
    return (
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          height: "100vh",
          width: "100%",
        }}
      >
        <Collapse
          defaultActiveKey={["all"]}
          style={{
            height: "50vh",
            width: "30vh",
            position: "fixed",
            bottom: "50px",
            right: 0,
            overflowY: "auto",
          }}
        >
          <Collapse.Panel key={"all"} header="会员">
            <Input.Search id="search-nick" onSearch={this.searchNick} />
            <List
              itemLayout="horizontal"
              dataSource={this.state.allSearch}
              renderItem={(item) => (
                <List.Item
                  className="olcs-item"
                  onClick={this.chat.bind(this, item)}
                >
                  <List.Item.Meta
                    avatar={this.avatar(item)}
                    title={this.name(item)}
                    description={item.content}
                  />
                </List.Item>
              )}
            />
          </Collapse.Panel>
          <Collapse.Panel key={"newer"} header="新会员">
            <List
              itemLayout="horizontal"
              dataSource={this.state.newer}
              renderItem={(item) => (
                <List.Item
                  className="olcs-item"
                  onClick={this.chat.bind(this, item)}
                >
                  <List.Item.Meta
                    avatar={this.avatar(item)}
                    title={this.name(item)}
                    description={item.content}
                  />
                </List.Item>
              )}
            />
          </Collapse.Panel>
        </Collapse>
        {this.message()}

        {/* <div
          style={{
            position: "absolute",
            right: 0,
            bottom: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            height: "50px",
            width: "30vh",
            zIndex: 999,
            background: "#FFF",
          }}
        >
          <Badge
            count={1}
          ></Badge>
        </div> */}
      </div>
    );
  };
}

export default Olcs;
