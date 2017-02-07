package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.screens.MainScreen;

public class SomeGame extends Game {
	public SpriteBatch batch;


	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new MainScreen(this));

	}

	@Override
	public void render () {
		super.render();
	}

		public void dispose () {
	this.getScreen().dispose();
		batch.dispose();
	}

}
