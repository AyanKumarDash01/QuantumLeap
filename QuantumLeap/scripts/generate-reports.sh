#!/bin/bash

# QuantumLeap E-Commerce Test Report Generation Script
# This script generates comprehensive test reports and manages report artifacts

set -e  # Exit on any error

# Configuration
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
REPORTS_DIR="$PROJECT_DIR/src/test/resources/reports"
SCREENSHOTS_DIR="$PROJECT_DIR/screenshots"
LOGS_DIR="$PROJECT_DIR/logs"
RESULTS_DIR="$PROJECT_DIR/test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to create directories
create_directories() {
    log_info "Creating necessary directories..."
    mkdir -p "$REPORTS_DIR"
    mkdir -p "$SCREENSHOTS_DIR"
    mkdir -p "$LOGS_DIR"
    mkdir -p "$RESULTS_DIR"
    log_success "Directories created successfully"
}

# Function to clean old reports
clean_old_reports() {
    local days=${1:-7}  # Default to 7 days
    log_info "Cleaning reports older than $days days..."
    
    if [ -d "$REPORTS_DIR" ]; then
        find "$REPORTS_DIR" -name "*.html" -mtime +$days -delete 2>/dev/null || true
        find "$REPORTS_DIR" -name "*.xml" -mtime +$days -delete 2>/dev/null || true
        find "$REPORTS_DIR" -name "*.json" -mtime +$days -delete 2>/dev/null || true
    fi
    
    if [ -d "$SCREENSHOTS_DIR" ]; then
        find "$SCREENSHOTS_DIR" -name "*.png" -mtime +$days -delete 2>/dev/null || true
        find "$SCREENSHOTS_DIR" -name "*.jpg" -mtime +$days -delete 2>/dev/null || true
    fi
    
    if [ -d "$LOGS_DIR" ]; then
        find "$LOGS_DIR" -name "*.log" -mtime +$days -delete 2>/dev/null || true
    fi
    
    log_success "Old reports cleaned successfully"
}

# Function to run tests with report generation
run_tests_with_reports() {
    local test_suite=${1:-"all"}
    log_info "Running tests for suite: $test_suite"
    
    cd "$PROJECT_DIR"
    
    case $test_suite in
        "smoke")
            mvn clean test -Dtest="LoginTests#testValidLogin,LoginTests#testLockedOutUser" \
                -Dmaven.test.failure.ignore=true \
                -Dreports.timestamp="$TIMESTAMP"
            ;;
        "ui")
            mvn clean test -Dgroups="ui,login,e2e" \
                -Dmaven.test.failure.ignore=true \
                -Dreports.timestamp="$TIMESTAMP"
            ;;
        "api")
            mvn clean test -Dgroups="api,crud" \
                -Dmaven.test.failure.ignore=true \
                -Dreports.timestamp="$TIMESTAMP"
            ;;
        "bdd")
            mvn clean test -Dgroups="bdd,cucumber" \
                -Dmaven.test.failure.ignore=true \
                -Dreports.timestamp="$TIMESTAMP"
            ;;
        "regression")
            mvn clean test -Dgroups="regression" \
                -Dmaven.test.failure.ignore=true \
                -Dreports.timestamp="$TIMESTAMP"
            ;;
        "all")
            mvn clean test \
                -Dmaven.test.failure.ignore=true \
                -Dreports.timestamp="$TIMESTAMP"
            ;;
        *)
            log_error "Unknown test suite: $test_suite"
            log_info "Available suites: smoke, ui, api, bdd, regression, all"
            exit 1
            ;;
    esac
    
    log_success "Test execution completed"
}

# Function to generate consolidated report
generate_consolidated_report() {
    log_info "Generating consolidated test report..."
    
    local report_file="$RESULTS_DIR/consolidated_report_$TIMESTAMP.html"
    
    cat > "$report_file" << EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuantumLeap E-Commerce Test Execution Summary</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .header { text-align: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 2px solid #e0e0e0; }
        .header h1 { color: #2c3e50; margin-bottom: 10px; }
        .header .subtitle { color: #7f8c8d; font-size: 18px; }
        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 8px; text-align: center; }
        .card h3 { margin: 0 0 10px 0; font-size: 24px; }
        .card p { margin: 0; font-size: 16px; opacity: 0.9; }
        .section { margin: 30px 0; padding: 20px; border-left: 4px solid #3498db; background-color: #f8f9fa; }
        .section h2 { color: #2c3e50; margin-top: 0; }
        .report-links { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; }
        .report-link { display: block; padding: 15px; background-color: white; border: 1px solid #e0e0e0; border-radius: 5px; text-decoration: none; color: #2c3e50; transition: all 0.3s; }
        .report-link:hover { background-color: #3498db; color: white; text-decoration: none; }
        .timestamp { color: #7f8c8d; font-style: italic; }
        .footer { text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #e0e0e0; color: #7f8c8d; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 QuantumLeap E-Commerce</h1>
            <div class="subtitle">Test Execution Summary Report</div>
            <div class="timestamp">Generated: $(date)</div>
        </div>
        
        <div class="summary">
            <div class="card">
                <h3>Extent Report</h3>
                <p>Comprehensive UI & API Test Report</p>
            </div>
            <div class="card">
                <h3>Screenshots</h3>
                <p>Test Failure Evidence</p>
            </div>
            <div class="card">
                <h3>Logs</h3>
                <p>Detailed Execution Logs</p>
            </div>
            <div class="card">
                <h3>TestNG</h3>
                <p>XML & HTML Reports</p>
            </div>
        </div>
        
        <div class="section">
            <h2>📊 Test Reports</h2>
            <div class="report-links">
EOF

    # Add links to available reports
    if [ -d "$REPORTS_DIR" ]; then
        find "$REPORTS_DIR" -name "*.html" -newer "$REPORTS_DIR" 2>/dev/null | sort -r | head -5 | while read report; do
            local report_name=$(basename "$report")
            echo "<a href=\"file://$report\" class=\"report-link\">📈 $report_name</a>" >> "$report_file"
        done
    fi

    cat >> "$report_file" << EOF
            </div>
        </div>
        
        <div class="section">
            <h2>📁 Artifacts Location</h2>
            <ul>
                <li><strong>Reports:</strong> <code>$REPORTS_DIR</code></li>
                <li><strong>Screenshots:</strong> <code>$SCREENSHOTS_DIR</code></li>
                <li><strong>Logs:</strong> <code>$LOGS_DIR</code></li>
                <li><strong>Test Results:</strong> <code>$RESULTS_DIR</code></li>
            </ul>
        </div>
        
        <div class="section">
            <h2>🔧 Test Execution Commands</h2>
            <pre><code># Run all tests
mvn clean test

# Run specific test suites
mvn test -Dgroups="smoke"
mvn test -Dgroups="ui,login"
mvn test -Dgroups="api,crud"
mvn test -Dgroups="bdd,cucumber"

# Run tests with specific browser
mvn test -Dbrowser=firefox -Dheadless=true</code></pre>
        </div>
        
        <div class="footer">
            <p>🎯 QuantumLeap E-Commerce Test Automation Framework</p>
            <p>Powered by Java, Maven, Selenium, TestNG, Cucumber, REST Assured & Extent Reports</p>
        </div>
    </div>
</body>
</html>
EOF
    
    log_success "Consolidated report generated: $report_file"
}

# Function to display report summary
display_summary() {
    log_info "Test Execution Summary:"
    echo "========================================"
    
    # Count reports generated
    local extent_reports=$(find "$REPORTS_DIR" -name "*ExtentReport*.html" 2>/dev/null | wc -l)
    local screenshots=$(find "$SCREENSHOTS_DIR" -name "*.png" 2>/dev/null | wc -l)
    local log_files=$(find "$LOGS_DIR" -name "*.log" 2>/dev/null | wc -l)
    
    echo "📈 Extent Reports Generated: $extent_reports"
    echo "📸 Screenshots Captured: $screenshots"
    echo "📝 Log Files Created: $log_files"
    echo "🕐 Execution Timestamp: $TIMESTAMP"
    echo "========================================"
    
    # Show latest report path
    local latest_report=$(find "$REPORTS_DIR" -name "*ExtentReport*.html" 2>/dev/null | sort | tail -1)
    if [ -n "$latest_report" ]; then
        log_success "Latest Extent Report: $latest_report"
        echo "Open in browser: file://$latest_report"
    fi
}

# Function to archive reports
archive_reports() {
    local archive_name="test_reports_$TIMESTAMP.tar.gz"
    local archive_path="$RESULTS_DIR/$archive_name"
    
    log_info "Creating test reports archive..."
    
    tar -czf "$archive_path" \
        -C "$PROJECT_DIR" \
        --exclude="*.class" \
        --exclude="target" \
        --exclude=".git" \
        "src/test/resources/reports" \
        "screenshots" \
        "logs" 2>/dev/null || true
    
    if [ -f "$archive_path" ]; then
        log_success "Reports archived: $archive_path"
    else
        log_warning "Archive creation failed or no reports to archive"
    fi
}

# Main execution function
main() {
    local command=${1:-"help"}
    
    echo "========================================"
    echo "🚀 QuantumLeap E-Commerce Report Generator"
    echo "========================================"
    
    case $command in
        "generate")
            local suite=${2:-"all"}
            create_directories
            clean_old_reports 7
            run_tests_with_reports "$suite"
            generate_consolidated_report
            display_summary
            ;;
        "clean")
            local days=${2:-7}
            clean_old_reports "$days"
            ;;
        "archive")
            create_directories
            archive_reports
            ;;
        "summary")
            display_summary
            ;;
        "help"|*)
            echo "Usage: $0 {generate|clean|archive|summary|help}"
            echo ""
            echo "Commands:"
            echo "  generate [suite]  - Run tests and generate reports"
            echo "                      Suites: smoke, ui, api, bdd, regression, all"
            echo "  clean [days]      - Clean reports older than N days (default: 7)"
            echo "  archive          - Create archive of all test reports"
            echo "  summary          - Display test execution summary"
            echo "  help             - Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0 generate smoke    # Run smoke tests with reports"
            echo "  $0 generate ui       # Run UI tests with reports"
            echo "  $0 clean 14          # Clean reports older than 14 days"
            echo "  $0 archive           # Archive all reports"
            ;;
    esac
}

# Execute main function with all passed arguments
main "$@"