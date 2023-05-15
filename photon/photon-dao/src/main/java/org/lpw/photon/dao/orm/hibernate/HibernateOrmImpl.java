package org.lpw.photon.dao.orm.hibernate;

import org.hibernate.LockOptions;
import org.hibernate.query.Query;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.dao.Mode;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.dao.orm.OrmSupport;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Repository("photon.dao.orm.hibernate")
public class HibernateOrmImpl extends OrmSupport<HibernateQuery> implements HibernateOrm {
    private static final String[] ARG = {"?", ":arg", "arg"};

    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Session session;

    @Override
    public <T extends Model> T findById(String dataSource, Class<T> modelClass, String id, boolean lock) {
        if (validator.isEmpty(id))
            return null;

        if (lock) {
            session.beginTransaction();

            return session.get(getDataSource(dataSource, null, null, modelClass), Mode.Write).get(modelClass,
                    (Object) id, LockOptions.UPGRADE);
        }

        return session.get(getDataSource(dataSource, null, null, modelClass), Mode.Read).get(modelClass, (Object) id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> T findOne(HibernateQuery query, Object[] args) {
        query.size(1).page(1);
        List<T> list = (List<T>) createQuery(getDataSource(null, query, null, null), Mode.Read, getQueryHql(query),
                args, query.isLocked(), 1, 1).list();

        return list.isEmpty() ? null : list.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> PageList<T> query(HibernateQuery query, Object[] args) {
        PageList<T> models = BeanFactory.getBean(PageList.class);
        if (query.getSize() > 0)
            models.setPage(query.isCountable() ? count(query, args) : query.getSize() * query.getPage(),
                    query.getSize(), query.getPage());
        models.setList((List<T>) createQuery(getDataSource(null, query, null, null), Mode.Read, getQueryHql(query),
                args, query.isLocked(), models.getSize(), models.getNumber()).list());

        return models;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> Iterator<T> iterate(HibernateQuery query, Object[] args) {
        return (Iterator<T>) createQuery(getDataSource(null, query, null, null), Mode.Read, getQueryHql(query), args,
                query.isLocked(), query.getSize(), query.getPage()).list().iterator();
    }

    private StringBuilder getQueryHql(HibernateQuery query) {
        StringBuilder hql = from(new StringBuilder().append("FROM "), query);
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());
        if (!validator.isEmpty(query.getGroup()))
            hql.append(" GROUP BY ").append(query.getGroup());
        if (!validator.isEmpty(query.getOrder()))
            hql.append(" ORDER BY ").append(query.getOrder());

        return hql;
    }

    @Override
    public int count(HibernateQuery query, Object[] args) {
        StringBuilder hql = from(new StringBuilder().append("SELECT COUNT(*) FROM "), query);
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());
        if (!validator.isEmpty(query.getGroup()))
            hql.append(" GROUP BY ").append(query.getGroup());

        return numeric
                .toInt(createQuery(getDataSource(null, query, null, null), Mode.Read, hql, args, query.isLocked(), 0, 0)
                        .list().get(0));
    }

    @Override
    public <T extends Model> boolean save(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要保存的Model为null！");

            return false;
        }

        if (validator.isEmpty(model.getId()))
            model.setId(null);
        session.get(getDataSource(dataSource, null, null, model.getClass()), Mode.Write).saveOrUpdate(model);

        return true;
    }

    @Override
    public <T extends Model> boolean insert(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要保存的Model为null！");

            return false;
        }

        session.get(getDataSource(dataSource, null, null, model.getClass()), Mode.Write).save(model);

        return true;
    }

    @Override
    public boolean update(HibernateQuery query, Object[] args) {
        StringBuilder hql = from(new StringBuilder().append("UPDATE "), query).append(" SET ").append(query.getSet());
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());

        createQuery(getDataSource(null, query, null, null), Mode.Write, hql, args, query.isLocked(), 0, 0)
                .executeUpdate();

        return true;
    }

    @Override
    public <T extends Model> boolean delete(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要删除的Model为null。");

            return false;
        }

        session.get(getDataSource(dataSource, null, null, model.getClass()), Mode.Write).delete(model);

        return true;
    }

    @Override
    public boolean delete(HibernateQuery query, Object[] args) {
        StringBuilder hql = from(new StringBuilder().append("DELETE "), query);
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());

        createQuery(getDataSource(null, query, null, null), Mode.Write, hql, args, query.isLocked(), 0, 0)
                .executeUpdate();

        return true;
    }

    @Override
    public <T extends Model> boolean deleteById(String dataSource, Class<T> modelClass, String id) {
        return delete(new HibernateQuery(modelClass).dataSource(dataSource).where("id=?"), new Object[]{id});
    }

    private StringBuilder from(StringBuilder hql, HibernateQuery query) {
        return hql.append(query.getModelClass().getName());
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> Query<T> createQuery(String dataSource, Mode mode, StringBuilder hql, Object[] args,
                                                   boolean lock, int size, int page) {
        if (logger.isDebugEnable())
            logger.debug("hql:{};args:{}", hql, converter.toString(args));

        Query<T> query = (Query<T>) session.get(dataSource, mode).createQuery(replaceArgs(hql));
        if (lock) {
            session.beginTransaction();
            query.setLockOptions(LockOptions.UPGRADE);
        }
        if (size > 0)
            query.setFirstResult(size * (page - 1)).setMaxResults(size);

        if (validator.isEmpty(args))
            return query;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                query.setParameter(ARG[2] + i, args[i]);
            else if (args[i] instanceof Collection<?> collection)
                query.setParameterList(ARG[2] + i, collection);
            else if (args[i].getClass().isArray())
                query.setParameterList(ARG[2] + i, (Object[]) args[i]);
            else
                query.setParameter(ARG[2] + i, args[i]);
        }

        return query;
    }

    private String replaceArgs(StringBuilder hql) {
        for (int i = 0, position; (position = hql.indexOf(ARG[0])) > -1; i++)
            hql.replace(position, position + 1, ARG[1] + i);

        return hql.toString();
    }

    @Override
    public void fail(Throwable throwable) {
        session.fail(throwable);
    }

    @Override
    public void close() {
        session.close();
    }
}
