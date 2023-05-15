package org.lpw.clivia.group.member;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.*;

@Service(MemberModel.NAME + ".service")
public class MemberServiceImpl implements MemberService {
    @Inject
    private DateTime dateTime;
    @Inject
    private UserService userService;
    @Inject
    private MemberDao memberDao;

    @Override
    public Set<String> groups(String user, int type) {
        Set<String> set = new HashSet<>();
        memberDao.query(user, type).getList().forEach(member -> set.add(member.getGroup()));

        return set;
    }

    @Override
    public Map<String, Integer> grades(String user, int type) {
        Map<String, Integer> map = new HashMap<>();
        memberDao.query(user, type).getList().forEach(member -> map.put(member.getGroup(), member.getGrade()));

        return map;
    }

    @Override
    public List<MemberModel> list(String group) {
        return memberDao.query(group).getList();
    }

    @Override
    public MemberModel find(String group, String user) {
        return memberDao.find(group, user);
    }

    @Override
    public String friend(String user1, String user2) {
        if (user1.equals(user2))
            return self(user1);

        Set<String> set = new HashSet<>();
        memberDao.query(user1, 0).getList().forEach(member -> set.add(member.getGroup()));
        for (MemberModel member : memberDao.query(user2, 0).getList())
            if (set.contains(member.getGroup()))
                return member.getGroup();

        return null;
    }

    @Override
    public String self(String user) {
        for (MemberModel member : memberDao.query(user, 0).getList())
            if (memberDao.count(member.getGroup()) == 1)
                return member.getGroup();

        return null;
    }

    @Override
    public String groups(String user) {
        StringBuilder sb = new StringBuilder();
        memberDao.query(user, -1).getList().forEach(member -> sb.append(',').append(member.getGroup()));

        return sb.length() == 0 ? "" : sb.substring(1);
    }

    @Override
    public void create(String group, Set<String> users, int type, String owner) {
        Timestamp now = dateTime.now();
        for (String user : users) {
            if (memberDao.find(group, user) != null)
                continue;

            MemberModel member = new MemberModel();
            member.setGroup(group);
            member.setUser(user);
            member.setType(type);
            if (user.equals(owner))
                member.setGrade(2);
            member.setTime(now);
            memberDao.save(member);
        }
    }

    @Override
    public void modify(String group, Set<String> users) {
        memberDao.query(group).getList().forEach(member -> {
            if (!users.remove(member.getUser()))
                memberDao.delete(group, member.getUser());
        });
        if (users.isEmpty())
            return;

        Timestamp now = dateTime.now();
        for (String user : users) {
            MemberModel member = new MemberModel();
            member.setGroup(group);
            member.setUser(user);
            member.setType(1);
            member.setTime(now);
            memberDao.save(member);
        }
    }

    @Override
    public void memo(String id, String memo) {
        MemberModel member = memberDao.findById(id);
        member.setMemo(memo);
        memberDao.save(member);
    }

    @Override
    public void delete(String group) {
        memberDao.delete(group);
    }

    @Override
    public void delete(String group, String user) {
        memberDao.delete(group, user);
    }
}
