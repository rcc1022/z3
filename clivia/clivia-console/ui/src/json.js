const toArray = text => {
    if (!text)
        return [];

    if (typeof (text) === 'object')
        return text;

    try {
        let array = JSON.parse(text);

        return array instanceof Array ? array : [];
    } catch (e) {
        return [];
    }
}

export {
    toArray
}