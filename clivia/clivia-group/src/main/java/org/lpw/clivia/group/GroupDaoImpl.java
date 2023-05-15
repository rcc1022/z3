package org.lpw.clivia.group;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Set;

@Repository(GroupModel.NAME + ".dao")
class GroupDaoImpl implements GroupDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<GroupModel> query(Set<String> ids) {
        return daoHelper.newQueryBuilder().in("c_id", ids).query(GroupModel.class, 0, 0);
    }

    @Override
    public GroupModel findById(String id) {
        return liteOrm.findById(GroupModel.class, id);
    }

    @Override
    public void save(GroupModel group) {
        liteOrm.save(group);
    }

    @Override
    public void delete(GroupModel group) {
        liteOrm.delete(group);
    }
}