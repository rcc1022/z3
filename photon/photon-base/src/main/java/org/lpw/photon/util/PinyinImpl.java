package org.lpw.photon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

// https://github.com/mozillazg/pinyin-data

@Component("photon.util.pinyin")
public class PinyinImpl implements Pinyin {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    private Map<String, String> map;

    @Override
    public String get(String text) {
        if (validator.isEmpty(text))
            return "";

        if (map == null)
            load();

        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = text.length(); i < length; i++) {
            String ch = text.substring(i, i + 1);
            if (map.containsKey(ch))
                sb.append(' ').append(map.get(ch));
            else
                sb.append(ch);
        }

        return sb.toString().trim();
    }

    private synchronized void load() {
        if (map != null)
            return;

        map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("pinyin.txt")))) {
            for (String line; (line = reader.readLine()) != null;) {
                if (line.startsWith("#"))
                    continue;

                String[] array = line.split(" ");
                map.put(array[array.length - 1], array[1]);
            }
        } catch (Throwable throwable) {
            logger.warn(throwable, "读取拼音数据时发生异常！");
        }
    }
}
