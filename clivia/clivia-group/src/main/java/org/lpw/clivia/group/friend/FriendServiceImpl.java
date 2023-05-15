package org.lpw.clivia.group.friend;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.group.GroupService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.scheduler.DateJob;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;

@Service(FriendModel.NAME + ".service")
public class FriendServiceImpl implements FriendService, UserListener, DateJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private GroupService groupService;
    @Inject
    private Optional<Set<FriendListener>> listeners;
    @Inject
    private FriendDao friendDao;

    @Override
    public JSONObject user() {
        return friendDao.query(userService.id(), pagination.getPageSize(20), pagination.getPageNum())
                .toJson((friend, object) -> object.put("proposer", userService.get(friend.getProposer())));
    }

    @Override
    public FriendModel find(String user1, String user2) {
        FriendModel friend = friendDao.find(user1, user2);
        if (friend != null) return friend;

        return friendDao.find(user2, user1);
    }

    @Override
    public void add(String user, String memo) {
        String proposer = userService.id();
        FriendModel friend = friendDao.find(user, proposer);
        if (friend == null) {
            friend = friendDao.find(proposer, user);
            if (friend != null) {
                agree(friend.getId());

                return;
            }

            friend = new FriendModel();
            friend.setUser(user);
            friend.setProposer(proposer);
        } else if (friend.getState() != 0)
            return;

        friend.setMemo(memo);
        friend.setTime(dateTime.now());
        friendDao.save(friend);
        FriendModel model = friend;
        listeners.ifPresent(set -> set.forEach(listener -> listener.friendPropose(model)));
    }

    @Override
    public void agree(String id) {
        FriendModel friend = state(id, 1);
        if (friend == null)
            return;

        if (groupService.friend(new String[]{friend.getUser(), friend.getProposer()}) != null)
            listeners.ifPresent(set -> set.forEach(listener -> listener.friendPass(friend)));
    }

    @Override
    public void reject(String id) {
        FriendModel friend = state(id, 2);
        if (friend != null)
            listeners.ifPresent(set -> set.forEach(listener -> listener.friendReject(friend)));
    }

    private FriendModel state(String id, int state) {
        FriendModel friend = friendDao.findById(id);
        if (friend == null || friend.getState() != 0 || !friend.getUser().equals(userService.id()))
            return null;

        friend.setState(state);
        friendDao.save(friend);

        return friend;
    }

    @Override
    public void delete(String user1, String user2) {
        FriendModel friend = find(user1, user2);
        if (friend != null)
            friendDao.delete(friend);
    }

    @Override
    public void userDelete(UserModel user) {
        friendDao.delete(user.getId());
    }

    @Override
    public void executeDateJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        friendDao.state(0, 3, dateTime.getStart(calendar.getTime()));
    }
}
