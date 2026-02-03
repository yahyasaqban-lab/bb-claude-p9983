#!/bin/bash

# Script to push bb-claude to GitHub repository bb-claude-p9983

set -e

echo "🚀 Pushing BlackBerry Claude to GitHub..."

# Change to project directory
cd /home/y7/Projects/bb-claude

# Check if we already have a remote
if git remote get-url origin 2>/dev/null; then
    echo "✅ Origin remote already exists"
else
    echo "❌ No origin remote found. Please create the repository first:"
    echo "   1. Go to https://github.com/new"
    echo "   2. Create repository 'bb-claude-p9983'"
    echo "   3. Run: git remote add origin <YOUR_REPO_URL>"
    exit 1
fi

# Check if we're authenticated
echo "🔍 Checking authentication..."
if git ls-remote origin >/dev/null 2>&1; then
    echo "✅ GitHub authentication working"
else
    echo "❌ Cannot access GitHub. Check your authentication:"
    echo "   - For HTTPS: git config credential.helper"
    echo "   - For SSH: ssh -T git@github.com"
    exit 1
fi

# Push to GitHub
echo "⬆️  Pushing to GitHub..."
git push -u origin master

echo "✅ Successfully pushed to GitHub!"
echo "🔗 Repository: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]//' | sed 's/.git$//')"
echo "🏗️  GitHub Actions build will start automatically"
echo "📊 Monitor build at: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]//' | sed 's/.git$//')/actions"