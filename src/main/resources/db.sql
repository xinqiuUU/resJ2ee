# --------------------------------------------------------------------
# --                创建库，表，约束，过程，用户，权限等脚本
# --------------------------------------------------------------------
create database res;

use res;
# 管理员表
create table resadmin(
    raid int primary key auto_increment,
    raname varchar(50),
    rapwd  varchar(50)
);
# 用户表
create table resuser(
	userid int  primary key auto_increment,
	username varchar(50),
	pwd varchar(50), 
	email varchar(500)
);

# normprice:原价  realprice:现价   description:简介 detail详细的
create table resfood(
	fid int  primary key auto_increment,
	fname varchar(50) ,  
	normprice numeric(8,2), #原价
    realprice numeric(8,2),#现价
	detail varchar(2000), #详细的
	fphoto varchar(1000)
);
# --订单表:   roid:订单号    userid:外键，下单的用户编号    ordertime:下单时间   uname:收货人姓名    deliverytype:送货方式   payment:支付方式, ps附言
create table resorder(
	roid int  primary key auto_increment, #订单号
	userid int,         # 下单的用户编号
	address varchar(500), #地址
	tel varchar( 100),  # 电话号码
	ordertime date,     # 下单时间
	deliverytime date,  # 送货时间
	ps varchar( 2000),  #附言
	status int          #状态
);
select * from resfood order by realprice asc limit 0,5;
# --订单表的下单人号与用户表中的客户编号有主外键关系.
alter table resorder
	add constraint fk_resorder
	     foreign key(userid) references resuser(userid);

drop table resorderitem;
# --dealprice:成交价   roid:订单号   fid:商品号  num:数量
create table resorderitem(
	roiid int  primary key auto_increment,
	roid  int,
	fid   int,
	dealprice numeric(8,2),
	num     int
);

alter table resorderitem 
   add constraint fk_resorderitem_roid
      foreign key(roid) references resorder( roid);
      
 alter table resorderitem
   add constraint fk_tbl_res_fid
      foreign key( fid ) references resfood( fid);
     

      
 commit;

use res;
insert into resadmin(raname,rapwd) values( 'a','d7afde3e7059cd0a0fe09eec4b0008cd');
select * from resadmin;
# ---用户表初始数据
insert into resuser( username, pwd,email) values( 'a', 'd7afde3e7059cd0a0fe09eec4b0008cd','a@163.com');
insert into resuser( username, pwd,email) values( 'b', 'd7afde3e7059cd0a0fe09eec4b0008cd','b@163.com');

# --插入菜
insert into resfood(fname,normprice,realprice,detail, fphoto)  values('素炒莴笋丝',22.0,20.0,'营养丰富','500008.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)  values('蛋炒饭',22.0,20.0,'营养丰富','500022.jpg');
insert into resfood( fname,normprice,realprice,detail, fphoto)  values('酸辣鱼',42.0,40.0,'营养丰富','500023.jpg');
insert into resfood( fname,normprice,realprice,detail, fphoto)  values('鲁粉',12.0,10.0,'营养丰富','500024.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)  values('西红柿蛋汤',12.0,10.0,'营养丰富','500025.jpg');



insert into resfood(fname,normprice,realprice,detail, fphoto)   values('炖鸡',102.0,100.0,'营养丰富','500026.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)  values('炒鸡',12.0,10.0,'营养丰富','500033.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)   values('炒饭',12.0,10.0,'营养丰富','500034.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)   values('手撕前女友',12.0,10.0,'营养丰富','500035.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)  values('面条',12.0,10.0,'营养丰富','500036.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)  values('端菜',12.0,10.0,'营养丰富','500038.jpg');
insert into resfood(fname,normprice,realprice,detail, fphoto)   values('酸豆角',12.0,10.0,'营养丰富','500041.jpg');

# --不测试:   生成一条订单   a用户订了  1号菜1份,及2号菜2份
insert into resorder(userid,address,tel,ordertime,deliverytime,ps,status)
values( 1,'湖南省衡阳市','13878789999',now(),now(),'送餐上门',0);

insert into resorderitem(roid,fid,dealprice,num)
values( 1,1,20,1);

insert into resorderitem(roid,fid,dealprice,num)
values( 1,2,20,1);
# --注意以上的三条语句要求在事务中处理.
commit;

select * from resuser;
select * from resorderitem;