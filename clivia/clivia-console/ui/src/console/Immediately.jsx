import React, { useState } from "react";
import { service } from "../http";
import { useEffect } from "react";
import { Table } from "antd";

export default function Immediately(props) {
  const [active, setActive] = useState(0);
  const [isUse, setIsUse] = useState("");
  const [open, setOpen] = useState("");
  const [data, setData] = useState([]);
  const [list, setList] = useState([]);

  const getList = () => {
    service(props.uri).then((data) => {
      setData(data);
      console.log('请求数据');
    //   handleTab(data[active], active);
    });
  };

  const handleTab = (item, i) => {
    setIsUse(item.issue);
    setOpen(item.open);
    setActive(i);
    setList(JSON.parse(item.list));
  };

  useEffect(() => {
    getList();

    const intervalId = setInterval(() => {
      getList();
    }, 8000);

    return () => clearInterval(intervalId);
  }, []);

  return (
    <div>
      <div
        className="nav"
        style={{ display: "flex", flexWrap: "wrap", gap: "10px" }}
      >
        {data?.map((item, index) => (
          <div
            key={item.gameName}
            onClick={() => handleTab(item, index)}
            style={{
              background:
                active === index ? "rgb(255,255,255" : "rgb(219,227,236)",
              padding: "10px",
              cursor: "pointer",
              color: active === index ? "rgb(77,98,123)" : "rgb(122,135,142)",
              border: "1px solid rgb(110, 109, 109, .1)",
            }}
          >
            {item.gameName}
          </div>
        ))}
      </div>

      <div
        className="top"
        style={{
          display: "flex",
          gap: "10px",
          alignItems: "center",
          fontSize: "18px",
        }}
      >
        <div
          style={{
            color: "rgb(0,150,136)",
          }}
        >
          {isUse}
        </div>

        <div>
          距离封盘：<span style={{ color: "red" }}>{open}</span>
        </div>
      </div>

      <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
        {list?.map((item, index) => (
          <Table
            rowKey={index}
            columns={[
              { title: item.type, dataIndex: "item" },
              {
                title: item.ratio,
                dataIndex: "rate",
                render: (item) => <span style={{ color: "red" }}>{item}%</span>,
              },
              {
                title: item.val,
                dataIndex: "amount",
                render: (item) => <span>{item}元</span>,
              },
            ]}
            dataSource={item.userBetsList || []}
            pagination={false}
            bordered
          />
        ))}
      </div>
    </div>
  );
}
