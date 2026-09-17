# HomeHub User Guide

HomeHub is a household pantry inventory manager. Keke helps you record what is
in the pantry, adjust stock as items are used or bought, and find entries that
are nearing their expiry date. Every successful change is saved locally.

The JavaFX header identifies Keke as your smart pantry companion. The welcome
message gives a quick command overview, and the `help` command is always
available when you need the complete guide. In the JavaFX view, command names
in the help response are bold and command sections are separated with blank
lines. Validation errors use a neutral `⚠️` marker.

## Getting started

1. Select JDK 25.
2. From the project folder, start HomeHub with `./gradlew run`.
3. Type a command in the message box and press **Send**. The command-line
   interface also accepts one command per line.

Inventory is saved in `data/pantry.txt` and reloaded the next time HomeHub
starts. Commands must be entered in lowercase.

The release JAR can be run with `java -jar release/homehub.jar`. To rebuild it,
run `./gradlew shadowJar` and copy `build/libs/homehub.jar` to
`release/homehub.jar`.

## Command reference

Item numbers are the one-based numbers displayed by `list`. Replace
angle-bracketed values with your own text; do not type the brackets. Commands
must be entered in lowercase.

### Inventory commands

#### `add`

Adds a new pantry entry. The command requires a name, current quantity,
measurement unit, and expiry date. Category, storage location, and minimum
stock level are optional.

**Usage**

```text
add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>]
```

**Example**

```text
add flour /qty 1 /unit bag /expires 2026-12-01 /category baking /location cabinet /min 2
```

The quantity must be a positive whole number. The minimum stock level may be
zero. Names, units, categories, and locations cannot be empty or contain `|`.

#### `list`

Displays every pantry entry in the order it was added. The displayed number is
used by commands such as `restock`, `consume`, `move`, and `delete`.

**Example**

```text
list
```

#### `search`

Finds pantry entries whose names contain the supplied keyword. Matching is
case-insensitive, so `search rice` also finds an entry named `Rice`.

**Usage**

```text
search <keyword>
```

**Example**

```text
search rice
```

### Stock update commands

#### `restock`

Increases the available quantity of an entry. The item number must refer to an
entry shown by `list`, and the quantity must be positive.

**Usage**

```text
restock <item number> <quantity>
```

**Example**

```text
restock 1 3
```

#### `consume`

Decreases the available quantity of an entry. HomeHub rejects the command if
the requested amount is greater than the available stock, so quantities cannot
become negative.

**Usage**

```text
consume <item number> <quantity>
```

**Example**

```text
consume 1 1
```

### Stock health and storage commands

#### `expiring`

Displays entries whose expiry date is on or before the supplied cutoff date.
The cutoff must use the strict `yyyy-MM-dd` format.

**Usage**

```text
expiring <yyyy-MM-dd>
```

**Example**

```text
expiring 2026-10-01
```

#### `lowstock`

Displays entries whose current quantity is at or below their configured
minimum stock level. Entries with a minimum level of zero are not treated as
low stock.

**Example**

```text
lowstock
```

#### `summary`

Displays the total number of tracked entries, the number of low-stock entries,
and the number of entries that have expired as of today.

**Example**

```text
summary
```

#### `move`

Changes the storage location of an entry. The location must not be empty or
contain `|`. Use a single-word location, such as `pantry`, `cabinet`, or
`freezer`.

**Usage**

```text
move <item number> <location>
```

**Example**

```text
move 1 freezer
```

#### `delete`

Removes an entry permanently from the pantry. The item number must refer to an
entry shown by `list`.

**Usage**

```text
delete <item number>
```

**Example**

```text
delete 2
```

### Other commands

#### `help`

Displays the complete command reference inside HomeHub. It is useful when you
need to check a command's syntax while using the application.

**Example**

```text
help
```

#### `bye`

Closes HomeHub after displaying a goodbye message. Inventory changes are saved
immediately, so the latest successful changes are available the next time the
application starts.

**Example**

```text
bye
```

Quantities must be positive whole numbers. Minimum stock can be zero or a
positive whole number. Expiry dates must be valid dates in `yyyy-MM-dd` format.
Names, categories, units, and locations cannot be empty or contain the `|`
character.

## Help menu

The `help` command displays the full command menu:

```text
📖 Keke's pantry command menu:

Inventory:
add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>] - add pantry stock.
list - show every tracked pantry entry.
search <keyword> - find pantry entries by name.

Stock updates:
restock <item number> <quantity> - increase available stock.
consume <item number> <quantity> - reduce available stock.

Stock health and storage:
expiring <yyyy-MM-dd> - show entries expiring by a cutoff date.
lowstock - show entries at or below their minimum stock level.
summary - show a stock health summary.
move <item number> <location> - move an entry to another storage location.
delete <item number> - remove an entry from the pantry.

Other:
help - show this command menu.
bye - close HomeHub.

Date format: yyyy-MM-dd. Example: 2026-09-30.
```

## Example session

```text
____________________________________________________________
🌿 Welcome to HomeHub. Keke is on duty.
HomeHub tracks pantry stock, expiry dates, locations, and low-stock levels.
Commands: add, list, search, restock, consume, expiring, lowstock, summary, move, delete.
Type 'help' anytime for the full command menu. 🏡
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
⏳ Pantry entries expiring by Oct 01 2026:
1.[P] rice — 1 kg (expires: Sep 30 2026)
____________________________________________________________
____________________________________________________________
Pantry secured. See you soon! 👋
____________________________________________________________
```
