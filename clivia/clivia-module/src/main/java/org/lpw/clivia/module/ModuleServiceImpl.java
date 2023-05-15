package org.lpw.clivia.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.freemarker.Freemarker;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service(ModuleModel.NAME + ".service")
public class ModuleServiceImpl implements ModuleService {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Freemarker freemarker;
    @Inject
    private Logger logger;
    @Inject
    private Json json;
    @Inject
    private Pagination pagination;
    @Inject
    private ModuleDao moduleDao;
    @Value("${" + ModuleModel.NAME + ".package:org.lpw.clivia}")
    private String pkg;
    @Value("${" + ModuleModel.NAME + ".version:1.0}")
    private String version;
    @Value("${" + ModuleModel.NAME + ".url:https://github.com/heisedebaise/clivia}")
    private String url;
    @Value("${" + ModuleModel.NAME + ".output:/WEB-INF/module/}")
    private String output;

    @Override
    public JSONObject query() {
        return moduleDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(ModuleModel module) {
        ModuleModel model = validator.isId(module.getId()) ? moduleDao.findById(module.getId()) : null;
        if (model == null)
            module.setId(null);
        moduleDao.save(module);
    }

    @Override
    public void generate(String id) {
        ModuleModel module = moduleDao.findById(id);
        String project = pkg.substring(pkg.lastIndexOf('.') + 1);
        String name = project + "-" + (validator.isEmpty(module.getMain()) ? module.getName() : module.getMain());
        String path = context.getAbsolutePath(output + name) + "/";
        io.delete(path);
        String java = path + "src/main/java/" + pkg.replace('.', '/') + "/" + main(module, "/") + module.getName() + "/";
        io.mkdirs(java);
        String resources = path + "src/main/resources/" + pkg.replace('.', '/') + "/" + main(module, "/") + module.getName() + "/";
        io.mkdirs(resources);

        JSONArray columns = json.toArray(module.getColumns());
        Map<String, Object> map = new HashMap<>();
        int search = 0;
        StringBuilder searchArg = new StringBuilder();
        int date = 0;
        int timestamp = 0;
        int key = 0;
        StringBuilder order = new StringBuilder();
        for (int i = 0, size = columns.size(); i < size; i++) {
            JSONObject column = columns.getJSONObject(i);
            String columnName = column.getString("name");
            String field = upper(columnName, false);
            column.put("field", field);
            column.put("method", upper(columnName, true));
            boolean hasSearch = json.has(column, "search", "1");
            if (hasSearch) {
                search++;
            }
            boolean hasKey = column.containsKey("key") && column.getIntValue("key") > 0;
            if (hasKey)
                key++;
            if (column.containsKey("type")) {
                String type = column.getString("type");
                String javaType = switch (type) {
                    case "int" -> "int";
                    case "date" -> "Date";
                    case "datetime" -> "Timestamp";
                    default -> "String";
                };
                column.put("javaType", javaType);

                if (hasSearch)
                    searchArg.append(javaType).append(' ').append(field).append(", ");

                String dn = (hasKey ? " NOT" : " DEFAULT") + " NULL";
                column.put("sqlType", switch (type) {
                    case "int" -> "INT DEFAULT 0";
                    case "id" -> "CHAR(36)" + dn;
                    case "date" -> "DATE" + dn;
                    case "datetime" -> "DATETIME" + dn;
                    case "text" -> "TEXT" + dn;
                    default -> "VARCHAR(255)" + dn;
                });

                if (type.equals("date")) {
                    if (hasSearch)
                        date = 2;
                    else if (date == 0)
                        date = 1;
                } else if (type.equals("datetime")) {
                    if (hasSearch)
                        timestamp = 2;
                    else if (timestamp == 0)
                        timestamp = 1;
                }
            } else {
                column.put("javaType", "String");
                column.put("sqlType", "VARCHAR(255) DEFAULT NULL");
            }
            if (column.containsKey("order")) {
                int n = column.getIntValue("order");
                if (n > 0) {
                    if (!order.isEmpty())
                        order.append(",");
                    order.append("c_").append(columnName);
                    if (n == 2)
                        order.append(" DESC");
                }
            }
        }

        String upperName = upper(module.getName(), true);
        map.put("pkg", pkg + "." + main(module, ".") + module.getName());
        map.put("name", upperName);
        map.put("table", main(module, "_") + module.getName());
        map.put("beanName", project + "." + main(module, ".") + module.getName());
        map.put("lowerName", upper(module.getName(), false));
        map.put("search", search);
        if (search > 0)
            map.put("searchArg", searchArg.toString());
        map.put("date", date);
        map.put("timestamp", timestamp);
        map.put("uri", "/" + main(module, "/") + module.getName() + "/");
        map.put("code", module.getCode());
        map.put("execute", module.getExecute());
        map.put("key", key);
        if (!order.isEmpty())
            map.put("order", order.toString());
        map.put("columns", columns);

        try {
            if (validator.isEmpty(module.getMain()))
                pom(name, path);
            output(java + upperName + "Model.java", "/module/model", map);
            output(java + upperName + "Dao.java", "/module/dao", map);
            output(java + upperName + "DaoImpl.java", "/module/dao-impl", map);
            output(java + upperName + "Service.java", "/module/service", map);
            output(java + upperName + "ServiceImpl.java", "/module/service-impl", map);
            output(java + upperName + "Ctrl.java", "/module/ctrl", map);
            output(resources + "create.sql", "/module/create", map);
            output(resources + "message.properties", "/module/message", map);
            output(resources + "meta.json", "/module/meta", map);
        } catch (Throwable throwable) {
            logger.warn(throwable, "生成模块[{}]时发生异常！", json.toObject(module));
        }
    }

    private String upper(String string, boolean upper) {
        StringBuilder sb = new StringBuilder();
        for (char ch : string.toCharArray()) {
            if (ch == '_') {
                upper = true;

                continue;
            }

            if (upper) {
                sb.append((char) (ch - 'a' + 'A'));
                upper = false;
            } else
                sb.append(ch);
        }

        return sb.toString();
    }

    private String main(ModuleModel module, String suffix) {
        return validator.isEmpty(module.getMain()) ? "" : (module.getMain() + suffix);
    }

    private void pom(String name, String path) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", pkg);
        map.put("name", name);
        map.put("version", version);
        map.put("url", url);
        output(path + "pom.xml", "/module/pom", map);
    }

    private void output(String path, String template, Map<String, Object> map) throws IOException {
        OutputStream outputStream = new FileOutputStream(path);
        freemarker.process(template, map, outputStream);
        outputStream.close();
    }

    @Override
    public void delete(String id) {
        moduleDao.delete(id);
    }
}
