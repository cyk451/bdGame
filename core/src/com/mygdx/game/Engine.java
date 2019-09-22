package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import java.util.*;
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
public class Engine extends Thread {
	final static public int	INTERVAL_MS = 3000;
	enum Status {
		WAITING,
		RUNNING,
		FINISHED
	}

	static public interface EventListener {
		/**
		 * EngineLister listen to engine's event. Allow other
		 * components to capture engine's events and do something.
		*/
		// void write(String what, int lastMs);
		void onRound(int round);
		void onLose();
		void onWin();
	}

	final Player 		[]mPlayers;
	Player			mWinner;
	private Unit		mActiveUnit;
	int			mRound;
	// TODO render this queue under bottom tab
	java.util.Queue<Unit>	mUnitQueue;

	private long		mLastTurnTS;
	private Player		mFirstPlayer;
	private Status		mStatus;
	private EventListener	mListener;

	// LinkedList<Event>	mEvents;

	public Engine(Player []p, EventListener el) {
		mWinner = null;
		mStatus = Status.WAITING;
		mPlayers = p;
		mListener = el;
		mFirstPlayer = mPlayers[0];
	}

	private void beforeBattle() {
		mUnitQueue = new LinkedList();
		// mUnitQueue = formGlobalOrderList();
	}

	public void run() {
		mStatus = Status.RUNNING;
		mRound = 1;
		mListener.onRound(mRound);
		beforeBattle();

		do {
			Gdx.app.log("Engine", "tick()");
			tick();
			try {
				sleep(INTERVAL_MS);
			} catch(InterruptedException ex) {
				System.out.println("Game thread sleep interrupted");
			}
		} while (mStatus == Status.RUNNING);
		System.out.println("battle thread ended");
	}

	private Array<Unit> searchTargets(Unit u) {
		Player p = u.getOwner();
		Player o = p.getOpponent();
		Array<Unit> result = new Array<Unit>();

		int lane = u.getTile().mY;

		int max = 5;
		do {
			result= o.getTargets(lane, u.getRange(), u.getPattern());
			if (result.size != 0)
				return result;
		} while(--max > 0);

		return result;
	}

	private java.util.Queue<Unit> formGlobalOrderList() {
		int activePlayerIdx = 0;
		int finished = 0;
		java.util.Queue<Unit> list = new LinkedList<Unit>();
		Iterator<Unit> iters[] = new Player.UnitIterator[2];

		iters[0] = mPlayers[0].iterator();
		iters[1] = mPlayers[1].iterator();
		Iterator<Unit> activeIter = iters[activePlayerIdx];

		do {
			// System.out.println("acting " + activePlayer.getName() + " popping");
			// Unit u = activePlayer.getNextUnit();
			if (!activeIter.hasNext()) {
				finished += 1;
				activePlayerIdx = 1 - activePlayerIdx;
				activeIter = iters[activePlayerIdx];
				continue;
			}
			Unit u = activeIter.next();
			// System.out.println(activePlayer.getName() + " enqueue one unit");
			list.add(u);
			if (u.switchPlayer()) {
				activePlayerIdx = 1 - activePlayerIdx;
				activeIter = iters[activePlayerIdx];
			}
		} while (finished < 2);
		System.out.println("formGlobalOrderList: list size " + list.size());
		return list;
	}

	// here some dead unit are removed.
	private Unit getActiveUnit() {
		while (mUnitQueue.size() != 0) {
			Unit candidate = mUnitQueue.remove();
			if (!candidate.isDead())
				return candidate;
		}
		// round over, next round
		mUnitQueue = formGlobalOrderList();
		return null;
	}

	public boolean isGameEnd() {
		if (mPlayers[0].isLose()) {
			mWinner = mPlayers[1];
			return true;
		}
		if (mPlayers[1].isLose()) {
			mWinner = mPlayers[0];
			return true;
		}
		return false;
	}

	public void tick(/*float delta*/) {
		if (isGameEnd()) {
			Gdx.app.log("Engine", "Player '" + mWinner.getName() + "' wins");
			if (mWinner == mPlayers[0])
				mListener.onWin();
			else
				mListener.onLose();
			mStatus = Status.FINISHED;
		}

		if (mStatus != Status.RUNNING)
			return ;

		if (mActiveUnit != null)
			mActiveUnit.setActive(false);

		mActiveUnit = getActiveUnit(); // pop queue
		// Gdx.app.log("Engine", "Player '" + mWinner.getName() + "' act " + mActiveUnit.getName());
		if (mActiveUnit == null) {
			endRound();
			return;
		}

		mActiveUnit.setActive(true);

		// System.out.println("tick: " + time + ": " + u.getOwner().getName() + u.getName() + " acting.");

		mActiveUnit.runTurn();
	}

	private void endRound() {
		// do anything required for round ending
		mListener.onRound(mRound);
		mRound += 1;
	}

	private void dealDamage(Unit attacker, Unit attacked) {
		int damage = attacker.getAtk();
		int hp = attacked.getHp();
		return;
	}
}
