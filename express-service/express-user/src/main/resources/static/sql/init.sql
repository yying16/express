drop
database if exists express;
create
database express;
use
express;

create table t_user
(
    account   varchar(32) not null comment '学号',
    password  varchar(32) not null comment '密码',
    username  varchar(32) not null comment '用户名',
    telephone varchar(32) not null comment '手机号码',
    email     varchar(32) not null comment '邮箱',
    primary key (account)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
create index index_account on t_user (account ASC);

insert into t_user(account, password, username, telephone, email)
values ('zhangsan', 'zs123456', '张三', '15915712354', 'zhangsan@163.com'),
       ('lisi', 'ls123456', '李四', '13430241235', 'lisi@qq.com'),
       ('wangwu', 'ww123456', '王五', '13645236589', 'wangwu@163.com'),
       ('chenliu', 'cl123456', '陈六', '13316397963', 'chenliu@163.com'),
       ('xuqi', 'xq123456', '许七', '13352679568', 'xuqi@163.com'),
       ('maba', 'mb123456', '马八', '13654879632', 'maba@163.com'),
       ('zhengjiu', 'zj123456', '郑九', '13912546983', 'zhengjiu@163.com'),
       ('huangshi', 'hs123456', '黄十', '15815632498', 'huangshi@163.com');

drop table if exists t_note;
CREATE TABLE t_note
(
    note_id     int auto_increment comment '帖子编号',
    promulgator varchar(32) comment '发布者',
    category    varchar(16) default '其他' comment '分类',
    title       varchar(50) not null comment '帖子标题',
    content     varchar(30) not null comment '帖子内容',
    photo       text        null comment '帖子图片',
    deleted     boolean     default false comment '删除标志',
    anonymous   boolean     default false comment '匿名标志',
    create_time datetime    default now() comment '创建时间',
    update_time datetime    default now() comment '更新时间',
    PRIMARY KEY (note_id),
    foreign key (promulgator) references t_user (account)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
    comment '帖子';
create index index_notes_id on t_note (note_id ASC);


CREATE TABLE t_photo
(
    photo_id     varchar(128) primary key,
    photo_source varchar(1024) not null,
    photo_name   varchar(1024) not null,
    photo_type   varchar(10)
);
create index index_photo_id on t_photo (photo_id ASC);

