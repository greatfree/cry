package org.greatfree.cry.framework.bitcoin.wallet;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.Coin;
import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Script;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.cry.framework.bitcoin.Wallet;
import org.greatfree.cry.framework.bitcoin.message.AddCoinNotification;
import org.greatfree.cry.framework.bitcoin.message.CheckBalanceRequest;
import org.greatfree.cry.framework.bitcoin.message.CheckBalanceResponse;
import org.greatfree.cry.framework.bitcoin.message.StartCoinMiningRequest;
import org.greatfree.cry.framework.bitcoin.message.StartCoinMiningResponse;
import org.greatfree.cry.framework.bitcoin.message.StopCoinMiningNotification;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.tncs.Config;
import org.greatfree.cry.messege.AllOwners;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * A wallet client can only interact with a peer which is owned by it. In the version, a client has only one owned peer. Further, a client might own multiple peers or even a cluster. 02/16/2022, Bing Li
 * 
 * I plan to implement a lightweight wallet client, i.e., all the data is saved in the block chains. 02/14/2022, Bing Li
 * 
 * @author Bing Li
 * 
 * 02/06/2022
 *
 */
class WalletNode
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.client");
	private Peer peer;
	private Wallet wallet;

	private WalletNode()
	{
	}
	
	private static WalletNode instance = new WalletNode();
	
	public static WalletNode CHAIN()
	{
		if (instance == null)
		{
			instance = new WalletNode();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}

	/*
	public void start(String peerName, String ownerName) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(peerName)
				.port(BlockConfig.CHAIN_NODE_PORT)
				.registryServerIP(BlockConfig.BC_REGISTRY_IP)
				.registryServerPort(BlockConfig.BC_REGISTRY_PORT)
				.task(new UserTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(Config.RSA)
				.asymCipherKeyLength(BlockConfig.RSA_LENGTH)
				.symCipherAlgorithm(Config.AES)
				.symCipherSpec(Config.AES_SPEC)
				.symCipherKeyLength(BlockConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(BlockConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(Config.SHA_WITH_RSA)
				.signature(peerName + Config.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.wallet = new Wallet(ownerName);
	}
	*/

	public void start(String peerName, String ownerName, String signature, String machineName) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(peerName)
				.port(BlockConfig.CHAIN_NODE_PORT)
				.registryServerIP(BlockConfig.BC_REGISTRY_IP)
				.registryServerPort(BlockConfig.BC_REGISTRY_PORT)
				.task(new WalletTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(Config.RSA)
				.asymCipherKeyLength(BlockConfig.RSA_LENGTH)
				.symCipherAlgorithm(Config.AES)
				.symCipherSpec(Config.AES_SPEC)
				.symCipherKeyLength(BlockConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(BlockConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(Config.SHA_WITH_RSA)
				.signature(signature + Config.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.wallet = new Wallet(ownerName, machineName);
	}

//	public void inviteMachine(String machineName) throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	public void inviteMachine() throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	{
		this.peer.inviteAsymPartner(this.wallet.getMachineName());
	}
	
//	public boolean claimOwner(String yourName, String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException
//	public boolean claimOwner(String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException
//	public boolean claimAsMachineOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException
	public AllOwners claimAsMachineOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException
	{
		return this.peer.claimOwner(this.wallet.getOwner(), this.wallet.getMachineName());
	}

	/*
	public boolean requestOwnership(String yourName, String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		try
		{
//			return ((OwnershipResponse)this.peer.readBySignature(machineName, new OwnershipRequest(yourName))).isSucceeded();
			return ((OwnershipResponse)this.peer.readByOwner(yourName, machineName, new OwnershipRequest(yourName))).isSucceeded();
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			log.info("You are cheating as the owner, " + yourName);
		}
		return false;
	}
	*/
	
//	public CheckBalanceResponse checkBalance(String yourName, String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
//	public CheckBalanceResponse checkBalance(String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	public CheckBalanceResponse checkBalance() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		try
		{
			return (CheckBalanceResponse)this.peer.readPrivately(this.wallet.getOwner(), this.wallet.getMachineName(), new CheckBalanceRequest());
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			log.info("You are cheating as the owner, " + this.wallet.getOwner());
		}
		return null;
	}
	
//	public StartCoinMiningResponse startCoinMining(String yourName, String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
//	public StartCoinMiningResponse startCoinMining(String machineName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	public StartCoinMiningResponse startCoinMining() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		try
		{
			return (StartCoinMiningResponse)this.peer.readPrivately(this.wallet.getOwner(), this.wallet.getMachineName(), new StartCoinMiningRequest());
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			log.info("You are cheating as the owner, " + this.wallet.getOwner());
		}
		return null;
	}
	
	public void stopCoinMining() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		this.peer.syncNotifyPrivately(this.wallet.getOwner(), this.wallet.getMachineName(), new StopCoinMiningNotification());
	}
	
	public void addCoin(Coin coin) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
//		this.wallet.addCoin(coin);
		this.peer.syncNotifyPrivately(this.wallet.getOwner(), this.wallet.getMachineName(), new AddCoinNotification(coin, new Transaction(new Input(this.wallet.getMachineName(), this.wallet.getOwner(), CoinConfig.COIN_VALUE_IN_CURRENCY, new Script()))));
	}

	/*
	public void notifyCoinEarnedTransaction()
	{
//		Transaction trans = new Transaction(CoinConfig.MINER, this.peer.getPeerName(), );
	}
	*/

	/*
	public void addFunds(float coins)
	{
		this.wallet.addFunds(coins);
	}
	*/
	
	public String getMachineName()
	{
		return this.wallet.getMachineName();
	}
}
