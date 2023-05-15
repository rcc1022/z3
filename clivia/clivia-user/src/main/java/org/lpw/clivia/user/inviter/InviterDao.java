package org.lpw.clivia.user.inviter;

import java.sql.Timestamp;

interface InviterDao {
    InviterModel findByPsid(String psid);

    void save(InviterModel inviter);

    void clean(Timestamp time);
}
