package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface MemberDao {
    PageList<MemberModel> query();

    PageList<MemberModel> unread();

    MemberModel findById(String id);

    int sum();

    void insert(MemberModel member);

    void save(MemberModel member);

    void content(String content, Timestamp time);

    void userRead(String id,Timestamp time);

    void replierRead(String id,Timestamp time);

    void delete(String id);
}