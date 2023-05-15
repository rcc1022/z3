package org.lpw.photon.dao.auto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Table;

import org.lpw.photon.dao.dialect.Dialect;
import org.lpw.photon.dao.jdbc.DataSource;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.dao.model.ModelTable;
import org.lpw.photon.dao.model.ModelTables;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository(AutoModel.NAME + ".create")
public class CreateImpl implements Create {
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Inject
    private DataSource dataSource;
    @Inject
    private Executer executer;
    @Value("${photon.dao.database.memory:true}")
    private boolean memory;

    @Override
    public void execute(Map<String, Set<String>> tables) {
        modelTables.getModelClasses().forEach(modelClass -> create(tables, modelTables.get(modelClass), modelClass));
    }

    private void create(Map<String, Set<String>> tables, ModelTable modelTable, Class<? extends Model> modelClass) {
        String dataSource = this.dataSource.getKey(modelTable.getDataSource());
        if (tables.containsKey(dataSource) && tables.get(dataSource).contains(modelTable.getTableName()))
            return;

        String[] array = read(modelClass);
        if (array == null)
            return;

        Dialect dialect = this.dataSource.getDialect(dataSource);
        for (String string : array)
            executer.execute(dataSource, memory ? string : dialect.noMemory(string), false);
        tables.get(dataSource).add(modelTable.getTableName());
    }

    @Override
    public String[] read(Class<? extends Model> modelClass) {
        try {
            InputStream inputStream = modelClass.getResourceAsStream("create.sql");
            if (inputStream == null)
                return null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            io.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

            return outputStream.toString().split(";\r*\n");
        } catch (IOException e) {
            logger.warn(e, "读取DDL文件[{}:create.sql]时发生异常！", modelClass);

            return null;
        }
    }

    @Override
    public void create(String dataSource, ModelTable modelTable, String tableName) {
        String[] array = read(modelTable.getModelClass());
        if (array == null)
            return;

        String name = modelTable.getModelClass().getAnnotation(Table.class).name();
        for (String string : array)
            executer.execute(dataSource, string.replaceFirst(name, tableName), true);
    }
}
