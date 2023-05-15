package ${data.pkg};

<#if data.execute?contains("query") || data.execute?contains("user")>
    <#if data.search gt 0>
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
    </#if>
import org.lpw.photon.dao.orm.PageList;
</#if>
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
<#if data.date==2>
import java.sql.Date;
</#if>
<#if data.timestamp==2>
import java.sql.Timestamp;
</#if>

@Repository(${data.name}Model.NAME + ".dao")
class ${data.name}DaoImpl implements ${data.name}Dao {
    @Inject
    private LiteOrm liteOrm;
<#if data.execute?contains("query") || data.execute?contains("user")>
    <#if data.search gt 0>
    @Inject
    private DaoHelper daoHelper;
    </#if>

    @Override
    public PageList<${data.name}Model> query(<#if data.searchArg??>${data.searchArg}</#if>int pageSize, int pageNum) {
    <#if data.search==0>
        return liteOrm.query(new LiteQuery(${data.name}Model.class)<#if data.order??>.order("${data.order}")</#if>.size(pageSize).page(pageNum), null);
    <#else>
        return daoHelper.newQueryBuilder()
        <#list data.columns as column>
            <#if column.search?? && column.search==1>
                .where("c_${column.name}", DaoOperation.Equals, ${column.field})
            </#if>
        </#list>
        <#if data.order??>
                .order("${data.order}")
        </#if>
                .query(${data.name}Model.class, pageSize, pageNum);
    </#if>
    }
</#if>
<#if data.execute?contains("save")>

    @Override
    public ${data.name}Model findById(String id) {
        return liteOrm.findById(${data.name}Model.class, id);
    }

    @Override
    public void save(${data.name}Model ${data.lowerName}) {
        liteOrm.save(${data.lowerName});
    }
</#if>
<#if data.execute?contains("delete")>

    @Override
    public void delete(String id) {
        liteOrm.deleteById(${data.name}Model.class, id);
    }
</#if>
}