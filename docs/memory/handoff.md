---
updated: 2026-11-07
review_by: 2026-12-07
---

# Handoff: the code is done, now we learn it and write

The whole app is written and tested, and it is on `main`.
Nobody needs to write Java now. Your job has two parts:

1. **Learn your layer**, so you can answer "why is this line here?" in the interview.
2. **Write your report sections** for Assignment 2 (due now) and the final report (later).

The teacher allows AI for the code. The teacher does **not** allow more than 19% AI-written text in the report.
So the report sentences must be your own words.

## 1. Get it running on your laptop (15 minutes)

You need Java 17 or newer (`java -version` to check). Then, in Git Bash, from the repo folder:

```
git pull
javac -d out -cp "lib/*" $(find src -name '*.java')
java -cp "out;lib/*" app.Main
```

Log in with these made-up demo accounts:

| Role | Username | Password |
| --- | --- | --- |
| Customer | `lan` | `lan123` |
| Staff | `hung` | `staff123` |
| Admin | `admin` | `admin123` |

Then do the demo in `plan.md` section 11 yourself, by clicking. To start again with fresh data, delete `restaurant.db`.

## 2. Who learns what

You "own" one layer in the interview. Read your files in this order. Also read `how-it-works.md` (10 minutes):
everyone must be able to trace one click through all 3 layers.

| Student | Layer | Read in this order | Lines |
| --- | --- | --- | --- |
| 1 (Ryan) | screens, `ui` | `LoginFrame` (start here), `MenuFrame`, `CartDialog`, `StaffOrdersFrame`, then the other 4 | ~700 |
| 2 | model + logic, `model` `service` `app` | `User`, `Staff`, `Admin`, `Customer`, `Order`, `OrderItem`, `MenuItem`, the 3 enums, then `OrderService`, `MenuService`, `AuthService`, `Main` | ~660 |
| 3 | database, `database` + `resources/` | `Database`, `DatabaseConnection`, `DatabaseSetup`, `MenuItemDatabase`, then `UserDatabase`, `OrderDatabase`, `DiningTableDatabase`, and the 2 `.sql` files | ~620 |

Tip: read one file, then explain it out loud to a teammate without looking. If you get stuck, that is the line to study.

## 3. Questions the teacher will probably ask

Short answers. Say them in your own words.

**Everyone**
- *Why 3 layers?* Each layer has one job. A bug points to one layer, and each person could build one part.
- *Trace "Place order".* `CartDialog.onPlaceOrder` → `OrderService.placeOrder` checks the rules →
  `OrderDatabase.insert` saves the order and its lines in one transaction → the window shows "Order 5 is PENDING".
- *Where is polymorphism?* `LoginFrame.openHomeWindow` calls `user.canManageMenu()`. Java runs the version of the
  real object (Customer, Staff or Admin), and that decides which window opens.

**Student 1 (screens)**
- *Why does each window extend `JFrame` or `JDialog`?* It inherits all the window behaviour from Swing. We only add our parts.
- *What is `e -> onLogin()`?* A lambda. It is a short way to say "when the button is clicked, run `onLogin`".
- *Why `JDialog` with `true` for the cart?* It is modal: the menu window waits until the cart closes, then updates the cart label.
- *Why catch `ValidationException` here?* The service throws it when a rule fails. The window shows its message in a pop-up.
- *Why are the click handlers `private`?* Only this window's own buttons call them.

**Student 2 (model and logic)**
- *Why is `User` abstract?* Nobody is "just a user". `getRole()` and `canManageMenu()` have no body in `User`; each child writes its own.
- *Where is overloading?* `Order.addItem` has 3 versions with different inputs. Also 2 constructors in `Order` and in `OrderItem`.
- *Where is overriding?* `Admin.canManageMenu()` replaces the `false` from `Staff` with `true`. Also every `toString()`.
- *Why does `OrderItem` copy the price?* So a later price change does not change old orders and receipts.
- *Why do setters throw `IllegalArgumentException`, not `ValidationException`?* `model` must not need `service`.
  The service catches it and throws a `ValidationException` with the same message.
- *Why is the field type `Database<MenuItem>` in `MenuService`?* It is the interface, so any class that implements it works, even a fake one for testing.

**Student 3 (database)**
- *Why `PreparedStatement`?* The values go in through `?` marks, never by joining strings. This stops SQL injection.
- *What is the transaction in `OrderDatabase.insert`?* `setAutoCommit(false)`, save the order and every line, then `commit()`.
  If anything fails, `rollback()` undoes it all. We never get an order with half its lines.
- *Why `PRAGMA foreign_keys = ON`?* SQLite does not check foreign keys unless we ask, on every connection.
  This is why you cannot delete a dish that old orders still use.
- *Why one `users` table for 3 kinds of user?* They share most columns. The `role` column tells `readUsers` which class to create.
- *Why sqlite-jdbc 3.42.0.1 and not the newest?* Newer versions also need a second library (slf4j). We keep one jar.
- *Why is the `DatabaseConnection` constructor `private`?* The class has only a static method, so nobody needs an object of it.

## 4. Writing: who writes what

Assignment 2 is only the blue sections. The skeleton is `docs/reference/report-a2-skeleton.md`.
The tables are already filled in. Check them against the code, then write the paragraphs in the `[brackets]`.

| Report part | Writer | Starts from |
| --- | --- | --- |
| A2: II.2 end (inheritance, abstract), III.1 to III.4 (classes, abstract classes, overloading, overriding, inheritance) | Student 2 | skeleton sections II.2 to III.4, `class-diagram.png` |
| A2: III.5 3-tier model, III.6 package design | Student 3 | skeleton III.5 and III.6, `package-design.png` |
| A2: III.7 access control | Student 1 | skeleton III.7 |
| A2: putting it in Word, checking format, submitting | Ryan | Times New Roman 13, justified, figures numbered |
| Final: Database (ERD, list of tables), Environment and tools | Student 3 | `resources/schema.sql` |
| Final: GUI (at least 4 screenshots, each button and what it does) | Student 1 | run the app and take screenshots |
| Final: Encapsulation vs Inheritance vs Polymorphism | everyone, one example each | section 3 above |
| Final: Conclusion, duty roster | Ryan | progress notes |

Only Ryan submits. We did not change topic, so we do not send Assignment 1 again.

## 5. What is left besides writing

- [ ] **Everyone:** pull `main` and run the app on your own laptop (section 1). Report any error in the group chat the same day.
- [ ] **Everyone:** click through the demo in `plan.md` section 11 once. A person has not done this yet; only automatic checks have.
- [ ] **Everyone:** learn your layer (section 2) and practise the questions (section 3).
- [ ] **Student 3:** draw the ERD (the diagram of the tables and how they link) from `schema.sql`.
- [ ] **Student 1:** take the GUI screenshots (at least 4) and list each button and its event.
- [ ] **Everyone:** write down your PC (CPU, RAM, operating system, Java version). The Environment section needs it.
- [ ] **Everyone:** keep the daily progress note, including the "AI used" line. The report needs it.
- [ ] **Step M5 (plan.md section 10):** a practice call. Each person explains 5 random classes; the others ask "why?".
