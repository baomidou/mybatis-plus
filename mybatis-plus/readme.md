
# Mbatis-Plus 使用文档

> 	更多文档查看 /mybatis-plus/src/test/resources/wiki

# 插入

> 	插入一条（id 如果不传入会自动生成）
	```
	long id = IdWorker.getId();
	int rlt = userMapper.insert(new User(id, "abc", 18, 0));
	```

> 	批量插入
	```
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
	```

# 删除

> 	删除一条
	```
	int rlt = userMapper.deleteById(id);
	```

> 	批量删除
	```
	List<Object> il = new ArrayList<Object>();
	il.add(16L);
	il.add(17L);
	int rlt = userMapper.deleteBatchIds(il);
	```

> 	按照条件删除
	```
	int rlt = userMapper.deleteSelective(new User(14L, "delname"));
	```


# 修改

> 	修改
	```
	int rlt = userMapper.updateById(new User(12L, "MybatisPlus"));
	```



# 查询

> 	根据ID查询
	```
	User user = userMapper.selectById(12L);
	```

> 	根据ID批量查询
	```
	List<Object> idList = new ArrayList<Object>();
	idList.add(11L);
	idList.add(12L);
	List<User> ul1 = userMapper.selectBatchIds(idList);
	```

> 	根据条件查询
	```
	User userOne = userMapper.selectOne(new User("MybatisPlus"));
	```

> 	查询列表 id 排序
	```
	List<User> ul2 = userMapper.selectList(RowBounds.DEFAULT, new EntityWrapper<User>(null, "id DESC"));
	```

> 	翻页查询 id 排序
	```
	Page<User> page = new Page<User>(1, 2);
	EntityWrapper<User> ew = new EntityWrapper<User>(new User(1), "id DESC");
	List<User> paginList = userMapper.selectList(page, ew);
	page.setRecords(paginList);
	for ( int i = 0 ; i < page.getRecords().size() ; i++ ) {
		print(page.getRecords().get(i));
	}
	```

