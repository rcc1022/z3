package org.lpw.photon.ctrl.template.string;

import org.lpw.photon.ctrl.Failure;
import org.lpw.photon.ctrl.template.TemplateSupport;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

@Controller("photon.ctrl.template.string")
public class TemplateImpl extends TemplateSupport {
    @Inject
    private Context context;
    @Inject
    private Message message;

    @Override
    public String getType() {
        return Templates.STRING;
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure failure) {
            write(getFailureCode(failure) + ":" + message.get(failure.getMessageKey()), outputStream);

            return;
        }

        write(data, outputStream);
    }

    private void write(Object data, OutputStream outputStream) throws IOException {
        outputStream.write(data.toString().getBytes(context.getCharset(null)));
    }
}
