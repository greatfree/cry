package org.greatfree.cry.framework.bitcoin;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class Wallet
{
	private final String owner;
	private final String machineName;
	// The classic design needs to have the below keys to represent the user. But the Wind's peer denotes itself with a mnemonic name. So it is unnecessary to keep the keys. 02/10/2022, Bing Li
//	private final PublicKey publicKey;
//	private final PrivateKey privateKey;

//	private List<Coin> coins;
//	private float funds;
//	private List<UTXO> utxos;
//	private float balance;
	
//	public Wallet(String owner, PublicKey publicKey, PrivateKey privateKey)
	public Wallet(String owner, String machineName)
	{
		this.owner = owner;
		this.machineName = machineName;
//		this.coins = new ArrayList<Coin>();
//		this.publicKey = publicKey;
//		this.privateKey = privateKey;
//		this.funds = 0;
//		this.utxos = new ArrayList<UTXO>();
//		this.balance = 0;
	}
	
	public String getOwner()
	{
		return this.owner;
	}
	
	public String getMachineName()
	{
		return this.machineName;
	}

	/*
	public synchronized void addCoin(Coin c)
	{
		this.coins.add(c);
	}
	*/

	/*
	public String getPublicKey()
	{
		return this.publicKey.toString();
	}

	public String getPrivateKey()
	{
		return this.privateKey.toString();
	}
	*/
	
	/*
	public synchronized float getFunds()
	{
		return this.funds;
	}
	
	public synchronized void addFunds(float coins)
	{
		this.funds += coins;
	}
	*/

	/*
	public synchronized void addUTXO(UTXO utxo)
	{
		this.utxos.add(utxo);
	}

	public synchronized float calculateBalance()
	{
		for (UTXO entry : this.utxos)
		{
			this.balance += entry.getLeftValue();
		}
//		this.funds -= this.balance;
		return this.balance;
	}
	*/
	
	public synchronized Transaction createTransaction()
	{
		return null;
	}
}
