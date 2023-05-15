package org.lpw.photon.util;

import org.lpw.photon.atomic.Closable;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component("photon.util.context")
public class ContextImpl implements Context, Closable, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Value("${photon.context.charset:UTF-8}")
    private String charset;
    @Value("${photon.context.i18n:}")
    private String i18n;
    private final ThreadLocal<Locale> locale = new ThreadLocal<>();
    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();
    private String root;
    private Locale i18nLocale;

    @Override
    public String getAbsoluteRoot() {
        return root;
    }

    @Override
    public String getAbsolutePath(String path) {
        if (path.startsWith("abs:"))
            return path.substring(4);

        if (path.startsWith("classpath:")) {
            URL url = getClass().getClassLoader().getResource(path.substring(10));
            if (url == null)
                return null;

            return url.getPath();
        }

        return new File(root + "/" + path).getAbsolutePath();
    }

    @Override
    public String getCharset(String charset) {
        return validator.isEmpty(charset) ? this.charset : charset;
    }

    @Override
    public Locale getLocale() {
        if (!validator.isEmpty(i18n))
            return i18nLocale == null ? i18nLocale = Locale.forLanguageTag(i18n) : i18nLocale;

        Locale locale = this.locale.get();

        return locale == null ? Locale.getDefault() : locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public void clearThreadLocal() {
        getThreadLocalMap().clear();
    }

    @Override
    public void putThreadLocal(String key, Object value) {
        getThreadLocalMap().put(key, value);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getThreadLocal(String key) {
        return (T) getThreadLocalMap().get(key);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T removeThreadLocal(String key) {
        return (T) getThreadLocalMap().remove(key);
    }

    @Override
    public void close() {
        getThreadLocalMap().clear();
    }

    private Map<String, Object> getThreadLocalMap() {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }

        return map;
    }

    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        String path = Objects.requireNonNull(getClass().getResource("/")).getPath();
        for (String name : new String[]{"/classes/", "/WEB-INF/"}) {
            int indexOf = path.lastIndexOf(name);
            if (indexOf > -1)
                path = path.substring(0, indexOf + 1);
        }

        root = path.replace(File.separatorChar, '/');

        if (logger.isInfoEnable())
            logger.info("设置运行期根路径：{}", root);
    }
}
