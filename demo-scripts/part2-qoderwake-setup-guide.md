# QoderWake SRE 配置指南

> 本文档指导你完成 Part 2 演示前的 QoderWake 环境配置。

## 前置条件

- macOS 13.0+ 已安装 QoderWake
- GitHub 账号 + 演示仓库（sre-demo 已推送）
- 网络可达 api.qoder.com

## Step 1: 安装 QoderWake（如未安装）

```bash
curl -fsSL https://qoder-ide.oss-ap-southeast-1.aliyuncs.com/qoderwake/install.sh | bash
```

安装完成后访问: http://127.0.0.1:19820/

## Step 2: 创建 SRE 数字员工

1. 打开控制台 → 点击左侧"创建 Waker"
2. 选择角色模板: **后端工程师**
3. 填写信息:
   - 名字: `SRE-小海`
   - 简介: `海底捞 SRE 工程师，负责监控告警响应、Bug 定位与修复、服务健康巡检`
4. 保存

## Step 3: 配置 GitHub 连接器

1. 进入 SRE-小海 详情页 → **连接器** Tab
2. 点击 "添加连接器" → 选择 **GitHub**
3. 授权 GitHub 账号（OAuth 跳转）
4. 选择关联仓库: `your-username/haidilao-order-service`（演示仓库）

## Step 4: 绑定项目目录

1. 进入 SRE-小海 详情页 → **项目** Tab
2. 添加项目:
   - 类型: Git 仓库
   - 地址: `https://github.com/your-username/haidilao-order-service`
   - 分支: `main`

## Step 5: 创建自动任务（GitHub Issue 触发）

1. 进入 SRE-小海 详情页 → **触发任务** Tab
2. 点击 "新建自动任务"
3. 填写配置:

**基本信息:**
- 任务名: `auto-fix-github-issues`
- AI 模型: Ultimate
- 工作目录: 选择上一步绑定的仓库

**任务描述（粘贴以下内容）:**
```
当收到新的 GitHub Issue 时，请按以下流程处理：

1. 分析 Issue 中的报错信息（堆栈、日志），定位根因
2. 在代码仓库中找到问题所在的具体文件和行号
3. 编写修复代码，确保：
   - 修复根本原因（不是临时 workaround）
   - 添加对应的单元测试验证修复
   - 代码风格与项目一致
4. 创建新分支（格式: fix/issue-{number}-简短描述）
5. 提交代码并创建 Pull Request，PR 描述中包含：
   - 根因分析
   - 修复方案
   - 测试验证结果
6. 在原 Issue 中回复修复进展，包含 PR 链接
```

**触发方式:**
- 类型: GitHub Webhook
- 事件: Issues
- Action: opened
- （即: 有新 Issue 创建时触发）

4. 保存并启用

## Step 6: 演示前验证

### 手动测试触发器

1. 在 GitHub 仓库创建一个测试 Issue:
   ```
   标题: [TEST] Verify auto-fix trigger
   内容: This is a test issue to verify the trigger works.
   ```
2. 回到 QoderWake 控制台 → 触发任务 → 查看是否有新的运行记录
3. 确认触发成功后，删除测试 Issue

### 演示用 Issue 模板

正式演示时创建的 Issue:

```
标题: [BUG] NullPointerException in OrderService.getOrderDetail

内容:
## 线上报错日志

```
java.lang.NullPointerException
  at com.haidilao.service.OrderService.getOrderDetail(OrderService.java:31)
  at com.haidilao.controller.OrderController.getOrderDetail(OrderController.java:27)
  at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)
```

## 复现步骤

1. 调用 `GET /api/orders/999`（不存在的订单ID）
2. 返回 500 Internal Server Error

## 环境信息

- 服务: haidilao-order-service
- 版本: 1.0.0-SNAPSHOT
- JDK: 17
```

## 演示当天 Checklist

- [ ] QoderWake Daemon 已启动（`http://127.0.0.1:19820/` 可访问）
- [ ] SRE-小海 状态正常（详情页首页可见）
- [ ] GitHub 连接器状态: 已连接
- [ ] 自动任务状态: 已启用
- [ ] 网络测试: 可访问 GitHub API
- [ ] 演示仓库: main 分支包含有 Bug 的代码
- [ ] 提前跑通一次完整流程（Issue → PR）
