package org.lpw.clivia.group;

import org.lpw.photon.dao.orm.PageList;

import java.util.Set;

interface GroupDao {
    PageList<GroupModel> query(Set<String> ids);

    GroupModel findById(String id);

    void save(GroupModel group);

    void delete(GroupModel group);
}