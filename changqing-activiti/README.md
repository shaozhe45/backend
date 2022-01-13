# 2.6.0 工作流介绍

- 当前版本工作流服务 不支持DATASOURCE模式！！！（后面继续研究，争取适配）
- 建议在工作流服务使用COLUMN模式，其他服务随意（原来是什么模式就用什么即可），每个服务是独立的，不会有影响！！！
- 使用NONE模式的请忽略以上所有说明

# NONE和COLUMN模式
- NONE 和 COLUMN 模式可以将这些表全部放到跟其他服务同一个库 changqing_column 库， 也可以新建一个库
- 新建库： changqing_activiti
- 导入脚本： changqing_activiti.sql
- changqing_activiti.sql 脚本包含了 工作流相关28张(ACT_*)表 + (WORKER_NODE) + 3张(b_biz_*)表， 共计32张表
- WORKER_NODE 用于生成雪花ID、 b_biz_* 是用来做演示用的业务表，大家可以根据自己的情况删除 b_biz_* 表
- ACT_* 可以让程序启动时自己生成
- 生产环境建议将 spring.activiti.database-schema-update = false
```
spring:
  activiti:
    database-schema-update: true
```

# SCHEMA模式
- 将changqing_activiti库的  3张(b_biz_*)表 删除
- 在changqing_extend_xxx 库新建 3张(b_biz_*)表


# 演示
1、启动前 + 后端
2、访问： 流程管理 - 流程部署
3、上传： 将 docs/activiti/my_leave.zip （请假）和 docs/activiti/my_reimbursement.zip （报销）依次上传
4、点击操作列第一个按钮：映射模式
5、访问： 流程管理 - 模型管理
6、点击操作列第一个按钮：编辑模型
7、依次选中部门经理审批 + 总经理审批节点， 点击下面的 "代理" ， 弹窗框后， 代理人下面的按钮点击清除， 然后重新选择右边的用户（右边没数据，就去用户管理添加用户）
8、上一步做的目的就是将代理人调整成本系统中存在的用户
9、左上角保存， 保存成功， 右上角的X 关闭界面
10、访问： 流程管理 - 请假流程 - 请假管理
11、添加 ， 填写表单， 确认 
12、访问： 流程管理 - 请假流程 - 请假任务 - 搜索（待办任务）
13、操作栏 - 查询详情 - 确认
14、登录其他账号（第7步选择的部门经理和总经理账号）登录系统， 访问 请假任务 页面进行办理
15、14步办理完成后，使用发起人账号在 请假管理 页面，就能查询出办结任务。

