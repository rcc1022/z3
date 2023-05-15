package org.lpw.clivia.customerservice;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(CustomerserviceModel.NAME + ".upload-listener.qrcode")
public class QrcodeUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return CustomerserviceModel.NAME + ".qrcode";
    }
}
