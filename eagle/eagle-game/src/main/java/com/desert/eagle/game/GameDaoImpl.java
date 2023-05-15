package com.desert.eagle.game;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(GameModel.NAME + ".dao")
class GameDaoImpl implements GameDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<GameModel> query(int on, int pageSize, int pageNum) {
        if (on == -1)
            return liteOrm.query(new LiteQuery(GameModel.class).order("c_sort").size(pageSize).page(pageNum), null);

        return liteOrm.query(new LiteQuery(GameModel.class).where("c_on=?").order("c_sort").size(pageSize).page(pageNum), new Object[]{on});
    }

    @Override
    public GameModel findById(String id) {
        return liteOrm.findById(GameModel.class, id);
    }

    @Override
    public GameModel findByType(int type) {
        return liteOrm.findOne(new LiteQuery(GameModel.class).where("c_type=?"), new Object[]{type});
    }

    @Override
    public void save(GameModel game) {
        liteOrm.save(game);
    }

    @Override
    public void on(String id, int on) {
        liteOrm.update(new LiteQuery(GameModel.class).set("c_on=?").where("c_id=?"), new Object[]{on, id});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(GameModel.class, id);
    }

    @Override
    public void close() {
        liteOrm.close();
    }
}