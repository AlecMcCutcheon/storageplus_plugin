package com.example.storageplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.World;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import org.bukkit.entity.Entity;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Barrel; 
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;


import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.io.File;


public class StoragePlus extends JavaPlugin implements Listener {
	
	private static final int SEARCH_RADIUS = 20;
	private File dataFile;
	private FileConfiguration dataConfig;

	
    @Override
    public void onEnable() {
		// Initialize the file for saving storage and armor stand data
		dataFile = new File(getDataFolder(), "storage_data.yml");
		
		// Initialize dataConfig before using it
		dataConfig = YamlConfiguration.loadConfiguration(dataFile);

		// Force overwrite and save the configuration (this will overwrite the file)
		try {
			saveDataFile(); // Will overwrite storage_data.yml each time
		} catch (Exception e) {
			getLogger().warning("Could not save default data file.");
			e.printStackTrace();
		}

		// Register events after initializing everything
		Bukkit.getPluginManager().registerEvents(this, this);
    }


    @Override
    public void onDisable() {
		// Save the data when the plugin is disabled
        saveDataFile();
    }
	
	// storage functions
	
	 // Save the data to the file
    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (Exception e) {
            getLogger().warning("Could not save data file.");
            e.printStackTrace();
        }
    }

    // Add or update a storage's data (GUID, Name, Location, Armorstand Location, StorageType)
    public void saveStorageData(String guid, String name, Location storageLocation, Location armorStandLocation) {
        String path = "storage." + guid;
		
		Material storageType = storageLocation.getBlock().getType();

        dataConfig.set(path + ".name", name);
        dataConfig.set(path + ".storageLocation", storageLocation);  // Store the Location directly
        dataConfig.set(path + ".armorStandLocation", armorStandLocation);  // Store the Location directly
        dataConfig.set(path + ".storageType", storageType.name());

        saveDataFile();
    }

    // Retrieve storage data by GUID
    public StorageData getStorageDataByGUID(String guid) {
        String path = "storage." + guid;
        if (dataConfig.contains(path)) {
            String name = dataConfig.getString(path + ".name");
            Location storageLocation = (Location) dataConfig.get(path + ".storageLocation"); // Directly retrieve Location
            Location armorStandLocation = (Location) dataConfig.get(path + ".armorStandLocation"); // Directly retrieve Location
            Material storageType = Material.valueOf(dataConfig.getString(path + ".storageType"));

            return new StorageData(guid, name, storageLocation, armorStandLocation, storageType);
        }
        return null; // Return null if storage data doesn't exist
    }
	
	public void updateStorageDataName(String guid, String newName) {
		String path = "storage." + guid;
		
		// Check if the storage data exists
		if (dataConfig.contains(path)) {
			// Update the name field
			dataConfig.set(path + ".name", newName);
			
			// Save the data file after updating
			saveDataFile();
		} else {
			//getLogger().warning("Storage data for GUID " + guid + " not found.");
		}
	}
	
	public void updateArmorStandLocation(String guid, Location newArmorStandLocation) {
		String path = "storage." + guid;

		// Check if the storage data exists
		if (dataConfig.contains(path)) {
			
			StorageData storageData = getStorageDataByGUID(guid);
			ArmorStand armorStand = storageData.getArmorStand();
			Location storageBlockLocation = storageData.getStorageBlock().getLocation();
			String name = storageData.getName();
			
			armorStand.teleport(newArmorStandLocation);
			
			Location updatedLocation = armorStand.getLocation();
			
			saveStorageData(guid, name, storageBlockLocation, updatedLocation);

			// Save the data file after updating
			saveDataFile();

			// Optionally log the teleportation
			//getLogger().info("ArmorStand with GUID " + guid + " has been teleported to: " + newArmorStandLocation);
		} else {
			//getLogger().warning("Storage data for GUID " + guid + " not found.");
		}
	}

	public StorageData getStorageDataByBlock(Block storageBlock) {
		// Iterate through all stored entries in the "storage" section
		for (String guid : getAllStorageGuids()) {
			String path = "storage." + guid;

			// Retrieve the stored storage location
			Location storedStorageLocation = (Location) dataConfig.get(path + ".storageLocation");
			Location blocklocation = storageBlock.getLocation();
			
			// Check if the provided block matches the stored storage location
			if (storageBlock != null && blocklocation.equals(storedStorageLocation)) {
				return getStorageDataByGUID(guid); // Return the corresponding StorageData if the storage block matches
			}
		}

		return null; // Return null if no matching storage block found
	}

	public StorageData getStorageDataByArmorStand(ArmorStand armorStand) {
		// Iterate through all stored entries in the "storage" section
		for (String guid : getAllStorageGuids()) {
			String path = "storage." + guid;

			// Retrieve the stored armor stand location
			Location storedArmorStandLocation = (Location) dataConfig.get(path + ".armorStandLocation");
		
			// Check if the provided armor stand matches the stored armor stand location
			if (armorStand != null && armorStand.getLocation().equals(storedArmorStandLocation)) {
				return getStorageDataByGUID(guid); // Return the corresponding StorageData if the armor stand matches
			}
		}

		return null; // Return null if no matching armor stand found
	}

    // Remove storage data by GUID
    public boolean removeStorageData(String guid) {
        String path = "storage." + guid;
        if (dataConfig.contains(path)) {
            dataConfig.set(path, null); // Remove the storage entry
            saveDataFile(); // Save changes
            return true; // Successfully removed
        }
        return false; // Entry with given GUID doesn't exist
    }

    // Helper class to represent storage data
    public static class StorageData {
        private final String guid;
        private final String name;
        private final Block storageBlock;  // Changed to store the block at the storage location
        private final ArmorStand armorStand;  // Changed to store the ArmorStand entity
        private final Material storageType;

        public StorageData(String guid, String name, Location storageLocation, Location armorStandLocation, Material storageType) {
            this.guid = guid;
            this.name = name;
            this.storageBlock = storageLocation.getBlock();
			
			ArmorStand foundArmorStand = null;
			    for (Entity entity : armorStandLocation.getWorld().getNearbyEntities(armorStandLocation, 0.1, 0.1, 0.1)) {
					if (entity instanceof ArmorStand && entity.getLocation().equals(armorStandLocation)) {
						foundArmorStand = (ArmorStand) entity;
					}
				}
	
            if (foundArmorStand != null) {
				this.armorStand = foundArmorStand;
			} else {
				this.armorStand = null;
				//Bukkit.getLogger().info("StorageData: No ArmorStand Found at Location");
			}
			
            this.storageType = storageType;
        }

        // Getters for all fields
        public String getGuid() {
            return guid;
        }

        public String getName() {
            return name;
        }

        public Block getStorageBlock() {
            return storageBlock;
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public Material getStorageType() {
            return storageType;
        }
    }

    // Retrieve all storage GUIDs from the config
    public List<String> getAllStorageGuids() {
        List<String> guids = new ArrayList<>();
        if (dataConfig.contains("storage")) {
            for (String key : dataConfig.getConfigurationSection("storage").getKeys(false)) {
                guids.add(key);
            }
        }
        return guids;
    }
	
	// storage naming functions 
	
	public static String translateColors(String text) {
        // Replace '&' with '§' for Minecraft color codes
        String translatedText = text.replace("&", "§");
        return ChatColor.translateAlternateColorCodes('§', translatedText);
    }
	
	private String getAdjacentChestDirection(Block currentBlock, Block adjacentBlock) {
		// Get the current block's position
		int currentX = currentBlock.getX();
		int currentZ = currentBlock.getZ();
		
		// Get the adjacent block's position
		int adjacentX = adjacentBlock.getX();
		int adjacentZ = adjacentBlock.getZ();
		
		// Check the X positions to determine east/west
		if (currentX != adjacentX) {
			if (adjacentX < currentX) {
				//getLogger().info("Direction: West");
				return "west";  // Adjacent block is to the west
			} else {
				//getLogger().info("Direction: East");
				return "east"; // Adjacent block is to the east
			}
		}
		
		// If X positions are the same, check Z positions for north/south determination
		if (currentZ != adjacentZ) {
			if (adjacentZ < currentZ) {
				//getLogger().info("Direction: North");
				return "north";  // Adjacent block is to the north
			} else {
				//getLogger().info("Direction: South");
				return "south"; // Adjacent block is to the south
			}
		}
		
		return "none"; // Shouldn't happen if blocks are properly aligned
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();

		// Check if the block placed is a chest
		if (block.getType() == Material.CHEST) {
			// Schedule a task to check the double chest after a short delay
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				@Override
				public void run() {
					// Re-check the block state and adjacent chest after the event has completed
					if (block.getType() == Material.CHEST) { // Verify that the block is still a chest

						// Check adjacent blocks for a connected chest
						Block adjacentBlock = getAdjacent(block);
						if (isDoubleChest(block, new Block[]{adjacentBlock})) {
							// If the adjacent storage has StorageData, replace the current chest
							StorageData adjacentStorageData = getStorageDataByBlock(adjacentBlock);
							
							if (adjacentStorageData != null) {
								String adjacentGuid = adjacentStorageData.getGuid();
								String adjacentName = adjacentStorageData.getName();

								String direction = getAdjacentChestDirection(adjacentBlock, block);
								removeStorageName(adjacentBlock);
								spawnOrUpdateFloatingText(adjacentBlock.getLocation(), adjacentName, adjacentGuid, true, direction);
							}
						}
					}
				}
			}, 1L);  
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		 
		if (block == null) {
			return;
		}
		
		if (isQuickDepositTrigger(block) && event.getAction().name().contains("RIGHT_CLICK")) {
			quickDeposit(player);
		}
		
		if (event.getAction().name().contains("LEFT_CLICK") && event.getClickedBlock() != null) {
			if (block.getType() == Material.CHEST || block.getType() == Material.BARREL) {
				ItemStack itemInHand = player.getInventory().getItemInMainHand();

				boolean isCrouching = player.isSneaking();
				boolean doublechest = false;
				String direction = "west";
				String guid = UUID.randomUUID().toString();
				StorageData blockStorageData = getStorageDataByBlock(block);
				
				// Handle double chest logic
				if (block.getType() == Material.CHEST) {
					Block adjacentBlock = getAdjacent(block);
					if (isDoubleChest(block, new Block[]{adjacentBlock})) {
						doublechest = true;
						direction = getAdjacentChestDirection(block, adjacentBlock);
						
						StorageData adjacentStorageData = getStorageDataByBlock(adjacentBlock);
					
						if (adjacentStorageData != null) {
							block = adjacentBlock;
							blockStorageData = adjacentStorageData;
						}
					}
				}
				
				String materialName = block.getType().name();
				
				if (blockStorageData != null) {
					guid = blockStorageData.getGuid();
				}
				
				if (itemInHand != null) {
					ItemMeta meta = itemInHand.getItemMeta();
					if (meta != null && meta.hasDisplayName()) {
						String name = meta.getDisplayName();

						if (name.toLowerCase().startsWith("[label] ") && itemInHand.getType() == Material.NAME_TAG) {
							String storageName = name.substring(8);
							
							String ColoredStorageName = translateColors(storageName);
							
							if (blockStorageData != null) {
								String existingName = blockStorageData.getName();
								if (existingName.equals(storageName)) {
									if (isCrouching) {
										removeStorageName(block);
										player.sendMessage(ChatColor.RED + "Removed " + materialName + " label");
									} else {
										player.sendMessage(ChatColor.YELLOW + "This " + materialName + " already has the label: " + ChatColor.RESET + ColoredStorageName);
									}
									return;
								}
							}
								
							player.sendMessage(ChatColor.GREEN + "Set " + materialName + " label to: " + ChatColor.RESET + ColoredStorageName);
							spawnOrUpdateFloatingText(block.getLocation(), storageName, guid, doublechest, direction);

						} else if (name.toLowerCase().startsWith("[label tool] ") && itemInHand.getType() == Material.STICK && blockStorageData != null) {
							
							String toolName = name.substring(13);
							Location ArmorStandLocation = blockStorageData.getArmorStand().getLocation().clone();
							
							if (toolName.equals("Y")) {
								if (isCrouching) {
									ArmorStandLocation.add(0,-0.1,0);
									player.sendMessage(ChatColor.AQUA + "Moved " + materialName + " label (" + ChatColor.RED + "-0.1" + ChatColor.AQUA + ") on the Y axis");
								} else {
									ArmorStandLocation.add(0,0.1,0);
									player.sendMessage(ChatColor.AQUA + "Moved " + materialName + " label (" + ChatColor.GREEN + "+0.1" + ChatColor.AQUA + ") on the Y axis");
								}
							} else if (toolName.equals("X")) {
								if (isCrouching) {
									ArmorStandLocation.add(-0.1,0,0);
									player.sendMessage(ChatColor.AQUA + "Moved " + materialName + " label (" + ChatColor.RED + "-0.1" + ChatColor.AQUA + ") on the X axis");
								} else {
									ArmorStandLocation.add(0.1,0,0);
									player.sendMessage(ChatColor.AQUA + "Moved " + materialName + " label (" + ChatColor.GREEN + "+0.1" + ChatColor.AQUA + ") on the X axis");
								}
							} else if (toolName.equals("Z")) {
								if (isCrouching) {
									ArmorStandLocation.add(0,0,-0.1);
									player.sendMessage(ChatColor.AQUA + "Moved " + materialName + " label (" + ChatColor.RED + "-0.1" + ChatColor.AQUA + ") on the Z axis");
								} else {
									ArmorStandLocation.add(0,0,0.1);
									player.sendMessage(ChatColor.AQUA + "Moved " + materialName + " label (" + ChatColor.GREEN + "+0.1" + ChatColor.AQUA + ") on the Z axis");
								}
							}
							updateArmorStandLocation(guid, ArmorStandLocation);
						}
					}
				}
			}
		}
	}
	
	private boolean isDoubleChest(Block block, Block[] adjacentBlock) {
		// Get the block's position
		if (block.getType() != Material.CHEST) return false;

		// Check if there's an adjacent chest
		Block adjacent = getAdjacent(block);
		if (adjacent != null && adjacent.getType() == Material.CHEST) {
			// Check if both blocks are part of a double chest (size 54)
			Chest chest1 = (Chest) block.getState();
			Chest chest2 = (Chest) adjacent.getState();

			return chest1.getInventory().getSize() == 54 && chest2.getInventory().getSize() == 54;
		}
		return false;
	}

    private Block getAdjacent(Block block) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                Block adjacent = block.getRelative(x, 0, z);
                if (adjacent.getType() == Material.CHEST) {
                    return adjacent;
                }
            }
        }
        return null;
    }

	private void removeStorageName(Block block) {
		StorageData blockData = getStorageDataByBlock(block);
		
		if (blockData != null) {
			//Bukkit.getLogger().info("BlockData Found: " + blockData.getGuid());
			ArmorStand armorStand = blockData.getArmorStand();
			armorStand.remove();
			removeStorageData(blockData.getGuid());
		}else {
			//Bukkit.getLogger().info("BlockData was Null");
		}
		
	}
	
	private void spawnOrUpdateFloatingText(Location location, String storageName, String guid, boolean doublechest, String direction) {
				
		double xOffset = 0.5;
		double yOffset = -1.3;
		double zOffset = 0.5;

		if (doublechest) {
			if (direction.equals("west")) {
				xOffset -= 0.5;
			} else if (direction.equals("east")) {
				xOffset += 0.5;
			} else if (direction.equals("north")) {
				zOffset -= 0.5;  // Adjust xOffset for north direction if needed
			} else if (direction.equals("south")) {
				zOffset += 0.5;  // Adjust xOffset for south direction if needed
			}
		}

		String ColoredStorageName = translateColors(storageName);
			
			ArmorStand existingStand = null;
			StorageData data = getStorageDataByGUID(guid);
			
			if (data != null) {
				existingStand = data.getArmorStand();
			}
			
			if (existingStand != null) {
				// Update the existing armor stand
				existingStand.setCustomName(ColoredStorageName);
				updateStorageDataName(guid, storageName);
			} else {
				
				
				Location armorstandLocation = location.clone();
				armorstandLocation.add(xOffset, yOffset, zOffset);
				
				saveStorageData(guid, storageName, location, armorstandLocation);
				
				ArmorStand armorStand = location.getWorld().spawn(armorstandLocation, ArmorStand.class);
				armorStand.setCustomName(ColoredStorageName);
				armorStand.setCustomNameVisible(true);
				armorStand.setGravity(false);
				armorStand.setInvisible(true);
				armorStand.setCollidable(false);
				armorStand.setInvulnerable(true);
				armorStand.setBasePlate(false); 
				armorStand.setArms(false);
				
			}
			
	}

	@EventHandler
	public void onArmorstandClick(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		//Bukkit.getLogger().info("Clicked Entity");
		if (event.getRightClicked() instanceof ArmorStand) {
			//Bukkit.getLogger().info("Clicked Armorstand");
			ArmorStand armorStand = (ArmorStand) event.getRightClicked();
			StorageData storageData = getStorageDataByArmorStand(armorStand);
			
			// Prevent naming if storage data exists and the player is holding a name tag
			if (storageData != null && player.getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
				event.setCancelled(true); // Cancel the event to block naming
				return; // Exit the method
			}
			
			Vector clickVector = event.getClickedPosition();
			if (clickVector.getY() < 1.3) {
				//player.sendMessage("You clicked Out Of Bounds: " + clickVector.getY());
			}
			
			if (storageData != null && clickVector.getY() > 1.3) {
				//player.sendMessage("You clicked In Bounds: " + clickVector.getY());
				Block storageBlock = storageData.getStorageBlock();
				
                // Determine if the block is a Chest or Barrel
                if (storageBlock.getState() instanceof Chest) {
                    Chest chest = (Chest) storageBlock.getState();

                    // Open the chest inventory directly without requiring a PlayerInteractEvent
                    player.openInventory(chest.getInventory());
					event.setCancelled(true);
                } else if (storageBlock.getState() instanceof Barrel) {
                    Barrel barrel = (Barrel) storageBlock.getState();

                    // Open the barrel inventory directly without requiring a PlayerInteractEvent
                    player.openInventory(barrel.getInventory());
					event.setCancelled(true);
                }
				
			}
		}
	}
	
	@EventHandler
	public void onArmorstandItemClick(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		// Check if the clicked entity is an ArmorStand
		if (event.getRightClicked() instanceof ArmorStand) {
			ArmorStand armorStand = (ArmorStand) event.getRightClicked();
			StorageData storageData = getStorageDataByArmorStand(armorStand);

			// Prevent naming if storage data exists and the player is holding a name tag
			if (storageData != null && player.getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
				event.setCancelled(true); // Cancel the event to block naming
				return; // Exit the method
			}
		}
	}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.CHEST || block.getType() == Material.BARREL) {
			if (block.getType() == Material.CHEST) {
				Block adjacentBlock = getAdjacent(block);
				if (isDoubleChest(block, new Block[]{adjacentBlock})) {
					
					StorageData blockStorageData = getStorageDataByBlock(block);
					StorageData adjacentStorageData = getStorageDataByBlock(adjacentBlock);
								
					if (adjacentStorageData != null) {
						String adjacentGuid = adjacentStorageData.getGuid();
						String adjacentName = adjacentStorageData.getName();
						removeStorageName(adjacentBlock);
						spawnOrUpdateFloatingText(adjacentBlock.getLocation(), adjacentName, adjacentGuid, false, "west");
					} else {
						if (blockStorageData != null) {
							String blockGuid = blockStorageData.getGuid();
							String blockName = blockStorageData.getName();
							removeStorageName(block);
							spawnOrUpdateFloatingText(adjacentBlock.getLocation(), blockName, blockGuid, false, "west");
						}
					}
					
				} else {
					removeStorageName(block);
				}
			} else {
					removeStorageName(block);
			}
        }
    }
	
	
    private void removeAllArmorStands() {
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class)) {
                armorStand.remove();
            }
        }
    }
	
	// Quick Deposit Functions
	
	private boolean isQuickDepositTrigger(Block block) {
		// Check if the block is a button
		if (block.getType().name().contains("BUTTON")) {
			// Loop through all adjacent blocks
			for (BlockFace face : BlockFace.values()) {
				// Check if any adjacent block is a barrel
				if (block.getRelative(face).getType() == Material.BARREL) {
					return true;
				}
			}
		}

		// Return false if no adjacent barrel is found
		return false;
	}

	public void quickDeposit(Player player) {
		Inventory playerInventory = player.getInventory();
		Location playerLocation = player.getLocation();
		List<Inventory> nearbyStorages = findNearbyStorages(playerLocation, SEARCH_RADIUS);

		for (Inventory storageInventory : nearbyStorages) {
			for (int i = 0; i < playerInventory.getSize(); i++) {
				ItemStack playerItem = playerInventory.getItem(i);
				if (playerItem != null && playerItem.getAmount() > 0) {
					depositItemIntoStorage(player, storageInventory, playerItem);
				}
			}
		}

		player.sendMessage(ChatColor.GREEN + "Quick deposit completed!");
	}

	private List<Inventory> findNearbyStorages(Location playerLocation, int radius) {
		List<Inventory> storageInventories = new ArrayList<>();
		World world = playerLocation.getWorld();

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Location checkLocation = playerLocation.clone().add(x, y, z);
					Block block = checkLocation.getBlock();
					if (block.getType() == Material.CHEST) {
						Chest chest = (Chest) block.getState();
						storageInventories.add(chest.getInventory());
					} else if (block.getType() == Material.BARREL) {
						Barrel barrel = (Barrel) block.getState();
						storageInventories.add(barrel.getInventory());
					}
				}
			}
		}

		return storageInventories;
	}

	private void depositItemIntoStorage(Player player, Inventory storageInventory, ItemStack playerItem) {
		for (int j = 0; j < storageInventory.getSize(); j++) {
			ItemStack storageItem = storageInventory.getItem(j);

			if (storageItem != null && storageItem.isSimilar(playerItem)) {
				int availableSpace = storageItem.getMaxStackSize() - storageItem.getAmount();
				
				if (availableSpace > 0) {
					int toAdd = Math.min(availableSpace, playerItem.getAmount());
					storageItem.setAmount(storageItem.getAmount() + toAdd);
					playerItem.setAmount(playerItem.getAmount() - toAdd);
					
					storageInventory.setItem(j, storageItem);

					if (playerItem.getAmount() == 0) {
						return;
					}
				}
			}
		}

		for (int j = 0; j < storageInventory.getSize(); j++) {
			if (storageInventory.getItem(j) == null) { 
				if (itemExistsInStorage(storageInventory, playerItem)) {
					storageInventory.setItem(j, playerItem.clone());
					playerItem.setAmount(0); 
					return;
				}
			}
		}

		if (playerItem.getAmount() > 0) {
		}
	}

	private boolean itemExistsInStorage(Inventory storageInventory, ItemStack playerItem) {
		for (int i = 0; i < storageInventory.getSize(); i++) {
			ItemStack storageItem = storageInventory.getItem(i);
			if (storageItem != null && storageItem.isSimilar(playerItem)) {
				return true; 
			}
		}
		return false;
	}
	
	// Command Handler
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("removearmorstands")) {
            if (sender.hasPermission("removearmorstands.use")) {
                removeAllArmorStands();
                sender.sendMessage(ChatColor.GREEN + "All ArmorStands have been removed.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }
        }
		if (command.getName().equalsIgnoreCase("quickdeposit") && sender instanceof Player) {
			if (sender.hasPermission("quickdeposit.use")) {
				Player player = (Player) sender;
				quickDeposit(player);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return false;
			}
        }
        return false;
    }

}
