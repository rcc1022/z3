package org.lpw.photon.freemarker;

import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Message;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller("photon.freemarker.model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ModelImpl implements Model {
    @Inject
    private Converter converter;
    @Inject
    private Message message;
    @Inject
    private DateTime dateTime;
    @Inject
    private Codec codec;
    private Object data;

    @Override
    public Converter getConverter() {
        return converter;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public DateTime getDatetime() {
        return dateTime;
    }

    @Override
    public Codec getCodec() {
        return codec;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Model setData(Object data) {
        this.data = data;

        return this;
    }
}
