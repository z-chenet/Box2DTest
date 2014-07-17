package com.zachchenet.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Play implements Screen {

	TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	
	private Player player;
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera.position.set(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 0);
		//camera.setToOrtho(false, w, h);
		//camera.zoom /= 2;
		camera.update();

		renderer.setView(camera);
		
		renderer.render();
		
		renderer.getSpriteBatch().begin();
		player.draw(renderer.getSpriteBatch());
		renderer.getSpriteBatch().end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		camera.viewportWidth = width / 2;
		camera.viewportHeight = height / 2;

	}

	@Override
	public void show() {

		map = new TmxMapLoader().load("testMap.tmx");
		
		renderer = new OrthogonalTiledMapRenderer(map);
		
		camera = new OrthographicCamera();
				
		camera.zoom *= 2.5;
		
		player = new Player(new Sprite(new Texture("sprite.png")), (TiledMapTileLayer) map.getLayers().get(0));
		player.setPosition(50, 800 / 2 - 64 / 2);
		
		
		//Gdx.input.setInputProcessor(player);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		map.dispose();
		renderer.dispose();
		player.getTexture().dispose();
		
	}

}
