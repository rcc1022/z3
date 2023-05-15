package org.lpw.clivia.keyvalue;

import java.util.Map;

public interface KeyvalueListener {
    /**
     * 更新。
     *
     * @param map 更新数据。
     */
    void keyvalueModify(Map<String, String> map);

    /**
     * 删除。
     *
     * @param map 删除数据。
     */
    void keyvalueDelete(Map<String, String> map);
}
