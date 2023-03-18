package edu.greatfree.cry.framework.cluster;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
public final class ClusterConfig
{
//	public final static String REGISTRY_IP = "192.168.1.18";
	public final static String REGISTRY_IP = "127.0.0.1";
	public final static int REGISTRY_PORT = 8941;
	
	public final static int ROOT_PORT = 8944;
	public final static String ROOT_NAME = "Root";
	public final static String ROOT_KEY = Tools.getHash(ROOT_NAME);

	public final static int CLIENT_PORT = 8900;
	
	public final static int NO_REPLICAS = 0;
	
	public final static int INTER_CHILDREN_COUNT = 5;
	
	public final static String CLIENT_NAME = "greatfree";
	public final static String CLIENT_KEY = Tools.getHash(CLIENT_NAME);
}
