Gotcha, RedInside. You want a **single, copy-paste “Ask Cursor” prompt** that tells Cursor to read the Cursor-CLI “code review” cookbook page and then wire up a **GitHub Action** that runs code reviews on every PR using the Cursor CLI.

Below is that prompt (ready to paste into Cursor). After it, I’ve included a **starter workflow YAML** with safe placeholders so you can see the intended shape.

---

# Prompt for Cursor (paste this into Cursor’s chat)

**Role:** You are an expert DevOps engineer.
**Goal:** Implement automated PR code reviews in this repository using **Cursor CLI** exactly as described in the official “Code Review” cookbook: [https://docs.cursor.com/en/cli/cookbook/code-review](https://docs.cursor.com/en/cli/cookbook/code-review).

## What to do (end-to-end)

1. **Read the docs page** above and follow the *recommended* approach for running Cursor CLI inside CI on GitHub Actions to review pull requests. Use the exact CLI command(s) and flags that the cookbook prescribes for PR review.

2. **Create a new GitHub Actions workflow** at `.github/workflows/cursor-code-review.yml` that:

   * Triggers on `pull_request` events: `opened`, `synchronize`, and `reopened`.
   * Has `permissions` set minimally to allow adding PR review comments (e.g., `pull-requests: write`, `contents: read`).
   * Checks out the repo with full history required by the cookbook (adjust `fetch-depth` accordingly).
   * **Installs Cursor CLI** the way the docs say (don’t guess—use the cookbook’s install steps).
   * Authenticates the CLI using a repo/org secret (e.g., `CURSOR_API_KEY` or whatever name the docs require). Do **not** echo the token.
   * Generates a review for the **diff of the PR** (base vs head). Use the cookbook’s command for “review this PR” or “review this diff,” including any flags for structured output, severity levels, or inline comments.
   * **Posts inline comments** on the PR the way the cookbook recommends. If the cookbook supports direct GitHub commenting via the CLI, use it. If it recommends piping to `gh` or `reviewdog`, follow that path.
   * Fails the job **only if** the cookbook recommends failing on certain severities. Otherwise, leave `fail-on-error` off so the review is advisory.

3. **Model & scope**

   * Use the model and options recommended in the cookbook. If multiple are supported, choose the **default** model specified by the docs.
   * Ensure the review only comments on **changed lines** (hunk-level commentary) to avoid noise.
   * If the cookbook supports a “checklist” or “rules” file, create `.cursor/review-rules.md` and load it as the docs show. Include items for correctness, security, readability, error handling, performance, and tests. Keep it terse.

4. **Secrets & docs**

   * Add a `README.md` section titled “Automated PR Reviews (Cursor CLI)” that explains:

     * What the action does
     * Which secrets are needed and the exact secret names
     * How to change model/strictness
     * How to run locally (the cookbook’s local invocation)
   * Confirm the **secret names and env vars** exactly match the cookbook (no placeholders).

5. **Acceptance criteria**

   * Open a test PR and have the action run.
   * The action adds inline review comments that clearly map to the changed lines.
   * The workflow passes with no plaintext token leakage.
   * The README section exists and matches the implementation.
   * All commands and flags match the cookbook; don’t substitute your own unless the docs give alternatives.

6. **Deliverables**

   * `.github/workflows/cursor-code-review.yml`
   * `.cursor/review-rules.md` (if supported by docs)
   * `README.md` updates (Automation section)

Make only the minimum necessary changes to the repo to satisfy the docs. When done, summarize what you implemented and point me to the test PR run.

---

## Starter workflow (shape & permissions only — Cursor will replace commands per the cookbook)

> **Note:** This is a scaffold so you see the structure. Cursor should replace the install/auth/run steps with the **exact** ones from the cookbook page.

```yaml
name: Cursor CLI – PR Code Review

on:
  pull_request:
    types: [opened, synchronize, reopened]

permissions:
  contents: read
  pull-requests: write

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          # Adjust if the cookbook needs more history for proper diffs
          fetch-depth: 0

      # ⬇ Replace this block with the cookbook’s official install steps
      - name: Install Cursor CLI
        run: |
          echo "Install Cursor CLI per cookbook (Cursor will fill this in)."

      # ⬇ Replace with the exact env var names from the docs (e.g., CURSOR_API_KEY)
      - name: Configure auth
        env:
          CURSOR_API_KEY: ${{ secrets.CURSOR_API_KEY }}
        run: |
          echo "Configure Cursor CLI auth per cookbook (Cursor will fill this in)."

      # ⬇ Replace with the cookbook’s exact review command & flags
      - name: Run code review on PR diff
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          echo "Run cursor CLI review per cookbook (Cursor will fill this in)."

      # ⬇ If the cookbook uses gh/reviewdog for comments, keep a step like this
      - name: Post inline comments
        if: always()
        run: |
          echo "Post PR comments per cookbook (Cursor will fill this in)."
```

---

### Why I wrote it this way

* You asked for a **Cursor-targeted prompt** that instructs it to **read and follow the official cookbook**. Since the CLI syntax and secret names are defined in that doc (and may change), the prompt explicitly forces Cursor to copy the **exact** commands/flags and secret names from the source.
* The scaffolded YAML shows the **structure and permissions** you’ll want in a GH Action, while letting Cursor fill in the authoritative steps from the doc.

If you want, I can also generate a **rules/checklist** starter (`.cursor/review-rules.md`) tailored to **Java/Spring Boot** so PR feedback skews toward your stack (nullability, Spring security misconfig, controller validation, transaction boundaries, N+1 JPA, etc.).
