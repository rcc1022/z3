package org.lpw.clivia.shortcut;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ShortcutModel.NAME + ".dao")
class ShortcutDaoImpl implements ShortcutDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public ShortcutModel find(String code) {
        return liteOrm.findOne(new LiteQuery(ShortcutModel.class).where("c_code=?"), new Object[]{code});
    }

    @Override
    public ShortcutModel find(String md5, int length) {
        return liteOrm.findOne(new LiteQuery(ShortcutModel.class).where("c_md5=? and c_length=?"), new Object[]{md5, length});
    }

    @Override
    public void save(ShortcutModel shortcut) {
        liteOrm.save(shortcut);
    }
}
