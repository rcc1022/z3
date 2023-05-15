package org.lpw.clivia.user.crosier;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.ctrl.execute.Executor;
import org.lpw.photon.ctrl.execute.ExecutorHelper;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service(CrosierModel.NAME + ".service")
public class CrosierServiceImpl implements CrosierService, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Message message;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private Cache cache;
    @Inject
    private ExecutorHelper executorHelper;
    @Inject
    private UserService userService;
    @Inject
    private Optional<CrosierGrade> crosierGrade;
    @Inject
    private Optional<Set<CrosierValid>> valids;
    @Inject
    private CrosierDao crosierDao;
    private final int[] grades = {0, 90};
    private final Map<Integer, Map<String, Set<Map<String, String>>>> map = new ConcurrentHashMap<>();
    private final Set<String> always = Set.of("/photon/ctrl/status");
    private final Set<String> signs = Set.of("/photon/ctrl/upload", "/photon/ctrl-http/upload");

    @Override
    public JSONArray signUpGrades() {
        return new JSONArray();
    }

    @Override
    public int signUpGrade(String grade) {
        int g = numeric.toInt(grade);
        for (int n : intGrades())
            if (g == n)
                return n;

        return 0;
    }

    @Override
    public JSONArray grades() {
        return cache.computeIfAbsent(CrosierModel.NAME + context.getLocale().toString(), key -> {
            JSONArray grades = new JSONArray();
            for (int grade : intGrades()) {
                JSONObject object = new JSONObject();
                object.put("grade", grade);
                object.put("name", name(numeric.toString(grade)));
                grades.add(object);
            }

            return grades;
        }, false);
    }

    private String name(String grade) {
        return message.get((crosierGrade.isPresent() ? crosierGrade.get().name() : (CrosierModel.NAME + ".grade.")) + grade);
    }

    @Override
    public JSONArray pathes(int grade) {
        JSONArray pathes = new JSONArray();
        crosierDao.query(grade).getList().forEach(crosier -> pathes.add(crosier.getPath()));

        return pathes;
    }

    @Override
    public void save(int grade, String pathes) {
        crosierDao.delete(grade);
        if (validator.isEmpty(pathes))
            return;

        for (String path : converter.toArray(pathes, ",,")) {
            String[] ps = converter.toArray(path, ";");
            String up = ps[ps.length - 1];
            int index = up.indexOf('{');
            String uri = index == -1 ? up : up.substring(0, index);
            if (uri.charAt(0) != '/' && ps.length > 1) {
                String parent = ps[ps.length - 2];
                int i = parent.indexOf('{');
                if (i > -1)
                    parent = parent.substring(0, i);
                i = parent.lastIndexOf('/');
                if (i > 0)
                    uri = parent.substring(0, i + 1) + uri;
            }

            CrosierModel crosier = new CrosierModel();
            crosier.setGrade(grade);
            crosier.setUri(uri);
            crosier.setParameter(index == -1 ? null : up.substring(index));
            crosier.setPath(path);
            crosierDao.save(crosier);
            valid(grade);
        }
    }

    @Override
    public boolean permit(String uri, Map<String, String> parameter) {
        if (always.contains(uri))
            return true;

        Integer grade = permitGrade(uri);
        if (grade != null && grade == -1)
            return true;

        UserModel user = userService.fromSession();
        if (user == null || user.getState() != 1)
            return false;

        if (signs.contains(uri))
            return true;

        if (grade != null)
            return user.getGrade() >= grade;

        if (user.getCode().equals("99999999"))
            return true;

        if (!map.containsKey(user.getGrade()))
            return false;

        Map<String, Set<Map<String, String>>> map = this.map.get(user.getGrade());
        if (!map.containsKey(uri))
            return false;

        Set<Map<String, String>> set = map.get(uri);
        if (set.isEmpty())
            return true;

        for (Map<String, String> param : set) {
            int count = 0;
            for (String key : param.keySet())
                if (parameter.containsKey(key) && parameter.get(key).equals(param.get(key)))
                    count++;
            if (count == param.size())
                return true;
        }

        return false;
    }

    private Integer permitGrade(String uri) {
        Executor executor = executorHelper.get(uri);
        if (executor == null)
            return null;

        String permit = executor.getPermit();
        if (permit.equals(""))
            return null;

        if (permit.equals(Permit.always))
            return -1;

        int grade = numeric.toInt(permit, -1);

        return grade < 0 ? null : grade;
    }

    @Override
    public int getContextRefreshedSort() {
        return 115;
    }

    @Override
    public void onContextRefreshed() {
        for (int grade : intGrades())
            valid(grade);
    }

    private int[] intGrades() {
        return crosierGrade.isPresent() ? crosierGrade.get().grades() : grades;
    }

    private void valid(int grade) {
        Map<String, Set<Map<String, String>>> map = new HashMap<>();
        crosierDao.query(grade).getList().forEach(crosier -> {
            Set<Map<String, String>> set = map.computeIfAbsent(crosier.getUri(), key -> new HashSet<>());
            if (!validator.isEmpty(crosier.getParameter()))
                set.add(json.toMap(json.toObject(crosier.getParameter())));
            map.put(crosier.getPath(), new HashSet<>());
        });
        this.map.put(grade, map);
        valids.ifPresent(set -> set.forEach(valid -> valid.crosierValid(grade)));
    }
}
