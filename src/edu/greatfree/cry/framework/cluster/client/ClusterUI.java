package edu.greatfree.cry.framework.cluster.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.cluster.ClusterConfig;
import edu.greatfree.cry.framework.cluster.message.HelloAnycastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloAnycastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloAnycastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloBroadcastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloBroadcastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloBroadcastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloInterAnycastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloInterAnycastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloInterAnycastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloInterBroadcastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloInterBroadcastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloInterBroadcastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloInterUnicastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloInterUnicastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloInterUnicastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloUnicastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloUnicastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloUnicastResponse;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public final class ClusterUI
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.cluster.client");
	private AtomicInteger cryptoOption;
	
	private ClusterUI()
	{
		this.cryptoOption = new AtomicInteger(MulticastConfig.PLAIN);
	}

	private static ClusterUI instance = new ClusterUI();
	
	public static ClusterUI CRY()
	{
		if (instance == null)
		{
			instance = new ClusterUI();
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
	
	public void setCryptoOption(int co)
	{
		this.cryptoOption.set(co);
	}

	public void printMenu()
	{
		System.out.println(ChatMenu.MENU_HEAD);
		System.out.println(ChatMenu.TYPE_MESSAGE);
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}

	public void send(int highOption, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, ShortBufferException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		int index = 0;
		CollectedClusterResponse response;
		List<HelloBroadcastResponse> bResponses;
		List<HelloAnycastResponse> aResponses;
		List<HelloUnicastResponse> uResponses;
		List<HelloInterUnicastResponse> iuResponses;
		List<HelloInterAnycastResponse> iaResponses;
		List<HelloInterBroadcastResponse> ibResponses;
		Date startTime;
		Date endTime;
		boolean isSucceeded;
		String message = null;
		Set<String> destinationKeys;
		
//		log.info("highOption = " + highOption);
//		log.info("option = " + option);
		if (highOption != ClusterOptions.CLAIM_OWNERSHIP && highOption != ClusterOptions.ABANDON_OWNERSHIP)
		{
			switch (option)
			{
				case ChatOptions.TYPE_CHAT:
					System.out.println("Please type your message: ");
					message = Tools.INPUT.nextLine();
					switch (highOption)
					{
						case ClusterOptions.BROADCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloBroadcastNotification(message), this.cryptoOption.get());
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is broadcast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case ClusterOptions.ANYCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloAnycastNotification(message), this.cryptoOption.get());
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is anycast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case ClusterOptions.UNICAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloUnicastNotification(message), this.cryptoOption.get());
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is unicast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case ClusterOptions.BROADCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloBroadcastRequest(message), this.cryptoOption.get());
								if (response != null)
								{
									bResponses = Tools.filter(response.getResponses(), HelloBroadcastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is broadcast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
									System.out.println("You got " + bResponses.size() + " responses");
									for (HelloBroadcastResponse entry : bResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case ClusterOptions.ANYCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloAnycastRequest(message), this.cryptoOption.get());
								if (response != null)
								{
									aResponses = Tools.filter(response.getResponses(), HelloAnycastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is anycast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
									System.out.println("You got " + aResponses.size() + " responses");
									for (HelloAnycastResponse entry : aResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case ClusterOptions.UNICAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloUnicastRequest(message), this.cryptoOption.get());
								if (response != null)
								{
									uResponses = Tools.filter(response.getResponses(), HelloUnicastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is unicast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
									System.out.println("You got " + uResponses.size() + " responses");
									for (HelloUnicastResponse entry : uResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

							/*
						case MultiOptions.SYM_BROADCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloBroadcastNotification(message), MulticastConfig.SYM);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is broadcast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.SYM_ANYCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloAnycastNotification(message), MulticastConfig.SYM);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is anycast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.SYM_UNICAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloUnicastNotification(message), MulticastConfig.SYM);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is unicast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.SYM_BROADCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloBroadcastRequest(message), MulticastConfig.SYM);
								if (response != null)
								{
									bResponses = Tools.filter(response.getResponses(), HelloBroadcastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is broadcast!");
									System.out.println("You got " + bResponses.size() + " responses");
									for (HelloBroadcastResponse entry : bResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.SYM_ANYCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloAnycastRequest(message), MulticastConfig.SYM);
								if (response != null)
								{
									aResponses = Tools.filter(response.getResponses(), HelloAnycastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is anycast!");
									System.out.println("You got " + aResponses.size() + " responses");
									for (HelloAnycastResponse entry : aResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.SYM_UNICAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloUnicastRequest(message), MulticastConfig.SYM);
								if (response != null)
								{
									uResponses = Tools.filter(response.getResponses(), HelloUnicastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is unicast!");
									System.out.println("You got " + uResponses.size() + " responses");
									for (HelloUnicastResponse entry : uResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.ASYM_BROADCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloBroadcastNotification(message), MulticastConfig.ASYM);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is broadcast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case MultiOptions.ASYM_ANYCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloAnycastNotification(message), MulticastConfig.ASYM);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is anycast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.ASYM_UNICAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloUnicastNotification(message), MulticastConfig.ASYM);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is unicast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.ASYM_BROADCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloBroadcastRequest(message), MulticastConfig.ASYM);
								if (response != null)
								{
									bResponses = Tools.filter(response.getResponses(), HelloBroadcastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is broadcast!");
									System.out.println("You got " + bResponses.size() + " responses");
									for (HelloBroadcastResponse entry : bResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.ASYM_ANYCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloAnycastRequest(message), MulticastConfig.ASYM);
								if (response != null)
								{
									aResponses = Tools.filter(response.getResponses(), HelloAnycastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is anycast!");
									System.out.println("You got " + aResponses.size() + " responses");
									for (HelloAnycastResponse entry : aResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.ASYM_UNICAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloUnicastRequest(message), MulticastConfig.ASYM);
								if (response != null)
								{
									uResponses = Tools.filter(response.getResponses(), HelloUnicastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is unicast!");
									System.out.println("You got " + uResponses.size() + " responses");
									for (HelloUnicastResponse entry : uResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.SIGNED_BROADCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloBroadcastNotification(message), MulticastConfig.SIGNED);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is broadcast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.SIGNED_ANYCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloAnycastNotification(message), MulticastConfig.SIGNED);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is anycast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.SIGNED_UNICAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloUnicastNotification(message), MulticastConfig.SIGNED);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is unicast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;

						case MultiOptions.SIGNED_BROADCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloBroadcastRequest(message), MulticastConfig.SIGNED);
								if (response != null)
								{
									bResponses = Tools.filter(response.getResponses(), HelloBroadcastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is broadcast!");
									System.out.println("You got " + bResponses.size() + " responses");
									for (HelloBroadcastResponse entry : bResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.SIGNED_ANYCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloAnycastRequest(message), MulticastConfig.SIGNED);
								if (response != null)
								{
									aResponses = Tools.filter(response.getResponses(), HelloAnycastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is anycast!");
									System.out.println("You got " + aResponses.size() + " responses");
									for (HelloAnycastResponse entry : aResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;

						case MultiOptions.SIGNED_UNICAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloUnicastRequest(message), MulticastConfig.SIGNED);
								if (response != null)
								{
									uResponses = Tools.filter(response.getResponses(), HelloUnicastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is unicast!");
									System.out.println("You got " + uResponses.size() + " responses");
									for (HelloUnicastResponse entry : uResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
							
						case MultiOptions.PRIVATE_BROADCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloBroadcastNotification(message), MulticastConfig.PRIVATE);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is broadcast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case MultiOptions.PRIVATE_ANYCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloAnycastNotification(message), MulticastConfig.PRIVATE);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is anycast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case MultiOptions.PRIVATE_UNICAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloUnicastNotification(message), MulticastConfig.PRIVATE);
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is unicast!");
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case MultiOptions.PRIVATE_BROADCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloBroadcastRequest(message), MulticastConfig.PRIVATE);
								if (response != null)
								{
									bResponses = Tools.filter(response.getResponses(), HelloBroadcastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is broadcast!");
									System.out.println("You got " + bResponses.size() + " responses");
									for (HelloBroadcastResponse entry : bResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
							
						case MultiOptions.PRIVATE_ANYCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloAnycastRequest(message), MulticastConfig.PRIVATE);
								if (response != null)
								{
									aResponses = Tools.filter(response.getResponses(), HelloAnycastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is anycast!");
									System.out.println("You got " + aResponses.size() + " responses");
									for (HelloAnycastResponse entry : aResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
							
						case MultiOptions.PRIVATE_UNICAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloUnicastRequest(message), MulticastConfig.PRIVATE);
								if (response != null)
								{
									uResponses = Tools.filter(response.getResponses(), HelloUnicastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is unicast!");
									System.out.println("You got " + uResponses.size() + " responses");
									for (HelloUnicastResponse entry : uResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
							*/
							
						case ClusterOptions.INTER_BROADCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							destinationKeys = new HashSet<String>();
							for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
							{
								destinationKeys.add(Tools.generateUniqueKey());
							}
							System.out.println("destinationKeys' size = " + destinationKeys.size());
							ClientUI.CRY().notify(new HelloInterBroadcastNotification(message, destinationKeys), this.cryptoOption.get());
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is broadcast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case ClusterOptions.INTER_ANYCAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							destinationKeys = new HashSet<String>();
							for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
							{
								destinationKeys.add(Tools.generateUniqueKey());
							}
							System.out.println("destinationKeys' size = " + destinationKeys.size());
							ClientUI.CRY().notify(new HelloInterAnycastNotification(message, destinationKeys), this.cryptoOption.get());
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is anycast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case ClusterOptions.INTER_UNICAST_NOTIFICATION:
							startTime = Calendar.getInstance().getTime();
							ClientUI.CRY().notify(new HelloInterUnicastNotification(message), this.cryptoOption.get());
							endTime = Calendar.getInstance().getTime();
							System.out.println("Your notification is unicast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
							System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
							break;
							
						case ClusterOptions.INTER_BROADCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								destinationKeys = new HashSet<String>();
								for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
								{
									destinationKeys.add(Tools.generateUniqueKey());
								}
								System.out.println("destinationKeys' size = " + destinationKeys.size());
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloInterBroadcastRequest(message, destinationKeys), this.cryptoOption.get());
								if (response != null)
								{
									ibResponses = Tools.filter(response.getResponses(), HelloInterBroadcastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is inter-broadcast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
									for (HelloInterBroadcastResponse entry : ibResponses)
									{
										for (String msg : entry.getMessages())
										{
											System.out.println(++index + ") response = " + msg);
										}
									}
									System.out.println("You got " + index + " responses");
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
							
						case ClusterOptions.INTER_ANYCAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								destinationKeys = new HashSet<String>();
								for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
								{
									destinationKeys.add(Tools.generateUniqueKey());
								}
								System.out.println("destinationKeys' size = " + destinationKeys.size());
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloInterAnycastRequest(message, destinationKeys), this.cryptoOption.get());
								if (response != null)
								{
									iaResponses = Tools.filter(response.getResponses(), HelloInterAnycastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is inter-anycast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
									System.out.println("You got " + iaResponses.size() + " responses");
									for (HelloInterAnycastResponse entry : iaResponses)
									{
										for (String msg : entry.getMessages())
										{
											System.out.println(++index + ") response = " + msg);
										}
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
							
						case ClusterOptions.INTER_UNICAST_REQUEST:
							try
							{
								startTime = Calendar.getInstance().getTime();
								response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloInterUnicastRequest(message), this.cryptoOption.get());
								if (response != null)
								{
									iuResponses = Tools.filter(response.getResponses(), HelloInterUnicastResponse.class);
									endTime = Calendar.getInstance().getTime();
									System.out.println("Your request is inter-unicast with " + MulticastConfig.cryptoOption(this.cryptoOption.get()));
									System.out.println("You got " + iuResponses.size() + " responses");
									for (HelloInterUnicastResponse entry : iuResponses)
									{
										System.out.println(++index + ") response = " + entry.getMessage());
									}
									System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
								}
								else
								{
									System.out.println("Response is null!");
								}
							}
							catch (OwnerCheatingException e)
							{
								System.out.println("You are cheating the cluster as an invalid owner!");
							}
							break;
					}
					break;

				case ChatOptions.QUIT_CHAT:
					break;
			}
		}
		else
		{
			switch (highOption)
			{
				case ClusterOptions.CLAIM_OWNERSHIP:
					isSucceeded = ClientUI.CRY().claimOwner();
					if (isSucceeded)
					{
						System.out.println("You are succeeded to own the cluster!");
					}
					else
					{
						System.out.println("You are failed to own the cluster!");
					}
					break;
		
				case ClusterOptions.ABANDON_OWNERSHIP:
					isSucceeded = ClientUI.CRY().abandonOwner();
					if (isSucceeded)
					{
						System.out.println("You are succeeded to abandon the cluster!");
					}
					else
					{
						System.out.println("You are failed to abandon the cluster!");
					}
					break;
			}
		}
	}
}
