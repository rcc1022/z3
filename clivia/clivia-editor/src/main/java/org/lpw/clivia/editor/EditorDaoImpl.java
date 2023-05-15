package org.lpw.clivia.editor;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(EditorModel.NAME + ".dao")
class EditorDaoImpl implements EditorDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<EditorModel> query(String key) {
        return liteOrm.query(new LiteQuery(EditorModel.class).where("c_key=?").order("c_order,c_time desc"), new Object[]{key});
    }

    @Override
    public EditorModel findById(String id) {
        return liteOrm.findById(EditorModel.class, id);
    }

    @Override
    public void insert(EditorModel editor, boolean close) {
        liteOrm.insert(editor);
        if (close)
            liteOrm.close();
    }

    @Override
    public void save(EditorModel editor, boolean close) {
        liteOrm.save(editor);
        if (close)
            liteOrm.close();
    }

    @Override
    public void delete(EditorModel editor) {
        liteOrm.delete(editor);
        liteOrm.close();
    }

    @Override
    public void delete(String key) {
        liteOrm.delete(new LiteQuery(EditorModel.class).where("c_key=?"), new Object[]{key});
    }
}