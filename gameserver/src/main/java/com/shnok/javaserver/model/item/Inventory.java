package com.shnok.javaserver.model.item;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBEtcItem;
import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.enums.item.EtcItemType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.item.listeners.GearListener;
import com.shnok.javaserver.model.item.listeners.StatsListener;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import javolution.util.FastList;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Getter
@Log4j2
public abstract class Inventory extends ItemContainer {
    private final ItemInstance[] gear;
    private List<GearListener> gearListeners;
    protected int totalWeight;
    private int inventorySize = 80;

    protected Inventory(Entity owner) {
        super(owner);
        gear = new ItemInstance[15];
        if(owner.isPlayer()) {
            gearListeners = new FastList<>();
            addGearListener(new StatsListener(getOwner()));
        }
    }

    /**
     * Adds new inventory's gear listener
     * @param listener
     */
    public synchronized void addGearListener(GearListener listener) {
        gearListeners.add(listener);
    }

    /**
     * Removes a gear listener
     * @param listener
     */
    public synchronized void removeGearListener(GearListener listener) {
        gearListeners.remove(listener);
    }

    public void setEquippedItems(List<DBItem> itemData) {}

    protected abstract ItemLocation getEquipLocation();

    public void equipItemAndRecord(ItemInstance item) {
        equipItem(item);
    }

    public synchronized void equipItem(ItemInstance item) {
        ItemSlot targetSlot = ItemSlot.none;
        DBItem itemData = item.getItem();
        if(itemData instanceof DBEtcItem) {
            if(itemData.getType() == EtcItemType.arrow) {
                targetSlot = ItemSlot.rhand;
            } else {
                log.warn("Tried to equip a wrong item.");
                return;
            }
        } else {
            targetSlot = itemData.getBodyPart();
        }

        switch (targetSlot) {
            case lrhand: {
                if (setEquipItem(ItemSlot.lhand, null) != null) {
                    // exchange 2h for 2h
                    setEquipItem(ItemSlot.rhand, null);
                    setEquipItem(ItemSlot.lhand, null);
                } else {
                    setEquipItem(ItemSlot.rhand, null);
                }

                setEquipItem(ItemSlot.rhand, item);
                setEquipItem(ItemSlot.lrhand, item);
                break;
            }
            case lhand: {
                if (!(item.getItem() instanceof DBEtcItem) ||
                        (item.getItem().getType() != EtcItemType.arrow)) {
                    ItemInstance old1 = setEquipItem(ItemSlot.lrhand, null);

                    if (old1 != null) {
                        setEquipItem(ItemSlot.rhand, null);
                    }
                }

                setEquipItem(ItemSlot.lhand, item);
                break;
            }
            case rhand: {
                if (gear[ItemSlot.lrhand.getValue()] != null) {
                    setEquipItem(ItemSlot.lrhand, null);
                }

                setEquipItem(ItemSlot.rhand, item);
                break;
            }
            case lear:
            case rear:
            case earring: {
                if (gear[ItemSlot.lear.getValue()] == null) {
                    setEquipItem(ItemSlot.lear, item);
                } else if (gear[ItemSlot.rear.getValue()] == null) {
                    setEquipItem(ItemSlot.rear, item);
                } else {
                    setEquipItem(ItemSlot.lear, item);
                }

                break;
            }
            case lfinger:
            case rfinger:
            case ring: {
                if (gear[ItemSlot.lfinger.getValue()] == null) {
                    setEquipItem(ItemSlot.lfinger, item);
                } else if (gear[ItemSlot.rfinger.getValue()] == null) {
                    setEquipItem(ItemSlot.rfinger, item);
                } else {
                    setEquipItem(ItemSlot.lfinger, item);
                }

                break;
            }
            case neck:
                setEquipItem(ItemSlot.neck, item);
                break;
            case fullarmor:
                setEquipItem(ItemSlot.legs, null);
                setEquipItem(ItemSlot.chest, item);
                break;
            case chest:
                setEquipItem(ItemSlot.chest, item);
                break;
            case legs: {
                // handle full armor
                ItemInstance chest = getEquippedItem(ItemSlot.chest);
                if ((chest != null) && (chest.getItem().getBodyPart() == ItemSlot.fullarmor)) {
                    setEquipItem(ItemSlot.chest, null);
                }

                setEquipItem(ItemSlot.legs, item);
                break;
            }
            case feet:
                setEquipItem(ItemSlot.feet, item);
                break;
            case gloves:
                setEquipItem(ItemSlot.gloves, item);
                break;
            case head:
                setEquipItem(ItemSlot.head, item);
                break;
            case underwear:
                setEquipItem(ItemSlot.underwear, item);
                break;
            default:
                log.warn("unknown body slot:" + targetSlot);
        }
    }

    public ItemInstance getEquippedItem(ItemSlot slot) {
        return gear[slot.getValue()];
    }

    public int getEquippedItemId(ItemSlot slot) {
        if(gear[slot.getValue()] != null) {
            return gear[slot.getValue()].getItemId();
        }

        return 0;
    }


    public ItemInstance setEquipItem(ItemSlot slot, ItemInstance item) {
        ItemInstance old = gear[slot.getValue()];
        if (old != item) {
            if (old != null) {
                gear[slot.getValue()] = null;
                // Put old item from equipment slot to base location
                old.setLocation(getBaseLocation());

                // Find the next available slot
                old.setSlot(findNextAvailableSlot(getInventorySize()));
                // If old item slot was lower than current slot
                if(item != null) {
                    if(old.getSlot() > item.getSlot()) {
                        old.setSlot(item.getSlot());
                    }
                }

                old.setLastChange(ItemInstance.MODIFIED);
                log.debug("[ITEM][{}] UnEquipped {}. New slot: {}.", getOwner().getId(), old.getItemId(), old.getSlot());

                //TODO: update db
                if(owner.isPlayer()) {
                    for (GearListener listener : gearListeners) {
                        if (listener == null) {
                            continue;
                        }
                        listener.notifyUnequipped(slot.getValue(), old);
                    }
                }
            }
            // Add new item in slot of equipment
            if (item != null) {
                gear[slot.getValue()] = item;
                item.setLocation(getEquipLocation(), slot.getValue());
                item.setLastChange(ItemInstance.MODIFIED);
                log.debug("[ITEM][{}] Equipped {} in slot {}.", getOwner().getId(), item.getItemId(), slot);
                //TODO: update db
                if(owner.isPlayer()) {
                    for (GearListener listener : gearListeners) {
                        if (listener == null) {
                            continue;
                        }
                        listener.notifyEquipped(slot.getValue(), item);
                    }
                }
            }
        }
        return old;
    }


    // drops item the ground not regarding the count
    public ItemInstance dropItem(ItemInstance item, PlayerInstance actor) {
        synchronized (item) {
            if (!items.contains(item)) {
                return null;
            }

            removeItem(item);
            item.setOwnerId(0, actor);
            item.setLocation(ItemLocation.VOID);

            //TODO Update database
            //item.updateDatabase();

            refreshWeight();
        }
        return item;
    }

    // drop item from inventory by using its objectID and updates database
    public ItemInstance dropItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }

        // Adjust item quantity and create new instance to drop
        if (item.getCount() > count) {
            item.changeCount(-count, actor);

            //TODO Update database
            //item.updateDatabase();

            //TODO copy the item with new count in DB

            //TODO Update database
            //item.updateDatabase();

            refreshWeight();
            return item;
        }

        // Directly drop entire item
        return dropItem(item, actor);
    }

    // adds item to inventory and equip it if necessary
    @Override
    public void addItem(ItemInstance item) {
        super.addItem(item);
        if (item.isEquipped()) {
            equipItem(item);
        }
    }

    // removes item from inventory
    @Override
    protected void removeItem(ItemInstance item) {
        // unequip item if equiped
        for (byte i = 0; i < gear.length; i++) {
            if (gear[i] == item) {
                unEquipItemInSlot(ItemSlot.getSlot(i));
            }
        }

        super.removeItem(item);
    }

    public DBWeapon getEquippedWeapon() {
        ItemInstance item = getEquippedItem(ItemSlot.rhand);
        if(item != null && item.getItem() instanceof DBWeapon) {
            return (DBWeapon) item.getItem();
        }

        item = getEquippedItem(ItemSlot.lhand);
        if(item != null && item.getItem() instanceof DBWeapon) {
            return (DBWeapon) item.getItem();
        }

        return null;
    }

    public DBArmor getEquippedSecondaryWeapon() {
        ItemInstance item = getEquippedItem(ItemSlot.lhand);
        if(item != null && item.getItem() instanceof DBArmor) {
            return (DBArmor) item.getItem();
        }

        return null;
    }

    public synchronized void unEquipItemInSlotAndRecord(ItemSlot slot) {
        unEquipItemInSlot(slot);
        //TODO: Update grade penalty
//        if (getOwner() != null) {
//            ((PlayerInstance) getOwner()).refreshExpertisePenalty();
//        }
    }

    public synchronized ItemInstance unEquipItemInSlot(ItemSlot slot) {
        return setEquipItem(slot, null);
    }

    public boolean isSlotUsed(ItemSlot slot) {
        return getEquippedItem(slot) != null;
    }

    public void moveItemAndRecord(int objectId, int slot) {
        moveItem(objectId, slot);
    }

    public synchronized void moveItem(int objectId, int slot) {
        ItemInstance item = getItemByObjectId(objectId);
        if(item != null) {
            moveItem(item, slot);
        } else {
            log.warn("[ITEM][{}] Trying to move an unkown item with id {}.", owner.getId(), objectId);
        }
    }

    public synchronized void moveItem(ItemInstance item, int slot) {
        if(item.isEquipped()) {
            log.warn("[ITEM][{}] Trying to move an equipped item.", owner.getId());
            return;
        }

        item.setLastChange(ItemInstance.MODIFIED);
        item.setSlot(slot);
    }

    public synchronized List<ItemInstance> getUpdatedItems() {
        List<ItemInstance> changedItems = new FastList<>();
        items.forEach(item -> {
            if(item.getLastChange() == ItemInstance.MODIFIED) {
                changedItems.add(item);
            }
        });

        return changedItems;
    }

    public synchronized void resetAndApplyUpdatedItems() {
        items.forEach(item -> {
            if(item.getLastChange() != ItemInstance.UNCHANGED) {
                item.setLastChange(ItemInstance.UNCHANGED);
            }
        });
    }
}
