package org.lpw.clivia.category;

import com.alibaba.fastjson.JSONArray;

public interface CategoryService {
    String VALIDATOR_EXISTS = CategoryModel.NAME + ".validator.exists";
    String VALIDATOR_LEAF = CategoryModel.NAME + ".validator.leaf";

    JSONArray query(String key, String pointTo);

    String name(String id);

    CategoryModel get(String id);

    void save(CategoryModel category);

    void delete(String id);
}
