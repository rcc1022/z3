package org.lpw.clivia.editor;

import org.lpw.photon.dao.orm.PageList;

interface EditorDao {
    PageList<EditorModel> query(String key);

    EditorModel findById(String id);

    void insert(EditorModel editor, boolean close);

    void save(EditorModel editor, boolean close);

    void delete(EditorModel editor);

    void delete(String key);
}