/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 20, 2009, 05:37:24 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: BL.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.cliser;

import com.codicentro.cliser.dao.CliserDao;
import com.codicentro.security.SessionEntityBase;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import com.codicentro.utils.Types.DBProtocolType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.util.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.WebApplicationContext;

@Service
public class BL implements Serializable {

    private Logger log = LoggerFactory.getLogger(BL.class);
    private RequestWrapper requestWrapper = null;
    private ResponseWrapper responseWrapper = null;
    private DBProtocolType dbProtocol = null;
    private String dbVersion = null;
    private DetachedCriteria criteria = null;
    private ProjectionList projections = null;
    private String rowCountUniqueProperty = null;
    private Object IU = "";
    private SessionEntityBase sessionEntity = null;
    /*** SERVICE CLASS ***/
    private Class bClazz = null;
    private String sessionName = null;
    private WebApplicationContext wac = null;
    /*** ENTITY CLASS ***/
    private Class eClazz = null;
    private String eClazzAlia = null;
    private Object oEntity = null;
    private String dateFormat = null;
    @Resource
    private CliserDao dao;

    /**
     *
     * @throws CDCException
     */
    public void checkSession() throws CDCException {
        if ((requestWrapper.getSession() == null) || (requestWrapper.getSession().getAttribute(sessionName) == null)) {
            //throw new CDCException("lng.msg.error.sessionexpired");
            newSession(new SessionEntityBase("avillalobos"));
        }
        sessionEntity = (SessionEntityBase) requestWrapper.getSession().getAttribute(sessionName);
        IU = sessionEntity.getIU();
    }

    /**
     *
     * @return
     */
    public Object getIU() {
        return IU;
    }

    /**
     *
     * @param <TEntity>
     * @param eClazz
     */
    public <TEntity> void entity(Class<TEntity> eClazz) {
        this.eClazz = eClazz;
    }

    /**
     * 
     * @param <TEntity>
     * @param eClazz
     * @param eClazzAlia
     */
    public <TEntity> void entity(Class<TEntity> eClazz, String eClazzAlia) {
        entity(eClazz);
        this.eClazzAlia = eClazzAlia;
    }

    /**
     * 
     * @param <TBean>
     * @param bClazz
     */
    public <TBean> void service(Class<TBean> bClazz) {
        this.bClazz = bClazz;
    }

    /**
     * 
     * @param <TBean>
     * @param <TEntity>
     * @param bClazz
     * @param eClazz
     * @param eClazzAlia
     */
    public <TBean, TEntity> void service(Class<TBean> bClazz, Class<TEntity> eClazz, String eClazzAlia) {
        this.bClazz = bClazz;
        entity(eClazz, eClazzAlia);
    }

    /**
     * 
     * @param <TEntity>
     * @param eClazz
     * @param idClass
     * @param id
     * @return
     * @throws CDCException
     */
    public <TEntity> TEntity instance(Class<TEntity> eClazz, Class idClass, Serializable id) throws CDCException {
        TEntity entity = (id == null) ? null : dao.get(eClazz, id);
        if (entity != null) {
            return entity;
        } else {
            try {
                return eClazz.getConstructor(idClass).newInstance(id);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new CDCException(ex);
            }
        }
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param paramName
     * @throws CDCException
     * @deprecated
     */
    public void EQ(String propertyName, String paramName) throws CDCException {
        EQ(propertyName, paramName, false);
    }

    /**
     * Apply case-insensitive an "equal" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param ignoreCase
     */
    public void EQ(String propertyName, String paramName, boolean ignoreCase) throws CDCException {
        if (!TypeCast.isNullOrEmpy(paramName)) {
            EQ(ignoreCase, propertyName, form(paramName));
        }
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param value
     * @throws CDCException     
     */
    public void EQ(String propertyName, Object value) throws CDCException {
        EQ(false, propertyName, value);
    }

    /**
     * Apply case-insensitive an "equal" constraint to the named property when ignoreCase is true
     * @param ignoreCase
     * @param propertyName
     * @param value
     * @throws CDCException
     */
    public void EQ(boolean ignoreCase, String propertyName, Object value) throws CDCException {
        if (value != null) {
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            try {
                if (ignoreCase) {
                    criteria.add(Restrictions.eq(propertyName, value).ignoreCase());
                } else {
                    criteria.add(Restrictions.eq(propertyName, value));
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new CDCException(ex);
            }
        }
    }

    /**
     * 
     * @param ignoreCase
     * @param propertyName
     * @param otherPropertyName
     * @throws CDCException
     */
    public void EQ(boolean ignoreCase, String propertyName, String otherPropertyName) throws CDCException {

        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        try {
            if (ignoreCase) {
                criteria.add(Restrictions.eq(propertyName, otherPropertyName).ignoreCase());
            } else {
                criteria.add(Restrictions.eq(propertyName, otherPropertyName));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }

    }

    /**
     * Apply an "like" constraint to the named property
     * @param propertyName
     * @param paramName
     * @param define, define like conditions, Values[?%||%?||%?%]
     */
    public void LK(String propertyName, String paramName, String define) throws CDCException {
        LK(propertyName, paramName, define, false);
    }

    /**
     * Apply case-insensitive an "like" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param define, define like conditions, Values[?%||%?||%?%]
     * @param ignoreCase
     */
    public void LK(String propertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
        if (paramName != null) {
            String param = paramString(paramName);
            if (TypeCast.isNullOrEmpy(param)) {
                return;
            }
            if (TypeCast.isNullOrEmpy(define)
                    || ((define.indexOf("?%") == -1)
                    && (define.indexOf("%?") == -1)
                    && (define.indexOf("%?%") == -1))) {
                throw new CDCException("cliser.msg.error.criteria.like.baddefined");
            } else {
                param = define.replaceAll("\\?", param);
            }
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            if (ignoreCase) {
                criteria.add(Restrictions.like(propertyName, param).ignoreCase());
            } else {
                criteria.add(Restrictions.like(propertyName, param));
            }
        }
    }

    /**
     * Apply case-insensitive an "like or" constraint to the named property when ignoreCase is true
     * @param lhsPropertyName
     * @param rhsPropertyName
     * @param paramName
     * @param define
     * @param ignoreCase
     * @throws CDCException
     */
    public void LKo(String lhsPropertyName, String rhsPropertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
        if (paramName != null) {
            String param = paramString(paramName);
            if (TypeCast.isNullOrEmpy(param)) {
                return;
            }
            if (TypeCast.isNullOrEmpy(define)
                    || ((define.indexOf("?%") == -1)
                    && (define.indexOf("%?") == -1)
                    && (define.indexOf("%?%") == -1))) {
                throw new CDCException("cliser.msg.error.criteria.like.baddefined");
            } else {
                param = define.replaceAll("\\?", param);
            }
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            if (ignoreCase) {
                criteria.add(Restrictions.or(Restrictions.like(lhsPropertyName, param).ignoreCase(), Restrictions.like(rhsPropertyName, param).ignoreCase()));
            } else {
                criteria.add(Restrictions.or(Restrictions.like(lhsPropertyName, param), Restrictions.like(rhsPropertyName, param)));
            }
        }
    }

    public void LK(String propertyNameJoin, String propertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
        if (paramName != null) {
            String param = paramString(paramName);
            if (TypeCast.isNullOrEmpy(param)) {
                return;
            }
            if (TypeCast.isNullOrEmpy(define)
                    || ((define.indexOf("?%") == -1)
                    && (define.indexOf("%?") == -1)
                    && (define.indexOf("%?%") == -1))) {
                throw new CDCException("cliser.msg.error.criteria.like.baddefined");
            } else {
                param = define.replaceAll("\\?", param);
            }
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(this.eClazz);
            }
            if (ignoreCase) {
                criteria.createCriteria(propertyNameJoin).add(Restrictions.like(propertyName, param).ignoreCase());
            } else {
                criteria.createCriteria(propertyNameJoin).add(Restrictions.like(propertyName, param));
            }
        }
    }

    /**
     * Specifies joining to an entity based on a left outer join.
     * @param associationPath
     * @param alias
     */
    public <TEntity> void LJN(Class<TEntity> eClazz, String propertyName) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(this.eClazz);
        }



    }

    /**
     * Specifies joining to an entity based on a full join.
     * @param associationPath
     * @param alias
     */
    public void FJN(String associationPath, String alias) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(this.eClazz);
        }
        criteria.createCriteria(associationPath, alias, DetachedCriteria.FULL_JOIN);
    }

    /**
     * Specifies joining to an entity based on an inner join.
     * @param associationPath
     * @param alias
     */
    public void IJN(String associationPath, String alias) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(this.eClazz);
        }
        criteria.createCriteria(associationPath, alias, DetachedCriteria.INNER_JOIN);
    }

    /**
     * A grouping property value
     * @param propertyName
     * @throws CDCException
     */
    public void GBy(String propertyName) throws CDCException {
        if (projections == null) {
            projections = Projections.projectionList();
        }
        projections.add(Projections.groupProperty(propertyName));
    }

    /**
     * A projected property value
     * @param propertyName
     * @throws CDCException
     */
    public void PV(String propertyName) throws CDCException {
        if (projections == null) {
            projections = Projections.projectionList();
        }
        projections.add(Projections.property(propertyName));

    }

    /**
     * Ascending order
     * @param propertyName
     */
    public void OByAsc(String propertyName) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        criteria.addOrder(Order.asc(propertyName));
    }

    /**
     * Descending order
     * @param propertyName
     */
    public void OByDesc(String propertyName) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        criteria.addOrder(Order.desc(propertyName));
    }

    /**
     * Property for rowCount 
     * @param propertyName
     * @throws CDCException
     */
    public void RCD(String propertyName) throws CDCException {
        rowCountUniqueProperty = propertyName;
    }

    /**
     * 
     * @throws CDCException
     */
    public void find() throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, findByCriteria());
    }

    public void find(String hql) throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, getDao().find(hql));
    }

    public <TEntity> List<TEntity> rsFind() throws CDCException {
        return findByCriteria();
    }

    public <TEntity> List<TEntity> rsFind(String hql) throws CDCException {
        return getDao().find(hql);
    }

    public WebApplicationContext getWac() {
        return wac;
    }

    /**
     * 
     * @param <TEntity>
     * @param o
     * @param m
     * @param args
     * @return
     * @throws CDCException
     */
    private <TEntity> List<TEntity> invoke(Object o, String m, Object... args) throws CDCException {
        try {
            if (args != null) {
                Class[] parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }
                return (List<TEntity>) (o.getClass().getMethod(m, parameterTypes)).invoke(o, args);
            } else {
                return (List<TEntity>) (o.getClass().getMethod(m)).invoke(o);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param <TEntity>
     * @param m, Method name, params
     * @param params
     * @throws CDCException
     */
    public <TEntity> void write(String m, Object... params) throws CDCException {
        write(invoke(bean(), m, params));
    }

    /**
     *
     * @param <TEntity>
     * @param pojos
     * @throws CDCException
     */
    public <TEntity> void write(List<TEntity> pojos) throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, pojos);
    }

    /**
     *
     * @param <TBean>
     * @return
     * @throws CDCException
     */
    public <TBean> TBean bean() throws CDCException {      
         return  (TBean) wac.getBean(bClazz);
    }

    /**
     * 
     * @param <TBean>
     * @param name
     * @return
     * @throws CDCException
     */
    public <TBean> TBean bean(String name) throws CDCException {
        return (TBean) wac.getBean(name, bClazz);
    }

    /**
     * 
     * @param rowCount
     * @param minValue
     * @throws CDCException
     */
    private void tPagin(int rowCount, int minValue) throws CDCException {
        int start = ((integerValue("start") == null) || (integerValue("start").intValue() == 0)) ? minValue : integerValue("start").intValue();
        int limit = ((integerValue("limit") == null) || (integerValue("limit").intValue() == 0)) ? minValue : integerValue("limit").intValue();
        responseWrapper.setPage(start);
        responseWrapper.setPageSize(limit);
        responseWrapper.setRowCount(rowCount);
    }

    /**
     * 
     * @param <TEntity>
     * @return
     * @throws CDCException
     */
    private <TEntity> List<TEntity> findByCriteria() throws CDCException {

        if (getDao() == null) {
            throw new CDCException("cliser.msg.error.dao.notinitialized");
        }
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        if (projections != null) {
            criteria.setProjection(projections);
        }
        extra();
        return getDao().find(criteria, responseWrapper.getPage(), responseWrapper.getPageSize());
    }

    /**
     * 
     * @param <TEntity>
     * @param projection
     * @param query
     * @throws CDCException
     */
    public <TEntity> void find(StringBuilder projection, StringBuilder query) throws CDCException {
        if (getDao() == null) {
            throw new CDCException("cliser.msg.error.dao.notinitialized");
        }
        /*** ***/
        List<TEntity> md = getDao().find(new StringBuilder("SELECT COUNT(*) ").append(query));
        tPagin(TypeCast.toInt(md.get(0)), 0);
        query.insert(0, projection);
        query.insert(0, "SELECT row_.*,rownum rownum_ FROM (");
        query.append(") row_ WHERE ROWNUM<=").append(responseWrapper.getPage() + responseWrapper.getPageSize());
        query.insert(0, "SELECT * FROM (");
        query.append(") WHERE rownum_>").append(responseWrapper.getPage());
        responseWrapper.setDataJSON(eClazz, eClazzAlia, getDao().find(query));
    }

    /**
     * 
     * @param <TEntity>
     * @throws CDCException
     */
    private <TEntity> void extra() throws CDCException {
        DetachedCriteria criteriaEx = (DetachedCriteria) SerializationHelper.clone(criteria);
        criteriaEx.setProjection(((rowCountUniqueProperty == null) ? Projections.rowCount() : Projections.countDistinct(rowCountUniqueProperty)));
        List<TEntity> md = getDao().find(criteriaEx);
        tPagin(TypeCast.toInt(md.get(0)), 0);
    }

    /**
     * 
     * @param field
     */
    public void exclude(String field) {
        responseWrapper.addExclude(field);
    }

    /**
     * 
     * @param field
     */
    public void include(String field) {
        responseWrapper.addInclude(field);
    }

    /**
     * 
     * @param field
     * @param alias
     */
    public void alias(String field, String alias) {
        responseWrapper.setAlias(field, alias);
    }

    /**
     *
     * @throws CDCException
     */
    public void addParam() throws CDCException {
        if (oEntity == null) {
            try {
                oEntity = eClazz.newInstance();
            } catch (Exception ex) {
                throw new CDCException(ex);
            }
        } else {
            TypeCast.getMethod(eClazz, dbVersion, eClazz);
        }
    }

    /**
     * 
     * @param <TEntity>
     * @param entity
     * @return
     */
    public <TEntity> TEntity save(TEntity entity) {
        return getDao().persist(entity);
    }

    /**
     * 
     * @param <TEntity>
     * @param id
     * @param eClazzJoinTable
     * @param entityJoinTable
     * @return
     * @throws CDCException
     */
    public <TEntity> TEntity save(Serializable id, Class<TEntity> eClazzJoinTable, TEntity entityJoinTable) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.save.entityisnull");
        }
        TEntity entity = (TEntity) getDao().get(eClazz, id);
        Object value = TypeCast.GN(entity, "get" + eClazzJoinTable.getSimpleName() + "List");
        if (value == null) {
            value = new ArrayList<TEntity>();
            ((List) value).add(entityJoinTable);
        } else {
            ((List) value).add(entityJoinTable);
        }
        return getDao().persist(entity);
    }

    /**
     * 
     * @param id
     */
    public void remove(Serializable id) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.save.entityisnull");
        }
        getDao().delete(getDao().get(eClazz, id));
    }

    /**
     * 
     * @param <TEntity>
     * @param id
     * @param eClazzJoinTable
     * @param idJoinTable
     * @throws CDCException
     */
    public <TEntity> void remove(Serializable id, Class<TEntity> eClazzJoinTable, Serializable idJoinTable) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.save.entityisnull");
        }
        TEntity entity = (TEntity) getDao().get(eClazz, id);
        Object value = TypeCast.GN(entity, "get" + eClazzJoinTable.getSimpleName() + "List");
        if (value != null) {
            TEntity entityJoinTable = (TEntity) getDao().get(eClazzJoinTable, idJoinTable);
            if (value instanceof List) {
                log.info("Remove Join Table: " + ((List) value).remove(entityJoinTable));
                save(entity);
            }
        }
    }

    /**
     * 
     * @param context
     */
    /*   public void setContext(WebApplicationContext context) {
    this.context = context;
    }*/
    /**
     *
     * @param request
     * @throws CDCException
     */
    public void setResquestWrapper(HttpServletRequest request) throws CDCException {
        requestWrapper = new RequestWrapper(request);
    }

    public HttpServletRequest getRequest() {
        return requestWrapper.getRequest();
    }

    /**
     * 
     * @param response
     */
    public void setResponseWrapper(HttpServletResponse response) throws CDCException {
        responseWrapper = new ResponseWrapper(response, stringValue("callback"));
    }

    /**
     *
     * @param dbProtocol
     */
    public void setDBProtocol(DBProtocolType dbProtocol) {
        this.dbProtocol = dbProtocol;
    }

    /**
     *
     * @param dbVersion
     */
    public void setDBVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    /**
     * 
     * @param sessionName
     */
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    /**
     * 
     * @param name
     * @return
     * @throws CDCException
     */
    public Object form(String name) throws CDCException {
        Object result = null;
        try {
            result = entry(name);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public String stringValue(String paramName) throws CDCException {
        return TypeCast.toString(form(paramName));
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Object value(String paramName) throws CDCException {
        return form(paramName);
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Short shortValue(String paramName) throws CDCException {
        return TypeCast.toShort(form(paramName));
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public BigInteger integerValue(String paramName) throws CDCException {
        return TypeCast.toBigInteger(form(paramName));
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public BigDecimal decimalValue(String paramName) throws CDCException {
        return TypeCast.toBigDecimal(form(paramName));
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Long longValue(String paramName) throws CDCException {
        return TypeCast.toLong(form(paramName));
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Date dateValue(String paramName) throws CDCException {
        return TypeCast.toDate(form(paramName), getDateFormat());
    }

    /**
     * 
     * @param paramName
     * @param dateFormat
     * @return
     * @throws CDCException
     */
    public Date dateValue(String paramName, String dateFormat) throws CDCException {
        return TypeCast.toDate(form(paramName), dateFormat);
    }

    /**
     * 
     * @param name
     * @return
     * @throws CDCException
     */
    private String paramString(String name) throws CDCException {
        return TypeCast.toString(form(name));
    }

    /**
     * 
     * @param name
     * @param replace
     * @return
     * @throws CDCException
     */
    private String paramString(String name, String replace) throws CDCException {
        String rs = TypeCast.toString(form(name));
        return (TypeCast.isNullOrEmpy(rs)) ? rs : replace;
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public List<Object> listValue(String paramName) throws CDCException {
        List<Object> rs = new ArrayList<Object>();
        StringTokenizer idx = new StringTokenizer(stringValue(paramName), "|,|");
        while (idx.hasMoreTokens()) {
            rs.add(idx.nextToken());
        }
        return rs;
    }

    /**
     * 
     * @param name
     * @return
     */
    private Object entry(String name) {
        return requestWrapper.getEntry().get(name);
    }

    /**
     * 
     * @param sessionEntity
     */
    public void newSession(Object sessionEntity) {
        requestWrapper.getSession().setAttribute(sessionName, sessionEntity);
        IU = ((SessionEntityBase) sessionEntity).getIU();
    }

    /**
     *
     * @param ex
     */
    public void error(CDCException ex) {
        responseWrapper.setMessage(ex);
    }

    /**
     *
     * @param e
     */
    public void error(String e) {
        responseWrapper.setMessage(e, false);
    }

    /**
     * 
     * @param ex
     */
    public void error(Exception ex) {
        responseWrapper.setMessage(ex);
    }

    /**
     * 
     * @param information
     */
    public void information(String information) {
        responseWrapper.setMessage(information, true);
    }

    /**
     * 
     * @param information
     * @param delimiter
     */
    public void information(String information, String delimiter) {
        StringTokenizer st = new StringTokenizer(information, delimiter);
        while (st.hasMoreTokens()) {
            responseWrapper.setMessage(st.nextToken(), true);
        }
    }

    /**
     *
     * @param key
     * @param data
     */
    public void data(String data) {
        responseWrapper.setData(data);
    }

    /**
     * 
     * @throws CDCException
     */
    public void commit() throws CDCException {
        responseWrapper.commit();
    }

    /**
     * @return the dao
     */
    public CliserDao getDao() {
        return dao;
    }

    /**
     * @param dao the dao to set
     * @deprecated 
     */
    public void setccDao(CliserDao dao) {
        this.dao = dao;
    }

    /**
     * Web application context
     * @param wac
     */
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
        // wac.getBean(BL.class).getDao()
        //this.wac.getBean(this.getClass());
        dao = this.wac.getBean(BL.class).getDao();
    }

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        responseWrapper.setDateFormat(dateFormat);
        this.dateFormat = dateFormat;
    }
}
