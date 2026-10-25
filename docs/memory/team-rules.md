---
updated: 2026-10-25
review_by: 2026-11-24
---

# Team rules

These rules keep the three of us in step. They are short on purpose. Read them once, then follow them every day.
The same rules are pinned on the architecture diagram (`docs/reference/architecture/tiers.html`).

## 1. Keep it simple

- Use only what the course teaches (Lectures 4 to 9). No frameworks and no extra libraries.
- Pick the simplest code that works and still shows the OOP idea.
- Do not commit a line that you cannot explain. The teacher will point at any line and ask "why?".
- AI help is fine, but you own the result. If the AI wrote it, you must still be able to explain it.

## 2. Write in plain English (STE)

STE means Simplified Technical English. Use it in docs, code comments, pull requests, commit messages and progress notes.

- Keep sentences short: 20 words or fewer.
- Put one idea in each sentence.
- Use common words: "use", not "utilise". "Check", not "validate the integrity of".
- Use active voice: "The service checks the cart", not "The cart is checked".
- Explain each short form the first time you use it, for example "FK (foreign key)".
- Use the same name for the same thing everywhere. Say "database class", not "DAO" in one place and "repository" in another.

## 3. Post a progress note every day

At the end of each day you work on the project, add a note to the top of your own file in `docs/memory/progress/`.
Each person has their own file, so nobody gets merge conflicts. The lead reads all three files every morning.

Copy this template:

```
## 2026-10-26
- Done: what you finished today.
- Next: what you will do next.
- Blocked: what stops you, or "nothing".
- Need from: who you need something from, and what. Or "nobody".
- AI used: what you asked the AI to do, or "none". (We need this for the report.)
```

Did you do no project work today? Then you don't need a note.

## 4. Don't break the agreed methods

- The method names in `docs/memory/plan.md` section 7 connect our three parts. Everyone codes against them.
- To change one, post in the group chat first. Wait for an OK from the person who uses that method.
- Then change `plan.md` in the same pull request as the code.

## 5. Git and pull requests

- Each person works on their own branch: `s1-ui`, `s2-service`, `s3-database`.
- Pull `main` into your branch at the start of every work day. Small, frequent updates hurt less than one big one.
- Nobody pushes directly to `main`. Every change goes in through a pull request.
- Every pull request description says three things: what you changed, why, and how you tested it.
- The lead reviews each pull request within one day and then merges it.
- Commit messages are one short line in plain English, for example "Add total price to Order".

## 6. Ask for help early

- Are you stuck for more than 2 hours? Say so in the group chat. Do not wait for your daily note.
- If someone asks you for something, reply on the same day, even if your reply is only "tomorrow".

## 7. Check in at each step

- At the end of each step (M1 to M5 in `plan.md` section 10), we have a short call.
- Each person shows what works on their own laptop. We agree on what comes next.

## 8. When a task is "done"

A task is done only when all of these are true:

1. The code compiles, and the app starts.
2. The code follows these rules and the code rules in `plan.md` section 12.
3. You can explain every line of it.
4. The matching demo step in `plan.md` section 11 works, or you wrote down why it can't work yet.
5. Your progress note says it is done.
