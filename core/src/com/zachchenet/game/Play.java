package com.zachchenet.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


public class Play implements Screen {

	TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	
	private Player player;
	
	float mapX;
	
	private World world;
	private Box2DDebugRenderer b2dr;
	private float tileSize;
	private ListenerClass cl;
	private Body playerBody;
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		camera.position.set(mapX, player.getY() + player.getHeight(), 0);
		player.setPosition(mapX, player.getY());
		playerBody.setTransform(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);
		Array<Body> bi = new Array<Body>();
		world.getBodies(bi);
		
		mapX += 3;
		camera.update();

		renderer.setView(camera);
		
		renderer.render();
		b2dr.render(world, camera.combined);
		
		renderer.getSpriteBatch().begin();
		player.draw(renderer.getSpriteBatch());
		renderer.getSpriteBatch().end();
		
		world.step(1/60f, 6, 2);
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		camera.viewportWidth = width / 2.5f;
		camera.viewportHeight = height / 2.5f;

	}

	@Override
	public void show() {
		System.out.println("test show");
		world = new World(new Vector2(0, 0), true);
		b2dr = new Box2DDebugRenderer();
		
		cl = new ListenerClass();
		world.setContactListener(cl);
		
		
		map = new TmxMapLoader().load("testMap.tmx");
		mapX = 0;
		
		renderer = new OrthogonalTiledMapRenderer(map);
		camera = new OrthographicCamera();
				
		camera.zoom *= 2.5;
		
		player = new Player(new Sprite(new Texture("sprite.png")), (TiledMapTileLayer) map.getLayers().get(1));
		player.setPosition(50, 800 / 2 - 64 / 2);
		
		//	gets layer that the obstacles are in
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("obstacles");
		tileSize = layer.getTileWidth();
		
		//	creates the bodys and fixtures
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		ChainShape shape = new ChainShape();
		
		//	making the box for player
		bdef.position.set(player.getX(), player.getY());
		bdef.position.set(0, 0);
		bdef.type = BodyType.DynamicBody;
		playerBody = world.createBody(bdef);
		playerBody.setUserData("player");
		
		Vector2[] v2 = new Vector2[5];
		v2[0] = new Vector2(
				-player.getWidth() / 2, -player.getHeight() / 2);
		v2[1] = new Vector2(
				-player.getWidth() / 2, player.getHeight() /2);
		v2[2] = new Vector2(
				player.getWidth() / 2, player.getHeight() / 2);
		v2[3] = new Vector2(
				player.getWidth()/2, -player.getHeight()/2);
		v2[4] = new Vector2(
				-player.getWidth() / 2, -player.getHeight() / 2);
		shape.createChain(v2);
		
		//shape.setAsBox(player.getWidth() / 2, player.getHeight() / 2);
		fdef.shape = shape;
		//fdef.isSensor = true;
		fdef.filter.categoryBits = 4;
		fdef.filter.maskBits = 2;
		playerBody.createFixture(fdef).setUserData("player");
		
		//PolygonShape sensorShape = new PolygonShape();
		//sensorShape.setAsBox(2, 2, new Vector2(0,-5), 0);
		//fdef.shape = sensorShape;
		//fdef.filter.categoryBits = 4;
		//fdef.filter.maskBits = 2;
		//fdef.isSensor = true;
		//playerBody.createFixture(fdef).setUserData("foot");
		
		//	goes through map and makes the boxes to deal with collisions
		for(int row = 0; row < layer.getHeight(); row++){
			for(int col = 0; col < layer.getWidth(); col++){
				Cell cell = layer.getCell(col, row);
				
				if(cell == null)continue;
				if(cell.getTile() == null)continue;
				
				bdef.type = BodyType.StaticBody;
				bdef.position.set(
						(col + 0.5f) * tileSize,
						(row + 0.5f) * tileSize
				);
				
				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[5];
				v[0] = new Vector2(
						-tileSize / 2, -tileSize / 2);
				v[1] = new Vector2(
						-tileSize / 2, tileSize /2);
				v[2] = new Vector2(
						tileSize / 2, tileSize / 2);
				v[3] = new Vector2(
						tileSize/2, -tileSize/2);
				v[4] = new Vector2(-tileSize/2, -tileSize/2);
				cs.createChain(v);
				
				fdef.shape = cs;
				fdef.isSensor = false;
				fdef.filter.categoryBits = 2;
				fdef.filter.maskBits = 4;
				
				world.createBody(bdef).createFixture(fdef);
			}
		}

		
		
		
		
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
		b2dr.dispose();
		world.dispose();
		
		
	}

}
