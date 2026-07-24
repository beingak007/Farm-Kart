#!/usr/bin/env bash
# ============================================================
# Branch Protection — main + develop
#
# Branch protection cannot be committed as a file; it is a
# repository SETTING. This script applies the full enterprise
# ruleset via the GitHub API. Run it once (and re-run any time
# to reconcile drift):
#
#   gh auth login          # needs admin access to the repo
#   ./scripts/setup-branch-protection.sh [owner/repo]
#
# Rules applied to BOTH main and develop:
#   - Pull Request required (no direct pushes)
#   - Minimum 2 approvals
#   - Code Owner review required (CODEOWNERS)
#   - Stale approvals dismissed on new commits
#   - Required status check: "Quality Gate" (from pr-checks.yml)
#   - Branch must be up-to-date before merging (strict checks)
#   - All conversations must be resolved
#   - Force pushes blocked
#   - Branch deletion blocked
#   - Rules enforced for admins too
# ============================================================
set -euo pipefail

REPO="${1:-$(gh repo view --json nameWithOwner -q .nameWithOwner)}"

# "Quality Gate" is the aggregating job in pr-checks.yml — one
# required check that covers build/test/sonar/security/owasp/docker.
REQUIRED_CHECKS='["Quality Gate"]'

protect_branch() {
  local branch="$1"
  echo "Applying protection to ${REPO}@${branch} …"

  gh api --method PUT "repos/${REPO}/branches/${branch}/protection" \
    --input - <<EOF
{
  "required_status_checks": {
    "strict": true,
    "contexts": ${REQUIRED_CHECKS}
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": true,
    "required_approving_review_count": 2,
    "require_last_push_approval": true
  },
  "restrictions": null,
  "required_linear_history": false,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "required_conversation_resolution": true,
  "lock_branch": false,
  "block_creations": false
}
EOF

  echo "✅ ${branch} protected"
}

protect_branch "main"
protect_branch "develop"

echo ""
echo "Done. Verify at: https://github.com/${REPO}/settings/branches"
