# StoragePlus — Data and Admin

Technical details for server admins and anyone editing or backing up plugin data.

---

## Data file: `storage_data.yml`

**Path:** `plugins/StoragePlus/storage_data.yml`

The plugin stores all labeled storage in a single YAML file. Each entry is keyed by a **GUID** (UUID string). The file is written when:

- A new label is added or updated
- A label is removed (that GUID is deleted)
- The plugin is disabled (full save)

### Structure (conceptual)

```yaml
storage:
  <guid-uuid-string>:
    name: "Label text (as typed, e.g. with & codes)"
    storageLocation: <Bukkit Location>   # Block location of chest/barrel
    armorStandLocation: <Bukkit Location>  # Where the floating label entity is
    storageType: "CHEST"   # or "BARREL"
```

- **`storage`** — Top-level section; each key is a GUID.
- **`name`** — Display name (can include `&` color codes).
- **`storageLocation`** / **`armorStandLocation`** — Bukkit `Location` objects (world, x, y, z, yaw, pitch). The plugin uses these to find the block and the armor stand when loading.
- **`storageType`** — Block type name (`CHEST` or `BARREL`).

If you edit the file by hand, keep YAML syntax valid and locations in the format the plugin expects. Back up the file before editing.

---

## Permissions

| Permission | Default | Description |
|------------|--------|-------------|
| `removearmorstands.use` | Ops / server-dependent | Allows `/removearmorstands`. |
| `quickdeposit.use` | Ops / server-dependent | Allows quick deposit (button click and `/quickdeposit`). |

Labeling (name tag and label tool) is **not** gated by a plugin permission; it follows normal block interaction rules (e.g. build/use permissions on your server).

---

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/removearmorstands` | `removearmorstands.use` | Removes all armor stands in all worlds. Use with care. |
| `/quickdeposit` | `quickdeposit.use` | Runs quick deposit for the player (same as button trigger). |

No command arguments. Console can run both; only players can benefit from `/quickdeposit` (it uses the player’s inventory and location).

---

## Quick deposit behavior

- **Trigger:** Left-click on a block that is a **button** (any type) with at least one **barrel** adjacent to it, **or** run `/quickdeposit`.
- **Scope:** All chests and barrels in a **20-block** cube around the player (search radius 20).
- **Logic:**
  1. For each item in the player’s inventory, try to add to existing stacks of the same type in nearby storage.
  2. Then try to put the rest in empty slots (one slot per item type if needed).
- Items that don’t fit stay in the player’s inventory. No items are dropped on the ground by the plugin.

---

## Double chest handling

- **Single chest + new adjacent chest:** The existing label is kept and applied to the double chest; the plugin repositions the armor stand so the label sits over the double chest.
- **Breaking one half of a double chest:** The label is moved to the remaining chest (single) and the data is updated so the GUID now points at that one block.
- Only one GUID is used per double chest; the “primary” block (the one that had or gets the label) is the one stored in `storage_data.yml`.

---

## Notes for admins

1. **Backups:** Back up `plugins/StoragePlus/storage_data.yml` before upgrading the plugin or doing bulk world edits. Restoring it restores which blocks are labeled and where the labels are.
2. **World / block changes:** If you move or replace blocks (e.g. with WorldEdit), locations in `storage_data.yml` may no longer match. Labels can “orphan” (armor stand exists but block is wrong) or disappear. Consider re-labeling or cleaning the file after big edits.
3. **`/removearmorstands`:** This removes **every** armor stand on the server, not only StoragePlus labels. Use only when you intend a full armor-stand wipe.
4. **API version:** Plugin targets **1.21** (`api-version` in `plugin.yml`). Use a matching Spigot/Paper build.

For a high-level summary and user-facing usage, see **OVERVIEW.md** and **USER_GUIDE.md**.
