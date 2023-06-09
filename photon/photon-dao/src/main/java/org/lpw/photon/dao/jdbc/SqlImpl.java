package org.lpw.photon.dao.jdbc;

import com.alibaba.fastjson.JSONArray;
import org.lpw.photon.dao.Mode;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository("photon.dao.jdbc.sql")
public class SqlImpl extends JdbcSupport<PreparedStatement> implements Sql {
    @Override
    public SqlTable query(String dataSource, String sql, Object[] args) {
        try {
            long time = System.currentTimeMillis();
            PreparedStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            SqlTable sqlTable = query(pstmt.executeQuery());
            pstmt.close();

            if (logger.isDebugEnable())
                logger.debug("执行SQL[dataSource={}:sql={}:args={}:count={}:duration={}]检索操作。", dataSource, sql,
                        converter.toString(args), sqlTable.getRowCount(), System.currentTimeMillis() - time);

            return sqlTable;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}:{}]检索时发生异常！", dataSource, sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONArray queryAsJson(String dataSource, String sql, Object[] args) {
        long time = System.currentTimeMillis();
        try {
            PreparedStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            JSONArray array = queryAsJson(pstmt.executeQuery());
            pstmt.close();

            if (logger.isDebugEnable())
                logger.debug("执行SQL[dataSource={}:sql={}:args={}:count={}:duration={}]检索操作。", dataSource, sql,
                        converter.toString(args), array.size(), System.currentTimeMillis() - time);

            return array;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[dataSource={}:sql={}:args={}:duration={}]检索时发生异常！", dataSource, sql,
                    converter.toString(args), System.currentTimeMillis() - time);

            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] update(String sql, List<Object[]> args) {
        return update(null, sql, args);
    }

    @Override
    public int[] update(String dataSource, String sql, List<Object[]> args) {
        if (validator.isEmpty(args))
            return new int[] { update(sql, new Object[0]) };

        if (args.size() == 1)
            return new int[] { update(sql, args.get(0)) };

        long time = System.currentTimeMillis();
        try {
            PreparedStatement pstmt = newPreparedStatement(dataSource, Mode.Write, sql);
            for (Object[] array : args) {
                setArgs(pstmt, array);
                pstmt.addBatch();
            }
            int[] array = pstmt.executeBatch();
            pstmt.close();

            if (logger.isDebugEnable())
                logger.debug("执行SQL[dataSource={}:sql={}:args={}:count={}:duration={}]批量更新操作。", dataSource, sql,
                        converter.toString(args), converter.toString(array), System.currentTimeMillis() - time);

            return array;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[dataSource={}:sql={}:args={}:duration={}]更新时发生异常！", dataSource, sql,
                    converter.toString(args), System.currentTimeMillis() - time);

            throw new RuntimeException(e);
        }
    }

    @Override
    public int backup(String from, String to, String where, Object[] args) {
        return backup(null, from, to, where, args);
    }

    @Override
    public int backup(String dataSource, String from, String to, String where, Object[] args) {
        update(dataSource, "INSERT INTO " + to + " SELECT * FROM " + from + " WHERE " + where, args);

        return update(dataSource, "DELETE FROM " + from + " WHERE " + where, args);
    }

    @Override
    protected PreparedStatement newPreparedStatement(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
