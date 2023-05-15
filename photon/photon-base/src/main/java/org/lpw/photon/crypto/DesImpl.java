package org.lpw.photon.crypto;

import org.springframework.stereotype.Component;

@Component("photon.crypto.des")
public class DesImpl extends CipherSupport implements Des {
    @Override
    protected String getAlgorithm() {
        return "DES";
    }

    @Override
    protected boolean validate(byte[] key) {
        if (key.length == 8)
            return true;

        logger.warn(null, "DES密钥[{}]长度[{}]必须是8个字节！", new String(key), key.length);

        return false;
    }
}
