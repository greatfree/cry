package org.greatfree.cry.framework.bitcoin.message;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
public class CoinAppID
{
	public final static int CHECK_BALANCE_REQUEST = 80017;
	public final static int CHECK_BALANCE_RESPONSE = 80018;

	public final static int START_COIN_MINING_REQUEST = 80019;
	public final static int START_COIN_MINING_RESPONSE = 80020;

	public final static int COIN_GENERATED_NOTIFICATION = 80021;
	public final static int STOP_COIN_MINING_NOTIFICATION = 80022;
	
//	public final static int EARN_COIN_TRANSACTION_NOTIFICATION = 80023;
//	public final static int EARN_COIN_TRANSACTION_RESPONSE = 80024;
	
//	public final static int IO_REQUEST = 80025;
//	public final static int IO_RESPONSE = 80026;
	
//	public final static int CREATE_BLOCKCHAIN_REQUEST = 80027;
//	public final static int CREATE_BLOCKCHAIN_RESPONSE = 80028;

	public final static int JOIN_COIN_SYSTEM_REQUEST = 80023;
	public final static int JOIN_COIN_SYSTEM_RESPONSE = 80024;

	public final static int JOIN_COIN_CHAIN_REQUEST = 80025;
	public final static int JOIN_COIN_CHAIN_RESPONSE = 80026;
	
//	public final static int HEAD_NOTIFICATION = 80031;
	
	public final static int ADD_COIN_NOTIFICATION = 80025;
	
	public final static int LEAVE_COIN_SYSTEM_REQUEST = 80026;
	public final static int LEAVE_COIN_SYSTEM_RESPONSE = 80027;
	
	public final static int START_VALIDATE_NOTIFICATION = 80028;
	public final static int JOIN_DONE_NOTIFICATION = 80029;
//	public final static int FORWARD_TRANSACTION_NOTIFICATION = 80030;
	public final static int RETAIN_TRANSACTION_NOTIFICATION = 80030;
	public final static int LINK_PRECEDING_NOTIFICATION = 80031;
	public final static int LEAVE_NOTIFICATION = 80032;
//	public final static int NO_PRECEDING_NOTIFICATION = 80033;
	public final static int VERIFICATION_RESULT_NOTIFICATION = 80033;

	public final static int PROPAGATE_TRANSACTIONS_REQUEST = 80034;
	public final static int PROPAGATE_TRANSACTIONS_RESPONSE = 80035;
	
	public final static int PROPAGATE_SUCCEEDINGLY_REQUEST = 80036;
	public final static int PROPAGATE_SUCCEEDINGLY_RESPONSE = 80037;
	
	public final static int HEAD_JOIN_CHAIN_REQUEST = 80038;
	public final static int HEAD_JOIN_CHAIN_RESPONSE = 80039;
	
	public final static int JOIN_CHAIN_SUCCEEDINGLY_REQUEST = 80040;
	public final static int JOIN_CHAIN_SUCCEEDINGLY_RESPONSE = 80041;
	
	public final static int CHAIN_LENGTH_REQUEST = 80042;
	public final static int CHAIN_LENGTH_RESPONSE = 80043;
	
	public final static int GO_AHEAD_VALIDATION_NOTIFICATION = 80044;
	public final static int VALIDATION_RESULT_NOTIFICATION = 80045;
	public final static int JOIN_CHAIN_NOTIFICATION = 80046;
	public final static int JOIN_STATE_NOTIFICATION = 80047;
	public final static int FINALIZE_TRANSACTION_MINING_NOTIFICATION = 80048;
}
