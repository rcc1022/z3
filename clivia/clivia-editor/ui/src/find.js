const findId = node => {
    for (let i = 0; i < 16; i++) {
        if (node.id)
            return node.id;

        node = node.parentNode;
    }

    return '';
};

const findIndex = node => {
    for (let i = 0; i < 16; i++) {
        if (node.dataset)
            return parseInt(node.dataset.index || '0');

        node = node.parentNode;
    }

    return 0;
};

const findById = (items, id) => {
    for (let item of items)
        if (item.id === id)
            return item;

    return null;
};

const findChild = (node, index, grandson) => {
    if (node.childNodes.length <= index)
        return node;

    let child = node.childNodes[index];
    if (grandson > -1 && child.childNodes.length > grandson)
        return child.childNodes[grandson];

    return child;
};

export {
    findId,
    findIndex,
    findById,
    findChild,
};