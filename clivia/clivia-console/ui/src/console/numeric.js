const toPercent = value => {
    if (!value) return '0.00 %';

    try {
        return (parseInt(value) / 100).toFixed(2) + ' %';
    } catch (e) {
        return '0.00 %';
    }
}

const fromPercent = value => {
    if (!value) return 0;

    try {
        value = value.trim();
        let length = value.length;
        if (length > 1 && value.substring(length - 1) === '%')
            value = value.substring(0, length - 1).trim();

        return Math.round(parseFloat(value) * 100);
    } catch (e) {
        return 0;
    }
}

const toDecimal = (value, size, empty) => {
    if (!value)
        return empty || (0).toFixed(size);

    try {
        return (parseInt(value) / Math.pow(10, size)).toFixed(size);
    } catch (e) {
        return empty || (0).toFixed(size);
    }
}

const fromDecimal = (value, size) => {
    if (!value) return 0;

    try {
        return Math.round(parseFloat(value) * Math.pow(10, size)) || 0;
    } catch (e) {
        return 0;
    }
}

const toInt = (value, defaultValue) => {
    if (!value) return defaultValue;

    try {
        return parseInt(value);
    } catch (e) {
        return defaultValue;
    }
}

export {
    toDecimal,
    fromDecimal,
    toPercent,
    fromPercent,
    toInt
};