# StoragePlus — User Guide

Step-by-step how to use each feature.

---

## 1. Adding a label to a chest or barrel

1. Get a **name tag** and rename it in an anvil. The name **must** start with:
   - **`[label] `** (including the space), then your label text.
   - Examples: `[label] Ore Storage`, `[label] &6Gold`, `[label] Cobblestone`.
2. **Left‑click** the chest or barrel with that name tag in hand.
3. A **floating label** appears above (or near) the block. The label supports Minecraft color codes using **`&`** (e.g. `&6` for gold, `&a` for green).

**Double chests:** If you add a second chest next to an already labeled chest, the plugin merges them into one labeled double chest. The existing label is kept and repositioned for the double chest.

**To remove a label:** Crouch and left‑click the same chest/barrel with a name tag whose name is exactly **`[label] <same name>`**. The floating text and stored data for that block are removed.

---

## 2. Opening storage by clicking the label

- **Right‑click the floating text** (the label) to open that chest or barrel.
- You must click in the **upper part** of the label (the plugin uses a height check). If nothing happens, try clicking a bit higher on the text.
- This lets you open storage without aiming at the block, which can help with builds where the block is hidden or awkward to click.

---

## 3. Moving the label (label position tool)

If the default position is wrong, you can nudge the label with a **label tool**:

1. Get a **stick** and rename it in an anvil to one of:
   - **`[label tool] X`** — move label left/right
   - **`[label tool] Y`** — move label up/down
   - **`[label tool] Z`** — move label forward/back
2. **Left‑click** the **chest or barrel** (not the armor stand) with the tool.
   - **Normal click** — move +0.1 on that axis.
   - **Sneak + click** — move -0.1 on that axis.

The block must already have a label. Use different sticks for X, Y, and Z as needed.

---

## 4. Quick deposit (button + barrel)

1. Place a **barrel**.
2. Place a **button** on any face of that barrel (or on a block directly beside the barrel).
3. **Left‑click the button.**

The plugin looks for all **chests and barrels within 20 blocks** of you and tries to **deposit your whole inventory** into them:

- It first stacks items with existing stacks of the same type.
- Then it fills empty slots.
- Items that don’t fit stay in your inventory.

You get a green message when the run finishes. You need permission **`quickdeposit.use`** for this to work.

---

## 5. Quick deposit (command)

- Run **`/quickdeposit`** (with permission **`quickdeposit.use`**).
- Same behavior as left‑clicking a quick‑deposit button: deposits from your inventory into all chests and barrels within 20 blocks.

---

## 6. Admin command: remove all armor stands

- **`/removearmorstands`** (permission **`removearmorstands.use`**).
- Removes **every** armor stand in every loaded world. This includes:
  - All StoragePlus labels (floating text).
  - Any other armor stands (decorations, custom NPCs, etc.).

Use only when you understand the impact. After running it, labeled chests/barrels will still have their data in `storage_data.yml`, but the visible labels are gone until you set them again (e.g. by re‑applying the same `[label]` name tag).

---

## Quick reference

| Action | What to do |
|--------|------------|
| Add label | Name tag: `[label] Your Name` → left‑click chest/barrel |
| Remove label | Crouch + left‑click with name tag `[label] <exact same name>` |
| Open storage | Right‑click the floating label text |
| Move label | Stick `[label tool] X` / `Y` / `Z` → left‑click block (sneak = negative) |
| Quick deposit | Left‑click button next to barrel, or run `/quickdeposit` |
| Remove all armor stands | `/removearmorstands` (admin) |
