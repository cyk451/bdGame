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
	private int mOrder;
	private int mCurrentHp;
	private boolean mPrepared;
	private Unit mMainTarget;
	private Array<Unit> mAttackingGroup;
	private LinkedList<Damage> mDamages;

	// free it somehow
	static private BitmapFont sFont = new BitmapFont();

	// private float posX, posY;
	Player mOwner;
	Tile mTile;
	UnitButton mButton;

	private Buff[] mBuffs;

	public class UnitButton extends ImageButton {
		Unit mUnit;
		UnitButton(Unit u) {
			super(new SpriteDrawable(u.getIllust()));
			mUnit = u;
			addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					System.out.println(mUnit.getName() + " selected...");
					if (!mUnit.isDeployed())
						sChosenUnit = mUnit;
					GameScreen.sInfoBar.setInformation(mUnit);
				}
			});
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
		}
	}

	public ImageButton asButton() {
		return mButton;
	}

	public Unit(UnitProperties p, Player o) {
		mOwner = o;
		mProps = p;
		mOrder = -1;
		mPrepared = false; // for static only;
		mCurrentHp = mProps.hitpoints;
		mButton = new UnitButton(this);
		mDamages = new LinkedList<Damage>();
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
				GameScreen.sUnitSelectBar.removeButton(asButton());
			mOwner.addUnit(this);
		} else {
			if (mOwner == GameScreen.getControllingPlayer())
				GameScreen.sUnitSelectBar.addButton(asButton());
			mOwner.removeUnit(this);
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
		mDamages.push(dmg); // for UI
	}

	private void engage(Unit target) {
		// TODO
		target.dealDamage(new Damage(getAtk()));
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
		sr.setColor(Color.ORANGE);

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
		float thick = 6f;
		float length = Grid.TILE_EDGE_PXL * Grid.SQRT3;
		y += 48f - thick;
		ShapeRenderer sr = game.mShapeRenderer;
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.RED);
		sr.rect(x, y, length, thick);
		sr.setColor(Color.GREEN);
		sr.rect(x, y, length * percentHp, thick);
		sr.end();
	}
}
