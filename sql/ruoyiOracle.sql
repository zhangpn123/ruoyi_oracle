-- ----------------------------
-- 1、部门表
-- ----------------------------
create sequence seq_sys_dept
 increment by 1
 start with 200
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_dept;
create table sys_dept (
  dept_id            varchar2(100)      not null ,
  parent_id         varchar2(200)     default 0   ,
  ancestors         varchar2(500)     default ''  ,
  dept_name         varchar2(200)     default '',
  dept_fulname    varchar2(200)     default '' ,
  order_num         number(10,0)          default 0   ,
  leader            varchar2(20)     default null,
  phone             varchar2(11)     default null,
  email             varchar2(50)     default null,
  status            char(1)         default '0' ,
  del_flag          char(1)         default '0' ,
  create_by         varchar2(64)     default ''  ,
  create_time 	    date                    ,
  update_by         varchar2(64)     default ''  ,
  update_time       date ,
  constraint pk_sys_dept primary key (dept_id)
);


-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------
insert into sys_dept values(100,  0,   '0',          '若依科技',   0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(101,  100, '0,100',      '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(102,  100, '0,100',      '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(103,  101, '0,100,101',  '研发部门',   1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(104,  101, '0,100,101',  '市场部门',   2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(105,  101, '0,100,101',  '测试部门',   3, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(106,  101, '0,100,101',  '财务部门',   4, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(107,  101, '0,100,101',  '运维部门',   5, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(108,  102, '0,100,102',  '市场部门',   1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
insert into sys_dept values(109,  102, '0,100,102',  '财务部门',   2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'));
commit;

-- ----------------------------
-- 2、用户信息表
-- ----------------------------
create sequence seq_sys_user
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;


drop table sys_user;
create table sys_user (
  user_id           number(20,0)      not null ,
  dept_id           number(20,0)      default null  ,
  login_name        varchar2(30)     not null       ,
  user_name         varchar2(30)     default ''     ,
  user_type         varchar2(2)      default '00'   ,
  email             varchar2(50)     default ''     ,
  phonenumber       varchar2(11)     default ''     ,
  sex               char(1)         default '0'     ,
  avatar            varchar2(100)    default ''     ,
  password          varchar2(50)     default ''     ,
  salt              varchar2(20)     default ''     ,
  status            char(1)         default '0'     ,
  del_flag          char(1)         default '0'     ,
  login_ip          varchar2(50)     default ''     ,
  login_date        date                            ,
  create_by         varchar2(64)     default ''     ,
  create_time       date                            ,
  update_by         varchar2(64)     default ''     ,
  update_time       date                            ,
  remark            varchar2(500)    default null
); 
alter table sys_user add constraint pk_sys_user primary key (user_id);

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1,  103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '29c67a30398638269fe600f73a054934', '111111', '0', '0', '127.0.0.1', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '管理员');
insert into sys_user values(2,  105, 'ry',    '若依', '00', 'ry@qq.com',  '15666666666', '1', '', '8e6d98b90472783cc73c17047ddccf36', '222222', '0', '0', '127.0.0.1', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '测试员');
commit;

-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
create sequence seq_sys_post
 increment by 1
 start with 10
 nomaxvalue
 nominvalue
 cache 20;

drop table sys_post;
create table sys_post
(
  post_id       number(20,0)      not null ,
  post_code     varchar2(64)     not null   ,
  post_name     varchar2(50)     not null    ,
  post_sort     number(10,0)          not null  ,
  status        char(1)         not null        ,
  create_by     varchar2(64)     default ''     ,
  create_time   date                            ,
  update_by     varchar2(64)     default ''		,
  update_time   date                            ,
  remark        varchar2(500)    default null  
);
alter table sys_post add constraint pk_sys_post primary key (post_id);

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, 'ceo',  '董事长',    1, '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_post values(2, 'se',   '项目经理',  2, '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_post values(3, 'hr',   '人力资源',  3, '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_post values(4, 'user', '普通员工',  4, '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
commit;

-- ----------------------------
-- 4、角色信息表
-- ----------------------------
create sequence seq_sys_role
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;

drop table sys_role;
create table sys_role (
  role_id           number(20,0)      not null ,
  role_name         varchar2(30)     not null      ,
  role_key          varchar2(100)    not null      ,
  role_sort         number(10,0)          not null ,
  data_scope        char(1)         default '1'    ,
  status            char(1)         not null       ,
  del_flag          char(1)         default '0'    ,
  create_by         varchar2(64)     default ''    ,
  create_time       date                           ,
  update_by         varchar2(64)     default ''    ,
  update_time       date                           ,
  remark            varchar2(500)    default null 
);
alter table sys_role add constraint pk_sys_role primary key (role_id);


-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values('1', '超级管理员', 'admin',  1, 1, '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '超级管理员');
insert into sys_role values('2', '普通角色',   'common', 2, 2, '0', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '普通角色');
commit;

-- ----------------------------
-- 5、菜单权限表
-- ----------------------------

create sequence seq_sys_menu
 increment by 1
 start with 2000
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_menu;
create table sys_menu (
  menu_id           number(20,0)      not null 			,
  menu_name         varchar2(50)     not null           ,
  parent_id         number(20,0)      default 0         ,
  order_num         number(10,0)          default 0     ,
  url               varchar2(200)    default '#'        ,
  target            varchar2(20)     default ''         ,
  menu_type         char(1)         default ''          ,
  visible           char(1)         default 0           ,
  perms             varchar2(100)    default null       ,
  icon              varchar2(100)    default '#'        ,
  create_by         varchar2(64)     default ''         ,
  create_time       date                                ,
  update_by         varchar2(64)     default ''         ,
  update_time       date                                ,
  remark            varchar2(500)    default ''         
);
alter table sys_menu add constraint pk_sys_menu primary key (menu_id);

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '1', '#',                '',          'M', '0', '', 'fa fa-gear',           'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统管理目录');
insert into sys_menu values('2', '系统监控', '0', '2', '#',                '',          'M', '0', '', 'fa fa-video-camera',   'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统监控目录');
insert into sys_menu values('3', '系统工具', '0', '3', '#',                '',          'M', '0', '', 'fa fa-bars',           'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统工具目录');
insert into sys_menu values('4', '若依官网', '0', '4', 'http://ruoyi.vip', 'menuBlank', 'C', '0', '', 'fa fa-location-arrow', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '若依官网地址');
-- 二级菜单
insert into sys_menu values('100',  '用户管理', '1', '1', '/system/user',          '', 'C', '0', 'system:user:view',         'fa fa-user-o',          'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '用户管理菜单');
insert into sys_menu values('101',  '角色管理', '1', '2', '/system/role',          '', 'C', '0', 'system:role:view',         'fa fa-user-secret',     'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '角色管理菜单');
insert into sys_menu values('102',  '菜单管理', '1', '3', '/system/menu',          '', 'C', '0', 'system:menu:view',         'fa fa-th-list',         'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '菜单管理菜单');
insert into sys_menu values('103',  '部门管理', '1', '4', '/system/dept',          '', 'C', '0', 'system:dept:view',         'fa fa-outdent',         'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '部门管理菜单');
insert into sys_menu values('104',  '岗位管理', '1', '5', '/system/post',          '', 'C', '0', 'system:post:view',         'fa fa-address-card-o',  'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '岗位管理菜单');
insert into sys_menu values('105',  '字典管理', '1', '6', '/system/dict',          '', 'C', '0', 'system:dict:view',         'fa fa-bookmark-o',      'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '字典管理菜单');
insert into sys_menu values('106',  '参数设置', '1', '7', '/system/config',        '', 'C', '0', 'system:config:view',       'fa fa-sun-o',           'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '参数设置菜单');
insert into sys_menu values('107',  '通知公告', '1', '8', '/system/notice',        '', 'C', '0', 'system:notice:view',       'fa fa-bullhorn',        'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '通知公告菜单');
insert into sys_menu values('108',  '日志管理', '1', '9', '#',                     '', 'M', '0', '',                         'fa fa-pencil-square-o', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '日志管理菜单');
insert into sys_menu values('109',  '在线用户', '2', '1', '/monitor/online',       '', 'C', '0', 'monitor:online:view',      'fa fa-user-circle',     'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '在线用户菜单');
insert into sys_menu values('110',  '定时任务', '2', '2', '/monitor/job',          '', 'C', '0', 'monitor:job:view',         'fa fa-tasks',           'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '定时任务菜单');
insert into sys_menu values('111',  '数据监控', '2', '3', '/monitor/data',         '', 'C', '0', 'monitor:data:view',        'fa fa-bug',             'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '数据监控菜单');
insert into sys_menu values('112',  '服务监控', '2', '3', '/monitor/server',       '', 'C', '0', 'monitor:server:view',      'fa fa-server',          'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '服务监控菜单');
insert into sys_menu values('113',  '表单构建', '3', '1', '/tool/build',           '', 'C', '0', 'tool:build:view',          'fa fa-wpforms',         'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '表单构建菜单');
insert into sys_menu values('114',  '代码生成', '3', '2', '/tool/gen',             '', 'C', '0', 'tool:gen:view',            'fa fa-code',            'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '代码生成菜单');
insert into sys_menu values('115',  '系统接口', '3', '3', '/tool/swagger',         '', 'C', '0', 'tool:swagger:view',        'fa fa-gg',              'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统接口菜单');
-- 三级菜单
insert into sys_menu values('500',  '操作日志', '108', '1', '/monitor/operlog',    '', 'C', '0', 'monitor:operlog:view',     'fa fa-address-book',    'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '操作日志菜单');
insert into sys_menu values('501',  '登录日志', '108', '2', '/monitor/logininfor', '', 'C', '0', 'monitor:logininfor:view',  'fa fa-file-image-o',    'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '登录日志菜单');
-- 用户管理按钮
insert into sys_menu values('1000', '用户查询', '100', '1',  '#', '',  'F', '0', 'system:user:list',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1001', '用户新增', '100', '2',  '#', '',  'F', '0', 'system:user:add',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1002', '用户修改', '100', '3',  '#', '',  'F', '0', 'system:user:edit',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1003', '用户删除', '100', '4',  '#', '',  'F', '0', 'system:user:remove',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1004', '用户导出', '100', '5',  '#', '',  'F', '0', 'system:user:export',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1005', '用户导入', '100', '6',  '#', '',  'F', '0', 'system:user:import',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1006', '重置密码', '100', '7',  '#', '',  'F', '0', 'system:user:resetPwd',    '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 角色管理按钮
insert into sys_menu values('1007', '角色查询', '101', '1',  '#', '',  'F', '0', 'system:role:list',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1008', '角色新增', '101', '2',  '#', '',  'F', '0', 'system:role:add',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1009', '角色修改', '101', '3',  '#', '',  'F', '0', 'system:role:edit',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1010', '角色删除', '101', '4',  '#', '',  'F', '0', 'system:role:remove',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1011', '角色导出', '101', '5',  '#', '',  'F', '0', 'system:role:export',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 菜单管理按钮
insert into sys_menu values('1012', '菜单查询', '102', '1',  '#', '',  'F', '0', 'system:menu:list',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1013', '菜单新增', '102', '2',  '#', '',  'F', '0', 'system:menu:add',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1014', '菜单修改', '102', '3',  '#', '',  'F', '0', 'system:menu:edit',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1015', '菜单删除', '102', '4',  '#', '',  'F', '0', 'system:menu:remove',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 部门管理按钮
insert into sys_menu values('1016', '部门查询', '103', '1',  '#', '',  'F', '0', 'system:dept:list',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1017', '部门新增', '103', '2',  '#', '',  'F', '0', 'system:dept:add',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1018', '部门修改', '103', '3',  '#', '',  'F', '0', 'system:dept:edit',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1019', '部门删除', '103', '4',  '#', '',  'F', '0', 'system:dept:remove',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 岗位管理按钮
insert into sys_menu values('1020', '岗位查询', '104', '1',  '#', '',  'F', '0', 'system:post:list',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1021', '岗位新增', '104', '2',  '#', '',  'F', '0', 'system:post:add',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1022', '岗位修改', '104', '3',  '#', '',  'F', '0', 'system:post:edit',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1023', '岗位删除', '104', '4',  '#', '',  'F', '0', 'system:post:remove',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1024', '岗位导出', '104', '5',  '#', '',  'F', '0', 'system:post:export',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 字典管理按钮
insert into sys_menu values('1025', '字典查询', '105', '1', '#', '',  'F', '0', 'system:dict:list',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1026', '字典新增', '105', '2', '#', '',  'F', '0', 'system:dict:add',          '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1027', '字典修改', '105', '3', '#', '',  'F', '0', 'system:dict:edit',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1028', '字典删除', '105', '4', '#', '',  'F', '0', 'system:dict:remove',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1029', '字典导出', '105', '5', '#', '',  'F', '0', 'system:dict:export',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 参数设置按钮
insert into sys_menu values('1030', '参数查询', '106', '1', '#', '',  'F', '0', 'system:config:list',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1031', '参数新增', '106', '2', '#', '',  'F', '0', 'system:config:add',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1032', '参数修改', '106', '3', '#', '',  'F', '0', 'system:config:edit',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1033', '参数删除', '106', '4', '#', '',  'F', '0', 'system:config:remove',    '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1034', '参数导出', '106', '5', '#', '',  'F', '0', 'system:config:export',    '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 通知公告按钮
insert into sys_menu values('1035', '公告查询', '107', '1', '#', '',  'F', '0', 'system:notice:list',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1036', '公告新增', '107', '2', '#', '',  'F', '0', 'system:notice:add',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1037', '公告修改', '107', '3', '#', '',  'F', '0', 'system:notice:edit',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1038', '公告删除', '107', '4', '#', '',  'F', '0', 'system:notice:remove',    '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 操作日志按钮
insert into sys_menu values('1039', '操作查询', '500', '1', '#', '',  'F', '0', 'monitor:operlog:list',    '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1040', '操作删除', '500', '2', '#', '',  'F', '0', 'monitor:operlog:remove',  '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1041', '详细信息', '500', '3', '#', '',  'F', '0', 'monitor:operlog:detail',  '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1042', '日志导出', '500', '4', '#', '',  'F', '0', 'monitor:operlog:export',  '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 登录日志按钮
insert into sys_menu values('1043', '登录查询', '501', '1', '#', '',  'F', '0', 'monitor:logininfor:list',         '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1044', '登录删除', '501', '2', '#', '',  'F', '0', 'monitor:logininfor:remove',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1045', '日志导出', '501', '3', '#', '',  'F', '0', 'monitor:logininfor:export',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1046', '账户解锁', '501', '4', '#', '',  'F', '0', 'monitor:logininfor:unlock',       '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 在线用户按钮
insert into sys_menu values('1047', '在线查询', '109', '1', '#', '',  'F', '0', 'monitor:online:list',             '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1048', '批量强退', '109', '2', '#', '',  'F', '0', 'monitor:online:batchForceLogout', '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1049', '单条强退', '109', '3', '#', '',  'F', '0', 'monitor:online:forceLogout',      '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 定时任务按钮
insert into sys_menu values('1050', '任务查询', '110', '1', '#', '',  'F', '0', 'monitor:job:list',                '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1051', '任务新增', '110', '2', '#', '',  'F', '0', 'monitor:job:add',                 '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1052', '任务修改', '110', '3', '#', '',  'F', '0', 'monitor:job:edit',                '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1053', '任务删除', '110', '4', '#', '',  'F', '0', 'monitor:job:remove',              '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1054', '状态修改', '110', '5', '#', '',  'F', '0', 'monitor:job:changeStatus',        '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1055', '任务详细', '110', '6', '#', '',  'F', '0', 'monitor:job:detail',              '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1056', '任务导出', '110', '7', '#', '',  'F', '0', 'monitor:job:export',              '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
-- 代码生成按钮
insert into sys_menu values('1057', '生成查询', '114', '1', '#', '',  'F', '0', 'tool:gen:list',     '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1058', '生成修改', '114', '2', '#', '',  'F', '0', 'tool:gen:edit',     '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1059', '生成删除', '114', '3', '#', '',  'F', '0', 'tool:gen:remove',   '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1060', '预览代码', '114', '4', '#', '',  'F', '0', 'tool:gen:preview',  '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_menu values('1061', '生成代码', '114', '5', '#', '',  'F', '0', 'tool:gen:code',     '#', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
commit;

-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
drop table sys_user_role;
create table sys_user_role (
  user_id   number(20,0) not null ,
  role_id   number(20,0) not null 
);
alter table sys_user_role add constraint pk_sys_user_role primary key (user_id,role_id);

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('2', '2');
commit;

-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
drop table sys_role_menu;
create table sys_role_menu (
  role_id   number(20,0) not null,
  menu_id   number(20,0) not null 
);
alter table sys_role_menu add constraint pk_sys_role_menu primary key (role_id,menu_id);

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '4');
insert into sys_role_menu values ('2', '100');
insert into sys_role_menu values ('2', '101');
insert into sys_role_menu values ('2', '102');
insert into sys_role_menu values ('2', '103');
insert into sys_role_menu values ('2', '104');
insert into sys_role_menu values ('2', '105');
insert into sys_role_menu values ('2', '106');
insert into sys_role_menu values ('2', '107');
insert into sys_role_menu values ('2', '108');
insert into sys_role_menu values ('2', '109');
insert into sys_role_menu values ('2', '110');
insert into sys_role_menu values ('2', '111');
insert into sys_role_menu values ('2', '112');
insert into sys_role_menu values ('2', '113');
insert into sys_role_menu values ('2', '114');
insert into sys_role_menu values ('2', '115');
insert into sys_role_menu values ('2', '500');
insert into sys_role_menu values ('2', '501');
insert into sys_role_menu values ('2', '1000');
insert into sys_role_menu values ('2', '1001');
insert into sys_role_menu values ('2', '1002');
insert into sys_role_menu values ('2', '1003');
insert into sys_role_menu values ('2', '1004');
insert into sys_role_menu values ('2', '1005');
insert into sys_role_menu values ('2', '1006');
insert into sys_role_menu values ('2', '1007');
insert into sys_role_menu values ('2', '1008');
insert into sys_role_menu values ('2', '1009');
insert into sys_role_menu values ('2', '1010');
insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
insert into sys_role_menu values ('2', '1016');
insert into sys_role_menu values ('2', '1017');
insert into sys_role_menu values ('2', '1018');
insert into sys_role_menu values ('2', '1019');
insert into sys_role_menu values ('2', '1020');
insert into sys_role_menu values ('2', '1021');
insert into sys_role_menu values ('2', '1022');
insert into sys_role_menu values ('2', '1023');
insert into sys_role_menu values ('2', '1024');
insert into sys_role_menu values ('2', '1025');
insert into sys_role_menu values ('2', '1026');
insert into sys_role_menu values ('2', '1027');
insert into sys_role_menu values ('2', '1028');
insert into sys_role_menu values ('2', '1029');
insert into sys_role_menu values ('2', '1030');
insert into sys_role_menu values ('2', '1031');
insert into sys_role_menu values ('2', '1032');
insert into sys_role_menu values ('2', '1033');
insert into sys_role_menu values ('2', '1034');
insert into sys_role_menu values ('2', '1035');
insert into sys_role_menu values ('2', '1036');
insert into sys_role_menu values ('2', '1037');
insert into sys_role_menu values ('2', '1038');
insert into sys_role_menu values ('2', '1039');
insert into sys_role_menu values ('2', '1040');
insert into sys_role_menu values ('2', '1041');
insert into sys_role_menu values ('2', '1042');
insert into sys_role_menu values ('2', '1043');
insert into sys_role_menu values ('2', '1044');
insert into sys_role_menu values ('2', '1045');
insert into sys_role_menu values ('2', '1046');
insert into sys_role_menu values ('2', '1047');
insert into sys_role_menu values ('2', '1048');
insert into sys_role_menu values ('2', '1049');
insert into sys_role_menu values ('2', '1050');
insert into sys_role_menu values ('2', '1051');
insert into sys_role_menu values ('2', '1052');
insert into sys_role_menu values ('2', '1053');
insert into sys_role_menu values ('2', '1054');
insert into sys_role_menu values ('2', '1055');
insert into sys_role_menu values ('2', '1056');
insert into sys_role_menu values ('2', '1057');
insert into sys_role_menu values ('2', '1058');
insert into sys_role_menu values ('2', '1059');
insert into sys_role_menu values ('2', '1060');
insert into sys_role_menu values ('2', '1061');
commit;
-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table sys_role_dept;
create table sys_role_dept (
  role_id   number(20,0) not null,
  dept_id   number(20,0) not null
);
alter table sys_role_dept add constraint pk_sys_role_dept primary key (role_id,dept_id);

-- ----------------------------
-- 初始化-角色和部门关联表数据
-- ----------------------------
insert into sys_role_dept values ('2', '100');
insert into sys_role_dept values ('2', '101');
insert into sys_role_dept values ('2', '105');
commit;

-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
drop table sys_user_post;
create table sys_user_post
(
  user_id   number(20,0) not null ,
  post_id   number(20,0) not null  
);
alter table sys_user_post add constraint pk_sys_user_post primary key (user_id,post_id);

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');
insert into sys_user_post values ('2', '2');
commit;

-- ----------------------------
-- 10、操作日志记录
-- ----------------------------

create sequence seq_sys_oper_log
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_oper_log;
create table sys_oper_log (
  oper_id           number(20,0)      not null ,
  title             varchar2(50)     default '' ,
  business_type     number(10,0)          default 0   ,
  method            varchar2(100)    default '' ,
  request_method    varchar2(10)     default '' ,
  operator_type     number(10,0)         default 0   ,
  oper_name         varchar2(50)     default '' ,
  dept_name         varchar2(50)     default '' ,
  oper_url          varchar2(255)    default '' ,
  oper_ip           varchar2(50)     default '' ,
  oper_location     varchar2(255)    default '' ,
  oper_param        varchar2(2000)   default '' ,
  json_result       varchar2(2000)   default '' ,
  status            number(10,0)         default 0   ,
  error_msg         varchar2(2000)   default '' ,
  oper_time         date                        
);
alter table sys_oper_log add constraint pk_sys_oper_log primary key (oper_id);
commit;

-- ----------------------------
-- 11、字典类型表
-- ----------------------------
create sequence seq_sys_dict_type
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;

drop table sys_dict_type;
create table sys_dict_type
(
  dict_id          number(20,0)      not null    ,
  dict_name        varchar2(100)    default ''   ,
  dict_type        varchar2(100)    default ''   ,
  status           char(1)         default '0'   ,
  create_by        varchar2(64)     default ''   ,
  create_time      date                          ,
  update_by        varchar2(64)     default ''   ,
  update_time      date                          ,
  remark           varchar2(500)    default null 
);

alter table sys_dict_type add constraint pk_sys_dict_type primary key (dict_id);
create unique index sys_dict_type_index1 on sys_dict_type (dict_type);


insert into sys_dict_type values(1,  '用户性别', 'sys_user_sex',        '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '用户性别列表');
insert into sys_dict_type values(2,  '菜单状态', 'sys_show_hide',       '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '菜单状态列表');
insert into sys_dict_type values(3,  '系统开关', 'sys_normal_disable',  '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统开关列表');
insert into sys_dict_type values(4,  '任务状态', 'sys_job_status',      '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '任务状态列表');
insert into sys_dict_type values(5,  '任务分组', 'sys_job_group',       '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '任务分组列表');
insert into sys_dict_type values(6,  '系统是否', 'sys_yes_no',          '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统是否列表');
insert into sys_dict_type values(7,  '通知类型', 'sys_notice_type',     '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '通知类型列表');
insert into sys_dict_type values(8,  '通知状态', 'sys_notice_status',   '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '通知状态列表');
insert into sys_dict_type values(9,  '操作类型', 'sys_oper_type',       '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '操作类型列表');
insert into sys_dict_type values(10, '系统状态', 'sys_common_status',   '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '登录状态列表');
commit;

-- ----------------------------
-- 12、字典数据表
-- ----------------------------
create sequence seq_sys_dict_data
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;

drop table sys_dict_data;
create table sys_dict_data
(
  dict_code        number(20,0)      not null   ,
  dict_sort        number(10,0)          default 0,
  dict_label       varchar2(100)    default ''    ,
  dict_value       varchar2(100)    default ''    ,
  dict_type        varchar2(100)    default ''    ,
  css_class        varchar2(100)    default null  ,
  list_class       varchar2(100)    default null  ,
  is_default       char(1)         default 'N'    ,
  status           char(1)         default '0'    ,
  create_by        varchar2(64)     default ''    ,
  create_time      date                           ,
  update_by        varchar2(64)     default ''    ,
  update_time      date                           ,
  remark           varchar2(500)    default null 
);

alter table sys_dict_data add constraint pk_sys_dict_data primary key (dict_code);

insert into sys_dict_data values(1,  1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '性别男');
insert into sys_dict_data values(2,  2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '性别女');
insert into sys_dict_data values(3,  3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '性别未知');
insert into sys_dict_data values(4,  1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '显示菜单');
insert into sys_dict_data values(5,  2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '隐藏菜单');
insert into sys_dict_data values(6,  1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '正常状态');
insert into sys_dict_data values(7,  2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '停用状态');
insert into sys_dict_data values(8,  1,  '正常',     '0',       'sys_job_status',      '',   'primary', 'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '正常状态');
insert into sys_dict_data values(9,  2,  '暂停',     '1',       'sys_job_status',      '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '停用状态');
insert into sys_dict_data values(10, 1,  '默认',     'DEFAULT', 'sys_job_group',       '',   '',        'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '默认分组');
insert into sys_dict_data values(11, 2,  '系统',     'SYSTEM',  'sys_job_group',       '',   '',        'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统分组');
insert into sys_dict_data values(12, 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统默认是');
insert into sys_dict_data values(13, 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '系统默认否');
insert into sys_dict_data values(14, 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '通知');
insert into sys_dict_data values(15, 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '公告');
insert into sys_dict_data values(16, 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '正常状态');
insert into sys_dict_data values(17, 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '关闭状态');
insert into sys_dict_data values(18, 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '其他操作');
insert into sys_dict_data values(19, 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '新增操作');
insert into sys_dict_data values(20, 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '修改操作');
insert into sys_dict_data values(21, 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '删除操作');
insert into sys_dict_data values(22, 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '授权操作');
insert into sys_dict_data values(23, 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '导出操作');
insert into sys_dict_data values(24, 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '导入操作');
insert into sys_dict_data values(25, 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '强退操作');
insert into sys_dict_data values(26, 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '生成操作');
insert into sys_dict_data values(27, 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '清空操作');
insert into sys_dict_data values(28, 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '正常状态');
insert into sys_dict_data values(29, 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '停用状态');
commit;

-- ----------------------------
-- 13、参数配置表
-- ----------------------------
create sequence seq_sys_config
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_config;
create table sys_config (
  config_id         number(10,0)          not null  ,
  config_name       varchar2(100)    default ''    ,
  config_key        varchar2(100)    default ''    ,
  config_value      varchar2(500)    default ''    ,
  config_type       char(1)         default 'N'    ,
  create_by         varchar2(64)     default ''    ,
  create_time       date                           ,
  update_by         varchar2(64)     default ''    ,
  update_time       date                           ,
  remark            varchar2(500)    default null  
);
alter table sys_config add constraint pk_sys_config primary key (config_id);

insert into sys_config values(1, '主框架页-默认皮肤样式名称',     'sys.index.skinName',       'skin-blue',     'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
insert into sys_config values(2, '用户管理-账号初始密码',         'sys.user.initPassword',    '123456',        'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '初始化密码 123456');
insert into sys_config values(3, '主框架页-侧边栏主题',           'sys.index.sideTheme',      'theme-dark',    'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '深黑主题theme-dark，浅色主题theme-light，深蓝主题theme-blue');
insert into sys_config values(4, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false',         'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(5, '用户管理-密码字符范围',         'sys.account.chrtype',      '0',             'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数组和特殊字符（密码必须包含字母，数字，特殊字符-_）');
insert into sys_config values(6, '主框架页-菜单导航显示风格',     'sys.index.menuStyle',      'default',       'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '菜单导航显示风格（default为左侧导航菜单，topnav为顶部导航菜单）');
insert into sys_config values(7, '主框架页-是否开启页脚',         'sys.index.ignoreFooter',   'true',          'Y', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '是否开启底部页脚显示（true显示，false隐藏）');
commit;

-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
create sequence seq_sys_logininfor
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_logininfor;
create table sys_logininfor (
  info_id        number(20,0)     not null ,
  login_name     varchar2(50)    default ''  ,
  ipaddr         varchar2(50)    default ''  ,
  login_location varchar2(255)   default ''  ,
  browser        varchar2(50)    default ''  ,
  os             varchar2(50)    default ''  ,
  status         char(1)        default '0'  ,
  msg            varchar2(255)   default ''  ,
  login_time     date                       
);

alter table sys_logininfor add constraint pk_sys_logininfor primary key (info_id);
commit;

-- ----------------------------
-- 15、在线用户记录
-- ----------------------------
drop table sys_user_online;
create table sys_user_online (
  sessionId         varchar2(50)   default '' ,
  login_name        varchar2(50)   default '' ,
  dept_name         varchar2(50)   default '' ,
  ipaddr            varchar2(50)   default '' ,
  login_location    varchar2(255)  default '' ,
  browser           varchar2(50)   default '' ,
  os                varchar2(50)   default '' ,
  status            varchar2(10)   default '' ,
  start_timestamp   date                      ,
  last_access_time  date                      ,
  expire_time       number(10,0)        default 0  
);

alter table sys_user_online add constraint pk_sys_user_online primary key (sessionId);
commit;

-- ----------------------------
-- 16、定时任务调度表
-- ----------------------------
create sequence seq_sys_job
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;


drop table sys_job;
create table sys_job (
  job_id              number(20,0)    not null ,
  job_name            varchar2(64)   default ''        ,
  job_group           varchar2(64)   default 'DEFAULT' ,
  invoke_target       varchar2(500)  not null          ,
  cron_expression     varchar2(255)  default ''        ,
  misfire_policy      varchar2(20)   default '3'       ,
  concurrent          char(1)       default '1'        ,
  status              char(1)       default '0'        ,
  create_by           varchar2(64)   default ''        ,
  create_time         date                             ,
  update_by           varchar2(64)   default ''        ,
  update_time         date                             ,
  remark              varchar2(500)  default ''      
);

alter table sys_job add constraint pk_sys_job primary key (job_id, job_name, job_group);

insert into sys_job values(1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams',        '0/10 * * * * ?', '3', '1', '1', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_job values(2, '系统默认（有参）', 'ryTask.ryParams('ry')',  '0/15 * * * * ?', '3', '1', '1', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
insert into sys_job values(3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams('ry', true, 2000L, 316.50D, 100)',  '0/20 * * * * ?', '3', '1', '1', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '');
commit;

-- ----------------------------
-- 17、定时任务调度日志表
-- ----------------------------
create sequence seq_sys_job_log
 increment by 1
 start with 1
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_job_log;
create table sys_job_log (
  job_log_id          number(20,0)     not null ,
  job_name            varchar2(64)    not null    ,
  job_group           varchar2(64)    not null    ,
  invoke_target       varchar2(500)   not null    ,
  job_message         varchar2(500)               ,
  status              char(1)        default '0'  ,
  exception_info      varchar2(2000)  default ''  ,
  create_time         date                        
);

alter table sys_job_log add constraint pk_sys_job_log primary key (job_log_id);
commit;

-- ----------------------------
-- 18、通知公告表
-- ----------------------------
create sequence seq_sys_notice
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;
 
drop table sys_notice;
create table sys_notice (
  notice_id         number(10,0)          not null   ,
  notice_title      varchar2(50)     not null        ,
  notice_type       char(1)         not null         ,
  notice_content    varchar2(2000)   default null    ,
  status            char(1)         default '0'      ,
  create_by         varchar2(64)     default ''      ,
  create_time       date                             ,
  update_by         varchar2(64)     default ''      ,
  update_time       date                             ,
  remark            varchar2(255)    default null    
);

alter table sys_notice add constraint pk_sys_notice primary key (notice_id);
commit;
-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '温馨提醒：2018-07-01 若依新版本发布啦', '2', '新版本内容', '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '管理员');
insert into sys_notice values('2', '维护通知：2018-07-01 若依系统凌晨维护', '1', '维护内容',   '0', 'admin', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), 'ry', TO_DATE('2018-03-16 11-33-00', 'YYYY/MM/DD HH24:MI:SS'), '管理员');
commit;

-- ----------------------------
-- 19、代码生成业务表
-- ----------------------------
create sequence seq_gen_table
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;
 
drop table gen_table;
create table gen_table (
  table_id             number(20,0)      not null      ,
  table_name           varchar2(200)    default ''     ,
  table_comment        varchar2(500)    default ''     ,
  sub_table_name       varchar2(64)     default null   ,
  sub_table_fk_name    varchar2(64)     default null   ,
  class_name           varchar2(100)    default ''     ,
  tpl_category         varchar2(200)    default 'crud' ,
  package_name         varchar2(100)                   ,
  module_name          varchar2(30)                    ,
  business_name        varchar2(30)                    ,
  function_name        varchar2(50)                    ,
  function_author      varchar2(50)                    ,
  gen_type             char(1)         default '0'     ,
  gen_path             varchar2(200)    default '/'    ,
  options              varchar2(1000)                  ,
  create_by            varchar2(64)     default ''     ,
  create_time 	       date                            ,
  update_by            varchar2(64)     default ''     ,
  update_time          date                            ,
  remark               varchar2(500)    default null   
);

alter table gen_table add constraint pk_gen_table primary key (table_id);
commit;

-- ----------------------------
-- 20、代码生成业务表字段
-- ----------------------------
create sequence seq_gen_table_column
 increment by 1
 start with 100
 nomaxvalue
 nominvalue
 cache 20;
 
drop table gen_table_column;
create table gen_table_column (
  column_id         number(20,0)      not null    ,
  table_id          varchar2(64)    ,
  column_name       varchar2(200)     ,
  column_comment    varchar2(500)   ,
  column_type       varchar2(100)     ,
  java_type         varchar2(500)   ,
  java_field        varchar2(200)  ,
  is_pk             char(1)    ,
  is_increment      char(1)   ,
  is_required       char(1)   ,
  is_insert         char(1)   ,
  is_edit           char(1)      ,
  is_list           char(1)        ,
  is_query          char(1)      ,
  query_type        varchar2(200)    default 'EQ' ,
  html_type         varchar2(200)                 ,
  dict_type         varchar2(200)    default ''   ,
  sort              number(10,0)        ,
  create_by         varchar2(64)     default ''   ,
  create_time 	    date                          ,
  update_by         varchar2(64)     default ''   ,
  update_time       date                         
);

alter table gen_table_column add constraint pk_gen_table_column primary key (column_id);
commit;

drop table SYS_ASYNDOWN;
CREATE TABLE SYS_ASYNDOWN (
	ID VARCHAR2(50) NOT NULL,
	FILEPATH VARCHAR2(200) NOT NULL,
	FILENAME VARCHAR2(200) NOT NULL,
	STATUS VARCHAR2(8) NOT NULL,
	MSG VARCHAR2(600) NOT NULL,
	DEPTID VARCHAR2(100) NOT NULL,
	UPDATEDATE VARCHAR2(50) NULL,
	CREATEDATE VARCHAR2(50) NOT NULL,
	TIMEINTERVAL VARCHAR2(50) NULL
)
TABLESPACE LEARN;
CREATE UNIQUE INDEX SYS_ASYNDOWN_ID_IDX ON LEARN.SYS_ASYNDOWN (ID);
COMMENT ON TABLE LEARN.SYS_ASYNDOWN IS '异步下载表';

commit;
-- ----------------------------
-- 函数 ，代替mysql的find_in_set
-- 例如： select * from sys_dept where FIND_IN_SET (101,ancestors) <> 0
-- mysql可接受0或其它number做为where 条件，oracle只接受表达式做为where 条件
-- ----------------------------
create or replace function find_in_set(arg1 in varchar2,arg2 in varchar)
return number is Result number;
begin
select instr(','||arg2||',' , ','||arg1||',') into Result from dual;
return(Result);
end find_in_set;
commit;