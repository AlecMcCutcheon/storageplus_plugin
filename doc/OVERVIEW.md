# StoragePlus — Plugin Overview

StoragePlus is a Spigot/Bukkit plugin for Minecraft 1.21 that adds **floating labels** for chests and barrels and **quick deposit** from your inventory into nearby storage.

---

## What It Does

### 1. Labeled storage (chests and barrels)

- Put a **custom floating label** above any chest or barrel (e.g. "&6Ore Storage", "Cobblestone").
- Labels are **invisible armor stands** with visible custom names, so they don’t get in the way.
- **Double chests** are supported: one label can cover both sides, and the plugin keeps the label correct when you add or remove the second chest.
- You can **open the chest/barrel by right‑clicking the label** (the floating text) instead of the block.
- Labels are stored in `storage_data.yml` and persist across restarts.

### 2. Quick deposit

- **Right‑click a button** (normal push) that is directly beside a **barrel** to trigger a **quick deposit**.
- The plugin finds all chests and barrels within **20 blocks** of you and tries to **deposit your entire inventory** into them (stacking with existing items first, then filling empty slots).
- You can also run **`/quickdeposit`** to do the same thing without using a button.

### 3. Admin/utility commands

- **`/removearmorstands`** — Removes **all** armor stands in all worlds. Use with care; it’s for cleanup (e.g. after testing or if label armor stands get out of hand).

---

## Core concepts

| Concept | Meaning |
|--------|--------|
| **Label** | A name tag renamed with `[label] <name>`. Left‑click a chest/barrel with it to set the floating label. |
| **Label tool** | A stick renamed `[label tool] X`, `Y`, or `Z` to move the label position on that axis. |
| **Quick deposit trigger** | A button (any type) placed **next to** a barrel. Right‑click the button to run quick deposit. |
| **Storage data** | Each labeled block has a unique ID (GUID), name, block location, and armor stand location, saved in `plugins/StoragePlus/storage_data.yml`. |

---

## Supported blocks

- **Chest** (single and double)
- **Barrel**

Labels and “open by clicking the label” work for both. Quick deposit considers both chests and barrels in the 20‑block radius.

---

## Permissions

| Permission | Purpose |
|------------|--------|
| `removearmorstands.use` | Use `/removearmorstands` |
| `quickdeposit.use` | Use quick deposit (button or `/quickdeposit`) |

There are no separate permissions for placing or editing labels; those are available to any player who can interact with the block.

---

## Files and data

- **Plugin JAR:** `StoragePlus-Beta-1.21.4.jar` (or similar) in `plugins/`.
- **Data folder:** `plugins/StoragePlus/`
- **Storage data:** `plugins/StoragePlus/storage_data.yml` — all labeled chest/barrel GUIDs, names, locations, and armor stand positions.

For more detail on using labels, tools, and commands, see **USER_GUIDE.md**. For data format and admin notes, see **DATA_AND_ADMIN.md**.
