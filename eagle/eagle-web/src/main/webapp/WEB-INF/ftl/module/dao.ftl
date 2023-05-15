package ${data.pkg};
<#if data.execute?contains("query") || data.execute?contains("user")>

import org.lpw.photon.dao.orm.PageList;
</#if>
<#if data.date==2 || data.timestamp==2>

    <#if data.date==2>
import java.sql.Date;
    </#if>
    <#if data.timestamp==2>
import java.sql.Timestamp;
    </#if>
</#if>

interface ${data.name}Dao {
<#if data.execute?contains("query") || data.execute?contains("user")>
    PageList<${data.name}Model> query(<#if data.searchArg??>${data.searchArg}</#if>int pageSize, int pageNum);
</#if>
<#if data.execute?contains("save")>

    ${data.name}Model findById(String id);

    void save(${data.name}Model ${data.lowerName});
</#if>
<#if data.execute?contains("delete")>

    void delete(String id);
</#if>
}