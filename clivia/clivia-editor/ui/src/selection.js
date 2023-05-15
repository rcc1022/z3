import { findId, findIndex, findById, findChild } from "./find";

const selection = {
    id: '',
    startIndex: 0,
    startOffset: 0,
    endIndex: 0,
    endOffset: 0,
};

const getRange = (cache) => {
    if (cache)
        return selection;

    let range = getSelection().getRangeAt(0);
    selection.id = findId(range.startContainer);
    selection.startIndex = findIndex(range.startContainer);
    selection.startOffset = range.startOffset;
    selection.endIndex = findIndex(range.endContainer);
    selection.endOffset = range.endOffset;

    return selection;
};

const setRange = (startContainer, start, endContainer, end) => {
    let range = document.createRange();
    range.setStart(startContainer, start);
    range.setEnd(endContainer, end);
    getSelection().removeAllRanges();
    getSelection().addRange(range);
    getRange();
};

const setCursor = (items, id, collapse) => {
    let source = findById(items, selection.id);
    let target = findById(items, id);
    if (!source || !target)
        return;

    let start = getCursor(source.text, selection.startIndex, selection.startOffset, target.text);
    let node = document.getElementById(id);
    let startContainer = findChild(node, start[0] + 1, 0);
    if (collapse) {
        setRange(startContainer, start[1], startContainer, start[1]);
    } else {
        let end = getCursor(source.text, selection.endIndex, selection.endOffset, target.text);
        setRange(startContainer, start[1], findChild(node, end[0] + 1, 0), end[1]);
    }
};

const getCursor = (source, index, offset, target) => {
    let cursor = 0;
    for (let i = 0; i < source.length; i++) {
        if (i < index && source[i].text)
            cursor += source[i].text.length;
        else if (i === index) {
            cursor += offset;

            break;
        } else
            break;
    }

    for (let i = 0; i < target.length; i++) {
        let length = target[i].text.length;
        if (cursor <= length)
            return [i, cursor];

        cursor -= length;
    }

    if (cursor > 0 && target.length > 0)
        return [target.length - 1, target[target.length - 1].text.length];

    return [0, 0];
};

export {
    getRange,
    setRange,
    setCursor,
};