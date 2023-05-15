import React, { useState } from "react";
import { Upload, Modal } from "antd";
import { PlusOutlined, LoadingOutlined } from "@ant-design/icons";
import { url, upload } from "../http";
import "./image.css";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";


const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};

const grid = 8;

const getListStyle = (isDraggingOver) => ({
  display: "flex",
  padding: grid,
  overflow: "auto",
});

class Image extends React.Component {
  state = {
    uri: null,
    changed: false,
    loading: false,
    preview: null,
    remove: 0,
  };
  onDragEnd = this.onDragEnd.bind(this);

  upload = (uploader) => {
    this.setState({ loading: true });
    upload(this.props.upload, uploader.file).then((data) => {
      if (data === null) {
        this.setState({ loading: false });

        return;
      }

      let uri = this.state.changed ? this.state.uri : this.props.value;
      let path = data.thumbnail || data.path;
      uri = uri ? uri + "," + path : path;
      this.setState(
        {
          uri: uri,
          changed: true,
          loading: false,
        },
        () => {
          this.props.form.value(this.props.name, this.state.uri);
        }
      );
    });
  };

  preview = (file) => {
    this.setState({ preview: file.url });
  };

  cancel = () => {
    this.setState({ preview: null });
  };

  remove = (file) => {
    if (this.props.readonly) return;

    let uri = this.state.changed ? this.state.uri : this.props.value;
    if (!uri) return;

    let uris = uri.split(",");
    let u = "";
    for (let i in uris) {
      if (i === file.uid) continue;

      if (u.length > 0) u += ",";
      u += uris[i];
    }
    this.setState(
      {
        uri: u,
        changed: true,
      },
      () => this.props.form.value(this.props.name, this.state.uri)
    );
  };

  onDragEnd(result) {
    if (!result.destination) {
      return;
    }

    const items = reorder(
      this.state.items,
      result.source.index,
      result.destination.index
    );

    this.setState({
      items,
    });
  }
  render() {
    let uri = this.state.changed ? this.state.uri : this.props.value;
    if (!this.state.changed && this.props.value) {
      this.props.form.value(this.props.name, this.props.value);
    }
    let list = [];
    if (uri) {
      let uris = uri.split(",");
      for (let i in uris) {
        list.push({
          uid: "" + i,
          name: uris[i],
          url: url(uris[i]),
          status: "done",
        });
      }
    }

    let props = {
      listType: "picture-card",
      fileList: list,
      className: "image-uploader",
      customRequest: this.upload,
      onPreview: this.preview,
    };
    if (!this.props.readonly) props.onRemove = this.remove;

    return (
      <div className="clearfix">
        <DragDropContext onDragEnd={this.onDragEnd}>
          <Droppable droppableId="droppable" direction="horizontal">
            {(provided, snapshot) => (
              <div
                ref={provided.innerRef}
                style={getListStyle(snapshot.isDraggingOver)}
                {...provided.droppableProps}
              >
                {props.fileList.map((item, index) => (
                  <Draggable key={item.id} draggableId={item.uid} index={index}>
                    {(provided, snapshot) => (
                      <div
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                      >
                        <div className="clearfix">
                          <Upload
                            customRequest={this.upload}
                            onPreview={this.preview}
                            fileList={[item]}
                            listType="picture-card"
                            className="image-uploader"
                          ></Upload>

                          <Modal
                            visible={this.state.preview != null}
                            footer={null}
                            onCancel={this.cancel}
                          >
                            <img
                              alt="preview"
                              style={{ width: "100%" }}
                              src={this.state.preview}
                            />
                          </Modal>
                        </div>
                      </div>
                    )}
                  </Draggable>
                ))}

                <Upload
                  customRequest={this.upload}
                  onPreview={this.preview}
                  listType="picture-card"
                  className="image-uploader"
                >
                  <PlusOutlined />
                </Upload>
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    );
  }
}

export default Image;
