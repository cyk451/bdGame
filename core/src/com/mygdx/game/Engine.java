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
public class Engine extends Thread {
	final static public int	INTERVAL_MS = 1000;
	enum Status {
		WAITING,
		RUNNING,
		FINISHED
	}

	static public interface EventListener {
		/** EngineLister listen to engine's event. Be care about recursive calls.
		*/
		// void write(String what, int lastMs);
		void onRound(int round);
		void onLose();
		void onWin();
	}

	final Player 		[]mPlayers;
	Player			mWinner;
	int			mRound;
	// TODO render this queue under bottom tab
	LinkedList<Unit>	mUnitQueue;
	private long		mLastTurnTS;
	private Player		mFirstPlayer;
	private Status		mStatus;
	private EventListener	mListener;

	public Engine(Player []p, EventListener el) {
		mWinner = null;
		mStatus = Status.WAITING;
		mPlayers = p;
		mListener = el;
		mFirstPlayer = mPlayers[0];
	}

	private void beforeBattle() {
		mUnitQueue = formGlobalOrderList();
	}

	public void run() {
		mStatus = Status.RUNNING;
		mRound = 1;
		mListener.onRound(mRound);
		beforeBattle();

		while (mStatus == Status.RUNNING) {
			tick();
			try {
				sleep(INTERVAL_MS);
			} catch(InterruptedException ex) {
				System.out.println("Game thread sleep interrupted");
			}
		}
		if (mPlayers[0].isLose())
			mListener.onLose();
		else
			mListener.onWin();
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

	private LinkedList<Unit> formGlobalOrderList() {
		Player activePlayer = mFirstPlayer;
		LinkedList<Unit> list = new LinkedList<Unit>();
		int finished = 0;

		mPlayers[0].rewind();
		mPlayers[1].rewind();


		while (finished < 2) {
			System.out.println("acting " + activePlayer.getName() + " popping");
			Unit u = activePlayer.getNextUnit();
			if (u == null) {
				finished += 1;
				activePlayer = activePlayer.getOpponent();
				continue;
			}
			System.out.println(activePlayer.getName() + " enqueue one unit");
			list.push(u);
			if (u.switchPlayer())
				activePlayer = activePlayer.getOpponent();
		}
		System.out.println("formGlobalOrderList: list size " + list.size());
		return list;
	}

	// here some dead unit are removed.
	private Unit getActiveUnit() {
		if (mUnitQueue.size() != 0) {
			return mUnitQueue.pop();
		}
		// round over, next round
		mUnitQueue = formGlobalOrderList();
		// if (mUnitQueue.size() == 0)
		return null;
		// endRound();
		// return mUnitQueue.pop();
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
		if (mStatus != Status.RUNNING)
			return ;

		/*
		long time = TimeUtils.nanoTime();
		if ((time - mLastTurnTS) < 1 * 1000 * 1000 * 1000) {
			return ;
		}
		*/
		// update();

		Unit u = getActiveUnit(); // pop queue

		if (u == null) {
			endRound();
			return;
		}

		// System.out.println("tick: " + time + ": " + u.getOwner().getName() + u.getName() + " acting.");

		u.runTurn();

		// mLastTurnTS = time;

		if (isGameEnd()) {
			System.out.println("Player '" + mWinner.getName() + "' wins");

			mStatus = Status.FINISHED;
		}
	}

	private void endRound() {
		// do anything required for round ending
		mRound += 1;
		mListener.onRound(mRound);
	}

	private void dealDamage(Unit attacker, Unit attacked) {

		int damage = attacker.getAtk();
		int hp = attacked.getHp();
		return;
	}
}
