package org.lpw.clivia.wps.file;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(FileModel.NAME + ".dao")
class FileDaoImpl implements FileDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public FileModel findById(String id) {
        return liteOrm.findById(FileModel.class, id);
    }

    @Override
    public FileModel find(String wps, String uri, int permission) {
        return liteOrm.findOne(new LiteQuery(FileModel.class).where("c_uri=? and c_permission=? and c_wps=?"), new Object[]{uri, permission, wps});
    }

    @Override
    public void save(FileModel file) {
        liteOrm.save(file);
    }

    @Override
    public void delete(String wps) {
        liteOrm.delete(new LiteQuery(FileModel.class).where("c_wps=?"), new Object[]{wps});
    }
}
