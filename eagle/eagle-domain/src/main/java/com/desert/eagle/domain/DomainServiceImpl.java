package com.desert.eagle.domain;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueListener;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.ctrl.upload.UploadService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.QrCode;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service(DomainModel.NAME + ".service")
public class DomainServiceImpl implements DomainService, KeyvalueListener {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private QrCode qrCode;
    @Inject
    private Context context;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private UploadService uploadService;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private DomainDao domainDao;

    @Override
    public JSONObject query(int type) {
        return domainDao.query(type, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject entrance() {
        DomainModel domain = domainDao.find(0, 1);
        if (domain == null)
            return new JSONObject();

        JSONObject object = modelHelper.toJson(domain);
        object.put("url", invite(domain, null));

        return object;
    }

    @Override
    public String video() {
        DomainModel domain = domainDao.find(3, 1);

        return domain == null ? "" : domain.getName();
    }

    @Override
    public DomainModel find(int type, int status) {
        return domainDao.find(type, status);
    }

    @Override
    public String invite(String uid) {
        DomainModel domain = domainDao.find(0, 1);
        if (domain == null)
            return "";

        return invite(domain, uid);
    }

    @Override
    public void creates(int type, String[] names, int status) {
        for (String name : names) {
            DomainModel domain = new DomainModel();
            domain.setType(type);
            domain.setName(name);
            domain.setStatus(status);
            domain.setCreate(dateTime.now());
            domain.setModify(domain.getCreate());
            if (type == 0) {
                domain.setQrcode(uploadService.newSavePath("image/png", "", ".png"));
                qrcode(domain);
            }
            domainDao.save(domain);
        }
    }

    @Override
    public void save(DomainModel domain) {
        DomainModel model = validator.isId(domain.getId()) ? domainDao.findById(domain.getId()) : null;
        if (model == null) {
            domain.setId(null);
            domain.setCreate(dateTime.now());
        } else
            domain.setCreate(model.getCreate());
        domain.setModify(dateTime.now());
        if (domain.getType() == 0) {
            domain.setQrcode(uploadService.newSavePath("image/png", "", ".png"));
            qrcode(domain);
        }
        domainDao.save(domain);
    }

    private String invite(DomainModel domain, String uid) {
        StringBuilder sb = new StringBuilder("http://").append(domain.getName());
        if (validator.isEmpty(uid))
            sb.append("/home/index");
        else
            sb.append("/home/index1?invite=").append(uid);

        return sb.toString();
    }

    @Override
    public void status(String id, int status) {
        domainDao.status(id, status);
    }

    @Override
    public void delete(String id) {
        domainDao.delete(id);
    }

    @Override
    public void keyvalueModify(Map<String, String> map) {
        if (map.containsKey("setting.home.qrcode-logo"))
            domainDao.query(0, 0, 0).getList().forEach(this::qrcode);
    }

    private void qrcode(DomainModel domain) {
        String logo = keyvalueService.value("setting.home.qrcode-logo");
        if (!validator.isEmpty(logo))
            logo = context.getAbsolutePath(logo);
        qrCode.create(invite(domain, null), 512, logo, context.getAbsolutePath(domain.getQrcode()));
    }

    @Override
    public void keyvalueDelete(Map<String, String> map) {
    }
}
