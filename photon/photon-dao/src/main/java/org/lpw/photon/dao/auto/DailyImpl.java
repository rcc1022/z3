package org.lpw.photon.dao.auto;

import org.lpw.photon.dao.jdbc.DataSource;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.dao.model.ModelTable;
import org.lpw.photon.dao.model.ModelTables;
import org.lpw.photon.util.TimeUnit;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;

@Repository(AutoModel.NAME + ".daily")
public class DailyImpl implements Daily {
    @Inject
    private ModelTables modelTables;
    @Inject
    private DataSource dataSource;
    @Inject
    private Executer executer;
    @Inject
    private Create create;

    @Override
    public void execute() {
        modelTables.getModelClasses().forEach(modelClass -> create(modelTables.get(modelClass), modelClass));
    }

    private void create(ModelTable modelTable, Class<? extends Model> modelClass) {
        if (modelTable.getDailyOverdue() <= 0)
            return;

        String[] array = create.read(modelClass);
        if (array == null)
            return;

        String dataSource = this.dataSource.getKey(modelTable.getDataSource());
        String tableName = modelTable.getTableName(null);
        long now = System.currentTimeMillis();
        for (String string : array) {
            if (string.startsWith("DROP TABLE"))
                for (int i = modelTable.getDailyOverdue(), max = i << 1; i < max; i++)
                    executer.execute(dataSource, string.replaceFirst(tableName, modelTable.getTableName(
                            new Date(now - TimeUnit.Day.getTime(i)))), false);

            for (int i = 0; i < 3; i++)
                executer.execute(dataSource, string.replaceFirst(tableName, modelTable.getTableName(
                        new Date(now + TimeUnit.Day.getTime(i)))), true);
        }
    }
}
