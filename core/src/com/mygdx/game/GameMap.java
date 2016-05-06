////////////////////////////////////////////////////////////////////////////////
//  Project:  theGame-core
//  File:     Map.java
//  
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.awt.Graphics;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.mygdx.ai.Agent;
import com.mygdx.ai.DefaultIndexedGraphWithPublicNodes;
import com.mygdx.ai.GraphCreator;
import com.mygdx.ai.PositionIndexedNode;


public class GameMap {
    private String title;
    public int numRows;
    public int numCols;
    public static final int TILE_WIDTH = Tile.getTileWidth();
    public static final int TILE_HEIGHT = Tile.getTileHeight();
    private Tile[][] mapTiles;
    public int mapWidth;
    public int mapHeight;
    private boolean mapMoveLeft;
    private boolean mapMoveRight;
    private boolean mapMoveUp;
    private boolean mapMoveDown;
    private boolean smallWidth;
    private boolean smallHeight;
    float stateTime;
    private static final JSONParser parser = new JSONParser();
    
    public List<Player> players;

    protected int mapPosX;
    protected int mapPosY;

    private int winX = 400;
    private int winY = 240;
    public LocalPlayer player;

    boolean multiplayerEnabled = true;

    Texture mapImage;
    TextureRegion fov;
    ItemCollector itemsOnField;
    public ObjectCollector objectList;

    //map for keeping track of textures to dispose of
    // thing = GameObject or Item
    AbstractMap<Object, Texture> thingToTextureMap;
    
    public DefaultIndexedGraphWithPublicNodes<PositionIndexedNode> nodeGraph;
	private TextureRegion defaultNodeTextureRegion;
	private ShapeRenderer shapeRenderer;
    
	protected List<Agent> agents;
	
    public GameMap(String mapFile, String tileFile, LocalPlayer player) throws IOException {
    	agents = new ArrayList<Agent>();
    	
    	shapeRenderer = new ShapeRenderer();
    	defaultNodeTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("art/node_circle_default.png")));
    	
        thingToTextureMap = new HashMap < Object, Texture > ();
        players = new ArrayList<Player>();
        players.add(player);
        this.player = player;
        player.setCurrentMap(this);

        ///////////////////////////////////
        // convert mapFile into Tile[][] //
        ///////////////////////////////////
        JSONObject mapJSON = (JSONObject) parser.parse(new FileReader(mapFile));
        title = "TITLE GOES HERE"; //TODO: map titles included in map json
        numRows = (Integer) mapJSON.get("height");
        numCols = (Integer) mapJSON.get("width");
        mapTiles = new Tile[numRows][numCols];
        stateTime = 0f;

        System.out.println("numrows: " + numRows);
        Scanner tileFileScanner = new Scanner(new File(tileFile));
        for (int r = numRows - 1; r >= 0; r--) {
            String currentRow = mapFileScanner.nextLine();
            String[] individualIds = currentRow.split("\\s+");
            for (int c = 0; c < numCols; c++ /*ha*/ ) {
                String individualTileId = individualIds[c];
                //find corresponding line in tileFile
                String currentTilesLine = null;
                String[] currentAttributes = null;
                boolean found = false;
                while (!found) {
                    currentTilesLine = tileFileScanner.nextLine();
                    currentAttributes = currentTilesLine.split(", ");
                    if (currentAttributes[1].equals(individualTileId)) { //id matches tile we want
                        found = true;
                    }
                }
                //now currentAttributes should have all the attributes needed to create our tile object
                //name, id, imageURI, left, right, top, bottom <--- order in Tiles.txt
                //String imageURI, String name, boolean leftWall, boolean rightWall, boolean topWall, boolean bottomWall <----- order in Tile()
                String imageURI = currentAttributes[2];
                String name = currentAttributes[0];
                boolean passable = Boolean.parseBoolean(currentAttributes[3]);
                //need to also get hazard once class Item is implemented
                Tile newTile = new Tile(c * TILE_WIDTH, r * TILE_HEIGHT, imageURI, name, passable);
                mapTiles[r][c] = newTile;
                tileFileScanner = new Scanner(new File(tileFile));
            }
        }

        tileFileScanner.close();

        //Initializes the ItemCollector object before giving it the information to create an array list of items.
        itemsOnField = new ItemCollector();
        int itemIndex = Integer.parseInt(mapFileScanner.nextLine());
        String[] currentAttributes = null;
        String className;
        int id;
        Point pos;
        if (itemIndex > 0) {
            for (int x = 0; x < itemIndex; x++) {
                String currentLine = mapFileScanner.nextLine();
                currentAttributes = currentLine.split(", ");
                className = currentAttributes[0];
                id = Integer.parseInt(currentAttributes[1]);
                pos = new Point(Float.parseFloat(currentAttributes[2]), Float.parseFloat(currentAttributes[3]));
                itemsOnField.addItem(className, id, pos);
            }

        }

        //Creates the ObjectCollector...object (kek) before giving it the information to create an array list of objects

        objectList = new ObjectCollector(TILE_WIDTH, TILE_HEIGHT, numCols, numRows, mapTiles);
        int objectIndex = Integer.parseInt(mapFileScanner.nextLine());
        if (objectIndex > 0) {
            for (int x = 0; x < objectIndex; x++) {
                String currentLine = mapFileScanner.nextLine();
                currentAttributes = currentLine.split(", ");
                className = currentAttributes[0];
                id = Integer.parseInt(currentAttributes[1]);
                pos = new Point(Float.parseFloat(currentAttributes[2]), Float.parseFloat(currentAttributes[3]));
                objectList.addObject(className, id, pos);
            }

        }


        mapFileScanner.close();
        ///////////////////////////////////////////////////////////
        // create and save png which is composite of tile images //
        ///////////////////////////////////////////////////////////
        System.out.println("CREATING COMPOSITE MAP IMAGE");
        BufferedImage bigImage = new BufferedImage(TILE_WIDTH * numCols, TILE_HEIGHT * numRows, BufferedImage.TYPE_INT_ARGB); //made up of combined tiles
        Graphics g = bigImage.getGraphics();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                BufferedImage currTileImg;
                currTileImg = ImageIO.read(new File("../core/assets/" + mapTiles[numRows - 1 - r][c].imageURI));
                g.drawImage(currTileImg, c * TILE_WIDTH, r * TILE_HEIGHT, null);
            }
        }

        try {
            ImageIO.write(bigImage, "PNG", new File(title + ".png"));
            System.out.println("))))))))))))))))))))))))");
            mapImage = new Texture(Gdx.files.internal(title + ".png"));
            System.out.println("((((((((((((((((((((((((");
        } catch (IOException e) {
            System.out.println(e);
        }
        mapWidth = TILE_WIDTH * numCols;
        mapHeight = TILE_HEIGHT * numRows;

        initialCharPos();
        updatePosY(0);
        updatePosX(0);


        fov = new TextureRegion(mapImage, mapPosX, mapPosY, 2 * winX, 2 * winY);

    }
    
    public void initializeGraph() {
        nodeGraph = GraphCreator.graphFromMap(this);
        System.out.println("initialized graph: " + nodeGraph);
    }

    //updating and drawing the visible part of the map
    public void update(SpriteBatch batch) {
        //All numbers in the y direction go from bottom to top in all other functions, but the final value is inverted within the the below function for proper usage.
        //player.update() must come before fov.setRegion or item drawing will lag behind map and player drawing
        for (Player player: players) {
            player.update(stateTime);
        }
        fov.setRegion(mapPosX, mapHeight - (mapPosY + 2 * winY), 2 * winX, 2 * winY);
        stateTime += Gdx.graphics.getDeltaTime();

        for (Agent agent: agents) {
        	agent.update(stateTime);
        }

    }

    public void draw(SpriteBatch batch) {

        batch.draw(fov, 0, 0);
        for (int x = 0; x < itemsOnField.getListSize(); x++) {
            Item item = itemsOnField.getItem(x);
            Texture textureToUse = thingToTextureMap.get(item);

            //see if item should be drawn
            if ((itemsOnField.getXPos(x) + itemsOnField.getWidth(x) > mapPosX && itemsOnField.getXPos(x) < mapPosX + 2 * winX) && (itemsOnField.getYPos(x) + itemsOnField.getHeight(x) > mapPosY && itemsOnField.getYPos(x) < mapPosY + 2 * winX)) {
                if (textureToUse == null) { //Item doesnt map to any texture, so make it one
                    textureToUse = new Texture(itemsOnField.getFloorImage(x));
                    thingToTextureMap.put(item, textureToUse); // save texture to use later
                }
                batch.draw(textureToUse, (float) itemsOnField.getXPos(x) - mapPosX, (float) itemsOnField.getYPos(x) - mapPosY);
            } else { //item should not be drawn, so free up its texture
                if (textureToUse != null) {
                    textureToUse.dispose();
                    thingToTextureMap.remove(item);
                }
            }
        }


        for (int x = 0; x < objectList.getListSize(); x++) {
            GameObject object = objectList.getObject(x);
            Texture textureToUse = thingToTextureMap.get(object);
            if ((objectList.getXPos(x) + objectList.getWidth(x) > mapPosX && objectList.getXPos(x) < mapPosX + 2 * winX) && (objectList.getYPos(x) + objectList.getHeight(x) > mapPosY && objectList.getYPos(x) < mapPosY + 2 * winX)) {
                if (textureToUse == null) { //Item doesnt map to any texture, so make it one
                    textureToUse = new Texture(objectList.getImage(x));
                    thingToTextureMap.put(object, textureToUse); // save texture to use later
                }
                batch.draw(textureToUse, (float) objectList.getXPos(x) - mapPosX, (float) objectList.getYPos(x) - mapPosY);
            } else {
                if (textureToUse != null) {
                    textureToUse.dispose();
                    thingToTextureMap.remove(object);
                }
            }
        }
        if (multiplayerEnabled) {
            for (Player player: players) {
                if (player instanceof RemotePlayer) {
                    player.drawAtPos(batch, (float) player.getPos().getX() - mapPosX, (float) player.getPos().getY() - mapPosY);
                }
            }
        }
        player.draw(batch);
        
        for (Agent agent: agents) {
        	agent.drawAtPos(batch, (float) agent.getPos().getX() - mapPosX, (float) agent.getPos().getY() - mapPosY);
        	agent.update(stateTime);
        }
    }

    //////////////////////////
    // ItemCollector methods//
    //////////////////////////

    public ItemCollector getItemList() {
        return itemsOnField;
    }

    public ItemCollector getNearbyItemList() {
        double itemRefPosX;
        double itemRefPosY;
        double charRefPosX = player.getPos().getX() + player.getLeft();
        double charRefPosY = player.getPos().getY() + player.getBottom();
        Array < Integer > indexValues = new Array < Integer > ();

        for (int x = 0; x < itemsOnField.getListSize(); x++) {
            if (player.getPos().getX() > itemsOnField.getXPos(x) + itemsOnField.getWidth(x)) {
                itemRefPosX = itemsOnField.getXPos(x) + itemsOnField.getWidth(x);
            } else {
                itemRefPosX = itemsOnField.getXPos(x);
                charRefPosX = player.getPos().getX() + player.getRight();
            }

            if (player.getPos().getY() > itemsOnField.getYPos(x) + itemsOnField.getHeight(x)) {
                itemRefPosY = itemsOnField.getYPos(x) + itemsOnField.getHeight(x);
            } else {
                itemRefPosY = itemsOnField.getYPos(x);
                charRefPosY = player.getPos().getY() + player.getTop();
            }

            if (Math.sqrt(Math.pow(itemRefPosX - charRefPosX, 2) + Math.pow(itemRefPosY - charRefPosY, 2)) <= 100) {
                indexValues.add(x);
            }
        }

        ItemCollector nearbyItemList = new ItemCollector();
        nearbyItemList = itemsOnField.createSubCollection(indexValues);

        return nearbyItemList;
    }

    protected void debugGraph() {
    	if (nodeGraph != null) { //if hosting
    		shapeRenderer.setAutoShapeType(true);
    		shapeRenderer.begin(ShapeType.Line);
    		shapeRenderer.setColor(Color.GREEN);
    		
    		//draw connections
        	for (PositionIndexedNode node: nodeGraph.getNodes()) {
        		for (Connection<PositionIndexedNode> connection: node.getConnections()) {
        			shapeRenderer.line(connection.getFromNode().x - mapPosX, connection.getFromNode().y - mapPosY, connection.getToNode().x - mapPosX, connection.getToNode().y - mapPosY);
        		}
        	}
        	shapeRenderer.end();
        	
        	//draw nodes
        	shapeRenderer.begin(ShapeType.Filled);
        	shapeRenderer.setColor(Color.BLUE);
        	for (PositionIndexedNode node: nodeGraph.getNodes()) {
        		node.draw(shapeRenderer, -mapPosX, -mapPosY);
        	}
        	shapeRenderer.end();
    		//shapeRenderer.flush();
        }
    }
    
    protected void debugShapes() {
    	if (nodeGraph != null) {
    		shapeRenderer.setColor(Color.GREEN);
    		shapeRenderer.begin(ShapeType.Line);
    		for (GameObject object: objectList.objectList) {
    			object.getShape().drawDebug(shapeRenderer, -mapPosX, -mapPosY);
    		}
        	shapeRenderer.end();
    		//shapeRenderer.flush();
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // set's the map and character draw positions based on char position //
    ///////////////////////////////////////////////////////////////////////

    public void initialCharPos() {

        if (mapWidth < 2 * winX) {
            smallWidth = true;
        } else {
            smallWidth = false;
        }
        if (mapHeight < 2 * winY) {
            smallHeight = true;
        } else {
            smallHeight = false;
        }

        adjustCharPlacement(); //Omit this line if movement testing with exact coordinates is being done,
        //as this method just provides a fail safe in case the char is placed off the map


        if (player.getPos().getX() < winX && smallWidth == false) {
            player.drawPosX = (float) player.getPos().getX();
        } else if (player.getPos().getX() > mapWidth - winX && smallWidth == false) {
            player.drawPosX = (float)(player.getPos().getX() - mapWidth + 2 * winX);
        }


        if (player.getPos().getY() < winY && smallHeight == false) {
            player.drawPosY = (float) player.getPos().getY();
        } else if (player.getPos().getY() > mapHeight - winY && smallHeight == false) {
            player.drawPosY = (float)(player.getPos().getY() - (mapHeight - winY) + winY);
        }
        if (smallWidth == true) {
            mapPosX = mapWidth / 2 - winX;
        }
        if (smallHeight == true) {
            mapPosY = mapHeight / 2 - winY;
        }
    }


    public void adjustCharPlacement() {
        Point playerPos = player.getPos();
        if (playerPos.getX() < -15) {
            player.setX(-15);
        } else if (playerPos.getX() > mapWidth - 50) {
            player.setX(mapWidth - 50);
        }
        if (playerPos.getY() < 5) {
            player.setY(5);
        } else if (playerPos.getY() > mapHeight - 55) {
            player.setY(mapHeight - 55);
        }
        if (smallWidth) {
            player.drawPosX = (float) playerPos.getX() + (winX - mapWidth / 2);
        }
        if (smallHeight) {
            player.drawPosY = (float) playerPos.getY() + (winY - mapHeight / 2);
        }
        boolean enclosed = true;
        while (enclosed == true) {
            int x = (int)(playerPos.getX() - 15) / TILE_WIDTH;
            int y = (int)(playerPos.getY()) / TILE_HEIGHT;
            if (!mapTiles[y][x].isPassable()) {
                x += TILE_WIDTH;
                y += TILE_HEIGHT;
            } else {
                enclosed = false;
            }
        }
    }

    public void updatePosX(float movement) {
        if (smallWidth == false) {
            if (player.getPos().getX() < winX) {
                mapPosX = 0;
            } else if (player.getPos().getX() > winX) {
                mapPosX = (int) player.getPos().getX() - winX;
            }

            if (player.getPos().getX() + winX > mapWidth) {
                mapPosX = mapWidth - 2 * winX;
            }



            mapMoveLeft = (mapPosX == 0) ? false : true;
            mapMoveRight = (mapPosX == (mapWidth - 2 * winX)) ? false : true;

            if (mapMoveLeft == true && mapMoveRight == true) {
                player.drawPosX = 400;
            } else {
                if (mapMoveLeft == false || mapMoveRight == false) {
                    player.drawPosX += movement;
                }
            }
        } else {
            player.drawPosX += movement;
        }
    }

    public void updatePosY(float movement) {
        if (smallHeight == false) {
            if (player.getPos().getY() < winY) {
                mapPosY = 0;
            } else if (player.getPos().getY() > winY) {
                mapPosY = (int) player.getPos().getY() - winY;
            }
            if (player.getPos().getY() + winY > mapHeight) {
                mapPosY = mapHeight - 2 * winY;
            }

            mapMoveDown = (mapPosY == 0) ? false : true;
            mapMoveUp = (mapPosY == (mapHeight - 2 * winY)) ? false : true;

            if (mapMoveUp == true && mapMoveDown == true) {
                player.drawPosY = 240;
            } else if (mapMoveUp == false || mapMoveDown == false) {
                player.drawPosY += movement;
            }
        } else {
            player.drawPosY += movement;
        }
    }


    ///////////////////////////////////////////
    // player movement (and camera movement) //
    ///////////////////////////////////////////

    public boolean moveLeft() throws Exception {
        boolean success = false;
        float deltaX = player.getMoveDist();
        if (!collides(Direction.LEFT, deltaX) && player.getPos().getX() > -player.getLeft()) {
            //player.getPos().getX() -= deltaX;
            player.setX((double)(player.getPos().getX() - deltaX));
            success = true;

            updatePosX(-deltaX);
        }
        return success;
    }

    public boolean moveRight() throws Exception {

        boolean success = false;
        float deltaX = player.getMoveDist();
        if (!collides(Direction.RIGHT, deltaX) && player.getPos().getX() < mapWidth - player.getRight()) {
            //player.getPos().getX() += deltaX;
            player.setX(player.getPos().getX() + deltaX);
            success = true;
            updatePosX(deltaX);
        }
        return success;
    }
    public boolean moveUp() throws Exception {
        boolean success = false;
        float deltaY = player.getMoveDist();
        if (!collides(Direction.UP, deltaY) && player.getPos().getY() < mapHeight - player.getTop()) {
            //player.getPos().getY() += deltaY;
            player.setY(player.getPos().getY() + deltaY);
            success = true;
            updatePosY(deltaY);
        }
        return success;
    }
    public boolean moveDown() throws Exception {
        boolean success = false;
        float deltaY = player.getMoveDist();
        if (!collides(Direction.DOWN, deltaY) && player.getPos().getY() > player.getBottom()) {
            //player.getPos().getY() -= deltaY;
            player.setY(player.getPos().getY() - deltaY);
            success = true;
            updatePosY(-deltaY);
        }
        return success;
    }


    public boolean collides(Direction direction, float speed) throws Exception {
        //////////////////////////////////////////////////
        //MAP IS INDEXED WITH (0,0) IN TOP LEFT         //
        //CHAR POS STARTS WITH (0,0) IN BOTTOM LEFT     //
        //////////////////////////////////////////////////
        double playerLeftSide = player.getPos().getX() + player.getLeft();
        double playerRightSide = player.getPos().getX() + player.getRight();
        double playerBottomSide = player.getPos().getY() + player.getBottom();
        double playerTopSide = player.getPos().getY() + player.getTop();


        int row0, col0, row1, col1;

        Shape playerShape = player.getShape();
        Shape futurePlayerShape = playerShape.deepCopy();
        Point moveDist;
        //calculate requested new position of character and the rows and cols of tiles to check for collision
        if (Direction.LEFT == direction) {
            int tilesToLeftXIndex = ((int) playerLeftSide / TILE_WIDTH);
            int bottomTileToLeftYIndex = (int) playerBottomSide / TILE_HEIGHT;
            int topTileToLeftYIndex = (int) playerTopSide / TILE_HEIGHT;


            //futurePlayerRect = new Rectangle(playerLeftSide - speed, playerRightSide - speed, playerTopSide, playerBottomSide);
            moveDist = new Point(-speed, 0);


            row0 = bottomTileToLeftYIndex;
            col0 = tilesToLeftXIndex;
            row1 = topTileToLeftYIndex;
            col1 = col0;



        } else if (Direction.RIGHT == direction) {
            int tilesToRightXIndex = ((int) playerRightSide / TILE_WIDTH);
            int bottomTileToLeftYIndex = (int) playerBottomSide / TILE_HEIGHT;
            int topTileToLeftYIndex = (int) playerTopSide / TILE_HEIGHT;


            //futurePlayerRect = new Rectangle(playerLeftSide + speed, playerRightSide + speed, playerTopSide, playerBottomSide);
            moveDist = new Point(speed, 0);

            row0 = bottomTileToLeftYIndex;
            col0 = tilesToRightXIndex;
            row1 = topTileToLeftYIndex;
            col1 = col0;


        } else if (Direction.UP == direction) {
            int tileToLeftXIndex = ((int) playerLeftSide / TILE_WIDTH);
            int tileToRightXIndex = ((int) playerRightSide / TILE_WIDTH);
            int tilesYIndex = ((int) playerTopSide / TILE_HEIGHT);


            //futurePlayerRect = new Rectangle(playerLeftSide, playerRightSide, playerTopSide + speed, playerBottomSide + speed);
            moveDist = new Point(0, speed);

            row0 = tilesYIndex;
            col0 = tileToLeftXIndex;
            row1 = row0;
            col1 = tileToRightXIndex;


        } else if (Direction.DOWN == direction) {
            int tileToLeftXIndex = ((int) playerLeftSide / TILE_WIDTH);
            int tileToRightXIndex = ((int) playerRightSide / TILE_WIDTH);
            int tilesYIndex = ((int) playerBottomSide / TILE_HEIGHT);


            //futurePlayerRect = new Rectangle(playerLeftSide, playerRightSide, playerTopSide + speed, playerBottomSide + speed);
            moveDist = new Point(0, -speed);

            row0 = tilesYIndex;
            col0 = tileToLeftXIndex;
            row1 = row0;
            col1 = tileToRightXIndex;
        } else {
            throw new Exception("invalid Direction");
        }
        futurePlayerShape.translate(moveDist);
        //row, col
        //tiles which the character *might* be in if they are allowed to continue to move in this direction
        Tile futureTile0 = mapTiles[row0][col0];
        Tile futureTile1 = mapTiles[row1][col1];

        // check for object intersection with future player shape
        List < GameObject > futureTile0Objects = (List < GameObject > ) objectList.objectGrid[row0][col0];
        List < GameObject > futureTile1Objects = (List < GameObject > ) objectList.objectGrid[row1][col1];

        for (GameObject object: futureTile0Objects) {
            if (object.getShape().intersects(futurePlayerShape)) {
                return true;
            }
        }


        for (GameObject object: futureTile1Objects) {
            if (object.getShape().intersects(futurePlayerShape)) {
                return true;
            }
        }


        if (!(futureTile0.isPassable() && futureTile1.isPassable())) {
            //check for collision
            Shape futureTile0Shape = futureTile0.getShape();
            Shape futureTile1Shape = futureTile1.getShape();





            return futurePlayerShape.intersects(futureTile0Shape) || futurePlayerShape.intersects(futureTile1Shape);
        }
        return false;
    }
    
    public Player getPlayerByUid(int uid) {
        for (Player player: players) {
            if (player.uid == uid) {
                return player;
            }
        }
        return null;
    }

	public void debugAgents() {
		for (Agent agent: agents) {
			if (agent.graphPath != null) {
				drawGraphPath(agent.graphPath);
			}
		}
	}
	
	private void drawGraphPath(GraphPath<PositionIndexedNode> graphPath) {
		shapeRenderer.setAutoShapeType(true);
		shapeRenderer.begin(ShapeType.Line);
		int nodeIndex = 0;
    	for (PositionIndexedNode node: graphPath) {
    		Array<Connection<PositionIndexedNode>> connections = node.getConnections();
    		
    		//highlight connection of path
    		if (nodeIndex <= graphPath.getCount() - 2) {
	    		for (int i = 0; i < connections.size; i++) {
	    			if (connections.get(i).getToNode().equals(graphPath.get(nodeIndex + 1))) {
	    				shapeRenderer.setColor(Color.PINK);
	    				shapeRenderer.line(connections.get(i).getFromNode().x 
	    						- mapPosX, connections.get(i).getFromNode().y 
	    						- mapPosY, connections.get(i).getToNode().x 
	    						- mapPosX, connections.get(i).getToNode().y - mapPosY);
	    			}
	    		}
    		}
    		
    		//highlight node
    		if (nodeIndex == 0) { //color start node
    			shapeRenderer.setColor(Color.GREEN);
    		} else if (nodeIndex == graphPath.getCount() - 1) { //color end node
    			shapeRenderer.setColor(Color.RED);
    		} else {
    			shapeRenderer.setColor(Color.PURPLE); //color intermediate nodes
    		}
    		node.draw(shapeRenderer, -mapPosX, -mapPosY);
    		nodeIndex++;
    	}
    	shapeRenderer.end();
    	
	}
}