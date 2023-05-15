import React from "react";
import { service } from "../http";
import meta from "./meta";
import Dashboard from "./dashboard";
import Grid from "./grid";
import Form from "./form";
import Setting from "./setting";
import Crosier from "./crosier";
import Olcs from "./olcs";
import Page from "../page";

class Body {
  history = [];

  setIndex = (index) => (this.index = index);

  setDialog = (dialog) => (this.dialog = dialog);

  setMode = (mode) => (this.mode = mode);

  uri = (uri, service) => {
    if (!service) return uri;

    if (service.startsWith("/")) return service;

    return uri.substring(0, uri.lastIndexOf("/") + 1) + service;
  };

  load = (uri, parameter, data, grid) => {
    if (!uri.startsWith("/"))
      uri = uri.substring(uri.indexOf(".")).replace(/\./g, "/");
    if (uri === "/user/sign-out") {
      service(uri).then((data) => window.location.reload());

      return;
    }

    if (uri === "/console/dashboard") {
      this.setState(<Dashboard />);

      return;
    }

    if (uri === "/olcs/query") {
      this.setState(<Olcs />);

      return;
    }

    this.history.push({ uri, parameter, data });
    if (this.history.length > 64) this.history.splice(0, 32);

    meta.get(uri).then((mt) => {
      if (mt === null) return;
      console.log(uri.substring(uri.lastIndexOf("/") + 1));
      console.log(mt);
      let m = mt[uri.substring(uri.lastIndexOf("/") + 1)];
      if (!m) return;
      if (m.type === "grid") {
        this.setState(
          <Grid
            props={mt.props}
            meta={m}
            uri={uri}
            parameter={parameter}
            data={data}
            body={this}
          />
        );
      } else if (m.type === "form") {
        this.setState(
          <Form
            props={mt.props}
            meta={m}
            uri={uri}
            parameter={parameter}
            data={data}
            body={this}
            grid={grid}
          />
        );
      } else if (m.type === "crosier") {
        this.setState(<Crosier meta={m} />);
      } else if (mt.key === "setting") {
        this.setState(
          <Setting
            props={mt.props}
            meta={m}
            uri={uri}
            parameter={parameter}
            data={data}
            body={this}
          />
        );
      } else if (m.type === "page") {
        this.page(m.page, parameter, data);
      }
    });
  };

  page = (page, parameter, data) =>
    this.setState(
      <Page page={page} parameter={parameter} data={data} body={this} />
    );

  back = () => {
    this.history.pop();
    let h = this.history.pop();
    this.load(h.uri, h.parameter, h.data);
  };

  setState = (state) => {
    if (this.mode === 1)
      this.dialog.setState({ body: <div /> }, () =>
        this.dialog.setState({ body: state })
      );
    else
      this.index.setState({ body: <div /> }, () =>
        this.index.setState({ body: state })
      );
  };
}

const body = new Body();

export default body;
