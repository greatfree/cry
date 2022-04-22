package org.greatfree.cry.framework.bitcoin.wallet;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.message.CheckBalanceResponse;
import org.greatfree.cry.framework.bitcoin.message.StartCoinMiningResponse;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author Bing Li
 * 
 * 02/06/2022
 *
 */
class WalletUI
{
	private WalletUI()
	{
	}

	private static WalletUI instance = new WalletUI();
	
	public static WalletUI CHAIN()
	{
		if (instance == null)
		{
			instance = new WalletUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void printMenu()
	{
		System.out.println(WalletMenu.MENU_HEAD);
		
		System.out.println(WalletMenu.REQUEST_OWNERSHIP);
		System.out.println(WalletMenu.CHECK_BALANCE);
		System.out.println(WalletMenu.START_COIN_MINING);
		System.out.println(WalletMenu.STOP_COIN_MINING);
		
		System.out.println(WalletMenu.QUIT);
		System.out.println(WalletMenu.MENU_TAIL);
		System.out.println(WalletMenu.INPUT_PROMPT);
	}
	
//	public void send(String yourName, String machineName, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
//	public void send(String machineName, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	public void send(int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, InterruptedException, PublicKeyUnavailableException
	{
		switch (option)
		{
			case WalletOptions.REQUEST_OWNERSHIP:
				boolean isSucceeded;
				try
				{
//					isSucceeded = WalletNode.CHAIN().claimOwner(yourName, machineName);
//					isSucceeded = WalletNode.CHAIN().claimOwner(machineName);
					isSucceeded = WalletNode.CHAIN().claimAsMachineOwner().isSucceeded();
					if (isSucceeded)
					{
						System.out.println("You are succeeded to own the machine, " + WalletNode.CHAIN().getMachineName());
					}
					else
					{
						System.out.println("You are failed to own the machine, " + WalletNode.CHAIN().getMachineName());
					}
				}
				catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
				{
					System.out.println("The machine, " + WalletNode.CHAIN().getMachineName() + ", is owned by another guy!");
				}
				break;
				
			case WalletOptions.CHECK_BALANCE:
//				CheckBalanceResponse cbr = WalletNode.CHAIN().checkBalance(yourName, machineName);
				CheckBalanceResponse cbr = WalletNode.CHAIN().checkBalance();
				if (cbr != null)
				{
					if (cbr.isSucceeded())
					{
						System.out.println("The balance is " + cbr.getCoins() + " coins, which is equivalent to $" + cbr.getValueInCurrency());
					}
					else
					{
						System.out.println("You are not the owner of the mining machine such that you have no right to check the balance!");
					}
				}
				else
				{
					System.out.println("You are not the owner of the mining machine such that you have no right to check the balance!");
				}
				break;
				
			case WalletOptions.START_COIN_MINING:
//				StartCoinMiningResponse scr = WalletNode.CHAIN().startCoinMining(yourName, machineName);
				StartCoinMiningResponse scr = WalletNode.CHAIN().startCoinMining();
				if (scr != null)
				{
					if (scr.isStarted())
					{
						System.out.println("Coin mining started at your mining machine ...");
					}
					else
					{
						System.out.println("Coin mining is failed to be started at your mining machine ...");
					}
				}
				else
				{
					System.out.println("Coin mining is failed to be started at your mining machine ...");
				}
				break;
				
			case WalletOptions.STOP_COIN_MINING:
				WalletNode.CHAIN().stopCoinMining();
				break;
		}
	}
	
}
