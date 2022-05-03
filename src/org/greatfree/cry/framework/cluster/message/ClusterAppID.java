package org.greatfree.cry.framework.cluster.message;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public final class ClusterAppID
{
	public final static int HELLO_BROADCAST_NOTIFICATION = 80000;
	public final static int HELLO_ANYCAST_NOTIFICATION = 80001;
	public final static int HELLO_UNICAST_NOTIFICATION = 80002;

	public final static int HELLO_BROADCAST_REQUEST = 80003;
	public final static int HELLO_BROADCAST_RESPONSE = 80004;

	public final static int HELLO_ANYCAST_REQUEST = 80005;
	public final static int HELLO_ANYCAST_RESPONSE = 80006;

	public final static int HELLO_UNICAST_REQUEST = 80007;
	public final static int HELLO_UNICAST_RESPONSE = 80008;
	
	public final static int STOP_CHILDREN_NOTIFICATION = 80009;
	public final static int STOP_ROOT_NOTIFICATION = 80010;
}
