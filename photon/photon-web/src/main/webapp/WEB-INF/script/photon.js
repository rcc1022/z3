function photon() {
};

photon.BeanFactory = Java.type("org.lpw.photon.bean.BeanFactory");
photon.cache = photon.BeanFactory.getBean("photon.cache");
photon.message = photon.BeanFactory.getBean("photon.util.message");
photon.logger = photon.BeanFactory.getBean("photon.util.logger");
photon.sql = photon.BeanFactory.getBean("photon.dao.sql");
photon.ctrl = {
    header: photon.BeanFactory.getBean("photon.ctrl.context.header"),
    session: photon.BeanFactory.getBean("photon.ctrl.context.session"),
    request: photon.BeanFactory.getBean("photon.ctrl.context.request")
};
photon.args = photon.BeanFactory.getBean("photon.script.arguments");

photon.ready = function (func) {
    photon.ready.functions[photon.ready.functions.length] = func;
};

photon.ready.functions = [];

photon.ready.execute = function () {
    if (photon.ready.functions.length == 0)
        return;

    for (var i = 0; i < photon.ready.functions.length; i++) {
        if (!photon.ready.functions[i])
            continue;

        if (typeof (photon.ready.functions[i]) == "function")
            photon.ready.functions[i]();
        else if (typeof (photon.ready.functions[i]) == "string")
            eval(photon.ready.functions[i]);

        photon.ready.functions[i] = null;
    }
};

photon.existsMethod = function () {
    try {
        var method = photon.arguments.get("method");
        if (!method)
            method = photon.ctrl.request.get("method");

        return typeof (eval(method)) == "function";
    } catch (e) {
        return false;
    }
};
