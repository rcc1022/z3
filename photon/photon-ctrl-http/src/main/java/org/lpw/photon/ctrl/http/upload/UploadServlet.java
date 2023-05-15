package org.lpw.photon.ctrl.http.upload;

import org.lpw.photon.bean.BeanFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

@MultipartConfig
@WebServlet(name = "UploadServlet", urlPatterns = {UploadService.UPLOAD})
public class UploadServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 2035601763794785803L;

    private UploadService uploadService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        uploadService = BeanFactory.getBean(UploadService.class);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        uploadService.upload(request, response, UploadService.UPLOAD);
    }
}
