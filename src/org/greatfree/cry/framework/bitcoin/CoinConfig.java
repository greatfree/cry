package org.greatfree.cry.framework.bitcoin;

import org.greatfree.util.Tools;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class CoinConfig
{
	public final static String INIT_COIN_HASH = "INIT_" + Tools.generateUniqueKey();

	public final static float COIN_VALUE_IN_CURRENCY = 34289.3f;
	
//	public final static int COIN_DIFFICULTY = 6;
	public final static int COIN_DIFFICULTY = 4;
//	public final static int COIN_DIFFICULTY = 5;
//	public final static int COIN_DIFFICULTY = 4;
//	public final static int COIN_DIFFICULTY = 3;
	
	public final static long COIN_MINING_DELAY = 0;
	public final static long COIN_MINING_PERIOD = 10000;

	public final static long TRANSACTION_PROCESSING_DELAY = 5000;
	public final static long TRANSACTION_PROCESSING_PERIOD = 10000;

	public final static String COIN_COORDINATOR = "CoinCoordinator";
	public final static String COIN_COORDINATOR_KEY = Tools.getHash(COIN_COORDINATOR);
	public final static int COIN_COORDINATOR_PORT = 8944;
	
	public final static long NO_TIMESTAMP = -1;
	
	public final static String MINER = "Miner";
	
	public final static String NO_HEAD = null;
	
	public final static long TRANSACTION_PROCESSING_TIMESPAN = 10000;
	
	public final static String BAR = "==============================\n";
	
//	public final static int LONG_CHAIN_LENGTH = 4;
	public final static int LONG_CHAIN_LENGTH = 2;
}
