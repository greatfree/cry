package org.greatfree.cry.framework.bitcoin.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.framework.bitcoin.Coin;
import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.bitcoin.CoinMiner;
import org.greatfree.cry.messege.OwnerInfo;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
class CoinMiningTask implements Runnable
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");

	@Override
	public void run()
	{
		log.info("Starting to mine coins ...");
		MiningTaskManager.COIN().setCoinMining(true);
		try
		{
//				ChainNode.CHAIN().notifyCoinGenerated(MinerMan.COIN().getOwner(), new Coin(CoinMiner.mine(BlockConfig.COIN_DIFFICULTY), Calendar.getInstance().getTime()));
//			CoinNode.COIN().notifyCoinGenerated(CoinNode.COIN().getOwners().getOwnerName(), new Coin(CoinMiner.mine(CoinConfig.COIN_DIFFICULTY), Calendar.getInstance().getTime()));
			Collection<OwnerInfo> owners = CoinNode.COIN().getOwners();
			for (OwnerInfo entry : owners)
			{
				CoinNode.COIN().notifyCoinGenerated(entry.getOwnerName(), new Coin(CoinMiner.mine(CoinConfig.COIN_DIFFICULTY), Calendar.getInstance().getTime()));
			}
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
				| SignatureException | RemoteReadException | IOException | InterruptedException | CryptographyMismatchException | DistributedNodeFailedException e)
		{
			log.info("Coin mining interrupted ...");
		}
		MiningTaskManager.COIN().setCoinMining(false);
		log.info("One coin generated ...");
	}

}
