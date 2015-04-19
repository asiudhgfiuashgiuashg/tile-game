////////////////////////////////////////////////////////////////////////////////
//  File:     GuiItemList.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.util.ArrayList;





import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GuiItemList extends GuiElement
{
    ItemCollector itemList;
    private int numberOfItems = 9;
    private BitmapFont font;
    Player player;
    int selectedIndex;
    
    String arrowFileURI;
    Texture arrowTexture;
    
    public GuiItemList(Player player)
    {
    	this.player = player;
    	posX = player.drawPosX;
    	
    	if (player.drawPosY < 500)
    	{	
    		posY = player.drawPosY + player.up + 50;
    	}
    	else
    	{
    		posY = player.drawPosY - 100;
    	}
    	
    	font = new BitmapFont();
    	arrowFileURI = "itemlist_arrow.png";
    	arrowTexture = new Texture(Gdx.files.internal("itemlist_arrow.png"));
    }
    
    public void setItemList(ItemCollector list)
    {
        itemList = list;
    }
    
    @Override
    public void update()
    {
    	if (listeningForInput) {
    		if (Gdx.input.isKeyJustPressed(Keys.DOWN) && selectedIndex < itemList.getListSize() - 1) {
    			selectedIndex++;
    		} 
    		else 
    		{ 
    			if (Gdx.input.isKeyJustPressed(Keys.UP) && selectedIndex > 0) 
    			{
	    			selectedIndex--;
    			}
    		}
    		if(Gdx.input.isKeyJustPressed(Keys.Z))
    		{
    	
    			player.inv.moveItem(itemList,selectedIndex);
    		}
    		if(Gdx.input.isKeyJustPressed(Keys.F))
    		{
    			for (int x = 0; x < player.inv.getListSize(); x++)
    			{
    				System.out.println(player.inv.getItemName(x));
    			}
    			System.out.println("__________________________");
    		}
    			
    	}
    }
    
    
    public void displayItems(SpriteBatch batch)
    {
        String tempItemList = "";
        for (int x = 0; x < numberOfItems && x < itemList.getListSize(); x++)
        {
            tempItemList += itemList.getItemName(x) + "\n";
        }
        font.drawMultiLine(batch, tempItemList, posX, posY);
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        displayItems(batch);
        drawArrow(batch);
    }
    
    public void drawArrow(SpriteBatch batch) {
    	batch.draw(arrowTexture, posX - arrowTexture.getWidth() - 10, posY - selectedIndex * font.getLineHeight() - arrowTexture.getHeight());
    }
    
    public void giveInput(int key) {
    	
    }
}
