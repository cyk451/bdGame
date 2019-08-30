package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.*;

public class Unit {
	// static public Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));
	static public Unit chosen = null;

	// static properties
	// public boolean deployed = false;
	/* cordinates relative to major */

	class Attack {
		private int atkPoints;
		private int damageDealt;
	}

	/* constant unit properties */
	final private UnitProperties prop;

	private int gridX, gridY;
	private int order = -1;
	private int currentHp;
	private boolean prepared;
	private Unit mMainTarget;
	private Array<Unit> mAttackingGroup;
	// private float posX, posY;
	Player mOwner;
	Tile mTile;

	private Buff[] mBuffs;

	public Unit(UnitProperties p, Player o) {
		mOwner = o;
		prop = p;
		prepared = false; // for static only;
		currentHp = prop.hitpoints;
	}

	public void deploy(Grid grid, int x, int y) {
		gridX = x;
		gridY = y;
	}

	public int getX() { return mTile.x; }
	public int getY() { return mTile.y; }

	public String getName() { return prop.name; }

	public int getHp() { return currentHp; }
	public void setHp(int hp) { currentHp = hp; }

	public int getAtk() { return prop.damage; }

	public Sprite getIllust() { return prop.illustSprite; }

	public UnitProperties.Range getRange() { return prop.range; }
	public UnitProperties.Type getType() { return prop.type; }

	public Tile getTile() { return mTile; }
	public void setTile(Tile t) { mTile = t; }

	public int getOrder() { return order; }
	public void setOrder(int o) { order = o; }

	public Player getOwner() { return mOwner; }
	public UnitProperties.Pattern getPattern() { return prop.pattern; }

	public boolean isDead() { return currentHp < 0; }

	public boolean isDeployed() { return mTile != null; }

	public boolean switchPlayer() {
		switch (prop.type) {
			case TROOP:
			case TURRET:
				return true;
			case STATIC:
				return prepared;
			case INFRA:
				return false;
		}
		return true;
	}

	private void engage(Unit target) {
		int damage = getAtk();
		int hp = target.getHp();

		hp -= damage;

		target.setHp(hp);
	}


	public void getTargets() {
		int lane = getTile().y;
		mAttackingGroup = mOwner.getOpponent().getTargets(lane, getRange(), getPattern());
	}

	public void runTurn() {
		getTargets();

		for (Unit t: mAttackingGroup) {
			engage(t);
			// if (t.isDead())
				// mUnitQueue.remove(t);
		}
		// end turn
		// for (ab: prop.abilities) { ab.do }
	}

	public void render(float[] spot, MyGdxGame game) {
		// just render here.
		Sprite unitSprite = getIllust();
		BitmapFont font = new BitmapFont();

		game.batch.begin();

		drawHpBar(game);

		game.batch.draw(unitSprite, spot[0], spot[1]);
		font.draw(game.batch, "[" + getOrder() + "]", 
				spot[0], spot[1]);

		game.batch.end();
	}

	public void drawHpBar(MyGdxGame game) {
	}
}
