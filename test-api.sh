#!/bin/bash

# Promax Workout Tracker API 测试脚本
# 测试所有 API 端点

API_BASE_URL="http://localhost:8080/api"
COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_YELLOW='\033[1;33m'
COLOR_BLUE='\033[0;34m'
COLOR_NC='\033[0m' # No Color

# 存储测试结果
PASSED=0
FAILED=0

# 测试计数器
TEST_COUNT=0

# 打印测试结果
print_test_result() {
    TEST_COUNT=$((TEST_COUNT + 1))
    if [ $1 -eq 0 ]; then
        echo -e "${COLOR_GREEN}✓ PASS${COLOR_NC} - $2"
        PASSED=$((PASSED + 1))
    else
        echo -e "${COLOR_RED}✗ FAIL${COLOR_NC} - $2"
        FAILED=$((FAILED + 1))
    fi
    echo ""
}

# 打印请求信息
print_request() {
    echo -e "${COLOR_BLUE}>>> $1${COLOR_NC}"
}

# 检查 API 是否可访问
check_api_health() {
    print_request "检查 API 健康状态..."
    response=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/health")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 200 ]; then
        print_test_result 0 "API 健康检查"
        echo "响应: $body" | head -c 200
        echo ""
        return 0
    else
        print_test_result 1 "API 健康检查 (HTTP $http_code)"
        return 1
    fi
}

# 1. 健康检查
echo "=========================================="
echo "开始测试 Promax Workout Tracker API"
echo "=========================================="
echo ""

if ! check_api_health; then
    echo -e "${COLOR_RED}API 服务不可用，请检查 Docker 容器是否正常运行${COLOR_NC}"
    exit 1
fi

# 生成随机用户数据
TIMESTAMP=$(date +%s)
TEST_USERNAME="testuser_$TIMESTAMP"
TEST_EMAIL="test_$TIMESTAMP@example.com"
TEST_PASSWORD="test123456"
JWT_TOKEN=""
USER_ID=""
WORKOUT_ID=""
GOAL_ID=""

# 2. 用户注册
echo "=========================================="
echo "测试用户相关 API"
echo "=========================================="
echo ""

print_request "注册新用户: $TEST_USERNAME"
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/v1/users/register" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"$TEST_USERNAME\",
        \"email\": \"$TEST_EMAIL\",
        \"password\": \"$TEST_PASSWORD\"
    }")
REGISTER_HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -n1)
REGISTER_BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')

if [ "$REGISTER_HTTP_CODE" -eq 201 ]; then
    print_test_result 0 "用户注册"
    # 提取用户ID（从响应中查找）
    USER_ID=$(echo "$REGISTER_BODY" | sed -n 's/.*"id":\([0-9]*\).*/\1/p' | head -1)
    echo "注册响应: $REGISTER_BODY" | head -c 300
    echo ""
else
    print_test_result 1 "用户注册 (HTTP $REGISTER_HTTP_CODE)"
    echo "错误响应: $REGISTER_BODY"
    exit 1
fi

# 3. 用户登录
print_request "用户登录: $TEST_EMAIL"
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/v1/users/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"email\": \"$TEST_EMAIL\",
        \"password\": \"$TEST_PASSWORD\"
    }")
LOGIN_HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
LOGIN_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')

if [ "$LOGIN_HTTP_CODE" -eq 200 ]; then
    print_test_result 0 "用户登录"
    JWT_TOKEN=$(echo "$LOGIN_BODY" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p' | head -1)
    # 提取 USER_ID（如果之前没有获取到）
    if [ -z "$USER_ID" ]; then
        USER_ID=$(echo "$LOGIN_BODY" | sed -n 's/.*"userId":\([0-9]*\).*/\1/p' | head -1)
    fi
    echo "登录成功，Token: ${JWT_TOKEN:0:50}..."
    echo ""
else
    print_test_result 1 "用户登录 (HTTP $LOGIN_HTTP_CODE)"
    echo "错误响应: $LOGIN_BODY"
    exit 1
fi

if [ -z "$JWT_TOKEN" ]; then
    echo -e "${COLOR_RED}无法获取 JWT Token，无法继续测试受保护的端点${COLOR_NC}"
    exit 1
fi

# 4. 检查邮箱（需要认证）
if [ -n "$JWT_TOKEN" ]; then
    print_request "检查邮箱可用性: $TEST_EMAIL"
    CHECK_EMAIL_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/checkEmail?email=$TEST_EMAIL" \
        -H "Authorization: Bearer $JWT_TOKEN")
    CHECK_EMAIL_HTTP_CODE=$(echo "$CHECK_EMAIL_RESPONSE" | tail -n1)
    CHECK_EMAIL_BODY=$(echo "$CHECK_EMAIL_RESPONSE" | sed '$d')
    
    if [ "$CHECK_EMAIL_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "检查邮箱可用性"
        echo "响应: $CHECK_EMAIL_BODY" | head -c 200
        echo ""
    else
        print_test_result 1 "检查邮箱可用性 (HTTP $CHECK_EMAIL_HTTP_CODE)"
    fi
    
    # 5. 获取用户数量（需要认证）
    print_request "获取用户总数"
    COUNT_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/count" \
        -H "Authorization: Bearer $JWT_TOKEN")
    COUNT_HTTP_CODE=$(echo "$COUNT_RESPONSE" | tail -n1)
    COUNT_BODY=$(echo "$COUNT_RESPONSE" | sed '$d')
    
    if [ "$COUNT_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取用户总数"
        echo "响应: $COUNT_BODY" | head -c 200
        echo ""
    else
        print_test_result 1 "获取用户总数 (HTTP $COUNT_HTTP_CODE)"
    fi
fi

# 6. 获取用户信息
if [ -n "$USER_ID" ]; then
    print_request "获取用户信息: ID=$USER_ID"
    GET_USER_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/$USER_ID" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_USER_HTTP_CODE=$(echo "$GET_USER_RESPONSE" | tail -n1)
    GET_USER_BODY=$(echo "$GET_USER_RESPONSE" | sed '$d')
    
    if [ "$GET_USER_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取用户信息"
        echo "响应: $GET_USER_BODY" | head -c 300
        echo ""
    else
        print_test_result 1 "获取用户信息 (HTTP $GET_USER_HTTP_CODE)"
    fi
fi

# 7. 测试 Workout API
echo "=========================================="
echo "测试运动记录相关 API"
echo "=========================================="
echo ""

if [ -n "$USER_ID" ]; then
    # 上传运动记录
    print_request "上传运动记录 (跑步)"
    UPLOAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/v1/workouts/upload?userId=$USER_ID" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -d '{
            "workoutType": "RUNNING",
            "durationMinutes": 30,
            "distanceKm": 5.5,
            "caloriesBurned": 350,
            "notes": "晨跑测试"
        }')
    UPLOAD_HTTP_CODE=$(echo "$UPLOAD_RESPONSE" | tail -n1)
    UPLOAD_BODY=$(echo "$UPLOAD_RESPONSE" | sed '$d')
    
    if [ "$UPLOAD_HTTP_CODE" -eq 201 ]; then
        print_test_result 0 "上传运动记录"
        WORKOUT_ID=$(echo "$UPLOAD_BODY" | sed -n 's/.*"workoutId":\([0-9]*\).*/\1/p' | head -1)
        echo "响应: $UPLOAD_BODY" | head -c 300
        echo ""
        
        # 再上传几条记录用于测试
        print_request "上传更多运动记录..."
        curl -s -X POST "$API_BASE_URL/v1/workouts/upload?userId=$USER_ID" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -d '{"workoutType": "CYCLING", "durationMinutes": 45, "distanceKm": 20.0, "caloriesBurned": 450}' > /dev/null
        
        curl -s -X POST "$API_BASE_URL/v1/workouts/upload?userId=$USER_ID" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -d '{"workoutType": "WALKING", "durationMinutes": 60, "distanceKm": 6.0, "caloriesBurned": 250}' > /dev/null
        
        echo "已上传 3 条运动记录"
        echo ""
    else
        print_test_result 1 "上传运动记录 (HTTP $UPLOAD_HTTP_CODE)"
        echo "错误响应: $UPLOAD_BODY"
    fi
    
    # 获取用户的运动记录
    print_request "获取用户的所有运动记录"
    GET_WORKOUTS_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_WORKOUTS_HTTP_CODE=$(echo "$GET_WORKOUTS_RESPONSE" | tail -n1)
    GET_WORKOUTS_BODY=$(echo "$GET_WORKOUTS_RESPONSE" | sed '$d')
    
    if [ "$GET_WORKOUTS_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取用户运动记录列表"
        echo "响应: $GET_WORKOUTS_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "获取用户运动记录列表 (HTTP $GET_WORKOUTS_HTTP_CODE)"
    fi
    
    # 获取分页运动记录
    print_request "获取分页运动记录 (page=0, size=2)"
    PAGINATED_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID/paginated?page=0&size=2" \
        -H "Authorization: Bearer $JWT_TOKEN")
    PAGINATED_HTTP_CODE=$(echo "$PAGINATED_RESPONSE" | tail -n1)
    PAGINATED_BODY=$(echo "$PAGINATED_RESPONSE" | sed '$d')
    
    if [ "$PAGINATED_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取分页运动记录"
        echo "响应: $PAGINATED_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "获取分页运动记录 (HTTP $PAGINATED_HTTP_CODE)"
    fi
    
    # 获取最近的运动记录
    print_request "获取最近的 2 条运动记录"
    RECENT_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID/recent?limit=2" \
        -H "Authorization: Bearer $JWT_TOKEN")
    RECENT_HTTP_CODE=$(echo "$RECENT_RESPONSE" | tail -n1)
    RECENT_BODY=$(echo "$RECENT_RESPONSE" | sed '$d')
    
    if [ "$RECENT_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取最近的运动记录"
        echo "响应: $RECENT_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "获取最近的运动记录 (HTTP $RECENT_HTTP_CODE)"
    fi
    
    # 获取运动类型汇总
    print_request "获取运动类型汇总"
    SUMMARY_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID/summary/byType" \
        -H "Authorization: Bearer $JWT_TOKEN")
    SUMMARY_HTTP_CODE=$(echo "$SUMMARY_RESPONSE" | tail -n1)
    SUMMARY_BODY=$(echo "$SUMMARY_RESPONSE" | sed '$d')
    
    if [ "$SUMMARY_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取运动类型汇总"
        echo "响应: $SUMMARY_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "获取运动类型汇总 (HTTP $SUMMARY_HTTP_CODE)"
    fi
    
    # 获取运动记录详情
    if [ -n "$WORKOUT_ID" ]; then
        print_request "获取运动记录详情: ID=$WORKOUT_ID"
        GET_WORKOUT_DETAIL_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/detail/$WORKOUT_ID" \
            -H "Authorization: Bearer $JWT_TOKEN")
        GET_WORKOUT_DETAIL_HTTP_CODE=$(echo "$GET_WORKOUT_DETAIL_RESPONSE" | tail -n1)
        GET_WORKOUT_DETAIL_BODY=$(echo "$GET_WORKOUT_DETAIL_RESPONSE" | sed '$d')
        
        if [ "$GET_WORKOUT_DETAIL_HTTP_CODE" -eq 200 ]; then
            print_test_result 0 "获取运动记录详情"
            echo "响应: $GET_WORKOUT_DETAIL_BODY" | head -c 400
            echo ""
        else
            print_test_result 1 "获取运动记录详情 (HTTP $GET_WORKOUT_DETAIL_HTTP_CODE)"
        fi
        
        # 更新运动记录
        print_request "更新运动记录: ID=$WORKOUT_ID"
        UPDATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$API_BASE_URL/v1/workouts/$WORKOUT_ID" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -d '{
                "workoutType": "RUNNING",
                "durationMinutes": 35,
                "distanceKm": 6.0,
                "caloriesBurned": 400,
                "notes": "更新后的晨跑记录"
            }')
        UPDATE_HTTP_CODE=$(echo "$UPDATE_RESPONSE" | tail -n1)
        UPDATE_BODY=$(echo "$UPDATE_RESPONSE" | sed '$d')
        
        if [ "$UPDATE_HTTP_CODE" -eq 200 ]; then
            print_test_result 0 "更新运动记录"
            echo "响应: $UPDATE_BODY" | head -c 300
            echo ""
        else
            print_test_result 1 "更新运动记录 (HTTP $UPDATE_HTTP_CODE)"
        fi
        
        # 删除运动记录（最后测试，避免影响其他测试）
        print_request "删除运动记录: ID=$WORKOUT_ID"
        DELETE_RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$API_BASE_URL/v1/workouts/$WORKOUT_ID" \
            -H "Authorization: Bearer $JWT_TOKEN")
        DELETE_HTTP_CODE=$(echo "$DELETE_RESPONSE" | tail -n1)
        DELETE_BODY=$(echo "$DELETE_RESPONSE" | sed '$d')
        
        if [ "$DELETE_HTTP_CODE" -eq 200 ]; then
            print_test_result 0 "删除运动记录"
            echo "响应: $DELETE_BODY" | head -c 200
            echo ""
        else
            print_test_result 1 "删除运动记录 (HTTP $DELETE_HTTP_CODE)"
        fi
    fi
fi

# 8. 测试 Goal API
echo "=========================================="
echo "测试目标相关 API"
echo "=========================================="
echo ""

if [ -n "$USER_ID" ]; then
    # 创建目标
    print_request "创建运动目标"
    CREATE_GOAL_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/v1/users/$USER_ID/goals" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -d '{
            "workoutType": "RUNNING",
            "period": "WEEKLY",
            "metrics": [
                {
                    "metric": "COUNT",
                    "targetValue": 5
                },
                {
                    "metric": "DISTANCE",
                    "targetValue": 25.0
                }
            ]
        }')
    CREATE_GOAL_HTTP_CODE=$(echo "$CREATE_GOAL_RESPONSE" | tail -n1)
    CREATE_GOAL_BODY=$(echo "$CREATE_GOAL_RESPONSE" | sed '$d')
    
    if [ "$CREATE_GOAL_HTTP_CODE" -eq 201 ]; then
        print_test_result 0 "创建运动目标"
        GOAL_ID=$(echo "$CREATE_GOAL_BODY" | sed -n 's/.*"id":\([0-9]*\).*/\1/p' | head -1)
        echo "响应: $CREATE_GOAL_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "创建运动目标 (HTTP $CREATE_GOAL_HTTP_CODE)"
        echo "错误响应: $CREATE_GOAL_BODY"
    fi
    
    # 获取用户的所有目标
    print_request "获取用户的所有目标"
    GET_GOALS_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/$USER_ID/goals" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_GOALS_HTTP_CODE=$(echo "$GET_GOALS_RESPONSE" | tail -n1)
    GET_GOALS_BODY=$(echo "$GET_GOALS_RESPONSE" | sed '$d')
    
    if [ "$GET_GOALS_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "获取用户目标列表"
        echo "响应: $GET_GOALS_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "获取用户目标列表 (HTTP $GET_GOALS_HTTP_CODE)"
    fi
    
    # 按状态筛选目标
    print_request "获取活跃状态的目标"
    GET_GOALS_ACTIVE_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/$USER_ID/goals?status=ACTIVE" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_GOALS_ACTIVE_HTTP_CODE=$(echo "$GET_GOALS_ACTIVE_RESPONSE" | tail -n1)
    GET_GOALS_ACTIVE_BODY=$(echo "$GET_GOALS_ACTIVE_RESPONSE" | sed '$d')
    
    if [ "$GET_GOALS_ACTIVE_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "按状态筛选目标"
        echo "响应: $GET_GOALS_ACTIVE_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "按状态筛选目标 (HTTP $GET_GOALS_ACTIVE_HTTP_CODE)"
    fi
fi

# 打印测试摘要
echo "=========================================="
echo "测试完成"
echo "=========================================="
echo -e "总测试数: $TEST_COUNT"
echo -e "${COLOR_GREEN}通过: $PASSED${COLOR_NC}"
echo -e "${COLOR_RED}失败: $FAILED${COLOR_NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${COLOR_GREEN}✓ 所有测试通过！${COLOR_NC}"
    exit 0
else
    echo -e "${COLOR_RED}✗ 有 $FAILED 个测试失败${COLOR_NC}"
    exit 1
fi

