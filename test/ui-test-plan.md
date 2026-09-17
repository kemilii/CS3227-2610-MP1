# HomeHub UI test plan

This plan tests HomeHub through its command-line pantry inventory interface.
Cases are run in order, each in a fresh temporary working directory. Output is
compared exactly after normalising only CRLF to LF; stderr must be empty and
each process must exit successfully.

## Scope and execution information

- Required runtime: OpenJDK 25.0.3 (Zulu25.34+17-CA).
- Java selection and UTF-8 locale: `env LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 JAVA_HOME=/Users/camelliaaa/.sdkman/candidates/java/25.0.3.fx-zulu PATH=/Users/camelliaaa/.sdkman/candidates/java/25.0.3.fx-zulu/bin:$PATH`.
- Compile command: `javac -encoding UTF-8 --release 25 -d <compiled-classes> $(find src/main/java -name '*.java' ! -name 'Main.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java' ! -name 'Launcher.java')`.
- Launch command: `java -Dfile.encoding=UTF-8 -Duser.language=en -Duser.country=SG -cp <compiled-classes> homehub.HomeHub`.
- Timeout: 20 seconds per process.
- Input: provide every line in the case, including a final newline.
- Persistence: cases use fresh temporary working directories with no initial pantry file.

## Test cases

### UI-001: Exit immediately

Aim: Verify the welcome screen and pantry-specific goodbye message.

Inputs:

```text
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-002: Add and list pantry stock

Aim: Verify that an inventory entry records its quantity, unit, and expiry date.

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
list
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
1.[P] rice — 2 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-003: Consume, restock, search, and expiry filter

Aim: Verify the inventory-specific operations update quantities and return relevant views.

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
consume 1 1
restock 1 3
search RICE
expiring 2026-10-01
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
Used from the pantry: 📉
  [P] rice — 1 kg (expires: Sep 30 2026)
Quantity changed by 1.
____________________________________________________________
____________________________________________________________
Restocked the pantry: 📈
  [P] rice — 4 kg (expires: Sep 30 2026)
Quantity changed by 3.
____________________________________________________________
____________________________________________________________
🔎 Moss found these pantry entries:
1.[P] rice — 4 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
⏳ Pantry entries expiring by Oct 01 2026:
1.[P] rice — 4 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-004: Reject invalid pantry input

Aim: Verify invalid inventory commands do not corrupt the pantry and legacy task commands are not supported.

Inputs:

```text
mark 1
add rice /qty 0 /unit kg /expires 2026-09-30
add rice /qty 2 /unit kg /expires 2026-02-30
consume 1 1
list
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
Moss says: 💬 Moss does not recognise that command yet. Try add, list, search, restock, consume, expiring, lowstock, summary, move, delete, or help.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Quantity must be a positive whole number.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Expiry dates must use yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry item number does not exist.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-005: Help and empty pantry

Aim: Verify that help documents every supported command and an empty pantry is displayed clearly.

Inputs:

```text
help
list
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
📖 Moss's pantry guide:
add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>] - add pantry stock.
list - show every tracked pantry entry.
search <keyword> - find pantry entries by name.
restock <item number> <quantity> - increase available stock.
consume <item number> <quantity> - reduce available stock.
expiring <yyyy-MM-dd> - show entries expiring by a cutoff date.
lowstock - show entries at or below their minimum stock level.
summary - show a stock health summary.
move <item number> <location> - move an entry to another storage location.
delete <item number> - remove an entry from the pantry.
help - show this pantry guide.
bye - close HomeHub.
Date format: yyyy-MM-dd. Example: 2026-09-30.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-006: Duplicate and no-match queries

Aim: Verify duplicate entries are rejected and search/expiry queries handle both matches and misses.

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
add milk /qty 1 /unit carton /expires 2026-10-15
add RICE /qty 5 /unit KG /expires 2026-09-30
search RICE
search tea
expiring 2026-10-01
expiring 2026-09-01
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] milk — 1 carton (expires: Oct 15 2026)
There are 2 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry entry is already being tracked.
____________________________________________________________
____________________________________________________________
🔎 Moss found these pantry entries:
1.[P] rice — 2 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
🫧 Moss couldn't find any pantry entries matching that keyword.
____________________________________________________________
____________________________________________________________
⏳ Pantry entries expiring by Oct 01 2026:
1.[P] rice — 2 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
✅ Moss found no pantry entries expiring by Sep 01 2026.
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-007: Quantity validation and deletion

Aim: Verify stock cannot become negative, invalid item references do not mutate the pantry, and deletion renumbers entries.

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
add milk /qty 1 /unit carton /expires 2026-10-15
consume 1 10
restock 1 0
restock 9 1
consume x 1
delete 1
delete 5
list
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] milk — 1 carton (expires: Oct 15 2026)
There are 2 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
Moss says: 💬 You cannot consume more than the available stock.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Quantity must be a positive whole number.
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry item number does not exist.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Item number and quantity must be whole numbers.
____________________________________________________________
____________________________________________________________
Removed from the pantry: 🗑️
  [P] rice — 2 kg (expires: Sep 30 2026)
That leaves 1 pantry entries tracked. ✨
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry item number does not exist.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
1.[P] milk — 1 carton (expires: Oct 15 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-008: Syntax and missing-argument errors

Aim: Verify malformed add markers and missing arguments produce actionable errors without changing inventory.

Inputs:

```text
add rice /qty 2 /unit kg
add rice /qty 2 /expires 2026-09-30 /unit kg
search
expiring
delete
list
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
Moss says: 💬 Use: add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>].
____________________________________________________________
____________________________________________________________
Moss says: 💬 Use: add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>].
____________________________________________________________
____________________________________________________________
Moss says: 💬 Please provide a keyword after search.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Please provide a cutoff date after expiring.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Please provide an item number after delete.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

### UI-009: Metadata, low-stock alert, summary, and relocation

Aim: Verify that optional item metadata drives low-stock reporting, summary counts, and location changes.

Inputs:

```text
add flour /qty 1 /unit bag /expires 2099-12-01 /category baking /location cabinet /min 2
lowstock
summary
move 1 freezer
list
bye
```

Expected output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: cabinet; min: 2)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
📉 Low-stock pantry entries:
1.[P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: cabinet; min: 2)
____________________________________________________________
____________________________________________________________
📊 Pantry summary:
Entries tracked: 1
Low-stock entries: 1
Expired entries: 0
____________________________________________________________
____________________________________________________________
Moved within the home: 🚚
  [P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: freezer; min: 2)
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
1.[P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: freezer; min: 2)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

## Test session record — 2026-09-17

- Java runtime: OpenJDK 25.0.3 (Zulu25.34+17-CA).
- Compile command: `javac -encoding UTF-8 --release 25 -d <compiled-classes> $(find src/main/java -name '*.java' ! -name 'Main.java' ! -name 'MainWindow.java' ! -name 'DialogBox.java' ! -name 'Launcher.java')`.
- Launch command: `java -Dfile.encoding=UTF-8 -Duser.language=en -Duser.country=SG -cp <compiled-classes> homehub.HomeHub`.
- Working-directory setup: a fresh temporary directory for each case.
- Output comparison: exact stdout after CRLF-to-LF normalisation; stderr was empty.
- Overall result: PASS. UI-001 through UI-009 passed with exit status 0.

### UI-001 console record

Inputs:

```text
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-005 console record

Inputs:

```text
help
list
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
📖 Moss's pantry guide:
add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>] - add pantry stock.
list - show every tracked pantry entry.
search <keyword> - find pantry entries by name.
restock <item number> <quantity> - increase available stock.
consume <item number> <quantity> - reduce available stock.
expiring <yyyy-MM-dd> - show entries expiring by a cutoff date.
lowstock - show entries at or below their minimum stock level.
summary - show a stock health summary.
move <item number> <location> - move an entry to another storage location.
delete <item number> - remove an entry from the pantry.
help - show this pantry guide.
bye - close HomeHub.
Date format: yyyy-MM-dd. Example: 2026-09-30.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-006 console record

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
add milk /qty 1 /unit carton /expires 2026-10-15
add RICE /qty 5 /unit KG /expires 2026-09-30
search RICE
search tea
expiring 2026-10-01
expiring 2026-09-01
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] milk — 1 carton (expires: Oct 15 2026)
There are 2 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry entry is already being tracked.
____________________________________________________________
____________________________________________________________
🔎 Moss found these pantry entries:
1.[P] rice — 2 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
🫧 Moss couldn't find any pantry entries matching that keyword.
____________________________________________________________
____________________________________________________________
⏳ Pantry entries expiring by Oct 01 2026:
1.[P] rice — 2 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
✅ Moss found no pantry entries expiring by Sep 01 2026.
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-007 console record

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
add milk /qty 1 /unit carton /expires 2026-10-15
consume 1 10
restock 1 0
restock 9 1
consume x 1
delete 1
delete 5
list
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] milk — 1 carton (expires: Oct 15 2026)
There are 2 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
Moss says: 💬 You cannot consume more than the available stock.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Quantity must be a positive whole number.
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry item number does not exist.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Item number and quantity must be whole numbers.
____________________________________________________________
____________________________________________________________
Removed from the pantry: 🗑️
  [P] rice — 2 kg (expires: Sep 30 2026)
That leaves 1 pantry entries tracked. ✨
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry item number does not exist.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
1.[P] milk — 1 carton (expires: Oct 15 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-008 console record

Inputs:

```text
add rice /qty 2 /unit kg
add rice /qty 2 /expires 2026-09-30 /unit kg
search
expiring
delete
list
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
Moss says: 💬 Use: add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>].
____________________________________________________________
____________________________________________________________
Moss says: 💬 Use: add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>].
____________________________________________________________
____________________________________________________________
Moss says: 💬 Please provide a keyword after search.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Please provide a cutoff date after expiring.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Please provide an item number after delete.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-009 console record

Inputs:

```text
add flour /qty 1 /unit bag /expires 2099-12-01 /category baking /location cabinet /min 2
lowstock
summary
move 1 freezer
list
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: cabinet; min: 2)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
📉 Low-stock pantry entries:
1.[P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: cabinet; min: 2)
____________________________________________________________
____________________________________________________________
📊 Pantry summary:
Entries tracked: 1
Low-stock entries: 1
Expired entries: 0
____________________________________________________________
____________________________________________________________
Moved within the home: 🚚
  [P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: freezer; min: 2)
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
1.[P] flour — 1 bag (expires: Dec 01 2099; category: baking; location: freezer; min: 2)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-002 console record

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
list
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
1.[P] rice — 2 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-003 console record

Inputs:

```text
add rice /qty 2 /unit kg /expires 2026-09-30
consume 1 1
restock 1 3
search RICE
expiring 2026-10-01
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
✨ Added to the pantry:
  [P] rice — 2 kg (expires: Sep 30 2026)
There are 1 pantry entries tracked. 🧺
____________________________________________________________
____________________________________________________________
Used from the pantry: 📉
  [P] rice — 1 kg (expires: Sep 30 2026)
Quantity changed by 1.
____________________________________________________________
____________________________________________________________
Restocked the pantry: 📈
  [P] rice — 4 kg (expires: Sep 30 2026)
Quantity changed by 3.
____________________________________________________________
____________________________________________________________
🔎 Moss found these pantry entries:
1.[P] rice — 4 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
⏳ Pantry entries expiring by Oct 01 2026:
1.[P] rice — 4 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.

### UI-004 console record

Inputs:

```text
mark 1
add rice /qty 0 /unit kg /expires 2026-09-30
add rice /qty 2 /unit kg /expires 2026-02-30
consume 1 1
list
bye
```

Actual output:

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household fresh and accounted for. 🏡
____________________________________________________________
____________________________________________________________
Moss says: 💬 Moss does not recognise that command yet. Try add, list, search, restock, consume, expiring, lowstock, summary, move, delete, or help.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Quantity must be a positive whole number.
____________________________________________________________
____________________________________________________________
Moss says: 💬 Expiry dates must use yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
Moss says: 💬 That pantry item number does not exist.
____________________________________________________________
____________________________________________________________
🧺 Moss's pantry inventory:
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```

Result: PASS; stderr empty; exit status 0.
