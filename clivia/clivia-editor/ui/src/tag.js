

const selectTag = (id, plus, e) => {
    let tag = '';
    for (let node = e.target; true; node = node.parentElement) {
        if (node.className === 'tag') {
            tag = node.dataset.name;

            break;
        }
    }
    if (tag === '')
        return;

    let index = findIndex(id);
    if (plus) {
        let nid = randomId();
        list.value.splice(index + 1, 0, {
            id: nid,
            tag,
            text: [],
            placeholder: message('placeholder.' + tag, 'placeholder')
        });
        setTimeout(() => setCursor(list.value, nid, true), timeout.min);
    } else {
        let item = list.value[index];
        let range = getRange(true);
        let text = item.text[range.startIndex].text;
        item.tag = tag;
        item.text[range.startIndex].text = text.substring(0, range.startOffset - 1) + text.substring(range.startOffset);
        item.placeholder = message('placeholder.' + tag, 'placeholder');
        setTimeout(() => setCursor(list.value, item.id, true), timeout.min);
    }
};

export {
    onTagSelect,
};