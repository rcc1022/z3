package org.lpw.clivia.wps;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.wps.file.FileService;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(WpsModel.NAME + ".service")
public class WpsServiceImpl implements WpsService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private FileService fileService;
    @Inject
    private WpsDao wpsDao;

    @Override
    public JSONObject query(String key, String name, String appId) {
        return wpsDao.query(key, name, appId, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public WpsModel findById(String id) {
        return wpsDao.findById(id);
    }

    @Override
    public WpsModel findByKey(String key) {
        return wpsDao.findByKey(key);
    }

    @Override
    public void save(WpsModel wps) {
        if (validator.isEmpty(wps.getId()) || wpsDao.findById(wps.getId()) == null)
            wps.setId(null);
        wpsDao.save(wps);
    }

    @Override
    public void delete(String id) {
        wpsDao.delete(id);
        fileService.delete(id);
    }
}
