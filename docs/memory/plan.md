---
updated: 2026-10-25
review_by: 2026-11-24
---

# Restaurant Ordering App: team plan

We are building a desktop app in Java. Customers order food. Staff prepare the orders.
An admin looks after the menu and the staff accounts.

This is a Java OOP course project (Lectures 4 to 9). The teacher grades the app and then gives us a
20-minute interview. In the interview, the teacher asks why each part of the code exists.
So every line must be simple enough for us to explain.

Read `docs/reference/how-it-works.md` first. It shows the big picture with diagrams.
Then read `docs/memory/team-rules.md`. It says how we work together every day.
This page gives the details: what we decided, who builds what, and the method names we all agree on.

## 1. What we decided, and why

The last column is the answer to give if the teacher asks "why did you do this?".

| Topic | Our choice | Why |
| --- | --- | --- |
| Language | Plain Java 17 or newer. No frameworks. | The course teaches plain Java. A framework would hide the OOP we must show. |
| Screens | Swing | It comes with Java, so there is nothing to install. Lecture 8 teaches it. |
| Saved data | SQLite, through JDBC | JDBC is the same as with MySQL (Lecture 9). But SQLite is one file and needs no server. To move to MySQL, we only change one line. |
| Structure | 3 layers: screens, logic, database access | One student builds each layer. Each layer only talks to the next one. |
| Build | `javac` and one driver file in `lib/` | There are no build tools to explain. |

## 2. Who uses the app

| User | What they can do |
| --- | --- |
| Customer | Make an account, log in, look at the menu, filter it by type, search it, fill a cart, place an order, follow the order, save a receipt. |
| Staff | Log in, see the list of open orders, move an order to its next step, cancel an order, mark a dish as sold out. |
| Admin | Everything staff can do. The admin can also add, change and remove dishes, and add or remove staff accounts. |

Most of our effort goes into the customer and staff parts. The admin part stays small: two screens.

## 3. How the parts connect

```
 Student 1             Student 2               Student 3
 SCREENS     ----->    LOGIC        ----->     DATABASE ACCESS   ----->   restaurant.db
 (ui)                  (service)               (database)                 (one SQLite file)
     \                     |                        /
      \---------->   MODEL CLASSES (model)   <-----/
                     used by all 3 layers
```

- **Screens** call service methods only. They never contain SQL.
- **Logic** (the services) uses the database classes only. All the rules live here.
  When a rule fails, the service throws an exception.
- **Database access** runs SQL only. It turns table rows into model objects, and model objects into rows.
  It has no rules and no windows.
- **Model classes** are simple classes that hold data, for example `MenuItem` and `Order`.
  All three layers use them, so Student 2 writes them first (step M1 in section 10).
- **`app.Main`** starts the app. It creates the database if it is missing, then creates the database
  classes and the services, and then opens the login window.

## 4. Folders and files

```
src/app/        Main.java
src/model/      User, Customer, Staff, Admin, MenuItem, Order, OrderItem, Role, Category, OrderStatus
src/database/   Database<T>, DatabaseConnection, DatabaseSetup,
                UserDatabase, MenuItemDatabase, OrderDatabase, DiningTableDatabase
src/service/    AuthService, MenuService, OrderService, ReceiptWriter, ValidationException, AuthException
src/ui/         LoginFrame, RegisterDialog, MenuFrame, CartDialog, OrderStatusFrame,
                StaffOrdersFrame, AdminMenuFrame, AdminStaffFrame
resources/      schema.sql (creates the tables), seed.sql (sample data)
lib/            sqlite-jdbc-<version>.jar (lets Java talk to SQLite)
```

## 5. Model classes (Student 2 writes them, all of us check them)

| Class | Type | What it holds | What it does |
| --- | --- | --- | --- |
| `User` | **abstract** class | id, username, password, full name (all `private`) | getters and setters. Two abstract methods: `getRole()` and `canManageMenu()`. |
| `Customer` | extends `User` | phone number | gives its own `getRole()`. `canManageMenu()` returns false. |
| `Staff` | extends `User` | job title | gives its own `getRole()`. `canManageMenu()` returns false. |
| `Admin` | extends `Staff` | nothing extra | gives its own `getRole()`. `canManageMenu()` returns true. |
| `MenuItem` | class | id, name, category, price, available or sold out | its own `toString()` |
| `Order` | class | id, customer, table number, status, time, list of `OrderItem` | `addItem(MenuItem)` and `addItem(MenuItem, int qty)`: same name, different inputs (**overloading**). `getTotal()`. |
| `OrderItem` | class | dish, quantity, price at the time of the order | `getSubtotal()` |
| `Role` | enum | CUSTOMER, STAFF, ADMIN | |
| `Category` | enum | MAIN, DRINK, DESSERT, SIDE | |
| `OrderStatus` | enum | PENDING, PREPARING, READY, SERVED, CANCELLED | `next()` gives the next step |

That is 7 classes and 3 enums. The teacher's guideline asks for 5 to 7 classes, with 10 at most.

## 6. Database tables (Student 3): 5 tables

"PK" (primary key) is the column that identifies each row. "FK" (foreign key) is a column that points to a row in another table.

| Table | Columns |
| --- | --- |
| `users` | id (PK), username (must be unique), password, full_name, role, phone, position |
| `menu_items` | id (PK), name, category, price, available |
| `dining_tables` | number (PK), seats |
| `orders` | id (PK), customer_id (FK to `users`), table_number (FK to `dining_tables`), status, created_at |
| `order_items` | id (PK), order_id (FK to `orders`), menu_item_id (FK to `menu_items`), quantity, unit_price |

- All three kinds of user share the `users` table. The `role` column tells us which class to create.
- We copy the price into `order_items` when the customer orders. Then a later price change
  does not change old orders.
- `seed.sql` adds sample data: 1 admin, 2 staff, 2 customers, about 15 dishes and 6 tables.

## 7. The methods we all agree on (these are the "musts")

These method names connect the layers. Each person can build their part alone, because we all know these names.
If you want to change one of them, ask the team first.

**Between logic and database access.** Every database class has these 5 methods. They come from one
shared interface (Lecture 5: interfaces, Lecture 7: generics). `T` stands for the model class, for example `MenuItem`.

```java
public interface Database<T> {
    List<T> findAll();       // every row in the table
    T findById(int id);      // one row, or null if it is not there
    int insert(T item);      // adds a row and gives back its new id
    void update(T item);     // changes a row
    void delete(int id);     // removes a row
}
```

A database class can have more methods if it needs them, for example
`UserDatabase.findByUsername(String)`, `OrderDatabase.findByStatus(OrderStatus)` and `OrderDatabase.findByCustomer(int)`.
`OrderDatabase.insert` saves the order and all its items together. If one part fails, it saves nothing
(this is called a **transaction**).

**Between screens and logic.** Student 2 writes these methods. Student 1 calls them from the windows.

| Service | Methods |
| --- | --- |
| `AuthService` | `User login(String username, String password)`, `Customer register(...)` |
| `MenuService` | `List<MenuItem> getAvailableItems()`, `getByCategory(Category)`, `search(String)`, `addItem`, `updateItem`, `deleteItem`, `setAvailable(int id, boolean)` |
| `OrderService` | `Order placeOrder(Customer, int table, List<OrderItem> cart)`, `List<Order> getQueue()`, `List<Order> getOrdersOf(Customer)`, `void advanceStatus(int orderId)`, `void cancel(int orderId)` |
| `ReceiptWriter` | `void save(Order order, File file)` writes the receipt to a .txt file (Lecture 6: files) |

When something goes wrong, the service throws an exception. It does not return `null` or a special number.
- `AuthException`: wrong username or password.
- `ValidationException`: empty cart, bad price, sold-out dish, or a step that is not allowed.

The window catches the exception and shows its message with `JOptionPane.showMessageDialog(...)`.

## 8. Where each lecture topic shows up in our code

The teacher will ask about these, so we want each topic in a place that is easy to point to.

| Lecture | Topic | Where we use it |
| --- | --- | --- |
| 4 | class, object, method | every model class |
| 5 | inheritance | `Customer` and `Staff` extend `User`. `Admin` extends `Staff`. |
| 5 | abstract class | `User` |
| 5 | package, interface | our 5 packages. The `Database<T>` interface. |
| 6 | exceptions | `ValidationException`, `AuthException`, and try/catch around the SQL code |
| 6 | enums | `Role`, `Category`, `OrderStatus.next()` |
| 6 | files | `ReceiptWriter`. `DatabaseSetup` reads `schema.sql`. |
| 7 | generics, ArrayList | `Database<T>`, `List<MenuItem>`, the list of items inside `Order` |
| 7 | lambda | button click handlers, `items.removeIf(i -> !i.isAvailable())`, sorting by price |
| 8 | GUI | 8 Swing windows (the report needs at least 4 screenshots) |
| 9 | JDBC | `DatabaseConnection`, `PreparedStatement`, the transaction in `OrderDatabase` |
| | overloading | `Order.addItem(...)`, two versions |
| | overriding | `getRole()` and `toString()` |
| | polymorphism | After login we call `user.canManageMenu()`. Each kind of user answers in its own way, and that decides which window opens. |
| | encapsulation | Every field is `private`. Setters check the value, for example price must be more than 0. |

## 9. Who does what

**Student 1 (Ryan, team lead): screens, package `ui`**
- Draw the wireframes (rough sketches of each screen) for the 8 windows first. Share them before anyone codes the screens.
- Build the Swing windows. The windows call services only. When a service throws an exception, show its message in a pop-up.
- After login, open the right window: customers go to `MenuFrame`, staff to `StaffOrdersFrame`, admins to `AdminMenuFrame`.
- Report sections: Introduction (table of users, use case diagram), GUI (4 or more screenshots), Conclusion.
- As lead: look after the `main` branch, check and merge every pull request, and keep the duty roster.

**Student 2: logic, packages `model`, `service` and `app`**
- Write the model classes first and get them approved (step M1), because everyone needs them.
- Write the services with all the rules: login check, cart check, total price, order steps, and admin-only actions.
- Until the real database classes are ready, use a fake one that keeps data in a list:
  `FakeMenuItemDatabase implements Database<MenuItem>`. This is also a good interview example of why interfaces help.
- Report sections: Class Analysis, Class Design, abstract classes, overloading and overriding, access control tables.

**Student 3: database access, package `database`, plus `resources/` and `lib/`**
- Draw the ERD (the diagram of the tables and how they link). Write `schema.sql` and `seed.sql`.
  Download the SQLite driver jar into `lib/`.
- Write `DatabaseConnection` (the database address lives here only), `DatabaseSetup` (creates the tables
  on the first run) and the 4 database classes.
- Always use `PreparedStatement`. Never build SQL by joining strings together. This stops SQL injection,
  and the teacher will probably ask about it.
- Report sections: Database (ERD, list of tables), Environment and Tools, Package Design.

**Everyone:** For the section on encapsulation, inheritance and polymorphism, use examples from your own code.
Also learn section 7, so you can explain how your part connects to the others.

## 10. Steps and checkpoints

| Step | What we deliver | Who | Done when |
| --- | --- | --- | --- |
| M1 | model classes, the `Database<T>` interface, empty service methods | S2, S3 | it compiles and the lead approves it |
| M2 | wireframes; tables and sample data; services working with the fake database class | S1, S3, S2 | the team reviews it on a call |
| M3 | real database classes; Swing windows using the real services | S3, S1 | one full order works from start to finish |
| M4 | staff and admin screens, receipt file, error pop-ups | all | the demo in section 11 works |
| M5 | report and practice: each person explains "why" for 5 random classes | all | the report is submitted |

## 11. The demo (our final test)

The app is done when all of these work:

1. A customer makes an account and logs in. They filter by "Drink", add 2 dishes (one of them 3 times),
   and order for table 4.
2. A wrong password shows an error. An empty cart cannot be ordered.
3. Staff log in and see the order as PENDING. They move it to PREPARING, then READY, then SERVED.
4. The customer sees the new status and saves a receipt as a .txt file.
5. The admin adds a dish and marks another one as sold out. The customer no longer sees the sold-out dish.
6. We close the app and open it again. All the data is still there.

## 12. Code rules (our standard)

- Every field is `private`. Use getters and setters. A setter throws an exception when the value is wrong.
- One public class per file. A file has 300 lines at most. If a method grows past about 30 lines, split it.
- Names say what the code does: `placeOrder`, not `doIt`. Each class starts with a one-line comment that says its job.
- Do not commit code you cannot explain. This includes code from AI or from the internet.
- Git: each person has their own branch (`s1-ui`, `s2-service`, `s3-database`). Open a pull request into `main`.
  The lead merges it.
