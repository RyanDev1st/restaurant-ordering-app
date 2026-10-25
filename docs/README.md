# Docs index

| Bucket | Holds | Lifecycle |
| --- | --- | --- |
| `docs/memory/` | State in force: decisions, current plans, model routing | edit in place; delete when untrue |
| `docs/research/` | Dated briefs and scans (`YYYY-MM-DD-topic.md`) | newer same topic supersedes; old to legacy |
| `docs/reference/` | How the system works now | edit in place |
| `docs/findings/` | Dated audits and reports | immutable; supersede by date |
| `docs/legacy/` | Superseded material | read only when asked |

## Files

- `docs/memory/plan.md`: team plan: decisions, layers, agreed methods, who does what, steps, demo
- `docs/memory/team-rules.md`: how we work together: simplicity, STE, daily notes, git, "done"
- `docs/memory/progress/`: daily progress notes, one file per student (newest on top)
- `docs/memory/model-routing.md`: worker model choice and fallbacks
- `docs/reference/how-it-works.md`: the 3 layers and one click traced step by step, in plain words
- `docs/reference/report-guideline.md`: teacher's required report sections, condensed
- `docs/reference/architecture/`: architecture diagram of the app (drawn from plan.md)
