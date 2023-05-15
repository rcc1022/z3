package org.lpw.clivia.user.upload;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.upload.UploadInterceptor;
import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(UploadModel.NAME + ".service")
public class UploadServiceImpl implements UploadService, UploadInterceptor {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private UploadDao uploadDao;

    @Override
    public JSONObject query() {
        return uploadDao.query("", pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject user() {
        return uploadDao.query(userService.id(), pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(String id, String filename) {
        UploadModel upload = uploadDao.findById(id);
        if (upload == null || !upload.getUser().equals(userService.id()))
            return;

        upload.setFilename(filename);
        uploadDao.save(upload);
    }

    @Override
    public void delete(String id) {
        uploadDao.delete(id, userService.id());
    }

    @Override
    public boolean enable(UploadReader uploadReader, UploadListener uploadListener, String contentType) {
        return userService.fromSession() != null;
    }

    @Override
    public void complete(UploadReader uploadReader, UploadListener uploadListener, String contentType, JSONObject object) {
        UploadModel upload = new UploadModel();
        upload.setUser(userService.id());
        upload.setName(uploadListener.getKey());
        upload.setContentType(contentType);
        upload.setFilename(uploadReader.getFileName());
        upload.setUri(object.getString("path"));
        upload.setTime(dateTime.now());
        uploadDao.save(upload);
    }
}
