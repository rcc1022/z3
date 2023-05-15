package org.lpw.photon.ctrl.security;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.lpw.photon.storage.StorageListener;
import org.lpw.photon.storage.Storages;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller("photon.ctrl.security.trustful-ip")
public class TrustfulIpImpl implements TrustfulIp, StorageListener {
    @Inject
    private Converter converter;
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${photon.ctrl.security.trustful-ip:/WEB-INF/security/trustful-ip}")
    private String trustfulIp;
    private Set<String> ips = new HashSet<>();
    private Set<String> patterns = new HashSet<>();

    @Override
    public boolean contains(String ip) {
        if (ips.contains(ip))
            return true;

        for (String pattern : patterns) {
            if (validator.isMatchRegex(pattern, ip)) {
                ips.add(ip);

                return true;
            }
        }

        return false;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[] { trustfulIp };
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Set<String> ips = new HashSet<>();
        Set<String> patterns = new HashSet<>();
        for (String string : converter.toArray(io.readAsString(absolutePath), "\n")) {
            if (validator.isEmpty(string))
                continue;

            string = string.trim();
            if (string.charAt(0) == '#')
                continue;

            if (string.startsWith("rg"))
                patterns.add(string.substring(2));
            else
                ips.add(string);
        }
        this.ips = ips;
        this.patterns = patterns;

        if (logger.isInfoEnable())
            logger.info("更新信任IP[{}|{}]集。", converter.toString(ips), converter.toString(patterns));
    }
}
