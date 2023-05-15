package ${data.pkg};

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
<#if data.date gt 0>
import java.sql.Date;
</#if>
<#if data.timestamp gt 0>
import java.sql.Timestamp;
</#if>

@Component(${data.name}Model.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ${data.name}Model.NAME)
@Table(name = "t_${data.table}")
public class ${data.name}Model extends ModelSupport {
    static final String NAME = "${data.beanName}";

<#list data.columns as column>
    private ${column.javaType} ${column.field}; // ${column.explain}
</#list>
<#list data.columns as column>

    @Jsonable
    @Column(name = "c_${column.name}")
    public ${column.javaType} get${column.method}() {
        return ${column.field};
    }

    public void set${column.method}(${column.javaType} ${column.field}) {
        this.${column.field} = ${column.field};
    }
</#list>
}