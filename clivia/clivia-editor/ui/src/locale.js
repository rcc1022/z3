const messages = {
    en: {
        'placeholder.text': "Type '/' for commands",
        'placeholder.h1': 'Heading 1',
        'placeholder.h2': 'Heading 2',
        'placeholder.h3': 'Heading 3',
        'placeholder.img': 'Add images',
        'tag.text': 'Text',
        'tag.text.sub': 'Just start writing with plain text.',
        'tag.h1': 'Heading 1',
        'tag.h1.sub': 'Big section heading.',
        'tag.h2': 'Heading 2',
        'tag.h2.sub': 'Medium section heading.',
        'tag.h3': 'Heading 3',
        'tag.h3.sub': 'Small section heading.',
        'tag.img': 'Image',
        'tag.img.sub': 'Upload images.',
        'tag.ai-text':'AI Text',
        'tag.ai-text.sub':'ai text',
        'tag.ai-image':'AI Image',
        'tag.ai-image.sub':'ai image',
        'tag.divider': 'Divider',
        'tag.divider.sub': 'Visually divide blocks.',
        'image.upload': 'Click to upload picture',
        'image.uploading': 'uploading',
        'error.empty': 'data error'
    },
    zh: {}
};

let language = 'en';

const message = (key) => {
    return messages[language][key] || '';
}

export {
    message
}