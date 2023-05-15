import { message } from "./locale";

const prefix = {
    '# ': 'h1',
    '## ': 'h2',
    '### ': 'h3',
    '#&nbsp;': 'h1',
    '##&nbsp;': 'h2',
    '###&nbsp;': 'h3',
};

const style = {
    '*': 'italic',
    '_': 'italic',
    '**': 'bold',
    '__': 'bold'
};

const markdown = (item) => {
    if (item.text.length === 0)
        return false;

    for (let key in prefix) {
        let indexOf = item.text[0].text.indexOf(key);
        if (indexOf === 0) {
            item.text[0].text = item.text[0].text.substring(key.length);
            item.tag = prefix[key];
            item.placeholder = message('placeholder.' + item.tag, 'placeholder');

            return true;
        }
    }

    let inner = document.getElementById(item.id).innerText;
    for (let key in style) {
        let start = inner.indexOf(key);
        if (start === -1)
            continue;

        let end = inner.indexOf(key, start + key.length);
        if (end === -1)
            continue;

        console.log(start + ',' + end);
    }

    return false;
};

export {
    markdown
}