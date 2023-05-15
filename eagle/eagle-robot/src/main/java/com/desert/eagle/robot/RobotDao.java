package com.desert.eagle.robot;

import org.lpw.photon.dao.orm.PageList;

interface RobotDao {
    PageList<RobotModel> query(int pageSize, int pageNum);

    RobotModel findById(String id);

    void save(RobotModel robot);

    void delete(String id);
}