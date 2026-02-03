#!/bin/bash

# Script to monitor GitHub Actions build for bb-claude-p9983

set -e

# Get repository URL from git remote
REPO_URL=$(git remote get-url origin 2>/dev/null || echo "")
if [ -z "$REPO_URL" ]; then
    echo "❌ No GitHub remote found. Please set up the repository first."
    exit 1
fi

# Extract owner/repo from URL
REPO_PATH=$(echo "$REPO_URL" | sed 's/.*github.com[:/]//' | sed 's/.git$//')
OWNER=$(echo "$REPO_PATH" | cut -d'/' -f1)
REPO=$(echo "$REPO_PATH" | cut -d'/' -f2)

echo "📊 Monitoring GitHub Actions for $OWNER/$REPO"
echo "🔗 Repository: https://github.com/$REPO_PATH"
echo "🏗️  Actions: https://github.com/$REPO_PATH/actions"

# Function to check build status using GitHub CLI
check_with_gh() {
    if command -v gh >/dev/null 2>&1; then
        echo "🔍 Checking latest workflow run..."
        gh run list --repo "$REPO_PATH" --limit 1 --json status,conclusion,url,displayTitle
    else
        echo "❌ GitHub CLI not available. Please check manually at:"
        echo "   https://github.com/$REPO_PATH/actions"
    fi
}

# Function to get artifacts
get_artifacts() {
    if command -v gh >/dev/null 2>&1; then
        echo "📦 Checking for APK artifacts..."
        gh run list --repo "$REPO_PATH" --status completed --limit 5 --json databaseId,conclusion | \
        jq -r '.[] | select(.conclusion=="success") | .databaseId' | \
        head -1 | \
        xargs -I {} gh api repos/$REPO_PATH/actions/runs/{}/artifacts | \
        jq -r '.artifacts[] | select(.name | contains("apk")) | "🎯 APK Download: " + .archive_download_url'
    else
        echo "💡 To download APK after build completes:"
        echo "   1. Go to: https://github.com/$REPO_PATH/actions"
        echo "   2. Click on the latest successful build"
        echo "   3. Download the 'bb-claude-debug-apk' artifact"
    fi
}

# Main monitoring loop
echo "⏳ Starting build monitor..."
check_with_gh

echo ""
echo "🎯 Quick Actions:"
echo "   - View Actions: https://github.com/$REPO_PATH/actions"
echo "   - View Repository: https://github.com/$REPO_PATH"
echo "   - Monitor build: gh run watch --repo $REPO_PATH"

# Check for artifacts from recent builds
echo ""
get_artifacts