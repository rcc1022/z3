import React from 'react';
import { EditorState, ContentState, convertToRaw } from 'draft-js';
import { Editor } from 'react-draft-wysiwyg';
import draftToHtml from 'draftjs-to-html';
import htmlToDraft from 'html-to-draftjs';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';
import './editor.css';

class Wysiwyg extends React.Component {
    state = {
        editor: EditorState.createEmpty(),
        changed: false
    };

    change = state => {
        this.setState({
            editor: state,
            changed: true
        });
        this.props.form.value(this.props.name, draftToHtml(convertToRaw(state.getCurrentContent())));
    }

    render = () => {
        let state = this.state.editor;
        if (!this.state.changed && this.props.value) {
            this.props.form.value(this.props.name, this.props.value);
            let content = htmlToDraft(this.props.value);
            if (content)
                state = EditorState.createWithContent(ContentState.createFromBlockArray(content));
        }

        return <Editor editorState={state} localization={{ locale: 'zh', }} onEditorStateChange={this.change} />;
    }
}

export default Wysiwyg;