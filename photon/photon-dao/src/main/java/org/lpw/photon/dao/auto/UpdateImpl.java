package org.lpw.photon.dao.auto;

import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(AutoModel.NAME + "update")
public class UpdateImpl implements Update {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Converter converter;
    @Inject
    private Executer executer;
    @Value("${" + AutoModel.NAME + ".update:/WEB-INF/update.sql}")
    private String update;

    @Override
    public void execute() {
        if (validator.isEmpty(update))
            return;

        String[] sqls = converter.toArray(io.readAsString(context.getAbsolutePath(update)), ";");
        if (validator.isEmpty(sqls))
            return;

        for (String sql : sqls)
            executer.execute(null, sql, true);
    }
}
