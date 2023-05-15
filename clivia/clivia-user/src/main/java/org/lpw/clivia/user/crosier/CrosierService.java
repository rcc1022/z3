package org.lpw.clivia.user.crosier;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

public interface CrosierService {
    JSONArray signUpGrades();

    int signUpGrade(String grade);

    JSONArray grades();

    JSONArray pathes(int grade);

    void save(int grade, String pathes);

    boolean permit(String uri, Map<String, String> parameter);
}
