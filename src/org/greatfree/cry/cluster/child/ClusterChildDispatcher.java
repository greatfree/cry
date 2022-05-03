package org.greatfree.cry.cluster.child;

import org.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/29/2022
 *
 */
class ClusterChildDispatcher extends CryptoCSDispatcher
{

	public ClusterChildDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		// TODO Auto-generated constructor stub
	}

}
