import { bold, italic, underline, through } from './style';

const stop = () => { };

const ctrl = {
    b: bold,
    i: italic,
    u: underline,
    v: stop
};

const ctrlShift = {
    S: through,
    X: through
};

const keymap = (items, e) => {
    if (e.ctrlKey && ctrl[e.key]) {
        ctrl[e.key](items);

        return true;
    }

    if (e.ctrlKey && e.shiftKey && ctrlShift[e.key]) {
        ctrlShift[e.key](items);

        return true;
    }

    return false;
};

export {
    keymap
};