package com.desert.eagle.robot;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(RobotModel.NAME + ".dao")
class RobotDaoImpl implements RobotDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<RobotModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(RobotModel.class).size(pageSize).page(pageNum), null);
    }

    @Override
    public RobotModel findById(String id) {
        return liteOrm.findById(RobotModel.class, id);
    }

    @Override
    public void save(RobotModel robot) {
        liteOrm.save(robot);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(RobotModel.class, id);
    }
}