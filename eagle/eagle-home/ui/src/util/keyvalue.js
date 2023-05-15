import { service } from './http';

const keyvalue = (key, success) => service('/keyvalue/object', { key }, success);

export {
    keyvalue
}