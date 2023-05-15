import { service } from '../http';
import { Base } from './form';

class Setting extends Base {
    load = () => service('/keyvalue/object', { key: this.key() });

    submit = (mt, values) => {
        let array = [];
        for (let name in values) {
            array.push({
                key: name,
                value: values[name] || ''
            });
        }

        return service('/keyvalue/saves', { kvs: JSON.stringify(array) });
    }

    key = () => this.props.uri.substring(1).replace(/\//g, '.') + '.';
}

export default Setting;