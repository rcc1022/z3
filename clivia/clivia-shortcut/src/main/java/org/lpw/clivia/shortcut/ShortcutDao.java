package org.lpw.clivia.shortcut;

interface ShortcutDao {
    ShortcutModel find(String code);

    ShortcutModel find(String md5, int length);

    void save(ShortcutModel shortcut);
}
