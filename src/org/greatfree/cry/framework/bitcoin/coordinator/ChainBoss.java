package org.greatfree.cry.framework.bitcoin.coordinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greatfree.cry.framework.bitcoin.CoinBlockInfo;
import org.greatfree.cry.framework.bitcoin.NeighborPeers;
import org.greatfree.cry.framework.blockchain.BlockConfig;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
class ChainBoss
{
//	private String head;
	private Map<String, List<CoinBlockInfo>> blockInfos;
	private List<String> peers;
	/*
	 * joinStates keeps the sessionKey and the joined count of the peers. 02/26/2022, Bing Li
	 */
	private Map<String, Integer> joinStates;
	
	private ChainBoss()
	{
	}
	
	private static ChainBoss instance = new ChainBoss();
	
	public static ChainBoss COIN()
	{
		if (instance == null)
		{
			instance = new ChainBoss();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
	}
	
	public void init()
	{
		this.blockInfos = new ConcurrentHashMap<String, List<CoinBlockInfo>>();
//		this.head = null;
		this.peers = new CopyOnWriteArrayList<String>();
		this.joinStates = new ConcurrentHashMap<String, Integer>();
	}

	/*
	public synchronized String initializeChain(Date timeStamp)
	{
		return Tools.getHash(this.head + timeStamp.getTime());
	}
	*/
	
	/*
	 * The head is the networking linked list head rather than the ones of block chains. 02/17/2022, Bing Li
	 */
	public synchronized String getHead(String currentPeerName)
	{
		if (this.peers.size() > 0)
		{
			return this.peers.get(0);
		}
//		return CoinConfig.NO_HEAD;
		return currentPeerName;
	}
	
	public int getChainLength()
	{
		return this.peers.size();
	}

	/*
	 * For multicasting to start from any peers, a ring is preferred. 02/17/2022, Bing Li
	 * 
	 * The neighbors are the networking linked list's preceding and succeeding peers rather than the ones of block chains. 02/17/2022, Bing Li
	 */
	public synchronized NeighborPeers joinCoinSystem(String peerName)
	{
		NeighborPeers pp;
		if (this.peers.size() > 0)
		{
			/*
			 * A ring is formed since the tail points to the head. 02/17/2022, Bing Li
			 */
			pp = new NeighborPeers(peerName, this.peers.get(this.peers.size() - 1), this.peers.get(0));
		}
		else
		{
			pp = new NeighborPeers(peerName);
		}
		this.peers.add(peerName);
		return pp;
	}
	
	public synchronized void leaveCoinSystem(String peerName)
	{
		this.peers.remove(peerName);
	}
	
	public String getPrecedingFingerPrint(String sessionKey)
	{
		if (this.blockInfos.containsKey(sessionKey))
		{
			List<CoinBlockInfo> chain = this.blockInfos.get(sessionKey);
			if (chain != null)
			{
				if (chain.size() > 0)
				{
					return chain.get(chain.size() - 1).getFingerPrint();
				}
			}
		}
		return BlockConfig.NO_PRECEDING_FINGER_PRINT;
	}
	
	public int getSequenceNO(String sessionKey)
	{
		if (this.blockInfos.containsKey(sessionKey))
		{
			return this.blockInfos.get(sessionKey).size() - 1;
		}
		return 0;
	}
	
	public String getChainHead(String sessionKey)
	{
		if (this.blockInfos.containsKey(sessionKey))
		{
			List<CoinBlockInfo> chain = this.blockInfos.get(sessionKey);
			if (chain != null)
			{
				if (chain.size() > 0)
				{
					return chain.get(0).getNodeName();
				}
			}
		}
		return null;
	}
	
	public void joinChain(CoinBlockInfo bi)
	{
		if (!this.blockInfos.containsKey(bi.getSessionKey()))
		{
			this.blockInfos.put(bi.getSessionKey(), new ArrayList<CoinBlockInfo>());
		}
		this.blockInfos.get(bi.getSessionKey()).add(bi);
	}
	
	public boolean joinDone(String sessionKey)
	{
		if (!this.joinStates.containsKey(sessionKey))
		{
			this.joinStates.put(sessionKey, 1);
		}
		int count = this.joinStates.get(sessionKey);
		this.joinStates.put(sessionKey, ++count);
		if (count >= this.peers.size())
		{
			return true;
		}
		return false;
	}

	/*
	public boolean isChainFull(String sessionKey)
	{
		if (this.blockInfos.get(sessionKey).size() >= this.peers.size())
		{
			return true;
		}
		return false;
	}
	*/
	
	public CoinBlockInfo getPrecedingBlock(String sessionKey)
	{
		if (this.blockInfos.containsKey(sessionKey))
		{
			List<CoinBlockInfo> chain = this.blockInfos.get(sessionKey);
			if (chain != null)
			{
				if (chain.size() > 1)
				{
					return chain.get(chain.size() - 2);
				}
			}
		}
		return null;
	}
	
	public synchronized void removeChain(String sessionKey)
	{
		this.blockInfos.remove(sessionKey);
		this.joinStates.remove(sessionKey);
	}
}
