package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	final private UnitProperties mProps;

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
		mProps = p;
		prepared = false; // for static only;
		currentHp = mProps.hitpoints;
	}

	public void deploy(Grid grid, int x, int y) {
		gridX = x;
		gridY = y;
	}

	public int getX() { 
		if (mTile != null) 
			return mTile.mX; 
		return 0;
	}

	public int getY() { 
		if (mTile != null) 
			return mTile.mY; 
		return 0;
	}

	public String getName() { return mProps.name; }

	public int getHp() { return currentHp; }
	public int getMaxHp() { return mProps.hitpoints; }
	public void setHp(int hp) { 
		if (hp < 0)
			hp = 0;
		if (hp > getMaxHp())
			hp = getMaxHp();

		currentHp = hp;

		if (hp == 0)
			getOwner().notifyUnitLost(this);
	}

	public int getAtk() { return mProps.damage; }

	public Sprite getIllust() { return mProps.illustSprite; }

	public UnitProperties.Range getRange() { return mProps.range; }
	public UnitProperties.Type getType() { return mProps.type; }

	public Tile getTile() { return mTile; }
	public void setTile(Tile t) { mTile = t; }

	public int getOrder() { return order; }
	public void setOrder(int o) { order = o; }

	public Player getOwner() { return mOwner; }
	public UnitProperties.Pattern getPattern() { return mProps.pattern; }

	public boolean isDead() { return currentHp <= 0; }

	public boolean isDeployed() { return mTile != null; }

	public boolean switchPlayer() {
		switch (mProps.type) {
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


	private void updateTargets() {
		int lane = getTile().mY;
		mAttackingGroup = mOwner.getOpponent().getTargets(lane, getRange(), getPattern());
	}

	public void runTurn() {
		updateTargets();
		// notifyTargetSelcted

		// mAttackingGroup;
		System.out.println("runTurn: " + getName() + " has " + mAttackingGroup.size + "' targets");

		for (Unit t: mAttackingGroup) {
			// notifyBeforeAttack
			engage(t);
			// if (t.isDead())
				// mUnitQueue.remove(t);
			// notifyAfterAttack
		}
		// end turn
		// for (ab: mProps.abilities) { ab.do }
	}

	public void render(float[] spot, MyGdxGame game) {
		// just render here.
		Sprite sprite = isDead()? UnitProperties.sDebrickSprite: getIllust();
		BitmapFont font = new BitmapFont();

		game.mBatch.begin();

		game.mBatch.draw(sprite, spot[0], spot[1]);
		font.draw(game.mBatch, "[" + getOrder() + "]", 
				spot[0], spot[1]);

		game.mBatch.end();

		drawHpBar(spot, game);
	}

	public void drawHpBar(float[] where, MyGdxGame game) {
		float percentHp = 1.0f * getHp() / getMaxHp();
		ShapeRenderer sr = game.mShapeRenderer;
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.RED);
		sr.rect(where[0], where[1], Grid.TILE_EDGE_PXL, 3);
		sr.setColor(Color.GREEN);
		sr.rect(where[0], where[1], 
				Grid.TILE_EDGE_PXL * percentHp, 3);
		sr.end();
	}
}
