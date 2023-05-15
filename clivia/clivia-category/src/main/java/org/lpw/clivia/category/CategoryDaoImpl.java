package org.lpw.clivia.category;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(CategoryModel.NAME + ".dao")
class CategoryDaoImpl implements CategoryDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<CategoryModel> query(String key, String parent) {
        return liteOrm.query(new LiteQuery(CategoryModel.class).where("c_key=? and c_parent=?").order("c_sort"), new Object[]{key, parent});
    }

    @Override
    public CategoryModel findById(String id) {
        return liteOrm.findById(CategoryModel.class, id);
    }

    @Override
    public int count(String key, String parent) {
        return liteOrm.count(new LiteQuery(CategoryModel.class).where("c_key=? and c_parent=?").order("c_sort"), new Object[]{key, parent});
    }

    @Override
    public void save(CategoryModel category) {
        liteOrm.save(category);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(CategoryModel.class, id);
    }
}
