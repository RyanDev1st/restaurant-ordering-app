# Restaurant Ordering App

A Java OOP group project for 3 students: a Swing desktop app where customers order food,
staff prepare the orders, and an admin runs the menu. The data is saved in SQLite through JDBC.

- Live diagrams and docs: https://ryandev1st.github.io/restaurant-ordering-app/
- Start here: [docs/reference/how-it-works.md](docs/reference/how-it-works.md)
- Team plan: [docs/memory/plan.md](docs/memory/plan.md)
- Team rules: [docs/memory/team-rules.md](docs/memory/team-rules.md)

## Build and run (Git Bash, from the repo root)

```
javac -d out -cp "lib/*" $(find src -name '*.java')
java -cp "out;lib/*" app.Main
```
