package org.lpw.clivia.chrome;

import java.util.Map;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;

import org.lpw.clivia.async.AsyncService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.temporary.Temporary;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.chrome.ChromeHelper;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

@Service(ChromeModel.NAME + ".service")
public class ChromeServiceImpl implements ChromeService {
    private static final String CACHE = ChromeModel.NAME + ".service.key:";

    @Inject
    private Cache cache;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Temporary temporary;
    @Inject
    private Pagination pagination;
    @Inject
    private ChromeHelper chromeHelper;
    @Inject
    private AsyncService asyncService;
    @Inject
    private ChromeDao chromeDao;

    @Override
    public JSONObject query(String key, String name) {
        return chromeDao.query(key, name, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public ChromeModel findByKey(String key) {
        String cacheKey = CACHE + key;
        ChromeModel chrome = cache.get(cacheKey);
        if (chrome == null)
            cache.put(cacheKey, chrome = chromeDao.findByKey(key), false);

        return chrome;
    }

    @Override
    public JSONObject save(ChromeModel chrome) {
        ChromeModel model = chromeDao.findByKey(chrome.getKey());
        chrome.setId(model == null ? null : model.getId());
        if (validator.isEmpty(chrome.getPages()))
            chrome.setPages("");
        chromeDao.save(chrome);
        cache.remove(CACHE + chrome.getKey());

        return modelHelper.toJson(chrome);
    }

    @Override
    public void delete(String id) {
        chromeDao.delete(id);
    }

    @Override
    public byte[] pdf(String key, String url, int width, int height, String pages, int wait) {
        ChromeModel model = findByKey(key, 0, 0, width, height, pages, wait);

        return io.read(chromeHelper.pdf(url, model.getWait(), model.getWidth(), model.getHeight(),
                model.getPages(), temporary.root()));
    }

    @Override
    public String pdf(String key, String url, int width, int height, String pages, int wait, Map<String, String> map) {
        ChromeModel model = findByKey(key, 0, 0, width, height, pages, wait);

        return asyncService.submit(ChromeModel.NAME + ".pdf", converter.toString(map), model.getWait() * 3, () -> {
            String file = chromeHelper.pdf(url, model.getWait(), model.getWidth(), model.getHeight(), model.getPages(), temporary.root());

            return file.substring(file.lastIndexOf(temporary.root()));
        });
    }

    @Override
    public byte[] png(String key, String url, int x, int y, int width, int height, int wait) {
        ChromeModel model = findByKey(key, x, y, width, height, null, wait);

        return io.read(chromeHelper.png(url, model.getWait(), model.getX(), model.getY(), model.getWidth(), model.getHeight(),
                temporary.root()));
    }

    @Override
    public byte[] jpg(String key, String url, int x, int y, int width, int height, int wait) {
        ChromeModel model = findByKey(key, x, y, width, height, null, wait);

        return io.read(chromeHelper.jpeg(url, model.getWait(), model.getX(), model.getY(), model.getWidth(), model.getHeight(),
                100, temporary.root()));
    }

    private ChromeModel findByKey(String key, int x, int y, int width, int height, String pages, int wait) {
        ChromeModel chrome = new ChromeModel();
        modelHelper.copy(findByKey(key), chrome, false);
        if (x > 0)
            chrome.setX(x);
        if (y > 0)
            chrome.setY(y);
        if (width > 0)
            chrome.setWidth(width);
        if (height > 0)
            chrome.setHeight(height);
        if (!validator.isEmpty(pages))
            chrome.setPages(pages);
        if (wait > 0)
            chrome.setWait(wait);

        return chrome;
    }
}
