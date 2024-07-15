package com.shnok.javaserver.model.item;

import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

import static com.shnok.javaserver.config.Configuration.server;

public class PlayerInventory extends Inventory {
    private ItemInstance money;

    public PlayerInventory(PlayerInstance owner) {
        super(owner);
    }

    public ItemInstance getMoneyInstance() {
        return money;
    }

    @Override
    public int getMoney() {
        return money != null ? money.getCount() : 0;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.EQUIPPED;
    }

    @Override
    protected Entity getOwner() {
        return owner;
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }

    // adds specified amount of money to player inventory
    public void addMoney(int count, PlayerInstance actor) {
        if (count > 0) {
            addItem(server.itemMoneyId(), count, actor);
        }
    }

    // removes specified amount of money from player inventory
    public void reduceMoney(String process, int count, PlayerInstance actor) {
        if (count > 0) {
            destroyItemByItemId(server.itemMoneyId(), count, actor);
        }
    }

    // adds item in inventory and checks money
    @Override
    public ItemInstance addItem(ItemInstance item, PlayerInstance actor) {
        item = super.addItem(item, actor);

        if ((item != null) && (item.getItemId() == server.itemMoneyId()) && !item.equals(money)) {
            money = item;
        }

        return item;
    }

    // adds item in inventory and checks money
    @Override
    public ItemInstance addItem(int itemId, int count, PlayerInstance actor) {
        ItemInstance item = super.addItem(itemId, count, actor);

        if ((item != null) && (item.getItemId() == server.itemMoneyId()) && !item.equals(money)) {
            money = item;
        }

        return item;
    }

    @Override
    public ItemInstance destroyItem(ItemInstance item, PlayerInstance actor) {
        item = super.destroyItem(item, actor);

        if ((money != null) && (money.getCount() <= 0)) {
            money = null;
        }

        return item;
    }

    // destroys item from inventory and checks money
    @Override
    public ItemInstance destroyItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = super.destroyItem(objectId, count, actor);

        if ((money != null) && (money.getCount() <= 0)) {
            money = null;
        }

        return item;
    }

    // destroy item from inventory by using its itemId and checks money
    @Override
    public ItemInstance destroyItemByItemId(int itemId, int count, PlayerInstance actor) {
        ItemInstance item = super.destroyItemByItemId(itemId, count, actor);

        if ((money != null) && (money.getCount() <= 0)) {
            money = null;
        }

        return item;
    }

    // drop item from inventory and checks money
    @Override
    public ItemInstance dropItem(ItemInstance item, PlayerInstance actor) {
        item = super.dropItem(item, actor);

        if ((money != null) && ((money.getCount() <= 0) || (money.getOwnerId() != getOwnerId()))) {
            money = null;
        }

        return item;
    }

    // drop item from inventory by using its objectID and money

    @Override
    public ItemInstance dropItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = super.dropItem(objectId, count, actor);

        if ((money != null) && ((money.getCount() <= 0) || (money.getOwnerId() != getOwnerId()))) {
            money = null;
        }

        return item;
    }

    // when removes item from inventory, remove also owner shortcuts.
    @Override
    protected void removeItem(ItemInstance item) {
        // Removes any reference to the item from Shortcut bar
        //getOwner().removeItemFromShortCut(item.getObjectId());

        if (item.getItemId() == server.itemMoneyId()) {
            money = null;
        }

        super.removeItem(item);
    }
}
