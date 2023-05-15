package org.lpw.clivia.user.illegal;

import org.lpw.photon.dao.orm.PageList;

interface IllegalDao {
    PageList<IllegalModel> query(int pageSize, int pageNum);

    void save(IllegalModel illegal);
}