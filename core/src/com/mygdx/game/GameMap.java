////////////////////////////////////////////////////////////////////////////////
//  Project:  theGame-core
//  File:     Map.java
//  
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.awt.Graphics;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.mygdx.ai.Agent;
import com.mygdx.ai.DefaultIndexedGraphWithPublicNodes;
import com.mygdx.ai.GraphCreator;
import com.mygdx.ai.PositionIndexedNode;
import com.mygdx.game.player.Player;


public class GameMap implements JSONable {
	
	/**
	 * a dummy instance from which fromJSON() may be called
	 * @see com.mygdx.game.JSONable#fromJSON()
	 * 
	 */
	private static final GameMap CREATOR = new GameMap();

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
    
    /**
     * list of players who are on the map
     * These players are generated from the LobbyPlayers upon transition from lobby to in-game
     */
    public List<Player> players;

    protected int mapPosX;
    protected int mapPosY;

    private int winX = 400;
    private int winY = 240;
    public Player localPlayer;

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
	private static final String TILEART_DIRECTORY = "tileart";
	
	
	/**
	 * constructor used by the server
	 * also forms the base of thhe constructor used by the client
	 * @param mapFile
	 */
	public GameMap(FileHandle mapFile) {
		agents = new ArrayList<Agent>();
		players = new ArrayList<Player>();
		
		
		///////////////////////////////////
		// convert mapFile into Tile[][] //
		///////////////////////////////////
		
		JSONObject mapJSON = null;
		try {
			mapJSON = (JSONObject) parser.parse(mapFile.reader());
		} catch (ParseException e) {
			System.exit(-1);
			e.printStackTrace();
		} catch (IOException e) {
			System.exit(-1);
			e.printStackTrace();
		}
		
		title = "TITLE GOES HERE"; //TODO: map titles included in map json
		numRows = ((Number) mapJSON.get("height")).intValue();
		numCols = ((Number) mapJSON.get("width")).intValue();
		
		mapTiles = new Tile[numRows][numCols];
		stateTime = 0f;
		JSONArray tiles = (JSONArray) mapJSON.get("tiles");
		
		System.out.println("numrows: " + numRows);
		
		for (int r = 0; r < numRows; r++) {
			JSONArray row = (JSONArray) tiles.get(r);
			for (int c = 0; c < numCols; c++ /* ha */) {
				String currTileArtURI = (String) row.get(c);
				String imageURI = currTileArtURI;
				String name = currTileArtURI.substring(0,
						currTileArtURI.indexOf('.')); // for now, the tile's
														// name can be its URI
														// without the extension
				boolean passable = true; // all tiles are passable for now

				Tile newTile = new Tile(c * TILE_WIDTH, r * TILE_HEIGHT,
						imageURI, name, passable);

				mapTiles[numRows - 1 - r][c] = newTile;

			}
		}
		
		
		// Initializes the ItemCollector object before giving it the information
		// to create an array list of items.
		itemsOnField = new ItemCollector();

		// FIX adding items to a map is not currently supported by map-maker
		/*
		 * String className; int id; Point pos;
		 * 
		 * 
		 * for (int x = 0; x < itemIndex; x++) { className =
		 * currentAttributes[0]; id = Integer.parseInt(currentAttributes[1]);
		 * pos = new Point(Float.parseFloat(currentAttributes[2]),
		 * Float.parseFloat(currentAttributes[3]));
		 * itemsOnField.addItem(className, id, pos); }
		 */
		// Creates the ObjectCollector...object (kek) before giving it the
		// information to create an array list of objects
		JSONArray objects = (JSONArray) mapJSON.get("objects");
		objectList = new ObjectCollector(TILE_WIDTH, TILE_HEIGHT, numCols,
				numRows, mapTiles);
		for (Object object : objects) {
			JSONObject objectMap = (JSONObject) object;
			objectList.addObject(objectMap);
		}
		
        mapWidth = TILE_WIDTH * numCols;
        mapHeight = TILE_HEIGHT * numRows;



	}
	
	/**
	 * a constructor used by a client to specify the local player as well as the spritebatch to draw on
	 * @param mapFile
	 * @param player the local player
	 * @param batch the batch for the map to draw itself on
	 * @throws IOException
	 */
    public GameMap(FileHandle mapFile, SpriteBatch batch) throws IOException {
    	
    	this(mapFile);
    	
    	shapeRenderer = new ShapeRenderer();
    	defaultNodeTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("art/node_circle_default.png")));
    	
        thingToTextureMap = new HashMap < Object, Texture > ();
        

        
        ///////////////////////////////////////////////////////////
        // create and save png which is composite of tile images //
        ///////////////////////////////////////////////////////////
        System.out.println("CREATING COMPOSITE MAP IMAGE");
        FrameBuffer mapFB = new FrameBuffer(Format.RGBA8888, TILE_WIDTH * numCols, TILE_HEIGHT * numRows, false);
        
        //http://stackoverflow.com/questions/14729961/ambiguous-results-with-frame-buffers-in-libgdx
        Matrix4 projectionMatrix = new Matrix4();
        projectionMatrix.setToOrtho2D(0, 0, TILE_WIDTH * numCols, TILE_HEIGHT * numRows);
        batch.setProjectionMatrix(projectionMatrix);
        
        mapFB.begin();
        batch.begin();
        List<Texture> imageTexturesToDisposeOf = new ArrayList<Texture>();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {

                FileHandle imageFileHandle = Gdx.files.internal(TILEART_DIRECTORY  + File.separator + mapTiles[numRows -1 -r][c].imageURI);
                Texture imageTexture = new Texture(imageFileHandle);
                imageTexturesToDisposeOf.add(imageTexture);

                TextureRegion imageTextureRegion = new TextureRegion(imageTexture);
                imageTextureRegion.flip(false, true);
                batch.draw(imageTextureRegion, c *  TILE_WIDTH, r * TILE_HEIGHT);

            }
        }
        batch.end();
        mapFB.end();
        
        projectionMatrix.setToOrtho2D(0, 0, TheGame.SCREEN_WIDTH, TheGame.SCREEN_HEIGHT);
        batch.setProjectionMatrix(projectionMatrix);


        System.out.println("))))))))))))))))))))))))");
        mapImage = mapFB.getColorBufferTexture();
        System.out.println("((((((((((((((((((((((((");
        
        //stop using memory for temporary tile textures
        for (Texture texture: imageTexturesToDisposeOf) {
        	texture.dispose();
        }




        fov = new TextureRegion(mapImage, mapPosX, mapPosY, 2 * winX, 2 * winY);

    }
    
    public GameMap() {
		// TODO Auto-generated constructor stub
	}

	public void initializeGraph() {
        nodeGraph = GraphCreator.graphFromMap(this);
        System.out.println("initialized graph: " + nodeGraph);
    }

    //updating and drawing the visible part of the map
    public void update() {
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
                    textureToUse = new Texture(Gdx.files.internal("objectart" + File.separator + objectList.getImage(x)));
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
                if (player != this.localPlayer) {
                    player.drawAtPos(batch, (float) player.getPos().getX() - mapPosX, (float) player.getPos().getY() - mapPosY);
                }
            }
        }
        
        
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
        double charRefPosX = localPlayer.getPos().getX() + localPlayer.getLeft();
        double charRefPosY = localPlayer.getPos().getY() + localPlayer.getBottom();
        Array < Integer > indexValues = new Array < Integer > ();

        for (int x = 0; x < itemsOnField.getListSize(); x++) {
            if (localPlayer.getPos().getX() > itemsOnField.getXPos(x) + itemsOnField.getWidth(x)) {
                itemRefPosX = itemsOnField.getXPos(x) + itemsOnField.getWidth(x);
            } else {
                itemRefPosX = itemsOnField.getXPos(x);
                charRefPosX = localPlayer.getPos().getX() + localPlayer.getRight();
            }

            if (localPlayer.getPos().getY() > itemsOnField.getYPos(x) + itemsOnField.getHeight(x)) {
                itemRefPosY = itemsOnField.getYPos(x) + itemsOnField.getHeight(x);
            } else {
                itemRefPosY = itemsOnField.getYPos(x);
                charRefPosY = localPlayer.getPos().getY() + localPlayer.getTop();
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


        if (localPlayer.getPos().getX() < winX && smallWidth == false) {
            localPlayer.drawPosX = (float) localPlayer.getPos().getX();
        } else if (localPlayer.getPos().getX() > mapWidth - winX && smallWidth == false) {
            localPlayer.drawPosX = (float)(localPlayer.getPos().getX() - mapWidth + 2 * winX);
        }


        if (localPlayer.getPos().getY() < winY && smallHeight == false) {
            localPlayer.drawPosY = (float) localPlayer.getPos().getY();
        } else if (localPlayer.getPos().getY() > mapHeight - winY && smallHeight == false) {
            localPlayer.drawPosY = (float)(localPlayer.getPos().getY() - (mapHeight - winY) + winY);
        }
        if (smallWidth == true) {
            mapPosX = mapWidth / 2 - winX;
        }
        if (smallHeight == true) {
            mapPosY = mapHeight / 2 - winY;
        }
    }


    public void adjustCharPlacement() {
        Point playerPos = localPlayer.getPos();
        if (playerPos.getX() < -15) {
            localPlayer.setX(-15);
        } else if (playerPos.getX() > mapWidth - 50) {
            localPlayer.setX(mapWidth - 50);
        }
        if (playerPos.getY() < 5) {
            localPlayer.setY(5);
        } else if (playerPos.getY() > mapHeight - 55) {
            localPlayer.setY(mapHeight - 55);
        }
        if (smallWidth) {
            localPlayer.drawPosX = (float) playerPos.getX() + (winX - mapWidth / 2);
        }
        if (smallHeight) {
            localPlayer.drawPosY = (float) playerPos.getY() + (winY - mapHeight / 2);
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
            if (localPlayer.getPos().getX() < winX) {
                mapPosX = 0;
            } else if (localPlayer.getPos().getX() > winX) {
                mapPosX = (int) localPlayer.getPos().getX() - winX;
            }

            if (localPlayer.getPos().getX() + winX > mapWidth) {
                mapPosX = mapWidth - 2 * winX;
            }



            mapMoveLeft = (mapPosX == 0) ? false : true;
            mapMoveRight = (mapPosX == (mapWidth - 2 * winX)) ? false : true;

            if (mapMoveLeft == true && mapMoveRight == true) {
                localPlayer.drawPosX = 400;
            } else {
                if (mapMoveLeft == false || mapMoveRight == false) {
                    localPlayer.drawPosX += movement;
                }
            }
        } else {
            localPlayer.drawPosX += movement;
        }
    }

    public void updatePosY(float movement) {
        if (smallHeight == false) {
            if (localPlayer.getPos().getY() < winY) {
                mapPosY = 0;
            } else if (localPlayer.getPos().getY() > winY) {
                mapPosY = (int) localPlayer.getPos().getY() - winY;
            }
            if (localPlayer.getPos().getY() + winY > mapHeight) {
                mapPosY = mapHeight - 2 * winY;
            }

            mapMoveDown = (mapPosY == 0) ? false : true;
            mapMoveUp = (mapPosY == (mapHeight - 2 * winY)) ? false : true;

            if (mapMoveUp == true && mapMoveDown == true) {
                localPlayer.drawPosY = 240;
            } else if (mapMoveUp == false || mapMoveDown == false) {
                localPlayer.drawPosY += movement;
            }
        } else {
            localPlayer.drawPosY += movement;
        }
    }


    ///////////////////////////////////////////
    // player movement (and camera movement) //
    ///////////////////////////////////////////

    public boolean moveLeft() throws Exception {
        boolean success = false;
        float deltaX = localPlayer.getMoveDist();
        if (!collides(Direction.LEFT, deltaX) && localPlayer.getPos().getX() > -localPlayer.getLeft()) {
            //player.getPos().getX() -= deltaX;
            localPlayer.setX((double)(localPlayer.getPos().getX() - deltaX));
            success = true;

            updatePosX(-deltaX);
        }
        return success;
    }

    public boolean moveRight() throws Exception {

        boolean success = false;
        float deltaX = localPlayer.getMoveDist();
        if (!collides(Direction.RIGHT, deltaX) && localPlayer.getPos().getX() < mapWidth - localPlayer.getRight()) {
            //player.getPos().getX() += deltaX;
            localPlayer.setX(localPlayer.getPos().getX() + deltaX);
            success = true;
            updatePosX(deltaX);
        }
        return success;
    }
    public boolean moveUp() throws Exception {
        boolean success = false;
        float deltaY = localPlayer.getMoveDist();
        if (!collides(Direction.UP, deltaY) && localPlayer.getPos().getY() < mapHeight - localPlayer.getTop()) {
            //player.getPos().getY() += deltaY;
            localPlayer.setY(localPlayer.getPos().getY() + deltaY);
            success = true;
            updatePosY(deltaY);
        }
        return success;
    }
    public boolean moveDown() throws Exception {
        boolean success = false;
        float deltaY = localPlayer.getMoveDist();
        if (!collides(Direction.DOWN, deltaY) && localPlayer.getPos().getY() > localPlayer.getBottom()) {
            //player.getPos().getY() -= deltaY;
            localPlayer.setY(localPlayer.getPos().getY() - deltaY);
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
        double playerLeftSide = localPlayer.getPos().getX() + localPlayer.getLeft();
        double playerRightSide = localPlayer.getPos().getX() + localPlayer.getRight();
        double playerBottomSide = localPlayer.getPos().getY() + localPlayer.getBottom();
        double playerTopSide = localPlayer.getPos().getY() + localPlayer.getTop();


        int row0, col0, row1, col1;

        Shape playerShape = localPlayer.getShape();
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
	
	/**
	 * draw a pathway through the graph
	 * Used to debug pathfinding.
	 * @param graphPath
	 */
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

	/**
	 * @return a full json representation of the map
	 */
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * create a map from json
	 */
	@Override
	public JSONable fromJSON(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * add a player to the map
	 * 
	 * for now, this is only called upon transition from lobby to in-game when lobbyplayers are converted to players
	 *  and need placed on the map
	 * 
	 * TODO this is where the json map file should be referenced to know where to spawn the player
	 * For now, players will just spawn at a hardcoded position
	 * @param player the player to add
	 */
	public void addPlayer(Player player) {
		player.setPos(new Point(0, 0));
		this.players.add(player);
	}
}