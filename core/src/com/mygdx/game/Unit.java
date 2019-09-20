package com.mygdx.game;

import com.mygdx.game.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.math.Vector2;
import java.util.*;

public class Unit {
	// static public Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));
	static public Unit sChosenUnit = null;

	// static properties
	// public boolean deployed = false;
	/* cordinates relative to major */

	class Damage {

		private int mAtkPoints;
		private int mDamageDealt;

		Damage(int atk) {
			mAtkPoints = atk;
		}
	}

	/* constant unit properties */
	final private UnitProperties mProps;

	private int gridX, gridY;
	private int				mOrder;
	private int				mCurrentHp;
	private boolean				mPrepared;
	private Unit				mMainTarget;
	private Array<Unit>			mAttackingGroup;
	private java.util.Queue<Damage> 	mReceivedDamages;
	private boolean				mActive;
	private Player				mTargetingPlayer;

	// free it somehow
	static private BitmapFont sFont = new BitmapFont();

	// private float posX, posY;
	Player		mOwner;
	Tile		mTile;
	DeployButton	mButton;

	private Buff[] mBuffs;

	public Unit(UnitProperties p, Player o) {
		mOwner = o;
		mProps = p;
		mOrder = -1;
		mPrepared = false; // for static only;
		mCurrentHp = mProps.hitpoints;
		mTargetingPlayer = mOwner.getOpponent();
		if (getType() == UnitProperties.Type.INFRA)
			mTargetingPlayer = mOwner;
		// mButton = new DeployButton();
		mReceivedDamages = new LinkedList<Damage>();
	}

	public void deploy(Grid grid, int x, int y) {
		gridX = x;
		gridY = y;
	}

	public String getName() { return mProps.name; }

	public int getHp() { return mCurrentHp; }
	public int getMaxHp() { return mProps.hitpoints; }
	public void setHp(int hp) {
		if (hp < 0)
			hp = 0;
		if (hp > getMaxHp())
			hp = getMaxHp();

		mCurrentHp = hp;

		if (hp == 0)
			getOwner().notifyUnitLost(this);
	}

	public int getAtk() { return mProps.damage; }

	public Sprite getIllust() { return mProps.illustSprite; }

	public UnitProperties.Range getRange() { return mProps.range; }
	public UnitProperties.Type getType() { return mProps.type; }

	public Tile getTile() { return mTile; }

	/**
	 * Unit.setTile() and Tile.setUnit() are symetric, which always call
	 * each other to ensure unit <-> tile pairs is syncronized.
	 */
	public void setTile(Tile t) {
		if (mTile == t)
			return;

		Tile oldTile = mTile;
		mTile = t;

		if (oldTile != null)
			oldTile.setUnit(null);

		if (mTile != null) {
			mTile.setUnit(this);
			if (mOwner == GameScreen.getControllingPlayer())
				mButton.setDisabled(true);
		} else {
			if (mOwner == GameScreen.getControllingPlayer())
				mButton.setDisabled(false);
		}

	}

	public int getOrder() { return mOrder; }
	public void setOrder(int o) { mOrder = o; }

	public Player getOwner() { return mOwner; }
	public UnitProperties.Pattern getPattern() { return mProps.pattern; }

	public boolean isDead() { return mCurrentHp <= 0; }

	public boolean isDeployed() { return mTile != null; }

	public boolean switchPlayer() {
		switch (mProps.type) {
			case TROOP:
			case TURRET:
				return true;
			case STATIC:
				return mPrepared;
			case INFRA:
				return false;
		}
		return true;
	}

	private void dealDamage(Damage dmg) {
		int hp = getHp();
		hp -= dmg.mAtkPoints;
		setHp(hp);
		mReceivedDamages.add(dmg); // for UI
	}

	private void engage(Unit target) {
		target.dealDamage(new Damage(getAtk()));
	}


	private void updateTargets() {
		int lane = getY();
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

	public void render(Vector2 spot, MyGdxGame game) {
		// just render here.
		Sprite sprite = isDead()? UnitProperties.sDebrickSprite: getIllust();

		game.mBatch.begin();
		game.mBatch.draw(sprite, spot.x, spot.y);
		game.mBatch.end();

		drawHpBar(spot.x, spot.y, game);

		drawOrderIcon(spot.x, spot.y, game);
		// sFont.dispose();
	}

	private void drawOrderIcon(float x, float y, MyGdxGame game) {
		ShapeRenderer sr = game.mShapeRenderer;
		float side = 14f;
		// x -= side;
		// y += side;
		x += 24f - side / 2;

		sr.begin(ShapeType.Filled);
		sr.setColor(mActive? Color.RED: Color.ORANGE);

		sr.rect(x, y, side, side);
		sr.end();

		y += side;

		sFont.setColor(Color.BLACK);

		game.mBatch.begin();
		y -= (side - sFont.getXHeight()) / 2;
		sFont.draw(game.mBatch, "" + getOrder(),
				x, y,
				side, //width
				Align.center,
				false // wrap
				);
		game.mBatch.end();
	}

	private void drawHpBar(float x, float y, MyGdxGame game) {
		float percentHp = 1.0f * getHp() / getMaxHp();
		float thick = 40f;
		float length = 6f;
		x -= length;
		ShapeRenderer sr = game.mShapeRenderer;
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.RED);
		sr.rect(x, y, length, thick);
		sr.setColor(Color.GREEN);
		sr.rect(x, y, length, thick * percentHp);
		sr.end();
	}

	public void setActive(boolean act) { mActive = act; }

	public int getX() { return mTile == null? -1: mTile.mX; }
	public int getY() { return mTile == null? -1: mTile.mY; }

	public Player getTargetingPlayer() { return mTargetingPlayer; }

	public class DeployButton extends Button {
		DeployButton(Skin skin) {
			super(skin, "deploy");
			Image inner = new Image(new SpriteDrawable(getIllust()));
			add(inner);

		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
		}
	}

	public Button asButton(Skin skin) {
		// if (skin != null)
			// mButton.setStyle(skin.getStyle("toggle", ImageButton.class));
		mButton = new DeployButton(skin);
		return mButton;
	}

	public Button asButton() {
		return mButton;
	}
}
