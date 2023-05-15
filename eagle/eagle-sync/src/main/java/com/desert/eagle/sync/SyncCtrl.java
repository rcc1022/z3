package com.desert.eagle.sync;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(SyncModel.NAME + ".ctrl")
@Execute(name = "/sync/", key = SyncModel.NAME, code = "291")
public class SyncCtrl {
    @Inject
    private Logger logger;
    @Inject
    private Request request;
    @Inject
    private Header header;
    @Inject
    private SyncService syncService;

    @Execute(name = "ip", permit = Permit.always)
    public Object ip() {
        logger.info("sync-ip:{}", header.getIp());

        return "";
    }
}