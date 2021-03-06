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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.abilities.*;
import java.util.*;

public class Unit implements Comparable<Unit> {
	// static public Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));
	static public Unit sChosenUnit = null;

	// static properties
	// public boolean deployed = false;
	/* cordinates relative to major */

	public class Damage {

		private int mAtkPoints;
		// for display
		private int mDamageDealt;

		public Damage(int atk) {
			mAtkPoints = atk;
		}
		public Unit getSource() {
			return Unit.this;
		}
	}

	/* constant unit properties */
	final private UnitProperties mProps;

	private int gridX, gridY;
	private int				mOrder;
	private int				mCurrentHp;
	private boolean				mPrepared;
	private Unit				mMainTarget;
	private Array<Unit>			mTargetGroup;
	private java.util.Queue<Damage> 	mReceivedDamages;
	private boolean				mActive;
	private Player				mTargetingPlayer;
	private Sprite				mSprite;
	private Array<Ability>			mAbilities;
	private Unit				mAttacker;
	private int				mUid;
	private static int			mUidCounter = 0;

	// free it somehow
	static private BitmapFont sFont = new BitmapFont();

	// private float posX, posY;
	Player		mOwner;
	Tile		mTile;

	private Buff[] mBuffs;

	public Unit(UnitProperties p, Player o) {
		if (o == null || p == null)
			throw new IllegalArgumentException("Unit() inputs cannot be null");
		mOwner = o;
		mProps = p;
		mOrder = -1;
		mPrepared = false; // for tastics only;
		mCurrentHp = mProps.hitpoints;
		mReceivedDamages = new LinkedList<Damage>();
		mTargetGroup = new Array<Unit>();

		mSprite = new Sprite(mProps.illustration);
		if (mOwner.getFlip())
			mSprite.setFlip(true /* x-axis */,
					false /* y-axis */);

		mTargetingPlayer = mOwner.getOpponent();
		if (getType() == UnitProperties.Type.INFRA)
			mTargetingPlayer = mOwner;
		mAbilities = new Array<Ability>();
		for (Ability.Props ap: mProps.abilities) {
			if (ap == null)
				continue;
			mAbilities.add(ap.instance());
		}
		mUid = mUidCounter;
		mUidCounter += 1;
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

		if (hp == 0) {
			getOwner().notifyUnitLost(this);
		}
	}

	public int getAtk() { return mProps.damage; }

	public Sprite getIllust() { return mSprite; }

	public UnitProperties.Range getRange() { return mProps.range; }
	public UnitProperties.Type getType() { return mProps.type; }

	public Tile getTile() { return mTile; }

	/**
	 * Unit.setTile() and Tile.setUnit() are symetric, which always call
	 * each other to keep unit, tile be paired.
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
			mOwner.addUnit(this);
		} else {
			mOwner.removeUnit(this);
		}

	}

	public UnitProperties.TargetSelector getTargetSelector() {
		return mProps.selector;
	}

	public int getOrder() { return mOrder; }
	public void setOrder(int o) { mOrder = o; }

	public void setAttacker(Unit u) { mAttacker = u; }
	public Unit getAttacker() { return mAttacker; }

	public Player getOwner() { return mOwner; }
	public UnitProperties.Pattern getPattern() { return mProps.pattern; }

	public boolean isDead() { return mCurrentHp <= 0; }

	public boolean isDeployed() { return mTile != null; }

	public boolean switchPlayer() {
		switch (mProps.type) {
			case TROOP:
			case TURRET:
				return true;
			case TASTICS:
				return mPrepared;
			case INFRA:
				return false;
		}
		return true;
	}

	public void dealDamage(Damage dmg) {
		int hp = getHp();
		hp -= dmg.mAtkPoints;
		setHp(hp);
		mReceivedDamages.add(dmg); // for UI
		if (isDead()) {
			uponKilling(dmg.getSource());
			uponDeath(dmg.getSource());
		}
	}

	private void engage(Unit target) {
		if (target.isDead())
			return ;

		target.setAttacker(this);
		target.beforeAttacked(this);
		target.dealDamage(new Damage(getAtk()));
		Gdx.app.log("Unit", "caster: " + getOwner().getName() + " - " + getName());
		Gdx.app.log("Unit", "target: " + target.getOwner().getName() + " - " + target.getName());
		// afterAttacking(target);
	}

	public Damage buildDamage(int val) {
		return new Damage(val);
	}

	private void updateTargets() {
		Unit main = getTargetSelector().findTarget(this);
		mTargetGroup.clear();
		// mTargetGroup.add(main);
		if (main == null)
			return ;

		int x = main.getX(), y = main.getY();
		Grid grid = main.getOwner().getGrid();
		for (GridPoint2 offset: main.getPattern()) {
			Tile t = grid.getTile(x + offset.x, y + offset.y);
			if (t != null && t.getUnit() != null) {
				Gdx.app.log("Unit", "target " + (x + offset.x) + ", " + (y + offset.y));
				mTargetGroup.add(t.getUnit());
			}
		}
	}

	public void runTurn() {
		updateTargets();
		// notifyTargetSelcted
		// targetSelected();

		// mTargetGroup;
		Gdx.app.log("Unit", "runTurn: " + getName()  + " of "
				+ mOwner.getName() + " has "
				+ mTargetGroup.size + "' targets");

		for (Unit t: mTargetGroup) {
			beforeAttacking(t);
		}
		for (Unit t: mTargetGroup) {
			engage(t);
		}
		for (Unit t: mTargetGroup) {
			afterAttacking(t);
		}
		// end turn
		// for (ab: mProps.abilities) { ab.do }
	}

	public void render(MyGdxGame game) {
		if (mTile == null)
			return;
		Vector2 spot = mTile.getRenderSpot();
		// just render here.
		Sprite sprite = isDead()? UnitProperties.sDebrickSprite: getIllust();

		game.mBatch.begin();
		game.mBatch.draw(sprite, spot.x, spot.y);
		game.mBatch.end();

		if (isDead())
			return ;

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

		String debug = "(" + getX() + ", " + getY() + ")";
		sFont.draw(game.mBatch, debug,
				x, y - 4,
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

	public Array<Unit> getTargets() {
		return new Array<Unit>(mTargetGroup);
	}

	public void applyAbility(Unit from, Unit to, Ability.EventType t) {
		for (Ability ab: mAbilities)
			if (ab.getType() == t)
				ab.apply(from);
				// ab.apply(from, to);
	}

	public void beforeAttacked(Unit from) {
		applyAbility(from, this, Ability.EventType.BEFORE_ATTACKED);
	}

	public void afterAttacked(Unit from) {
		applyAbility(from, this, Ability.EventType.AFTER_ATTACKED);
	}

	public void beforeAttacking(Unit target) {
		applyAbility(this, target, Ability.EventType.BEFORE_ATTACKING);
	}

	public void afterAttacking(Unit target) {
		applyAbility(this, target, Ability.EventType.AFTER_ATTACKING);
	}

	public void uponDeath(Unit killer) {
		applyAbility(this, killer, Ability.EventType.UPON_DEATH);
	}

	public void uponKilling(Unit victim) {
		applyAbility(this, victim, Ability.EventType.UPON_KILLING);
	}

	@Override
	public int compareTo(Unit u) {
		return mUid - u.mUid;
	}
}
