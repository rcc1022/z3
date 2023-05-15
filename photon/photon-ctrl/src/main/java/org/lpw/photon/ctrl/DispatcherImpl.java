package org.lpw.photon.ctrl;

import org.lpw.photon.atomic.Closables;
import org.lpw.photon.atomic.Failable;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.ctrl.console.Console;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.context.Response;
import org.lpw.photon.ctrl.execute.ExecuteInvocation;
import org.lpw.photon.ctrl.execute.ExecutorHelper;
import org.lpw.photon.ctrl.security.Xss;
import org.lpw.photon.ctrl.status.Status;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller("photon.ctrl.dispatcher")
public class DispatcherImpl implements Dispatcher, Forward, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Set<Failable> failables;
    @Inject
    private Closables closables;
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Inject
    private Response response;
    @Inject
    private Xss xss;
    @Inject
    private Counter counter;
    @Inject
    private Status status;
    @Inject
    private Console console;
    @Inject
    private Validators validators;
    @Inject
    private ExecutorHelper executorHelper;
    private final ThreadLocal<Long> time = new ThreadLocal<>();
    private final ThreadLocal<Map<String, Object>> parameters = new ThreadLocal<>();
    private final List<Interceptor> interceptors = new ArrayList<>();

    @Override
    public void execute() {
        time.set(System.currentTimeMillis());
        String uri = request.getUri();
        String param = "";
        if (logger.isDebugEnable()) {
            if (uri.equals("/photon/ctrl/upload")) {
                Map<String, String> map = new HashMap<>(request.getMap());
                map.remove("base64");
                map.remove("string");
                param = converter.toString(map);
            } else
                param = converter.toString(request.getMap());
        }
        if (logger.isDebugEnable())
            logger.debug("开始处理请求[{}:{}:{}]。", uri, param, converter.toString(header.getMap()));

        boolean statusService = status.isStatus(uri);
        String ip = header.getIp();
        if (!counter.increase(uri, ip) && !statusService) {
            failure(uri, ip, Failure.Busy);

            return;
        }

        boolean consoleService = console.isConsole(uri);
        if (!statusService && !consoleService)
            executorHelper.set(uri);
        if (!statusService && !consoleService && executorHelper.get() == null) {
            counter.decrease(uri, ip);
            logger.warn(null, "无法获得请求[{}]的处理服务！", uri);
            response.sendError(404);

            return;
        }

        if (xss.contains(uri, request.getMap())) {
            failure(uri, ip, Failure.Danger);

            return;
        }

        Object object = execute(statusService, consoleService);
        if (logger.isDebugEnable())
            logger.debug("处理请求[{}:{}:{}]完成[{}]，耗时[{}]毫秒。", uri, param, converter.toString(header.getMap()),
                    object instanceof JSONObject || object instanceof String ? object : "not json or string",
                    duration());
        closables.close();
        counter.decrease(uri, ip);
    }

    private void failure(String uri, String ip, Failure failure) {
        counter.decrease(uri, ip);
        response.write(failure);
    }

    private Object execute(boolean statusService, boolean consoleService) {
        Object object;
        if (statusService)
            object = status.execute(counter.get());
        else if (consoleService)
            object = console.execute();
        else
            object = exe();
        response.write(object);

        return object;
    }

    @Override
    public Object redirect(String uri) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", uri);

        executorHelper.set(uri);

        return exe();
    }

    private Object exe() {
        try {
            return new ExecuteInvocation(interceptors, validators, executorHelper.get()).invoke();
        } catch (Throwable e) {
            failables.forEach(failable -> failable.fail(e));
            logger.warn(e, "执行请求[{}:{}:{}:{}]时发生异常！", request.getUri(), header.getMap(), request.getMap(),
                    executorHelper.get().getMethod());

            return Failure.Exception;
        }
    }

    @Override
    public long duration() {
        return System.currentTimeMillis() - time.get();
    }

    @Override
    public Object redirect(String uri, Map<String, Object> parameters) {
        this.parameters.set(parameters);

        return redirect(uri);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> T getParameter(String name) {
        Map<String, Object> parameters = getParameters();
        if (validator.isEmpty(parameters))
            return null;

        return (T) parameters.get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters.get();
    }

    @Override
    public void redirectTo(String url) {
        response.redirectTo(url);
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        interceptors.clear();
        interceptors.addAll(BeanFactory.getBeans(Interceptor.class));
        interceptors.sort(Comparator.comparingInt(Interceptor::getSort));
    }
}
