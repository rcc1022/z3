package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.category.CategoryService;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.crosier.CrosierService;
import org.lpw.clivia.user.crosier.CrosierValid;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(ConsoleModel.NAME + ".menu")
public class MenuHelperImpl implements MenuHelper, ContextRefreshedListener, CrosierValid {
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private Header header;
    @Inject
    private CategoryService categoryService;
    @Inject
    private UserService userService;
    @Inject
    private CrosierService crosierService;
    @Inject
    private MetaHelper metaHelper;
    @Inject
    private Dashboard dashboard;
    @Value("${" + ConsoleModel.NAME + ".console:/WEB-INF/console/}")
    private String console;
    private final Map<String, MenuSupplier> suppliers = new HashMap<>();
    private final Map<String, JSONArray> map = new ConcurrentHashMap<>();

    @Override
    public JSONArray get(boolean all) {
        if (all) {
            JSONArray menus = get("");
            menus.addAll(get("o"));
            operation(menus);

            return menus;
        }

        String referer = header.get("referer");
        String key = validator.isEmpty(referer) || !referer.endsWith("/o/") ? "" : "o";
        JSONArray array = json.copy(map.computeIfAbsent(key + numeric.toString(userService.grade(), "0"), k -> permit("", get(key))));
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.containsKey("supplier")) {
                String supplier = object.getString("supplier");
                if (suppliers.containsKey(supplier))
                    object.put("items", suppliers.get(supplier).items());
            }
        }

        return array;
    }

    private JSONArray get(String key) {
        JSONArray array = json.copy(map.get(key));
        for (int i = array.size() - 1; i > -1; i--) {
            JSONObject object = array.getJSONObject(i);
            if (!object.containsKey("category"))
                continue;

            array.remove(i);
            array.addAll(i, category(categoryService.query(object.getString("category"), null)));
        }

        return array;
    }

    private JSONArray category(JSONArray categories) {
        JSONArray array = new JSONArray();
        for (int i = 0, size = categories.size(); i < size; i++) {
            JSONObject object = new JSONObject();
            JSONObject category = categories.getJSONObject(i);
            object.put("label", category.getString("name"));
            String value = category.getString("value");
            if (!validator.isEmpty(value)) {
                object.put("service", value);
                JSONObject parameter = new JSONObject();
                parameter.put("category", category.getString("id"));
                object.put("parameter", parameter);
            }
            if (category.containsKey("children"))
                object.put("items", category(category.getJSONArray("children")));
            array.add(object);
        }

        return array;
    }

    private void operation(JSONArray menus) {
        if (validator.isEmpty(menus))
            return;

        for (int i = 0, size = menus.size(); i < size; i++) {
            JSONObject menu = menus.getJSONObject(i);
            if (menu.containsKey("items")) {
                operation(menu.getJSONArray("items"));

                continue;
            }

            if (!menu.containsKey("service"))
                continue;

            String service = menu.getString("service").replace('.', '/');
            int index = service.lastIndexOf('/') + 1;
            if (index == 0)
                continue;

            JSONArray items = new JSONArray();
            operation(metaHelper.get(service.substring(0, index), true), service.substring(index), new String[]{"toolbar", "ops"}, items, 0);
            if (!items.isEmpty())
                menu.put("items", items);
        }
    }

    private void operation(JSONObject meta, String name, String[] ops, JSONArray items, int depth) {
        if (meta == null || depth > 1 || validator.isEmpty(name) || !meta.containsKey(name))
            return;

        JSONObject m = meta.getJSONObject(name);
        JSONArray props = metaHelper.props(meta, m);
        if (!validator.isEmpty(props)) {
            for (int i = 0, size = props.size(); i < size; i++) {
                JSONObject prop = props.getJSONObject(i);
                if (prop.containsKey("service"))
                    items.add(prop);
            }
        }

        for (String op : ops) {
            if (!m.containsKey(op))
                continue;

            JSONArray is = m.getJSONArray(op);
            items.addAll(is);
            for (int i = 0, size = is.size(); i < size; i++) {
                JSONObject obj = is.getJSONObject(i);
                operation(meta, obj.getString(obj.containsKey("service") ? "service" : "type"), ops, items, depth + 1);
            }
        }
    }

    private JSONArray permit(String path, JSONArray menus) {
        JSONArray array = new JSONArray();
        for (int i = 0, size = menus.size(); i < size; i++) {
            JSONObject object = menus.getJSONObject(i);
            String p = validator.isEmpty(path) ? "" : (path + ";");
            if (object.containsKey("service")) {
                p += object.getString("service");
                if (object.containsKey("parameter"))
                    p += object.getJSONObject("parameter").toJSONString();
            } else
                p += object.getString("label");
            if (!crosierService.permit(p, new HashMap<>()))
                continue;

            if (object.containsKey("items")) {
                JSONArray items = permit(p, object.getJSONArray("items"));
                if (!items.isEmpty()) {
                    object.put("items", items);
                    array.add(object);
                }
            } else
                array.add(object);
        }

        return array;
    }

    @Override
    public void crosierValid(int grade) {
        map.computeIfAbsent("", key -> load(""));
        map.computeIfAbsent("o", key -> load("-o"));
        map.remove(numeric.toString(grade, "0"));
        map.remove("o" + numeric.toString(grade, "0"));
    }

    private JSONArray load(String key) {
        JSONArray array = json.toArray(io.readAsString(context.getAbsolutePath(console + "menu" + key + ".json")));
        if (array == null) {
            logger.warn(null, "读取菜单配置[{}]失败！", key);

            return new JSONArray();
        }

        if (logger.isInfoEnable())
            logger.info("载入菜单配置[{}:{}]。", key, array);

        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            if (json.has(object, "service", "/console/dashboard")) {
                JSONArray items = new JSONArray();
                JSONArray cards = dashboard.cards();
                for (int j = 0, s = cards.size(); j < s; j++) {
                    JSONObject card = cards.getJSONObject(j);
                    card.put("label", card.getString("title"));
                    items.add(card);
                }
                object.put("items", items);

                break;
            }
        }

        return array;
    }

    @Override
    public int getContextRefreshedSort() {
        return 191;
    }

    @Override
    public void onContextRefreshed() {
        suppliers.clear();
        Collection<MenuSupplier> collection = BeanFactory.getBeans(MenuSupplier.class);
        if (validator.isEmpty(collection))
            return;

        collection.forEach(supplier -> suppliers.put(supplier.key(), supplier));
    }
}
