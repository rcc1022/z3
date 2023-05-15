package com.desert.eagle.control;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ControlModel.NAME + ".service")
public class ControlServiceImpl implements ControlService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private ControlDao controlDao;

    @Override
    public JSONObject query(int mode) {
        return controlDao.query(mode, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public ControlModel find(String type) {
        return controlDao.find(keyvalueService.valueAsInt("setting.control." + type, 0) - 1, switch (type){
            case "sc"->1;
            default -> 0;
        });
    }

    @Override
    public void save(ControlModel control) {
        ControlModel model = validator.isId(control.getId()) ? controlDao.findById(control.getId()) : null;
        if (model == null)
            control.setId(null);
        controlDao.save(control);
    }

    @Override
    public void profit(int type, int profit) {
        ControlModel control = controlDao.find(1, type);
        if (control == null)
            return;

        if (profit > 0) {
            control.setToWin(control.getToWin() - profit);
        } else {
            control.setToLose(control.getToLose() + profit);
            if (control.getToLose() <= 0) {
                control.setToWin(control.getWin() + control.getToWin());
                control.setToLose(control.getLose() + control.getToLose());
            }
        }
        controlDao.save(control);
    }

    @Override
    public void delete(String id) {
        controlDao.delete(id);
    }
}
