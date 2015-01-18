package com.mygdx.game;
 
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input.Keys;
 
public class TheGame extends ApplicationAdapter {
SpriteBatch batch;
private BitmapFont font;
private Sprite sprite;
@Override
public void create () {
batch = new SpriteBatch();
Texture texture = new Texture(Gdx.files.internal("index.png"));
sprite = new Sprite(texture, 20, 20, 50, 50);
sprite.setPosition(10, 10);
font = new BitmapFont();
font.setColor(Color.WHITE);
}
 
private int charX = 0;
private int charY = 0;
@Override
public void render () {
Gdx.gl.glClearColor(1, 0, 0, 1);
Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
if(Gdx.input.isKeyPressed(Keys.LEFT)) {
charX -= 200 * Gdx.graphics.getDeltaTime();
}
if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
charX += 200 * Gdx.graphics.getDeltaTime();
}
if(Gdx.input.isKeyPressed(Keys.UP)) {
charY += 200 * Gdx.graphics.getDeltaTime();
}
if(Gdx.input.isKeyPressed(Keys.DOWN)) {
charY -= 200 * Gdx.graphics.getDeltaTime();
}
batch.begin();
batch.draw(sprite, charX, charY);
batch.end();
}
} 