---
updated: 2026-10-27
review_by: 2026-11-26
---

# Report skeleton: Assignment 1 (sections I, II.1, start of II.2)

Copy this into the Word report. Font: Times New Roman 13, justified.
The text in `[brackets]` tells you what to write. Delete it after.
Write the sentences yourselves. The teacher allows at most 19% AI-written text.
Every table or figure needs at least one paragraph that talks about it.

---

## I. INTRODUCTION (business requirements)

Think of it this way: you are the restaurant owner who pays for this app.
Write what the restaurant needs. Do not write about code here.

### I.1 The problem

[2-4 sentences. How does the restaurant take orders today, and what goes wrong?
Example ideas: paper slips get lost, staff cannot see which order is next, the menu changes but the paper menu does not.]

### I.2 What the app does

[1-2 sentences. "In this project, we build a desktop app that lets customers order food, lets staff ... and lets the admin ..."]

This app gives these functions to its users:

- [Customer: make an account and log in]
- [Customer: see the menu, filter it by type, search it]
- [Customer: add dishes to a cart and place an order for a table]
- [Customer: follow the order status, save a receipt]
- [Staff: see open orders, move an order to the next step, cancel an order, mark a dish sold out]
- [Admin: add, change and remove dishes; add and remove staff accounts]

### I.3 User roles

Table 1: User roles

| No | Role | Who they are | What they can do |
| --- | --- | --- | --- |
| 1 | Customer | [a guest at a table] | [...] |
| 2 | Staff | [...] | [...] |
| 3 | Admin | [...] | [everything staff can do, plus ...] |

[One paragraph about the table. For example: why Admin includes all staff rights.]

### I.4 Use case diagram

Figure 1: Use case diagram

Ready to paste: `architecture/use-case.png` (source: `use-case.svg`).

[If you redraw it in draw.io or StarUML, keep the same content: 3 stick figures on the left: Customer, Staff, Admin.
Ovals in the middle: Log in, Register, View menu, Search menu, Place order, Track order, Save receipt,
View order queue, Update order status, Cancel order, Mark sold out, Manage menu, Manage staff.
Draw a line from each actor to the ovals they use. Admin points to Staff with a hollow arrow (Admin is a kind of Staff).]

[One paragraph that walks through the diagram.]

### I.5 How we build it

[1-2 sentences. "We use Java OOP to design the classes. We apply encapsulation, inheritance, abstraction and polymorphism.
The data is saved in a database through JDBC."]

---

## II. CLASS ANALYSIS

### II.1 Objects

An object is one real thing with real values. Not "a customer", but "customer Lan".
For each object, write:
- **State**: what it has right now (example values).
- **Behaviours**: what it can do, or what can happen to it.

No data types, no variable names, no code.

Table 2: List of objects

| No | Object name | State | Behaviours |
| --- | --- | --- | --- |
| 1 | Customer Lan | [Tran Thi Lan, username lan01, phone 0901 234 567] | [logs in, views the menu, places an order, ...] |
| 2 | Customer Minh | [...] | [...] |
| 3 | Staff Hung | [Le Van Hung, username hung, job: cashier] | [logs in, sees the queue, moves an order to the next step, ...] |
| 4 | Admin Mai | [...] | [everything Hung does, plus adds a dish, ...] |
| 5 | Dish "Pho bo" | [main dish, 45,000 VND, available] | [is shown on the menu, is marked sold out, ...] |
| 6 | Dish "Iced tea" | [...] | [...] |
| 7 | Order #12 | [by Lan, table 3, status Preparing, 2 items, made at 12:05] | [adds an item, works out the total, moves to the next status, is cancelled] |
| 8 | Order line "2 x Pho bo" | [...] | [works out its subtotal] |
| 9 | Table 3 | [4 seats] | [...] |

[One paragraph about the table.]

**Group the objects.** Put together the objects that have the same kind of state and the same behaviours.

Table 3: Groups of objects

| No | Group | Objects in it | What they share |
| --- | --- | --- | --- |
| 1 | Customers | Lan, Minh | [...] |
| 2 | Staff | Hung, Mai | [...] |
| 3 | Dishes | Pho bo, Iced tea | [...] |
| 4 | Orders | #12, ... | [...] |
| ... | ... | ... | ... |

[One paragraph. Point out what ALL people (customers, staff, admin) have in common: a name, a username, a password, they can log in.
This is the reason for the parent class in II.2.]

### II.2 Classes (beginning part)

A class is the blueprint for one group. Each group from Table 3 becomes a class.
List attributes and functions in plain words. Still no data types or parameter lists.

Table 4: Classes

| No | Class | Attributes | Functions | Made from group |
| --- | --- | --- | --- | --- |
| 1 | User | [id, username, password, full name] | [log in, get role, can manage menu?] | all people |
| 2 | Customer | [phone number] | [...] | Customers |
| 3 | Staff | [job title] | [...] | Staff |
| 4 | Admin | [nothing extra] | [...] | Staff (admin) |
| 5 | MenuItem | [name, category, price, available] | [...] | Dishes |
| 6 | Order | [...] | [add item, get total, ...] | Orders |
| 7 | OrderItem | [...] | [get subtotal] | Order lines |
| 8 | DiningTable | [number, seats] | [...] | Tables |

[One paragraph about the table.]

**Inheritance and abstract classes**

[A short paragraph for each point:]
- [Customer and Staff are kinds of User, so they inherit from User. Why: they share id, username, password, full name, and log in.]
- [Admin is a kind of Staff, so Admin inherits from Staff. Why: an admin does all staff work, plus more.]
- [User is abstract. Why: nobody is "just a user". Every person is a customer, staff or admin.
  Name the functions that each child must answer in its own way: get role, can manage menu.]

Figure 2: Inheritance diagram

[Boxes with only the class name. Hollow arrows point from child to parent:
Customer → User, Staff → User, Admin → Staff. Write "abstract" (or use italics) on User.
No attributes, no types, no methods in this figure.]

[One paragraph that explains the figure.]
