import React from "react";
import { Menu } from "antd";
import { service } from "../http";
import body from "./body";

class LeftMenu extends React.Component {
  constructor() {
    super();

    this.state = {
      items: [],
      item: "",
      open: ["0-0"],
    };
    this.map = {};
    service("/console/menu").then((data) => {
      console.log(data);
      if (data === null || data.length === 0) return;

      let item = data[0].service || data[0].page;

      this.setState(
        {
          items: [
            ...data,
            // {
            //   label: "在线客服",
            //   items: [
            //     {
            //       service: "/olcs/faq/query",
            //       label: "常见问题",
            //     },
            //     {
            //       service: "/olcs/query",
            //       label: "人工回复",
            //     },
            //   ],
            // },
          ],
          item: item,
        },
        () => this.load(item ? data[0] : data[0].items[0])
      );
    });
  }

  click = (e) => {
    const path = this.map[e.key];
    if (path.service === "/olcs/query") {
      window.open("http://localhost:3000/olcs/query")
    }
    this.load(this.map[e.key]);
  };

  load = (item) => {
    body.setMode(0);
    if (item.service) body.load(item.service, item.parameter, item.data);
    else if (item.page) body.page(item.page, item.parameter, item.data);
  };

  open = (keys) => {
    this.setState({
      open: keys.length === 0 ? [] : [keys[keys.length - 1]],
    });
  };

  render = () => {
    if (this.state.items.length === 0) return null;

    return (
      <Menu
        onClick={this.click}
        mode="inline"
        theme="dark"
        openKeys={this.state.open}
        defaultSelectedKeys={[this.state.item ? "0-0" : "0-0-0"]}
        onOpenChange={this.open}
        items={this.menu(this.state.items, "0")}
      />
    );
  };

  menu = (items, parent) => {
    let menus = [];
    if (items.length === 0) return menus;

    for (let i = 0; i < items.length; i++) {
      let key = parent + "-" + i;
      let item = items[i];
      if (item.service || !item.items) {
        this.map[key] = item;
        menus.push({ key, label: item.label });
      } else {
        menus.push({
          key,
          label: item.label,
          children: this.menu(item.items, key),
        });
      }
    }

    return menus;
  };
}

export default LeftMenu;
