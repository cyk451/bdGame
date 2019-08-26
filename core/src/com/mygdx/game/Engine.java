package com.mygdx.game;

import java.util.*;
import com.badlogic.gdx.utils.*;
// import com.badlogic.gdx.utils;
//
// Terms:
// Turn, end of action of an individual unit.
// Round, end when all units end turns.

// Engine is a state machine. It should be called in following order.
//
// init
// loop (update)
//
public class Engine {
	enum Status {
		WAITING,
		RUNNING,
		FINISHED
	}

	final Player []mPlayers;
	int mRound;
	// TODO render this queue under bottom tab
	LinkedList<Unit>	mUnitQueue;
	private long		mLastTurnTS;
	private Player		mFirstPlayer;
	private Status		mStatus;

	public Engine(Player []p) {
		mStatus = Status.WAITING;
		mPlayers = p;
		mFirstPlayer = mPlayers[0];
	}

	private void beforeBattle() {
		mUnitQueue = formGlobalOrderList();
	}

	public void run() {
		mStatus = Status.RUNNING;
		mRound = 1;
		beforeBattle();
	}

	private Array<Unit> searchTargets(Unit u) {
		Player p = u.getOwner();
		Player o = p.getOpponent();
		Array<Unit> result = new Array<Unit>();

		int lane = u.getTile().y;

		int max = 5;
		do {
			result= o.getTargets(lane, u.getRange(), u.getPattern());
			--max;
		} while((result.size == 0) && (max > 0));

		return result;
	}

	private LinkedList<Unit> formGlobalOrderList() {
		Player activePlayer = mFirstPlayer;
		LinkedList<Unit> list = new LinkedList<Unit>();
		int finished = 0;

		mPlayers[0].rewind();
		mPlayers[1].rewind();

		System.out.println("");

		while (finished < 2) {
			Unit u = activePlayer.getNextUnit();
			if (u == null) {
				finished += 1;
				activePlayer = activePlayer.getOpponent();
				continue;
			}
			list.push(u);
			if (u.switchPlayer())
				activePlayer = activePlayer.getOpponent();
		}
		return list;
	}

	// here some dead unit are removed.
	private Unit getActiveUnit() {
		if (mUnitQueue.size() != 0) {
			return mUnitQueue.pop();
		}
		// round over, next round
		mUnitQueue = formGlobalOrderList();
		if (mUnitQueue.size() == 0)
			return null;
		endRound();
		return mUnitQueue.pop();
	}

	public void tick(float delta) {
		if (mStatus != Status.RUNNING)
			return ;
		long time = TimeUtils.nanoTime();
		if ((time - mLastTurnTS) < 1 * 1000 * 1000 * 1000) {
			return ;
		}
		// update();

		Unit u = getActiveUnit(); // pop queue

		u.runTurn();
		if (u.isDead())
			mUnitQueue.remove(u);

		mLastTurnTS = time;
	}

	private void endRound() {
		// do anything required for round ending
		mRound += 1;
	}

	private void dealDamage(Unit attacker, Unit attacked) {

		int damage = attacker.getAtk();
		int hp = attacked.getHp();
		return;
	}
}
