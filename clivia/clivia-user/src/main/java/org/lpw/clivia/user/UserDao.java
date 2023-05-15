package org.lpw.clivia.user;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Set;

interface UserDao {
    PageList<UserModel> query(Set<String> ids, String idcard, String name, String nick, String mobile, String email, String weixin, String qq,
                              String code, int minGrade, int maxGrade, int state, String register, String from, int pageSize, int pageNum);

    PageList<UserModel> query(String inviter, int pageSize, int pageNum);

    PageList<UserModel> query(int grade);

    PageList<UserModel> query();

    UserModel findById(String id);

    UserModel findByCode(String code);

    Set<String> ids(String idcard, String name, String nick, String mobile, String email, String weixin, String qq);

    int count();

    int count(Timestamp[] register);

    void insert(UserModel user);

    void save(UserModel user);

    void state(String id, int state);

    void delete(String id);

    void close();
}
