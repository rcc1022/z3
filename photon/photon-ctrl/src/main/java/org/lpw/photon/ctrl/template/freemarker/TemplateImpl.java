package org.lpw.photon.ctrl.template.freemarker;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.Failure;
import org.lpw.photon.ctrl.template.TemplateSupport;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.freemarker.Freemarker;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

@Controller("photon.ctrl.template.freemarker")
public class TemplateImpl extends TemplateSupport {
    @Inject
    private Freemarker freemarker;

    @Override
    public String getType() {
        return Templates.FREEMARKER;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void process(String name, Object data, OutputStream output) throws IOException {
        if (data instanceof Failure failure) {
            failure(getFailure(failure), output);

            return;
        }

        if (data instanceof JSONObject object && failure(object, output))
            return;

        freemarker.process(name, data, output);
    }

    private boolean failure(JSONObject object, OutputStream outputStream) throws IOException {
        if (object.containsKey("code") && object.getIntValue("code") > 0) {
            outputStream.write(object.toString().getBytes());

            return true;
        }

        return false;
    }
}
