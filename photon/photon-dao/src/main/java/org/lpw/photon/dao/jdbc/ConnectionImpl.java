package org.lpw.photon.dao.jdbc;

import org.lpw.photon.dao.ConnectionSupport;
import org.lpw.photon.dao.Mode;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;

@Repository("photon.dao.jdbc.connection")
public class ConnectionImpl extends ConnectionSupport<Connection> implements org.lpw.photon.dao.jdbc.Connection {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private org.lpw.photon.dao.jdbc.DataSource dataSource;
    private final ThreadLocal<Map<String, Connection>> connections = new ThreadLocal<>();
    private final ThreadLocal<Map<String, Savepoint>> savepoints = new ThreadLocal<>();
    private final ThreadLocal<Boolean> transactional = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        transactional.set(true);
    }

    @Override
    public Connection get(String dataSource, Mode mode) {
        if (dataSource == null)
            dataSource = this.dataSource.getDefaultKey();
        if ((transactional.get() != null && transactional.get()) || !this.dataSource.hasReadonly(dataSource))
            mode = Mode.Write;
        Map<String, Connection> connections = this.connections.get();
        if (connections == null)
            connections = new HashMap<>();
        String key = dataSource + mode.ordinal();
        Connection connection = connections.get(key);
        try {
            if (connection != null) {
                if (isOpen(connection))
                    return connection;

                connections.remove(key);
                savepoints.get().remove(key);
            }

            DataSource ds = null;
            if (mode == Mode.Read)
                ds = this.dataSource.getReadonly(dataSource);
            if (ds == null)
                ds = this.dataSource.getWriteable(dataSource);
            connection = ds.getConnection();
            connection.setAutoCommit(mode == Mode.Read);
            if (savepoints.get() == null)
                savepoints.set(new HashMap<>());
            savepoints.get().put(key, connection.setSavepoint());
            connections.put(key, connection);
            this.connections.set(connections);

            return connection;
        } catch (Throwable throwable) {
            this.dataSource.addGetFailure(dataSource, mode, throwable);
            logger.warn(throwable, "获取数据库[{}:{}]连接时发生异常！", dataSource, mode);

            throw new NullPointerException("获取数据库[" + dataSource + "," + mode + "]连接时发生异常！");
        }
    }

    @Override
    public void fail(Throwable throwable) {
        Map<String, Connection> connections = this.connections.get();
        if (!validator.isEmpty(connections)) {
            rollback(connections);
            close(connections);

            if (logger.isDebugEnable())
                logger.debug("回滚[{}]个数据库连接！", connections.size());
        }
        remove();
    }

    @Override
    public void close() {
        Map<String, Connection> connections = this.connections.get();
        if (!validator.isEmpty(connections)) {
            commit(connections);
            close(connections);

            if (logger.isDebugEnable())
                logger.debug("关闭[{}]个数据库连接！", connections.size());
        }
        remove();
    }

    private void commit(Map<String, Connection> connections) {
        try {
            for (Connection connection : connections.values())
                if (isOpen(connection) && !connection.getAutoCommit())
                    connection.commit();
        } catch (Throwable throwable) {
            logger.warn(throwable, "提交数据库[{}]事务时发生异常！", converter.toString(connections));

            fail(throwable);
        }
    }

    private void rollback(Map<String, Connection> connections) {
        connections.forEach((key, connection) -> {
            try {
                if (isOpen(connection) && !connection.getAutoCommit())
                    connection.rollback(savepoints.get().get(key));
            } catch (Throwable throwable) {
                logger.warn(throwable, "回滚数据库连接[{}]时发生异常！", key);
            }
        });
    }

    private void close(Map<String, Connection> connections) {
        connections.forEach((key, connection) -> {
            try {
                if (isOpen(connection)) {
                    connection.releaseSavepoint(savepoints.get().get(key));
                    connection.close();
                }
            } catch (Throwable throwable) {
                logger.warn(throwable, "关闭数据库[{}]事务时发生异常！", key);
            }
        });
    }

    private boolean isOpen(Connection connection) throws SQLException {
        return !connection.isClosed();
    }

    private void remove() {
        connections.remove();
        savepoints.remove();
        transactional.remove();
    }
}
