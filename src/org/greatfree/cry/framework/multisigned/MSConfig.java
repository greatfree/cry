package org.greatfree.cry.framework.multisigned;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class MSConfig
{
	public final static String UNI_NOTIFY_OPERATION = ".executeUniNotify()";
	public final static String UNI_NOTIFY_DESCRIPTION = "I am notifying in a uni-consensus manner ...";
	public final static String ANY_NOTIFY_OPERATION = ".executeAnyNotify()";
	public final static String ANY_NOTIFY_DESCRIPTION = "I am notifying in an any-consensus manner ...";
	public final static String BROAD_NOTIFY_OPERATION = ".executeBroadNotify()";
	public final static String BROAD_NOTIFY_DESCRIPTION = "I am notifying in a broad-consensus manner ...";
	
	public final static String UNI_REQUEST_OPERATION = ".executeUniRequest()";
	public final static String UNI_REQUEST_DESCRIPTION = "I am requesting in a uni-consensus manner ...";
	public final static String ANY_REQUEST_OPERATION = ".executeAnyRequest()";
	public final static String ANY_REQUEST_DESCRIPTION = "I am requesting in an any-consensus manner ...";
	public final static String BROAD_REQUEST_OPERATION = ".executeBroadRequest()";
	public final static String BROAD_REQUEST_DESCRIPTION = "I am requesting in a broad-consensus manner ...";
	
	public final static float MAX_APPROVAL_RATE = 1.0f;
}
