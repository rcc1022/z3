import { timeout, now } from "./common";
import { findById } from "./find";
import { mergeText } from "./text";
import { getRange, setCursor } from "./selection";

const bold = (items) => {
    style(items, text => {
        format(text, 'bold');
    });
};

const italic = (items) => {
    style(items, text => {
        format(text, 'italic');
    });
};

const underline = (items) => {
    style(items, text => {
        format(text, 'underline');
    });
};

const through = (items) => {
    style(items, text => {
        format(text, 'through');
    });
};

const format = (text, style) => {
    if (text.style) {
        let map = {};
        for (let name of text.style.trim().split(' '))
            if (name != '')
                map[name] = true;
        map[style] = !map[style];
        let names = [];
        for (let name in map)
            if (map[name])
                names.push(name);
        text.style = names.sort().join(' ');
    } else {
        text.style = style;
    }
};

const style = (items, set) => {
    let range = getRange(true);
    let item = findById(items, range.id);
    if (range.startIndex === range.endIndex) {
        let text = item.text[range.startIndex];
        let texts = [{ ...text }, { ...text }, { ...text }];
        texts[0].text = text.text.substring(0, range.startOffset);
        texts[1].text = text.text.substring(range.startOffset, range.endOffset);
        texts[2].text = text.text.substring(range.endOffset);
        set(texts[1]);
        item.text.splice(range.startIndex, 1, ...texts);
    } else {
        let texts = [{ ...item.text[range.startIndex] }, { ...item.text[range.startIndex] }];
        texts[0].text = item.text[range.startIndex].text.substring(0, range.startOffset);
        texts[1].text = item.text[range.startIndex].text.substring(range.startOffset);
        set(texts[1]);
        for (let i = range.startIndex + 1; i < range.endIndex; i++) {
            set(item.text[i]);
            texts.push(item.text[i]);
        }
        texts.push({ ...item.text[range.endIndex] });
        texts.push({ ...item.text[range.endIndex] });
        texts[texts.length - 2].text = item.text[range.endIndex].text.substring(0, range.endOffset);
        set(texts[texts.length - 2]);
        texts[texts.length - 1].text = item.text[range.endIndex].text.substring(range.endOffset);
        item.text.splice(range.startIndex, range.endIndex - range.startIndex + 1, ...texts);
    }
    mergeText(item);
    item.time = now();
    setTimeout(() => setCursor(items, range.id), timeout.min);
};

export {
    bold,
    italic,
    underline,
    through,
};