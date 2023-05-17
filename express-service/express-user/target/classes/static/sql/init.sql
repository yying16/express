drop
database if exists express;
create
database express;
use
express;

create table t_user
(
    account   varchar(32) not null comment 'ѧ��',
    password  varchar(32) not null comment '����',
    username  varchar(32) not null comment '�û���',
    telephone varchar(32) not null comment '�ֻ�����',
    email     varchar(32) not null comment '����',
    primary key (account)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
create index index_account on t_user (account ASC);

insert into t_user(account, password, username, telephone, email)
values ('zhangsan', 'zs123456', '����', '15915712354', 'zhangsan@163.com'),
       ('lisi', 'ls123456', '����', '13430241235', 'lisi@qq.com'),
       ('wangwu', 'ww123456', '����', '13645236589', 'wangwu@163.com'),
       ('chenliu', 'cl123456', '����', '13316397963', 'chenliu@163.com'),
       ('xuqi', 'xq123456', '����', '13352679568', 'xuqi@163.com'),
       ('maba', 'mb123456', '���', '13654879632', 'maba@163.com'),
       ('zhengjiu', 'zj123456', '֣��', '13912546983', 'zhengjiu@163.com'),
       ('huangshi', 'hs123456', '��ʮ', '15815632498', 'huangshi@163.com');

drop table if exists t_note;
CREATE TABLE t_note
(
    note_id     int auto_increment comment '���ӱ��',
    promulgator varchar(32) comment '������',
    category    varchar(16) default '����' comment '����',
    title       varchar(50) not null comment '���ӱ���',
    content     varchar(30) not null comment '��������',
    photo       text        null comment '����ͼƬ',
    deleted     boolean     default false comment 'ɾ����־',
    anonymous   boolean     default false comment '������־',
    create_time datetime    default now() comment '����ʱ��',
    update_time datetime    default now() comment '����ʱ��',
    PRIMARY KEY (note_id),
    foreign key (promulgator) references t_user (account)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
    comment '����';
create index index_notes_id on t_note (note_id ASC);


CREATE TABLE t_photo
(
    photo_id     varchar(128) primary key,
    photo_source varchar(1024) not null,
    photo_name   varchar(1024) not null,
    photo_type   varchar(10)
);
create index index_photo_id on t_photo (photo_id ASC);

