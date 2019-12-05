- BaseMapper 接口两个 page 方法优化
- IService 以及 ServiceImpl 对应 page 方法优化,个别返回 collection 的方法修改为返回 list
- 逻辑删除字段的两个表示已删除和未删除的定义支持字符串 `"null"`
- 修复批量操作未清空二级缓存
- 批量操作异常转换为DataAccessException
- mybatis up 3.5.3, mybatis-spring up 2.0.3, jsqlparser up 3.1
- mapper 选装件包调整, chainWrapper 包调整
- 新增 ChainWrappers 工具类
- 新增 IdentifierGenerator 接口,支持自定义id生成

