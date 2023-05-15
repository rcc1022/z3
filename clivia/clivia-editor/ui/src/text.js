const formatText = (text, node, depth) => {
    if (node.nodeName === '#text') {
        if (node.data != '') {
            if (depth === 1) {
                text.push({ text: node.data, style: '' });
                node.parentNode.removeChild(node);
            } else {
                text.push({ text: node.data, style: node.parentNode.className || '' });
            }
        }
    } else {
        for (let child of node.childNodes)
            formatText(text, child, depth + 1);
    }
};

const mergeText = (item) => {
    let texts = [];
    for (let i = 0; i < item.text.length; i++) {
        if (item.text[i].text === '')
            continue;

        if (texts.length > 0 && texts[texts.length - 1].style === item.text[i].style)
            texts[texts.length - 1].text += item.text[i].text;
        else
            texts.push(item.text[i]);
    }
    item.text = texts;
};

const clearBr = (items) => {
    for (let item of items) {
        let node = document.getElementById(item.id);
        for (let child of node.childNodes) {
            if (child.nodeName === 'BR') {
                node.removeChild(child);
            }
        }
    }
};

export {
    formatText,
    mergeText,
    clearBr,
};