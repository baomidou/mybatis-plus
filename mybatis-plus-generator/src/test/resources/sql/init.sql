drop table if exists `t_simple`;
create table `t_simple`
(
    id          int IDENTITY primary key comment 'id',
    name        varchar(50) comment '姓名',
    age         int comment '年龄',
    delete_flag tinyint(1) comment '删除标识1',
    deleted tinyint(1) comment '删除标识2',
    version     bigint comment '版本',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    primary key (id)
) COMMENT = '测试表';
