package org.lpw.clivia.dao;

import org.lpw.photon.dao.jdbc.DataSource;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Repository("clivia.util.dao-helper")
public class DaoHelperImpl implements DaoHelper {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private DataSource dataSource;
    @Inject
    private LiteOrm liteOrm;

    @Override
    public QueryBuilder newQueryBuilder() {
        return new QueryBuilder(liteOrm, this);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, String value) {
        where(where, args, column, operation, value, false);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, String value, boolean and) {
        if (validator.isEmpty(value))
            return;

        appendWhere(where, args, column, operation, value, and);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, long value) {
        where(where, args, column, operation, value, false);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, long value, boolean and) {
        if (value < 0L)
            return;

        appendWhere(where, args, column, operation, value, and);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, Date value) {
        where(where, args, column, operation, value, false);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, Date value, boolean and) {
        if (value == null)
            return;

        appendWhere(where, args, column, operation, value, and);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, Timestamp value) {
        where(where, args, column, operation, value, false);
    }

    @Override
    public void where(StringBuilder where, List<Object> args, String column, DaoOperation operation, Timestamp value, boolean and) {
        if (value == null)
            return;

        appendWhere(where, args, column, operation, value, and);
    }

    @Override
    public void between(StringBuilder where, List<Object> args, String column, ColumnType type, String value) {
        between(where, args, column, type, value, false);
    }

    @Override
    public void between(StringBuilder where, List<Object> args, String column, ColumnType type, String value, boolean and) {
        if (value == null || value.trim().length() <= 1 || value.indexOf(',') == -1)
            return;

        String[] array = converter.toArray(value, ",");
        Object start;
        Object end;
        switch (type) {
            case Int:
                int[] ns = numeric.toInts(array, -1);
                if (ns[0] == -1 || ns[1] == -1) {
                    start = ns[0] == -1 ? null : ns[0];
                    end = ns[1] == -1 ? null : ns[1];
                    break;
                }

                if (ns[0] > ns[1]) {
                    start = ns[1];
                    end = ns[0];
                } else {
                    start = ns[0];
                    end = ns[1];
                }
                break;
            case Money:
                double[] ms = numeric.toDoubles(array, -1.0D);
                if (ms[0] == -1.0D || ms[1] == -1.0D) {
                    start = ms[0] == -1.0D ? null : fromMoney(ms[0]);
                    end = ms[1] == -1.0D ? null : fromMoney(ms[1]);
                    break;
                }

                if (ms[0] > ms[1]) {
                    start = fromMoney(ms[1]);
                    end = fromMoney(ms[0]);
                } else {
                    start = fromMoney(ms[0]);
                    end = fromMoney(ms[1]);
                }
                break;
            case Date:
                Date[] ds = dateTime.toDateRange(array);
                start = ds[0];
                end = ds[1];
                break;
            case Timestamp:
                Timestamp[] ts = dateTime.toTimeRange(array);
                start = ts[0];
                end = ts[1];
                break;
            default:
                return;
        }

        if (start == null && end == null)
            return;

        if (start == null)
            appendWhere(where, args, column, DaoOperation.LessEquals, end, and);
        else if (end == null)
            appendWhere(where, args, column, DaoOperation.GreaterEquals, start, and);
        else {
            appendColumn(where, args, column, and);
            where.append(" BETWEEN ? AND ?");
            args.add(start);
            args.add(end);
        }
    }

    private long fromMoney(double money) {
        return numeric.toInt(money * 100);
    }

    @Override
    public void in(StringBuilder where, List<Object> args, String column, Set<?> values) {
        in(where, args, column, values, false);
    }

    @Override
    public void in(StringBuilder where, List<Object> args, String column, Set<?> values, boolean and) {
        if (validator.isEmpty(values))
            return;

        if (values.size() == 1) {
            appendWhere(where, args, column, DaoOperation.Equals, values.iterator().next(), and);

            return;
        }

        appendColumn(where, args, column, and);
        where.append(" IN(");
        boolean has = false;
        for (Object value : values) {
            if (has)
                where.append(',');
            where.append('?');
            args.add(value);
            has = true;
        }
        where.append(')');
    }

    @Override
    public void like(String dataSource, StringBuilder where, List<Object> args, String column, String value) {
        like(dataSource, where, args, column, value, true, true, false);
    }

    @Override
    public void like(String dataSource, StringBuilder where, List<Object> args, String column, String value, boolean and) {
        like(dataSource, where, args, column, value, true, true, and);
    }

    @Override
    public void like(String dataSource, StringBuilder where, List<Object> args, String column, String value, boolean prefix, boolean suffix) {
        like(dataSource, where, args, column, value, prefix, suffix, false);
    }

    @Override
    public void like(String dataSource, StringBuilder where, List<Object> args, String column, String value,
                     boolean prefix, boolean suffix, boolean and) {
        if (validator.isEmpty(value))
            return;

        if (!prefix && !suffix) {
            appendWhere(where, args, column, DaoOperation.Equals, value, and);

            return;
        }

        appendColumn(where, args, column, and);
        where.append(" LIKE ?");
        args.add(this.dataSource.getDialect(dataSource).getLike(value, prefix, suffix));
    }

    private void appendWhere(StringBuilder where, List<Object> args, String column, DaoOperation operation, Object value, boolean and) {
        appendColumn(where, args, column, and);
        where.append(operation.get());
        args.add(value);
    }

    private void appendColumn(StringBuilder where, List<Object> args, String column, boolean and) {
        if (and || !args.isEmpty())
            where.append(" AND ");
        where.append(column);
    }
}
