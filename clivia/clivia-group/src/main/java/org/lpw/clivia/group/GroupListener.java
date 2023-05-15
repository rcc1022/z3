package org.lpw.clivia.group;

import org.lpw.clivia.group.member.MemberModel;

import java.util.List;

/**
 * 群组监听器。
 */
public interface GroupListener {
    /**
     * 创建群组。
     *
     * @param group    群组。
     * @param prologue 开场白。
     */
    void groupCreate(GroupModel group, String prologue);

    /**
     * 群更新。
     *
     * @param group 群组。
     */
    void groupUpdate(GroupModel group);

    /**
     * 退出群。
     *
     * @param group 群组。
     * @param user  退出用户。
     */
    void groupExit(GroupModel group, String user);

    /**
     * 删除群组。
     *
     * @param group   群组。
     * @param members 群成员集。
     */
    void groupDelete(GroupModel group, List<MemberModel> members);
}
