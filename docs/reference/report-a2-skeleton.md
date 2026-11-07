---
updated: 2026-11-07
review_by: 2026-12-07
---

# Report skeleton: Assignment 2 (the blue sections)

Assignment 2 is the **class design**: how each class is built, and why.
The blue parts of the teacher's guideline are: the end of II.2 (inheritance and abstract classes), and in III,
Classes, Abstract classes, OOP techniques, Inheritance, the 3-tier model, Package Design and Access Control.
Interface Design and "Encapsulation vs Inheritance vs Polymorphism" are not blue, so they are not part of this assignment.

How to use this page:
- Same format as Assignment 1: Times New Roman 13, justified. Text in `[brackets]` tells you what to write. Delete it after.
- The **tables are filled in**, because they are design facts we agreed on. Check them. Do not just copy them.
- The **paragraphs are yours to write**. The teacher allows at most 19% AI-written text.
  Each bracket lists the points to make. Every table and figure needs at least one paragraph about it.
- Code samples are copied from the real code in `src/` (built and tested on 2026-11-07).
- Groups that changed topic must also submit Assignment 1. We did not change topic, so we don't.

---

## II.2 Classes (the end part): inheritance and abstract classes

This part is already in `report-a1-skeleton.md` (the end of II.2), and the figure is `architecture/inheritance.png`.
Analysis stage: class names only, no data types, no method details.

[A short paragraph for each point:]
- [Customer and Staff are kinds of User. They share an id, a username, a password and a full name, and they all log in.]
- [Admin is a kind of Staff. An admin does all staff work, and can also manage the menu and the staff.]
- [User is abstract: nobody is "just a user". Each child must answer two questions in its own way:
  "what is your role?" and "can you manage the menu?".]
- [Two more "is a" links come from Java itself: our windows are kinds of `JFrame`, and our two
  error classes are kinds of `Exception`. Mention them in one sentence.]

---

## III. CLASS DESIGN

### III.1 Classes

**Figure 3: Class diagram of the model package.** Paste `architecture/class-diagram.png`.

[One paragraph that walks through Figure 3. Points:
- The left side is the people. One abstract parent, `User`, and three children.
- The right side is the ordering. An `Order` **owns** its `OrderItem`s (filled diamond): if the order is deleted,
  its lines go with it. Each line points to one `MenuItem`.
- `Order` keeps `customerId` and `tableNumber` as numbers, not as objects. Why: that is how the database
  stores them, so loading an order stays simple.
- The three enums hold fixed lists of values (roles, categories, order steps).
- Getters and setters are left out of the picture to keep it readable. Table 5 lists them.]

**Table 5: Details of classes.** One row per class. Modifiers are explained in the last column and in III.7.

| No | Class | Instance variables | Methods | Description |
| --- | --- | --- | --- | --- |
| 1 | `User` (abstract) | `private int id`, `private String username`, `private String password`, `private String fullName` | `protected User(id, username, password, fullName)`; public getters; `setUsername`, `setPassword`, `setFullName` check the value; `setId(int)` (the database gives the id after saving); `public abstract Role getRole()`; `public abstract boolean canManageMenu()`; `public String toString()` | Holds what every person has. The constructor is `protected`, because only the child classes call it. |
| 2 | `Customer` extends `User` | `private String phone` | `public Customer(id, username, password, fullName, phone)`; `getPhone()`; `setPhone(String)` checks for 10 digits; `getRole()` gives CUSTOMER; `canManageMenu()` gives false | A guest who orders food. |
| 3 | `Staff` extends `User` | `private String position` | `public Staff(..., position)`; `getPosition()`, `setPosition(String)`; `getRole()` gives STAFF; `canManageMenu()` gives false | A worker who runs the order queue. |
| 4 | `Admin` extends `Staff` | none (it gets everything from `Staff` and `User`) | `public Admin(..., position)`; `getRole()` gives ADMIN; `canManageMenu()` gives true | A staff member who can also manage the menu and staff. |
| 5 | `MenuItem` | `private int id`, `private String name`, `private Category category`, `private int price`, `private boolean available` | `public MenuItem(id, name, category, price, available)`; getters; `isAvailable()`; `setId(int)`; `setName` checks it is not empty; `setPrice` checks price > 0; `setAvailable(boolean)`; `toString()` | One dish or drink. The price is an `int` in VND, because VND has no decimals. |
| 6 | `Order` | `private int id`, `private int customerId`, `private int tableNumber`, `private OrderStatus status`, `private LocalDateTime createdAt`, `private final List<OrderItem> items` | two constructors; `addItem(MenuItem)`, `addItem(MenuItem, int)`, `addItem(OrderItem)`; `getItems()` gives a copy; `getTotal()`; `isEmpty()`; getters; `setId(int)`; `setStatus(OrderStatus)`; `toString()` | One order for one table. `items` is `final`: the list itself never changes, only what is in it. |
| 7 | `OrderItem` | `private MenuItem menuItem`, `private int quantity`, `private int unitPrice` | two constructors; getters; `setQuantity` checks it is at least 1; `getSubtotal()`; `toString()` | One line of an order. It copies the price at order time, so a later price change does not change old orders. |
| 8 | `DiningTable` | `private int number`, `private int seats` | `public DiningTable(number, seats)`; getters; `setSeats` checks seats > 0 | One table in the restaurant. |
| 9 | `Role` (enum) | values CUSTOMER, STAFF, ADMIN | none | The fixed list of user kinds. Saved in the `role` column. |
| 10 | `Category` (enum) | values MAIN, DRINK, DESSERT, SIDE | none | The fixed list of menu types, used by the filter. |
| 11 | `OrderStatus` (enum) | values PENDING, PREPARING, READY, SERVED, CANCELLED | `public OrderStatus next()` | The steps of an order. `next()` gives the next step and throws an error after SERVED or CANCELLED. |
| 12 | `AuthService` | `private final UserDatabase users` | `login(username, password)` gives a `User` or throws `AuthException`; `register(username, password, fullName, phone)` gives a `Customer`; `getStaff()`; `addStaff(username, password, fullName, position)`; `removeStaff(int id)`; `private checkUsernameFree(String)` | Logging in and managing accounts. |
| 13 | `MenuService` | `private final Database<MenuItem> menu` | `getAvailableItems()`, `getAllItems()`, `getByCategory(Category)`, `search(String)`, `addItem(name, category, price)`, `updateItem(id, name, category, price)`, `deleteItem(int id)`, `setAvailable(int id, boolean)`; `private findOrFail(int id)` | The menu rules. The variable's type is the interface, so we can swap in a fake database for testing. |
| 14 | `OrderService` | `private final OrderDatabase orders`, `private final Database<MenuItem> menu`, `private final Database<DiningTable> tables` | `placeOrder(Customer, int table, List<OrderItem> cart)`, `getQueue()`, `getOrdersOf(Customer)`, `advanceStatus(int orderId)`, `cancel(int orderId)`; `private void checkCart(List<OrderItem>)`; `private findOrFail(int orderId)` | The order rules: no empty cart, no sold-out dish, the table must exist, only allowed steps. |
| 15 | `ReceiptWriter` | none | `public void save(Order order, File file) throws IOException` | Writes a receipt to a .txt file. |
| 16 | `ValidationException`, `AuthException` (extend `Exception`) | none | `public ...(String message)` | Our two error types. Checked exceptions, so the compiler makes the windows handle them. |
| 17 | `Database<T>` (interface) | none | `findAll()`, `findById(int)`, `insert(T)`, `update(T)`, `delete(int)` | The 5 methods every database class must have. |
| 18 | `UserDatabase`, `MenuItemDatabase`, `OrderDatabase`, `DiningTableDatabase` (implement `Database<T>`) | none | the 5 interface methods; extras: `UserDatabase.findByUsername(String)`, `UserDatabase.findByRole(Role)`, `OrderDatabase.findByStatus(OrderStatus)`, `OrderDatabase.findByCustomer(int)`; private helpers that turn a table row into an object | One class per table. The only classes with SQL. |
| 19 | `DatabaseConnection` | `private static final String URL = "jdbc:sqlite:restaurant.db"` | `private DatabaseConnection()`; `public static Connection open()` | The database address lives here only. The constructor is `private`: nobody needs an object of it. |
| 20 | `DatabaseSetup` | none | `public static void createIfMissing()`; `private static void runScript(String path)` | Creates the tables and sample data the first time the app runs. |
| 21 | `Main` | none | `public static void main(String[] args)` | Creates the database classes and the services, then opens `LoginFrame`. |
| 22 | 8 windows, for example `MenuFrame` (extends `JFrame`) | `private final MenuService menuService`, `private final OrderService orderService`, `private final Customer customer`, `private final List<OrderItem> cart` | `public MenuFrame(...)`; private click handlers such as `onAddToCart()` and `onPlaceOrder()` | One window each. Only the constructor is public; the handlers are private because only the window's own buttons use them. |

[One paragraph about Table 5. Points: every variable is `private`; `final` marks things that never change after
the object is made; `static` is used only where no object is needed (`main`, `DatabaseConnection.open()`,
`DatabaseSetup`); a setter checks the value and throws `IllegalArgumentException` when it is wrong.]

Why setters throw `IllegalArgumentException` and not `ValidationException`:
`ValidationException` lives in `service`. If `model` used it, the model would need the service package,
and the two would depend on each other. So the model throws Java's own "bad value" error, and the service
catches it and throws a `ValidationException` with the same message.

### III.2 Abstract classes

**Table 6: Abstract classes**

| No | Abstract class | Abstract methods | Concrete methods | Description |
| --- | --- | --- | --- | --- |
| 1 | `User` | `getRole()`, `canManageMenu()` | constructor, `getId()`, `getUsername()`, `getPassword()`, `getFullName()`, the 4 setters, `toString()` | The parent of `Customer`, `Staff` and `Admin` (see Figure 3). Nobody is "just a user", so we cannot write `new User(...)`. |

[One paragraph. Points: an abstract class can have both kinds of method. The concrete ones are shared by
every child, so we write them once. The abstract ones force each child to give its own answer.
Also say why `Database<T>` is an interface and not an abstract class: it has no shared code or data, only a list
of methods that each database class promises to have.]

### III.3 Some OOP techniques

#### III.3.1 Overloading

Same method name, different inputs. Java picks the version by looking at the inputs.

| Class | Overloaded methods |
| --- | --- |
| `Order` | `addItem(MenuItem)`, `addItem(MenuItem, int)`, `addItem(OrderItem)` |
| `Order` | two constructors: a new order, and an order loaded from the database |
| `OrderItem` | two constructors: copy today's price, or keep the saved price |

Code (`src/model/Order.java`):

```java
// Overloading: three methods share the name addItem but take different inputs.
public void addItem(MenuItem item) {
    addItem(item, 1);
}

public void addItem(MenuItem item, int qty) {
    // qty is a copy of the caller's number (call by value).
    // item is a copy of the reference, so it points to the same MenuItem object.
    items.add(new OrderItem(item, qty));
}

public void addItem(OrderItem line) {
    items.add(line);
}
```

[One paragraph. Points: `addItem(pho)` adds one; `addItem(tea, 3)` adds three; `addItem(line)` is used when an
order is loaded from the database. The one-input version calls the two-input version, so the logic is in one place.
Call by value: Java always copies what you pass in. For a number, it copies the number. For an object, it copies the
reference, so both point to the same object.]

#### III.3.2 Overriding

A child class gives its own version of a method it got from its parent.

| Class | Overridden methods | Comes from |
| --- | --- | --- |
| `Customer`, `Staff`, `Admin` | `getRole()`, `canManageMenu()` | `User` |
| `Admin` | `canManageMenu()` again: `Staff` says false, `Admin` says true | `Staff` |
| `User`, `MenuItem`, `Order`, `OrderItem` | `toString()` | `Object` |
| the 4 database classes | the 5 methods of `Database<T>` | `Database<T>` |

Code (`src/model/Staff.java` and `src/model/Admin.java`):

```java
public class Staff extends User {
    @Override
    public boolean canManageMenu() { return false; }
}

public class Admin extends Staff {
    @Override
    public boolean canManageMenu() { return true; } // replaces the "false" from Staff
}
```

[One paragraph. Points: `@Override` asks the compiler to check that the parent really has this method.
After login we call `user.canManageMenu()` without knowing which kind of user it is. Java runs the child's version.
This is polymorphism, and it decides which window opens.]

Example run (we tested this with the design code):

```
Tran Thi Lan (CUSTOMER) canManageMenu=false
Le Van Hung (STAFF) canManageMenu=false
Nguyen Thi Mai (ADMIN) canManageMenu=true
```

### III.4 Inheritance

Techniques we use:

| Technique | Where |
| --- | --- |
| `extends` one parent | `Customer extends User`, `Staff extends User` |
| Many levels | `User` → `Staff` → `Admin` |
| Abstract parent with abstract methods | `User` |
| `super(...)` calls the parent's constructor | every child constructor |
| `protected` constructor | `User`: only the children use it |
| `implements` an interface | the 4 database classes implement `Database<T>` |
| Extending Java's own classes | windows extend `JFrame` / `JDialog`; our errors extend `Exception` |

Code (`src/model/User.java`, shortened, and `src/model/Customer.java`):

```java
public abstract class User {
    private int id;
    private String username;
    private String password;
    private String fullName;

    // protected: only the child classes (Customer, Staff, Admin) call this.
    protected User(int id, String username, String password, String fullName) {
        this.id = id;
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
    }

    public abstract Role getRole();
    public abstract boolean canManageMenu();
}

public class Customer extends User {
    private String phone;

    public Customer(int id, String username, String password, String fullName, String phone) {
        super(id, username, password, fullName);
        setPhone(phone);
    }
}
```

[One paragraph. Points: `Customer` does not repeat the 4 shared fields; it gets them from `User`.
`super(...)` must be the first line, because the parent part is built first.
The child cannot touch `username` directly (it is `private` in `User`); it uses the public getters and setters.]

### III.5 The 3-tier model

**Figure 4: The three layers.** Paste `architecture/tiers.visual-check.2048x1320.light.png`,
or describe it with Figure 5 below.

[One paragraph. Points: screens (`ui`) call only services; services call only database classes;
only database classes use JDBC. Why: each student builds one layer alone, and a bug points to one layer.
Example: placing an order goes `MenuFrame` → `OrderService.placeOrder` → `OrderDatabase.insert` → `restaurant.db`.]

### III.6 Package design

**Figure 5: Packages and their classes.** Paste `architecture/package-design.png`.

**Table 7: Packages**

| No | Package | Classes | Job | Can use |
| --- | --- | --- | --- | --- |
| 1 | `app` | `Main` | start the app and connect the parts | all packages |
| 2 | `ui` | the 8 windows | show things, react to clicks | `service`, `model` |
| 3 | `service` | 3 services, `ReceiptWriter`, 2 exceptions | the rules | `database`, `model` |
| 4 | `database` | `Database<T>`, 4 database classes, `DatabaseConnection`, `DatabaseSetup` | SQL and JDBC | `model` |
| 5 | `model` | 8 classes, 3 enums | hold data | nothing of ours |

[One paragraph. Points: the arrows only go down, never up or back. `model` uses none of our packages,
so everyone can build on it from day one. No `utils` package: every class has one home.]

### III.7 Access control

**Table 8: Data access control**

| No | Data | Class | Modifier | Description |
| --- | --- | --- | --- | --- |
| 1 | username, fullName | `User` | `private` | Read through public getters. Changed only through setters, which check the value (a username cannot be empty). |
| 1b | id | `User`, `MenuItem`, `Order` | `private` | Read through `getId()`. Only the database class calls `setId`, right after it saves a new row and gets its number. |
| 2 | password | `User` | `private` | Personal data. Only `AuthService` (to check a login) and `UserDatabase` (to save it) read it, through `getPassword()`. |
| 3 | phone | `Customer` | `private` | Personal data. `setPhone` checks it has 10 digits. |
| 4 | price | `MenuItem` | `private` | If it were public, anyone could set it to 0 or below. `setPrice` stops that. |
| 5 | items | `Order` | `private final` | Nobody outside can add or remove lines without `addItem`. `getItems()` gives a copy. `final`: the list is never swapped for another. |
| 6 | unitPrice | `OrderItem` | `private`, no setter | It is set once, when the line is made, and never changes. |
| 7 | URL | `DatabaseConnection` | `private static final` | One fixed value for the whole app, used by this class only. |
| 8 | the services a window uses | each window | `private final` | Given once in the constructor and never changed. |

[One paragraph about Table 8. Point: there is no public data in our app. Note: the teacher's example in the guideline
makes a name public; say why we keep even the name private (the setter checks it, and we can change how it is
stored later without breaking other classes).]

**Table 9: Method access control**

| No | Method | Class | Modifier | Description |
| --- | --- | --- | --- | --- |
| 1 | `User(...)` constructor | `User` | `protected` | Only the child classes call it. Nobody can make a plain `User`. |
| 2 | getters and setters | model classes | `public` | Every layer needs them. |
| 3 | `getRole()`, `canManageMenu()` | `User` | `public abstract` | Every child must write them; every layer can call them. |
| 4 | service methods (`placeOrder`, `login`, ...) | services | `public` | These are the agreed methods the windows call. |
| 5 | `checkCart(...)` | `OrderService` | `private` | A helper used only inside `placeOrder`. Nobody else should call it. |
| 6 | `open()` | `DatabaseConnection` | `public static` | Every database class needs a connection. No object needed. |
| 7 | `DatabaseConnection()` constructor | `DatabaseConnection` | `private` | Stops anyone from making a useless object of it. |
| 8 | `runScript(...)` | `DatabaseSetup` | `private static` | Only `createIfMissing()` uses it. |
| 9 | click handlers (`onPlaceOrder`, ...) | windows | `private` | Only the window's own buttons call them. |

[One paragraph about Table 9. Rule we follow: start with `private`; open it up only when another class needs it.]

**Classes, packages and the interface**

| Item | Access | Why |
| --- | --- | --- |
| every class and enum | `public` | Classes in other packages use them (for example `ui` uses `model`). |
| `Database<T>` | `public` interface | Services in another package hold variables of this type. |
| interface methods | `public` (always, in an interface) | A class that implements them must keep them public. |
| default (no modifier) | not used | Every class and member is either shared (`public`), for children (`protected`) or a helper (`private`). |

[One paragraph that sums up access control: private data, public agreed methods, protected only where children need it.]
