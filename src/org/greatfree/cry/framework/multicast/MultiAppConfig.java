package org.greatfree.cry.framework.multicast;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
public class MultiAppConfig
{
	public final static String ROOT_NAME = "Root";
	public final static String ROOT_KEY = Tools.getHash(ROOT_NAME);
	public final static int ROOT_PORT = 8901;

	public final static int ROOT_BRANCH_COUNT = 2;
	public final static int TREE_BRANCH_COUNT = 2;
//	public final static long MULTICAST_WAIT_TIME = 2000;
//	public final static long MULTICAST_WAIT_TIME = 10000;
//	public final static long MULTICAST_WAIT_TIME = 20000;
//	public final static long MULTICAST_WAIT_TIME = 40000;
	public final static long MULTICAST_WAIT_TIME = 100000;
	
//	public final static int ROOT_THREAD_POOL_SIZE = 50;
//	public final static long ROOT_THREAD_POOL_ALIVE_TIME = 10000;
	
	public final static int DEFAULT_CHILD_PORT = 8944;

//	public final static int CHILD_THREAD_POOL_SIZE = 50;
//	public final static long CHILD_THREAD_POOL_ALIVE_TIME = 10000;
	
	public final static String REGISTRY_SERVER_IP = "192.168.1.18";
	public final static int REGISTRY_SERVER_PORT = 8941;
}
