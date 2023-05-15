package org.lpw.photon.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component("photon.util.validator")
public class ValidatorImpl implements Validator {
    private final Map<String, Pattern> patterns = new ConcurrentHashMap<>();

    @Override
    public boolean isEmpty(Object object) {
        if (object == null)
            return true;

        if (object instanceof String string)
            return string.trim().length() == 0;

        if (object.getClass().isArray())
            return Array.getLength(object) == 0;

        if (object instanceof Iterable iterable)
            return !iterable.iterator().hasNext();

        if (object instanceof Map map)
            return map.isEmpty();

        return false;
    }

    @Override
    public boolean isEmail(String email) {
        return isMatchRegex("^(?:\\w+\\.?-?)*\\w+@(?:\\w+\\.?-?)*\\w+$", email);
    }

    @Override
    public boolean isMobile(String mobile) {
        return isMatchRegex("^1\\d{10}$", mobile);
    }

    @Override
    public boolean isMatchRegex(String regex, String string) {
        return regex != null && string != null && getPattern(regex).matcher(string).matches();
    }

    private Pattern getPattern(String regex) {
        return patterns.computeIfAbsent(regex, Pattern::compile);
    }

    @Override
    public boolean isId(String string) {
        return isMatchRegex("[\\da-f-]{36}", string) && string.split("-").length == 5;
    }

    @Override
    public boolean startsWith(byte[] bytes, byte[] prefix) {
        if (isEmpty(bytes) || isEmpty(prefix) || bytes.length < prefix.length)
            return false;

        for (int i = 0; i < prefix.length; i++)
            if (bytes[i] != prefix[i])
                return false;

        return true;
    }
}
