package org.lpw.photon.dao.dialect;

import org.springframework.stereotype.Repository;

@Repository("photon.dao.dialect.mysql")
public class MysqlDialect extends DialectSupport {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getDriver() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        return "jdbc:mysql://" + ip + "/" + schema
                + "?useSSL=false&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8"
                + "&autoReconnect=true&allowPublicKeyRetrieval=true";
    }

    @Override
    public String selectTables(String schema) {
        return "show tables";
    }

    @Override
    public String noMemory(String create) {
        return create.replace("ENGINE=Memory", "ENGINE=InnoDB");
    }

    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.MySQLDialect";
    }

    @Override
    public void appendPagination(StringBuilder sql, int size, int page) {
        sql.append(" LIMIT ").append(size * (page - 1)).append(',').append(size);
    }
}
