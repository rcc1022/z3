package org.lpw.photon.ctrl.execute;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.ctrl.FailureCode;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller("photon.ctrl.execute.map")
public class ExecutorHelperImpl implements ExecutorHelper, FailureCode, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private Templates templates;
    @Inject
    private Request request;
    private final Map<String, Executor> map = new ConcurrentHashMap<>();
    private final Set<String> regexes = new HashSet<>();
    private final Map<String, String> codes = new HashMap<>();
    private final ThreadLocal<Executor> executor = new ThreadLocal<>();

    @Override
    public void set(String uri) {
        executor.remove();
        Executor executor = get(uri);
        if (executor != null)
            this.executor.set(executor);
    }

    @Override
    public Executor get() {
        return executor.get();
    }

    @Override
    public Executor get(String uri) {
        Executor executor = map.get(uri);
        if (executor != null)
            return executor;

        for (String regex : regexes) {
            if (validator.isMatchRegex(regex, uri)) {
                executor = map.get(regex);
                if (executor != null)
                    map.put(uri, executor);

                return executor;
            }
        }

        return null;
    }

    @Override
    public int get(int code) {
        return get(request.getUri(), code);
    }

    @Override
    public int get(String uri, int code) {
        String prefix = codes.get(uri);
        if (prefix != null)
            return getCode(prefix, code);

        for (String regex : codes.keySet())
            if (validator.isMatchRegex(regex, uri))
                return getCode(codes.get(regex), code);

        return code;
    }

    private int getCode(String prefix, int code) {
        int n = numeric.toInt(prefix + numeric.toString(code, "000"));

        return n == 0 ? -1 : n;
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        if (!map.isEmpty())
            return;

        Collection<ExecuteListener> listeners = BeanFactory.getBeans(ExecuteListener.class);
        for (String name : BeanFactory.getBeanNames()) {
            Class<?> clazz = BeanFactory.getBeanClass(name);
            Execute classExecute = clazz.getAnnotation(Execute.class);
            for (String prefix : classExecute == null ? new String[]{""} : classExecute.name().split(",")) {
                boolean regex = classExecute != null && classExecute.regex();
                String prefixCode = classExecute == null ? "" : classExecute.code();
                for (Method method : clazz.getMethods()) {
                    Execute execute = method.getAnnotation(Execute.class);
                    if (execute == null || validator.isEmpty(prefix + execute.name()))
                        continue;

                    Executor executor = new ExecutorImpl(BeanFactory.getBean(name), method, getKey(classExecute, execute),
                            execute.permit(), execute.validates(), templates.get(execute.type()),
                            prefix + (validator.isEmpty(execute.template()) ? execute.name() : execute.template()));
                    String code = prefixCode + execute.code();
                    for (String service : execute.name().split(",")) {
                        String key = prefix + service;
                        map.put(key, executor);
                        if (regex || execute.regex())
                            regexes.add(key);
                        codes.put(key, code);
                    }
                    listeners.forEach(listener -> listener.definition(classExecute, execute, executor));
                }
            }
        }

        if (logger.isInfoEnable()) {
            StringBuilder sb = new StringBuilder().append("共[").append(map.size()).append("]个服务[");
            boolean hasElement = false;
            for (String key : map.keySet()) {
                if (hasElement)
                    sb.append(',');
                sb.append(key);
                hasElement = true;
            }
            logger.info(sb.append("]。").toString());
        }
    }

    private String getKey(Execute classExecute, Execute execute) {
        if (!validator.isEmpty(execute.key()))
            return execute.key();

        return classExecute == null ? "" : classExecute.key();
    }
}
