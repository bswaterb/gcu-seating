### 其实还只是个更新日志233

v1.0.0 beta2更新内容：

1. 增加 ==删除学生入座接口 (/teacher/deleteSeatingInfo)==
2. 课堂过期，从Redis向Mysql移交内容时增加新字段 ==class_id==
3. Mysql - seating_record 增加字段 class_id，同时设class_id与classroom_id为联合主键
4. 新增 创建课堂时自定义密码及课室大小 检测功能



beta3更新：

1. 增加教师评价学生状态功能



beta4更新：

1. 修复绑定身份信息错误BUG

beta5更新：

1. 修复BUG
2. 增加searchClass的字段

beta6更新：

1. 修复BUG

beta7更新：

1. 修复BUG
2. 增加恢复原课堂座位接口
3. 增加教师端查看历史创建记录接口