//  Project:  theGame-core
//  File:     ItemCollector.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.io.FileNotFoundException;

import com.badlogic.gdx.utils.Array;
public class ItemCollector {
	public Array < Item > itemList = new Array < Item > ();

	ItemCollector() {

	}

	ItemCollector(Array < Item > items) {
		itemList = items;
	}

	public void addItem(String className, int id, Point pos) {
		try {
			if (className == "Weapon") {
				//not implemented
			} else {
				itemList.add(new Item(id, pos));
			}
		} catch (FileNotFoundException e) {
			System.out.println("Why would this even!");
		}

	}

	public void addItem(Item item) {
		itemList.add(item);
	}

	public boolean isEmpty() {
		if (itemList.size == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param uid
	 * @return true if something was actually removed, false otherwise
	 */
	public Item removeByUid(int uid) {
		int index = 0;
		for (Item item: itemList) {
			if (uid == item.uid) {
				itemList.removeIndex(index);
				return item;
			}
			index++;
		}
		return null;
	}

	public void moveItem(ItemCollector rootList, int index) {
		addItem(rootList.getItem(index));
		rootList.deleteItem(index);
	}

	public void deleteSet(ItemCollector deletedList) {
		for (int x = 0; x < deletedList.getListSize(); x++) {

			itemList.removeValue(deletedList.getItem(x), true);

			for (int y = 0; y < itemList.size; y++) {
				System.out.println(itemList.get(y).getName());
			}
		}
	}

	public ItemCollector createSubCollection(Array < Integer > indexValues) {
		Array < Item > childItemList = new Array < Item > ();

		for (int x = 0; x < indexValues.size; x++) {
			childItemList.add(itemList.get(indexValues.get(x)));
		}
		ItemCollector childItemCollector = new ItemCollector(childItemList);

		return childItemCollector;
	}

	public void deleteItem(int index) {
		itemList.removeIndex(index);
	}

	public void deleteItem(Item x) {
		itemList.removeValue(x, true);
	}

	public Item getItem(int index) {
		return itemList.get(index);
	}

	public String getItemName(int index) {
		return itemList.get(index).getName();
	}

	public double getXPos(int index) {
		return itemList.get(index).getXPos();
	}

	public double getYPos(int index) {
		return itemList.get(index).getYPos();
	}

	public String getFloorImage(int index) {
		return itemList.get(index).getFloorImage();
	}

	public String getInvImage(int index) {
		return itemList.get(index).getInventoryImage();
	}
	public int getWidth(int index) {
		return itemList.get(index).getWidth();
	}
	public int getHeight(int index) {
		return itemList.get(index).getHeight();
	}

	public int getListSize() {
		return itemList.size;
	}@Override
	public String toString() {
		String toReturn = "";
		for (Item item: itemList) {
			toReturn += item.toString() + '\n';
		}
		return toReturn;
	}

	public Item getByUid(int uid) {
		for (Item item: itemList) {
			if (item.uid == uid) {
				return item;
			}
		}
		return null;
	}
}