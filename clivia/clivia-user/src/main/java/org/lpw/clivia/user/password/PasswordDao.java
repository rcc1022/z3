package org.lpw.clivia.user.password;

import org.lpw.photon.dao.orm.PageList;

interface PasswordDao {
    PageList<PasswordModel> query(String user);

    PasswordModel find(String user, String type);

    void save(PasswordModel password);

    void delete(PasswordModel password);

    void delete(String user);
}