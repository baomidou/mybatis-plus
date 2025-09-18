package com.baomidou.mybatisplus.code.sub;

import com.baomidou.mybatisplus.code.Overwrite;
import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * {@link org.apache.ibatis.scripting.defaults.DefaultParameterHandler}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class DefaultParameterHandler extends OverwriteFile {

    public DefaultParameterHandler() {
        addStep(i -> i
            .source("""
                import java.util.List;
                """)
            .target("""
                import com.baomidou.mybatisplus.annotation.IdType;
                import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
                import com.baomidou.mybatisplus.core.metadata.TableInfo;
                import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
                import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
                import com.baomidou.mybatisplus.core.toolkit.Constants;
                import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
                import com.baomidou.mybatisplus.core.toolkit.StringUtils;
                import org.apache.ibatis.logging.Log;
                import org.apache.ibatis.mapping.SqlCommandType;
                import org.apache.ibatis.type.SimpleTypeRegistry;
                import org.apache.ibatis.ognl.OgnlOps;
                import java.util.*;
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                private final Configuration configuration;
                """)
            .target("""
                private final SqlCommandType sqlCommandType;
                private final Log log;
                """)
        );
        addStep(i -> i
            .source("""
                this.boundSql = boundSql;
                """)
            .target("""
                this.sqlCommandType = mappedStatement.getSqlCommandType();
                this.log = mappedStatement.getStatementLog();
                processParameter();
                """)
        );
        addStep(i -> i
            .source("""
                @Override
                public Object getParameterObject() {
                    return parameterObject;
                }
                """)
            .target("""
                public void processParameter() {
                  /* 只处理插入或更新操作 */
                  if (parameterObject != null && !SimpleTypeRegistry.isSimpleType(parameterObject.getClass())) {
                      if (SqlCommandType.INSERT == this.sqlCommandType || SqlCommandType.UPDATE == this.sqlCommandType) {
                          extractParameters().forEach(this::process);
                      }
                  }
                }

                private void process(Object parameter) {
                  if (parameter != null) {
                      TableInfo tableInfo = null;
                      Object entity = parameter;
                      if (parameter instanceof Map) {
                          // 处理单参数使用注解标记的时候，尝试提取et来获取实体参数
                          Map<?, ?> map = (Map<?, ?>) parameter;
                          Object et = null;
                          if(map.containsKey(Constants.ENTITY)){
                              et = map.get(Constants.ENTITY);

                          } else if(map.containsKey(Constants.MP_FILL_ET)){
                              et = map.get(Constants.MP_FILL_ET);
                          }
                          if (et != null) {
                              entity = et;
                              tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
                          }
                      } else {
                          tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
                      }
                      if (tableInfo != null) {
                          //到这里就应该转换到实体参数对象了,因为填充和ID处理都是针对实体对象处理的,不用传递原参数对象下去.
                          MetaObject metaObject = this.configuration.newMetaObject(entity);
                          if (SqlCommandType.INSERT == this.sqlCommandType) {
                              populateKeys(tableInfo, metaObject, entity);
                              insertFill(metaObject, tableInfo);
                          } else {
                              updateFill(metaObject, tableInfo);
                          }
                      }
                  }
                }

                protected void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object entity) {
                  final IdType idType = tableInfo.getIdType();
                  final String keyProperty = tableInfo.getKeyProperty();
                  if (StringUtils.isNotBlank(keyProperty) && null != idType && idType.getKey() >= 3) {
                      final IdentifierGenerator identifierGenerator = GlobalConfigUtils.getGlobalConfig(this.configuration).getIdentifierGenerator();
                      Object idValue = metaObject.getValue(keyProperty);
                      if (identifierGenerator.assignId(idValue)) {
                          if (idType.getKey() == IdType.ASSIGN_ID.getKey()) {
                              Number id = identifierGenerator.nextId(entity);
                              metaObject.setValue(keyProperty, OgnlOps.convertValue(id, tableInfo.getKeyType()));
                          } else if (idType.getKey() == IdType.ASSIGN_UUID.getKey()) {
                              if(String.class.equals(tableInfo.getKeyType())) {
                                  metaObject.setValue(keyProperty, identifierGenerator.nextUUID(entity));
                              } else {
                                  log.warn("The current ID generation strategy does not support: " + tableInfo.getKeyType());
                              }
                          }
                      }
                  }
                }

                protected void insertFill(MetaObject metaObject, TableInfo tableInfo) {
                  GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
                      if (metaObjectHandler.openInsertFill() && metaObjectHandler.openInsertFill(mappedStatement) && tableInfo.isWithInsertFill()) {
                          metaObjectHandler.insertFill(metaObject);
                      }
                  });
                }

                protected void updateFill(MetaObject metaObject, TableInfo tableInfo) {
                  GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
                      if (metaObjectHandler.openUpdateFill() && metaObjectHandler.openUpdateFill(mappedStatement) && tableInfo.isWithUpdateFill()) {
                          metaObjectHandler.updateFill(metaObject);
                      }
                  });
                }

                /**
                * 处理正常批量插入逻辑
                * <p>
                * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
                * wrapCollection 实现 StrictMap 封装逻辑
                * </p>
                *
                * @return 集合参数
                * @deprecated 3.5.3.2
                */
                @SuppressWarnings({"rawtypes", "unchecked"})
                @Deprecated
                protected Collection<Object> getParameters(Object parameterObject) {
                  Collection<Object> parameters = null;
                  if (parameterObject instanceof Collection) {
                      parameters = (Collection) parameterObject;
                  } else if (ArrayUtils.isArray(parameterObject)) {
                      parameters = toCollection(parameterObject);
                  } else if (parameterObject instanceof Map) {
                      Map parameterMap = (Map) parameterObject;
                      // 约定 coll collection list array 这四个特殊key值处理批量.
                      // 尝试提取参数进行填充，如果是多参数时，在使用注解时，请注意使用collection，list，array进行声明
                      if (parameterMap.containsKey("collection")) {
                          return toCollection(parameterMap.get("collection"));
                      } else if (parameterMap.containsKey(Constants.COLL)) {
                          // 兼容逻辑删除对象填充，这里的集合字段后面重构的时候应该和原生保持一致，使用collection
                          parameters = toCollection(parameterMap.get(Constants.COLL));
                      } else if (parameterMap.containsKey(Constants.LIST)) {
                          parameters = toCollection(parameterMap.get(Constants.LIST));
                      } else if (parameterMap.containsKey(Constants.ARRAY)) {
                          parameters = toCollection(parameterMap.get(Constants.ARRAY));
                      }
                  }
                  return parameters;
                }

                /**
                * 提取特殊key值 (只支持外层参数,嵌套参数不考虑)
                * List<Map>虽然这种写法目前可以进去提取et,但不考虑再提取list等其他类型,只做简单参数提取
                *
                * @return 预期可能为填充参数值
                */
                @SuppressWarnings({"rawtypes", "unchecked"})
                private Collection<Object> extractParameters() {
                  if (parameterObject instanceof Collection) {
                      return (Collection) parameterObject;
                  } else if (ArrayUtils.isArray(parameterObject)) {
                      return toCollection(parameterObject);
                  } else if (parameterObject instanceof Map) {
                      Collection<Object> parameters = new ArrayList<>();
                      Map<String, Object> parameterMap = (Map) parameterObject;
                      Set<Object> objectSet = new HashSet<>();
                      parameterMap.forEach((k, v) -> {
                          if (objectSet.add(v)) {
                              Collection<Object> collection = toCollection(v);
                              parameters.addAll(collection);
                          }
                      });
                      return parameters;
                  } else {
                      return Collections.singleton(parameterObject);
                  }
                }

                @SuppressWarnings("unchecked")
                protected Collection<Object> toCollection(Object value) {
                  if (value == null) {
                      return Collections.emptyList();
                  }
                  if (ArrayUtils.isArray(value) && !value.getClass().getComponentType().isPrimitive()) {
                      return Arrays.asList((Object[]) value);
                  } else if (Collection.class.isAssignableFrom(value.getClass())) {
                      return (Collection<Object>) value;
                  } else {
                      return Collections.singletonList(value);
                  }
                }
                """)
        );
    }
}
