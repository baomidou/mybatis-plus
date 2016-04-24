
# Mbatis-Plus 使用文档

> 	更多文档查看 /mybatis-plus/src/test/resources/wiki

# 插入

> 	插入一条（id 如果不传入会自动生成）

	long id = IdWorker.getId();
	int rlt = userMapper.insert(new User(id, "abc", 18, 0));


> 	插入一条记录（选择字段， null 字段不插入）
	int rlt = userMapper.insertSelective(new User("abc", 18));


> 	批量插入

	List<User> ul = new ArrayList<User>();
	//手动输入 ID
	ul.add(new User(11L, "1", 1, 0));
	ul.add(new User(12L, "2", 2, 1));
	ul.add(new User(13L, "3", 3, 1));
	ul.add(new User(14L, "delname", 4, 0));
	ul.add(new User(15L, "5", 5, 1));
	ul.add(new User(16L, "6", 6, 0));
	ul.add(new User(17L, "7", 7, 0));
	//使用 ID_WORKER 自动生成 ID
	ul.add(new User("8", 8, 1));
	ul.add(new User("9", 9, 1));
	rlt = userMapper.insertBatch(ul);


# 删除

> 	删除一条

	int rlt = userMapper.deleteById(id);


> 	批量删除

	List<Long> il = new ArrayList<Long>();
	il.add(16L);
	il.add(17L);
	int rlt = userMapper.deleteBatchIds(il);


> 	按照条件删除

	int rlt = userMapper.deleteSelective(new User(14L, "delname"));



# 修改

> 	修改

	int rlt = userMapper.updateById(new User(12L, "MybatisPlus"));


> 	根据 ID 选择修改

	int rlt = userMapper.updateSelectiveById(new User(12L, "MybatisPlus"));


> 	根据 whereEntity 条件，更新记录（支持 null 查询无条件更新）

	int rlt = userMapper.update(new User("55", 55, 5), new User(15L, "5"));


> 	根据 whereEntity 条件，选择更新记录（支持 null 查询无条件更新）

	int rlt = userMapper.updateSelective(new User("00"), new User(15L, "55"));


> 	根据ID 批量更新

	List<User> userList = new ArrayList<User>();
	userList.add(new User(11L, "updateBatchById-1", 1, 1));
	userList.add(new User(12L, "updateBatchById-2", 2, 2));
	userList.add(new User(13L, "updateBatchById-3", 3, 3));
	int rlt = userMapper.updateBatchById(userList);


# 查询

> 	根据ID查询

	User user = userMapper.selectById(12L);

> 	根据ID批量查询

	List<Long> idList = new ArrayList<Long>();
	idList.add(11L);
	idList.add(12L);
	List<User> ul1 = userMapper.selectBatchIds(idList);

> 	根据条件查询

	User userOne = userMapper.selectOne(new User("MybatisPlus"));

> 	根据条件查询总记录数（支持 null 查询无条件查询）

	int count = userMapper.selectCount(null);

> 	查询列表 id 排序

	List<User> ul2 = userMapper.selectList(new EntityWrapper<User>(null, "id DESC"));

> 	翻页查询 id 排序

	Page<User> page = new Page<User>(1, 2);
	EntityWrapper<User> ew = new EntityWrapper<User>(new User(1), "id DESC");
	List<User> paginList = userMapper.selectPage(page, ew);
	page.setRecords(paginList);
	for ( int i = 0 ; i < page.getRecords().size() ; i++ ) {
		print(page.getRecords().get(i));
	}

