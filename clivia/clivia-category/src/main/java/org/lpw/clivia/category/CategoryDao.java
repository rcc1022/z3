package org.lpw.clivia.category;

import org.lpw.photon.dao.orm.PageList;

interface CategoryDao {
    PageList<CategoryModel> query(String key, String parent);

    CategoryModel findById(String id);

    int count(String key, String parent);

    void save(CategoryModel category);

    void delete(String id);
}
