#!/bin/bash

# Promax Workout Tracker API test script
# Test all API endpoints

API_BASE_URL="http://localhost:8080/api"
COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_YELLOW='\033[1;33m'
COLOR_BLUE='\033[0;34m'
COLOR_NC='\033[0m' # No Color

# Store test results
PASSED=0
FAILED=0

# Test counter
TEST_COUNT=0

# Print test results
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

# Print request information
print_request() {
    echo -e "${COLOR_BLUE}>>> $1${COLOR_NC}"
}

# Check if API is accessible
check_api_health() {
    print_request "Checking API health..."
    response=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/health")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 200 ]; then
        print_test_result 0 "API health check"
        echo "Response: $body" | head -c 200
        echo ""
        return 0
    else
        print_test_result 1 "API health check (HTTP $http_code)"
        return 1
    fi
}

# 1. Health check
echo "=========================================="
echo "Starting test for Promax Workout Tracker API"
echo "=========================================="
echo ""

if ! check_api_health; then
    echo -e "${COLOR_RED}API service is not available, please check if the Docker container is running correctly${COLOR_NC}"
    exit 1
fi

# Generate random user data
TIMESTAMP=$(date +%s)
TEST_USERNAME="testuser_$TIMESTAMP"
TEST_EMAIL="test_$TIMESTAMP@example.com"
TEST_PASSWORD="test123456"
JWT_TOKEN=""
USER_ID=""
WORKOUT_ID=""
GOAL_ID=""

# 2. User registration
echo "=========================================="
echo "Testing user-related API"
echo "=========================================="
echo ""

print_request "Registering new user: $TEST_USERNAME"
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
    print_test_result 0 "User registration"
    # Extract user ID from response
    USER_ID=$(echo "$REGISTER_BODY" | sed -n 's/.*"id":\([0-9]*\).*/\1/p' | head -1)
    echo "Registration response: $REGISTER_BODY" | head -c 300
    echo ""
else
    print_test_result 1 "User registration (HTTP $REGISTER_HTTP_CODE)"
    echo "Error response: $REGISTER_BODY"
    exit 1
fi

# 3. User login
print_request "User login: $TEST_EMAIL"
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/v1/users/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"email\": \"$TEST_EMAIL\",
        \"password\": \"$TEST_PASSWORD\"
    }")
LOGIN_HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
LOGIN_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')

if [ "$LOGIN_HTTP_CODE" -eq 200 ]; then
    print_test_result 0 "User login"
    JWT_TOKEN=$(echo "$LOGIN_BODY" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p' | head -1)
    # Extract USER_ID if not already extracted
    if [ -z "$USER_ID" ]; then
        USER_ID=$(echo "$LOGIN_BODY" | sed -n 's/.*"userId":\([0-9]*\).*/\1/p' | head -1)
    fi
    echo "Login successful, Token: ${JWT_TOKEN:0:50}..."
    echo ""
else
    print_test_result 1 "User login (HTTP $LOGIN_HTTP_CODE)"
    echo "Error response: $LOGIN_BODY"
    exit 1
fi

if [ -z "$JWT_TOKEN" ]; then
    echo -e "${COLOR_RED}Cannot get JWT Token, cannot continue testing protected endpoints${COLOR_NC}"
    exit 1
fi

# 4. Check email availability (requires authentication)
if [ -n "$JWT_TOKEN" ]; then
    print_request "Checking email availability: $TEST_EMAIL"
    CHECK_EMAIL_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/checkEmail?email=$TEST_EMAIL" \
        -H "Authorization: Bearer $JWT_TOKEN")
    CHECK_EMAIL_HTTP_CODE=$(echo "$CHECK_EMAIL_RESPONSE" | tail -n1)
    CHECK_EMAIL_BODY=$(echo "$CHECK_EMAIL_RESPONSE" | sed '$d')
    
    if [ "$CHECK_EMAIL_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Checking email availability"
        echo "Response: $CHECK_EMAIL_BODY" | head -c 200
        echo ""
    else
        print_test_result 1 "Checking email availability (HTTP $CHECK_EMAIL_HTTP_CODE)"
    fi
    
    # 5. Get user count (requires authentication)
    print_request "Get user count"
    COUNT_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/count" \
        -H "Authorization: Bearer $JWT_TOKEN")
    COUNT_HTTP_CODE=$(echo "$COUNT_RESPONSE" | tail -n1)
    COUNT_BODY=$(echo "$COUNT_RESPONSE" | sed '$d')
    
    if [ "$COUNT_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get user count"
        echo "Response: $COUNT_BODY" | head -c 200
        echo ""
    else
        print_test_result 1 "Get user count (HTTP $COUNT_HTTP_CODE)"
    fi
fi

# 6. Get user information
if [ -n "$USER_ID" ]; then
    print_request "Get user information: ID=$USER_ID"
    GET_USER_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/$USER_ID" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_USER_HTTP_CODE=$(echo "$GET_USER_RESPONSE" | tail -n1)
    GET_USER_BODY=$(echo "$GET_USER_RESPONSE" | sed '$d')
    
    if [ "$GET_USER_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get user information"
        echo "Response: $GET_USER_BODY" | head -c 300
        echo ""
    else
        print_test_result 1 "Get user information (HTTP $GET_USER_HTTP_CODE)"
    fi
fi

# 7. Test Workout API
echo "=========================================="
echo "Testing workout-related API"
echo "=========================================="
echo ""

if [ -n "$USER_ID" ]; then
    # Upload workout record
    print_request "Upload workout record (Running)"
    UPLOAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/v1/workouts/upload?userId=$USER_ID" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -d '{
            "workoutType": "RUNNING",
            "durationMinutes": 30,
            "distanceKm": 5.5,
            "caloriesBurned": 350,
            "notes": "Morning run test"
        }')
    UPLOAD_HTTP_CODE=$(echo "$UPLOAD_RESPONSE" | tail -n1)
    UPLOAD_BODY=$(echo "$UPLOAD_RESPONSE" | sed '$d')
    
    if [ "$UPLOAD_HTTP_CODE" -eq 201 ]; then
        print_test_result 0 "Upload workout record"
        WORKOUT_ID=$(echo "$UPLOAD_BODY" | sed -n 's/.*"workoutId":\([0-9]*\).*/\1/p' | head -1)
        echo "Response: $UPLOAD_BODY" | head -c 300
        echo ""
        
        # Upload more records for testing
        print_request "Uploading more workout records..."
        curl -s -X POST "$API_BASE_URL/v1/workouts/upload?userId=$USER_ID" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -d '{"workoutType": "CYCLING", "durationMinutes": 45, "distanceKm": 20.0, "caloriesBurned": 450}' > /dev/null
        
        curl -s -X POST "$API_BASE_URL/v1/workouts/upload?userId=$USER_ID" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -d '{"workoutType": "WALKING", "durationMinutes": 60, "distanceKm": 6.0, "caloriesBurned": 250}' > /dev/null
        
        echo "Uploaded 3 workout records"
        echo ""
    else
        print_test_result 1 "Upload workout record (HTTP $UPLOAD_HTTP_CODE)"
        echo "Error response: $UPLOAD_BODY"
    fi
    
    # Get user's workout records
    print_request "Get all user workout records"
    GET_WORKOUTS_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_WORKOUTS_HTTP_CODE=$(echo "$GET_WORKOUTS_RESPONSE" | tail -n1)
    GET_WORKOUTS_BODY=$(echo "$GET_WORKOUTS_RESPONSE" | sed '$d')
    
    if [ "$GET_WORKOUTS_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get user workout records list"
        echo "Response: $GET_WORKOUTS_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Get user workout records list (HTTP $GET_WORKOUTS_HTTP_CODE)"
    fi
    
    # Get paginated workout records
    print_request "Get paginated workout records (page=0, size=2)"
    PAGINATED_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID/paginated?page=0&size=2" \
        -H "Authorization: Bearer $JWT_TOKEN")
    PAGINATED_HTTP_CODE=$(echo "$PAGINATED_RESPONSE" | tail -n1)
    PAGINATED_BODY=$(echo "$PAGINATED_RESPONSE" | sed '$d')
    
    if [ "$PAGINATED_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get paginated workout records"
        echo "Response: $PAGINATED_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Get paginated workout records (HTTP $PAGINATED_HTTP_CODE)"
    fi
    
    # Get recent workout records
    print_request "Get recent 2 workout records"
    RECENT_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID/recent?limit=2" \
        -H "Authorization: Bearer $JWT_TOKEN")
    RECENT_HTTP_CODE=$(echo "$RECENT_RESPONSE" | tail -n1)
    RECENT_BODY=$(echo "$RECENT_RESPONSE" | sed '$d')
    
    if [ "$RECENT_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get recent workout records"
        echo "Response: $RECENT_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Get recent workout records (HTTP $RECENT_HTTP_CODE)"
    fi
    
    # Get workout summary by type
    print_request "Get workout summary by type"
    SUMMARY_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/$USER_ID/summary/byType" \
        -H "Authorization: Bearer $JWT_TOKEN")
    SUMMARY_HTTP_CODE=$(echo "$SUMMARY_RESPONSE" | tail -n1)
    SUMMARY_BODY=$(echo "$SUMMARY_RESPONSE" | sed '$d')
    
    if [ "$SUMMARY_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get workout summary by type"
        echo "Response: $SUMMARY_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Get workout summary by type (HTTP $SUMMARY_HTTP_CODE)"
    fi
    
    # Get workout record details
    if [ -n "$WORKOUT_ID" ]; then
        print_request "Get workout record details: ID=$WORKOUT_ID"
        GET_WORKOUT_DETAIL_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/workouts/detail/$WORKOUT_ID" \
            -H "Authorization: Bearer $JWT_TOKEN")
        GET_WORKOUT_DETAIL_HTTP_CODE=$(echo "$GET_WORKOUT_DETAIL_RESPONSE" | tail -n1)
        GET_WORKOUT_DETAIL_BODY=$(echo "$GET_WORKOUT_DETAIL_RESPONSE" | sed '$d')
        
        if [ "$GET_WORKOUT_DETAIL_HTTP_CODE" -eq 200 ]; then
            print_test_result 0 "Get workout record details"
            echo "Response: $GET_WORKOUT_DETAIL_BODY" | head -c 400
            echo ""
        else
            print_test_result 1 "Get workout record details (HTTP $GET_WORKOUT_DETAIL_HTTP_CODE)"
        fi
        
        # Update workout record
        print_request "Update workout record: ID=$WORKOUT_ID"
        UPDATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$API_BASE_URL/v1/workouts/$WORKOUT_ID" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $JWT_TOKEN" \
            -d '{
                "workoutType": "RUNNING",
                "durationMinutes": 35,
                "distanceKm": 6.0,
                "caloriesBurned": 400,
                "notes": "Updated morning run record"
            }')
        UPDATE_HTTP_CODE=$(echo "$UPDATE_RESPONSE" | tail -n1)
        UPDATE_BODY=$(echo "$UPDATE_RESPONSE" | sed '$d')
        
        if [ "$UPDATE_HTTP_CODE" -eq 200 ]; then
            print_test_result 0 "Update workout record"
            echo "Response: $UPDATE_BODY" | head -c 300
            echo ""
        else
            print_test_result 1 "Update workout record (HTTP $UPDATE_HTTP_CODE)"
        fi
        
        # Delete workout record (test last to avoid affecting other tests)
        print_request "Delete workout record: ID=$WORKOUT_ID"
        DELETE_RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$API_BASE_URL/v1/workouts/$WORKOUT_ID" \
            -H "Authorization: Bearer $JWT_TOKEN")
        DELETE_HTTP_CODE=$(echo "$DELETE_RESPONSE" | tail -n1)
        DELETE_BODY=$(echo "$DELETE_RESPONSE" | sed '$d')
        
        if [ "$DELETE_HTTP_CODE" -eq 200 ]; then
            print_test_result 0 "Delete workout record"
            echo "Response: $DELETE_BODY" | head -c 200
            echo ""
        else
            print_test_result 1 "Delete workout record (HTTP $DELETE_HTTP_CODE)"
        fi
    fi
fi

# 8. Test Goal API
echo "=========================================="
echo "Testing goal-related API"
echo "=========================================="
echo ""

if [ -n "$USER_ID" ]; then
    # Create goal
    print_request "Create workout goal"
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
        print_test_result 0 "Create workout goal"
        GOAL_ID=$(echo "$CREATE_GOAL_BODY" | sed -n 's/.*"id":\([0-9]*\).*/\1/p' | head -1)
        echo "Response: $CREATE_GOAL_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Create workout goal (HTTP $CREATE_GOAL_HTTP_CODE)"
        echo "Error response: $CREATE_GOAL_BODY"
    fi
    
    # Get all user goals
    print_request "Get all user goals"
    GET_GOALS_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/$USER_ID/goals" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_GOALS_HTTP_CODE=$(echo "$GET_GOALS_RESPONSE" | tail -n1)
    GET_GOALS_BODY=$(echo "$GET_GOALS_RESPONSE" | sed '$d')
    
    if [ "$GET_GOALS_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Get user goals list"
        echo "Response: $GET_GOALS_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Get user goals list (HTTP $GET_GOALS_HTTP_CODE)"
    fi
    
    # Filter goals by status
    print_request "Get active goals"
    GET_GOALS_ACTIVE_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE_URL/v1/users/$USER_ID/goals?status=ACTIVE" \
        -H "Authorization: Bearer $JWT_TOKEN")
    GET_GOALS_ACTIVE_HTTP_CODE=$(echo "$GET_GOALS_ACTIVE_RESPONSE" | tail -n1)
    GET_GOALS_ACTIVE_BODY=$(echo "$GET_GOALS_ACTIVE_RESPONSE" | sed '$d')
    
    if [ "$GET_GOALS_ACTIVE_HTTP_CODE" -eq 200 ]; then
        print_test_result 0 "Filter goals by status"
        echo "Response: $GET_GOALS_ACTIVE_BODY" | head -c 400
        echo ""
    else
        print_test_result 1 "Filter goals by status (HTTP $GET_GOALS_ACTIVE_HTTP_CODE)"
    fi
fi

# Print test summary
echo "=========================================="
echo "Test completed"
echo "=========================================="
echo -e "Total tests: $TEST_COUNT"
echo -e "${COLOR_GREEN}Passed: $PASSED${COLOR_NC}"
echo -e "${COLOR_RED}Failed: $FAILED${COLOR_NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${COLOR_GREEN}✓ All tests passed!${COLOR_NC}"
    exit 0
else
    echo -e "${COLOR_RED}✗ $FAILED test(s) failed${COLOR_NC}"
    exit 1
fi

