package org.lpw.clivia.user.invitecode;

import org.lpw.photon.dao.orm.PageList;

interface InvitecodeDao {
    PageList<InvitecodeModel> query(String batch, int pageSize, int pageNum);

    InvitecodeModel find(String code);

    void save(InvitecodeModel invitecode);

    void delete(String id);
}