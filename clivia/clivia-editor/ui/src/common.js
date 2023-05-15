const timeout = {
    min: 10,
    larger: 100,
};

const timestamp = {
    offset: 0,
    sync: 0,
}

const now = () => new Date().getTime() + timestamp.offset;

export {
    timeout,
    timestamp,
    now,
};