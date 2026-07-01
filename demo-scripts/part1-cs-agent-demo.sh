#!/bin/bash
# ============================================================
# 海底捞 Qoder Cloud Agents Demo - Part 1: 智能客服 Agent
# ============================================================
# 演示流程：5 步 API 调用创建一个懂海底捞业务的智能客服 Agent
# 使用方式：逐段复制粘贴到终端执行，或 source 后按函数调用
# ============================================================

set -e

# ============================================================
# Step 0: 配置 PAT（演示前完成）
# ============================================================
# 登录 https://qoder.com → 设置 → 个人访问令牌 → 创建
export QODER_PAT="${QODER_PAT:-pt-your-token-here}"
export BASE_URL="https://api.qoder.com/api/v1/cloud"

echo "=== Qoder Cloud Agents Demo: 海底捞智能客服 ==="
echo "PAT: ${QODER_PAT:0:10}..."
echo ""

# ============================================================
# Step 1: 创建执行环境 (Environment)
# ============================================================
# Environment = Agent 的"工位"，一个隔离的云端沙箱容器
step1_create_environment() {
    echo ">>> Step 1: 创建执行环境..."
    echo ""

    RESPONSE=$(curl -s -X POST "$BASE_URL/environments" \
      -H "Authorization: Bearer $QODER_PAT" \
      -H "Content-Type: application/json" \
      -d '{
        "name": "haidilao-cs-demo",
        "config": {
          "type": "cloud",
          "networking": {"type": "unrestricted"}
        }
      }')

    ENV_ID=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null || echo "")
    echo "Environment ID: $ENV_ID"
    echo "✅ 环境创建成功 — Agent 的云端工位已就绪"
    echo ""
    export ENV_ID
}

# ============================================================
# Step 2: 创建 Agent（海底捞智能客服"小捞"）
# ============================================================
# Agent = 可复用的 AI 配置模板（角色 + 工具 + 知识）
step2_create_agent() {
    echo ">>> Step 2: 创建智能客服 Agent..."
    echo ""

    RESPONSE=$(curl -s -X POST "$BASE_URL/agents" \
      -H "Authorization: Bearer $QODER_PAT" \
      -H "Content-Type: application/json" \
      -d '{
        "name": "haidilao-cs-agent",
        "model": "ultimate",
        "system": "你是海底捞官方智能客服，名字叫小捞。\n\n## 你的职责\n1. 回答顾客关于会员、积分、等级的问题\n2. 处理投诉并按标准给出补偿方案\n3. 提供门店信息、预约帮助、菜品推荐\n4. 语气亲切温暖，体现海底捞的服务精神\n\n## 工作流程\n1. 收到问题后先在 /knowledge/ 目录中查找相关文档\n2. 基于文档内容给出准确回复\n3. 如果文档中没有答案，诚实告知并建议联系人工客服\n\n## 回复规范\n- 开头称呼顾客：亲爱的海友\n- 回答要简洁，不超过200字\n- 涉及补偿方案必须严格按照 faq-complaints.md 中的标准\n- 不能自行承诺文档中没有的优惠",
        "tools": [{"type": "agent_toolset_20260401", "enabled_tools": ["Read", "Glob", "Grep"]}]
      }')

    AGENT_ID=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null || echo "")
    echo "Agent ID: $AGENT_ID"
    echo "✅ 智能客服 Agent 创建成功"
    echo "   - 模型: ultimate"
    echo "   - 工具: Read, Glob, Grep (只读权限)"
    echo "   - 角色: 海底捞客服小捞"
    echo ""
    export AGENT_ID
}

# ============================================================
# Step 3: 创建 Session（开始工作）
# ============================================================
# Session = Agent + Environment 的执行实例
step3_create_session() {
    echo ">>> Step 3: 创建 Session（Agent 开始上班）..."
    echo ""

    RESPONSE=$(curl -s -X POST "$BASE_URL/sessions" \
      -H "Authorization: Bearer $QODER_PAT" \
      -H "Content-Type: application/json" \
      -d "{\"agent\": \"$AGENT_ID\", \"environment_id\": \"$ENV_ID\"}")

    SESSION_ID=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null || echo "")
    echo "Session ID: $SESSION_ID"
    echo "✅ Session 创建成功 — Agent 已上线等待顾客提问"
    echo ""
    export SESSION_ID
}

# ============================================================
# Step 4: 模拟顾客提问
# ============================================================

# 演示问题 1：积分查询（简单 FAQ）
demo_question_1() {
    echo ">>> 演示问题 1: 积分查询"
    echo '顾客: "我昨天在望京SOHO店消费了523元，为什么没看到积分到账？"'
    echo ""

    curl -s -X POST "$BASE_URL/sessions/$SESSION_ID/events" \
      -H "Authorization: Bearer $QODER_PAT" \
      -H "Content-Type: application/json" \
      -d '{"events": [{"type": "user.message", "content": [{"type": "text", "text": "我昨天在望京SOHO店消费了523元，为什么没看到积分到账？"}]}]}'

    echo ""
    echo "--- 等待 Agent 回复（查看 SSE 事件流）---"
    echo ""
}

# 演示问题 2：投诉处理（复杂场景）
demo_question_2() {
    echo ">>> 演示问题 2: 异物投诉处理"
    echo '顾客: "我今天在国贸店吃火锅，锅里发现了一根头发！我非常生气，你们怎么处理？"'
    echo ""

    curl -s -X POST "$BASE_URL/sessions/$SESSION_ID/events" \
      -H "Authorization: Bearer $QODER_PAT" \
      -H "Content-Type: application/json" \
      -d '{"events": [{"type": "user.message", "content": [{"type": "text", "text": "我今天在国贸店吃火锅，锅里发现了一根头发！我非常生气，你们怎么处理？"}]}]}'

    echo ""
    echo "--- 预期：Agent 按标准回复 当餐免单 + 200元代金券 + 店长回访 ---"
    echo ""
}

# 演示问题 3：菜品推荐（知识整合）
demo_question_3() {
    echo ">>> 演示问题 3: 菜品推荐"
    echo '顾客: "我带3个朋友聚餐，预算500左右，有什么推荐？我们有一个人不吃辣。"'
    echo ""

    curl -s -X POST "$BASE_URL/sessions/$SESSION_ID/events" \
      -H "Authorization: Bearer $QODER_PAT" \
      -H "Content-Type: application/json" \
      -d '{"events": [{"type": "user.message", "content": [{"type": "text", "text": "我带3个朋友聚餐，预算500左右，有什么推荐？我们有一个人不吃辣。"}]}]}'

    echo ""
    echo "--- 预期：Agent 推荐四人欢聚套餐 488元 + 鸳鸯锅搭配建议 ---"
    echo ""
}

# ============================================================
# Step 5: 监听 SSE 事件流（实时观看 Agent 思考和回复）
# ============================================================
watch_stream() {
    echo ">>> 监听 Agent 实时事件流（Ctrl+C 停止）..."
    echo ""

    curl -s -N "$BASE_URL/sessions/$SESSION_ID/events/stream" \
      -H "Authorization: Bearer $QODER_PAT"
}

# ============================================================
# 使用说明
# ============================================================
echo "使用方式:"
echo "  source demo-scripts/part1-cs-agent-demo.sh"
echo ""
echo "  step1_create_environment   # 创建环境"
echo "  step2_create_agent         # 创建客服 Agent"
echo "  step3_create_session       # 创建 Session"
echo "  demo_question_1            # 积分查询"
echo "  demo_question_2            # 投诉处理"
echo "  demo_question_3            # 菜品推荐"
echo "  watch_stream               # 监听事件流"
echo ""
echo "或一键执行全部 setup:"
echo "  step1_create_environment && step2_create_agent && step3_create_session"
echo ""
