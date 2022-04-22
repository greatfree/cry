package org.greatfree.cry.framework.bitcoin;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import org.greatfree.cry.framework.blockchain.BlockCryptor;
import org.greatfree.util.Time;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class CoinMiner
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin");

	public static String mine(int difficulty)
	{
		Date startTime = Calendar.getInstance().getTime();
		log.info("Coin mining started at " + startTime);
		log.info("Please be patient ...");
		long timeStamp = Calendar.getInstance().getTimeInMillis();
		int nonce = 0;
		String coinHash = CoinConfig.INIT_COIN_HASH;
		String target = BlockCryptor.getDifficultyString(difficulty);
		log.info("target = " + target);
		while (!coinHash.substring(0, difficulty).equals(target))
		{
//			log.info("1) coinHash = " + coinHash);
			coinHash = BlockCryptor.calculateCoin(coinHash, timeStamp, nonce);
//			log.info("2) coinHash = " + coinHash);
			nonce++;
		}
		Date endTime = Calendar.getInstance().getTime();
		log.info("Mined coinHash = " + coinHash);
		log.info("It took " + Time.getTimeSpanInSecond(endTime, startTime) + " seconds to mine the coin!");
		log.info("Coin mining ended at " + startTime);
		return coinHash;
	}

}
