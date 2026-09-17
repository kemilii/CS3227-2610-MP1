# HomeHub User Guide

HomeHub is a household pantry inventory manager. Moss helps you record what is
in the pantry, adjust stock as items are used or bought, and find entries that
are nearing their expiry date. Every successful change is saved locally.

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

Item numbers are the one-based numbers shown by `list`. Replace angle-bracketed
values with your own text; do not type the brackets.

| Command | Description | Example |
| --- | --- | --- |
| `add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>]` | Adds a pantry entry with optional stock metadata. | `add flour /qty 1 /unit bag /expires 2026-12-01 /category baking /location cabinet /min 2` |
| `list` | Shows every tracked entry. | `list` |
| `search <keyword>` | Finds entries by name, ignoring case. | `search rice` |
| `restock <item number> <quantity>` | Increases an entry's stock. | `restock 1 3` |
| `consume <item number> <quantity>` | Reduces an entry's stock. | `consume 1 1` |
| `expiring <yyyy-MM-dd>` | Shows entries expiring on or before a cutoff date. | `expiring 2026-10-01` |
| `lowstock` | Shows entries at or below their minimum quantity. | `lowstock` |
| `summary` | Shows stock counts and expired-entry count. | `summary` |
| `move <item number> <location>` | Moves an entry to another storage location. | `move 1 freezer` |
| `delete <item number>` | Removes an entry. | `delete 2` |
| `help` | Shows the command guide. | `help` |
| `bye` | Closes HomeHub. | `bye` |

Quantities must be positive whole numbers. Minimum stock can be zero or a
positive whole number. Expiry dates must be valid dates in `yyyy-MM-dd` format.
Names, categories, units, and locations cannot be empty or contain the `|`
character.

## Example session

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
HomeHub tracks pantry stock, expiry dates, locations, and low-stock levels.
Commands: add, list, search, restock, consume, expiring, lowstock, summary, move, delete.
Type help anytime for the full command menu. 🏡
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

## Testing

Run `./gradlew test` for the automated tests and `./gradlew check` for tests
plus Checkstyle. The end-to-end command-line scenarios are documented in
[`test/ui-test-plan.md`](../test/ui-test-plan.md).
