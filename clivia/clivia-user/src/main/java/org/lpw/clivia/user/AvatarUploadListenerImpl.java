package org.lpw.clivia.user;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Image;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

@Controller(UserModel.NAME + ".upload-listener.avatar")
public class AvatarUploadListenerImpl extends ImageUploadListener {
    @Inject
    private Image image;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private UserService userService;

    @Override
    public String getKey() {
        return UserModel.NAME + ".avatar";
    }

    @Override
    public void complete(UploadReader uploadReader, JSONObject object) {
        if (!object.containsKey("path"))
            return;

        try {
            String path = object.getString("path");
            String absolute = context.getAbsolutePath(path);
            BufferedImage bi = image.read(io.read(absolute));
            int width = bi.getWidth();
            int height = bi.getHeight();
            if (width == height)
                return;

            if (!path.endsWith(".jpg") && !path.endsWith(".jpeg")) {
                object.put("path", path + ".jpg");
                absolute += ".jpg";
            }

            int size = Math.min(width, height);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            image.write(image.subimage(bi, x, y, size, size), Image.Format.Jpeg, new FileOutputStream(absolute));
        } catch (Throwable throwable) {
            logger.warn(throwable, "截取头像图片时发生异常！");
        }
    }
}
