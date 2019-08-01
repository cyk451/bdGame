package com.mygdx.game;

import java.util.*;
import com.badlogic.gdx.utils.*;
// import com.badlogic.gdx.utils;

class Engine {
	enum Status {
		WAITING,
		RUNNING,
		FINISHED
	}
	Array<Player> players;
	// Player activePlayer;
	Player firstPlayer;
	private Status status;
	int round;
	int unitCounter;
	// TODO render this queue under bottom tab
	LinkedList<Unit> unitList;
	long lastTurnTS;

	Engine(Array<Player> p) {
		status = Status.WAITING;
		players = p;
		firstPlayer = players.get(0);
	}
	private void beforeBattle() {
	}

	public void run() {
		status = Status.RUNNING;
		round = 1;
		unitCounter = 0;
		beforeBattle();
		// unitList = formGlobalOrderList();
	}

	// return terminated
	//	private boolean performRound() {
	//		for (Unit act: unitList) {
	//			Array<Unit> targets = searchTargets(act, p, o);
	//			if (act.isDead()) // well he died before his turn
	//				continue;
	//
	//			/*
	//			for (Abilities a: act.abilities) {
	//				a.beforeAttacking();
	//			}
	//			*/
	//			for (Unit u: targets) {
	//				act.perform(u);
	//			}
	//			// sleep 1 second
	//		}
	//	}

	private Array<Unit> searchTargets(Unit act) {
		Player p = act.getOwner();
		Player o = p.getOpponent();
		Array<Unit> result = new Array<Unit>();

		return result;
	}

	private LinkedList<Unit> formGlobalOrderList() {
		Player activePlayer = firstPlayer;
		LinkedList<Unit> list = new LinkedList<Unit>();
		int finished = 0;

		players.get(0).rewind();
		players.get(1).rewind();

		while (finished < 2) {
			Unit u = activePlayer.getNextUnit();
			if (u == null) {
				finished += 1;
				activePlayer = activePlayer.getOpponent();
				continue;
			}
			if (u.isDead())
				continue;
			list.push(u);
			if (u.switchPlayer())
				activePlayer = activePlayer.getOpponent();
		}
		return list;
	}

	// here some dead unit are removed.
	private void update() {
		if (unitList.size() != 0) {
			return ;
		}
		round += 1;
		// show rounds
		unitList = formGlobalOrderList();
		// activeUnit = 0;
	}

	public void tick() {
		if (status != Status.RUNNING)
			return ;
		long time = TimeUtils.nanoTime();
		if ((time - lastTurnTS) < 1 * 1000 * 1000 * 1000) {
			return ;
		}
		update();

		// Unit u = activePlayer.nextUnit();

		Unit u = unitList.pop(); // pop queue
		Array<Unit> targets = searchTargets(u);

		for (Unit t: targets) {
			u.engage(t);
			if (t.isDead())
				unitList.remove(t);
		}

		lastTurnTS = time;
		// activeUnit += 1;
	}
}
