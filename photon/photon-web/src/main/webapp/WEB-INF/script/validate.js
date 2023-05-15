photon.validator = function (name, func) {
    photon.validators[name] = func;
};

photon.validators = {};

photon.validate = function () {
    try {
        console.log(photon.args.get("parameter"));
        var json = JSON.parse(photon.args.get("parameter"));
        var names = photon.args.get("names");
        for (var i = 0; i < names.length; i++) {
            if (!photon.validators[names[i]])
                return "{\"code\":9996,\"name\":" + names[i] + "}";

            var result = photon.validators[names[i]](json);
            if (!result || result.code != 0)
                return JSON.stringify(result);
        }

        return "{\"code\":0}";
    } catch (e) {
        return "{\"code\":9999}";
    }
};
