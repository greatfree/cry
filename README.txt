04/20/2022, Bing Li

Full cryptography algorithms are implemented in the multicasting.

Since the encryption results in low performance, it is necessary to optimize the multicasting through improving concurrency, i.e., ATM.

ATM is not utilized sufficiently in many cases.

A practical solution to the low performance issue is to keep internal interaction within the multicasting tree without encryption.

Only external interactions need to be encrypted.

The current lines of code are 24218.

The current number of files is 356.

========================================================

04/19/2022, Bing Li

One bug exists in the design of org.greatfree.cry.server.EncryptedRequestThread. Too many cases are listed here. Meanwhile, those cases are processed sequentially. Thus, if one message of one case cannot be processed fast enough, other cases have to wait for that. It is really unreasonable.

One typical example for the above problem is that a broadcast request is sent from the client to the root. The root has to wait for all the children's responses. Thus, during the waiting time, any other messages to the root cannot be processed until the waiting time is passed. It is a big bug.

It is necessary to check the org.greatfree.cry.server.EncryptedNotificationThread as well.

The current lines of code are 23034.

The current number of files is 332.

========================================================

04/18/2022, Bing Li

In the past version, only asymmetric cryptography algorithm is used to encrypt transmitted data.

The current version upgraded it to use symmetric, asymmetric, and signature to transmit data.

The plain data is also borrowed to transmit data.

The ownership needs to be implemented as well.

The ownership needs to be performed on the root of the multicasting tree.

One bug, SymmetricCrypto, is fixed.

The SymmetricCrypto needs to contain two keys, the source peer key and the destination key.

The current lines of code are 23016.

The current number of files is 332.

========================================================

04/14/2022, Bing Li

The names of peers are important for cryptography algorithms.

It is not necessary to keep the map between the names and the IPs through revising the low level code.

ServiceProvider is responsible for the management.

The current lines of code are 22324.

The current number of files is 330.

========================================================

04/14/2022, Bing Li

After testing for one day, the encrypted multicasting runs well.

No big bugs are found.

The primary problems are concentrated on the initialization of the cryptography algorithms.

The performance is not good.

The next step is to migrate the multicasting to clustering.

To raise the performance of a cluster, it is recommended to keep the encryption between the client and the root of the cluster only.

The interaction within the cluster is not encrypted.

The current lines of code are 22284.

The current number of files is 330.

========================================================

04/13/2022, Bing Li

The overall code for the encrypted multicasting is done.

I need to test it.

The current lines of code are 22073.

The current number of files is 330.

========================================================

04/07/2022, Bing Li

I am now implementing the cryptography-based multicasting.

I wonder whether it is necessary to the invitation among the nodes within the multicasting tree.

It is time-consuming to implement the feature among the nodes of the tree.

A better approach is to enable the ownership of the root of the multicasting tree.

But it is more reasonable to do that for a cluster rather a multicasting tree.

For a tree, to guarantee the safety, it is required to keep inviting the children for multicasting.

========================================================

03/26/2022, Bing Li

The multi-signatures function is implemented.

Six types of consensus as follows are designed for the feature.

	1) Uni-Consensus Notify;
	2) Any-Consensus Notify;
	3) Broad-Consensus Notify;
	4) Uni-Consensus Request;
	5) Any-Consensus Request;
	6) Broad-Consensus Request;

The current lines of code are 15626.

The current number of files is 237.

========================================================

03/24/2022, Bing Li

The multiple-ownership mechanism is implemented.

It is time to reach the destination of multiple-signature.

I think it is necessary to implement a more generic case, i.e., one operation can only be performed only if it is admitted by multiple owners.

The operation is called the consensus one.

To fulfill the requirement, the design is discussed as follows.

1) Multiple owners of one machine are available from the machine they own.

2) One specific message, ConsensusNotification or ConsensusRequest/ConsensusResponse, needs to be designed for the case.

3) If one consensus operation needs to be performed by one of the owners, the message is sent to all the owners for approval.

4) The operation request needs to be approved by each owner manually before the operation is performed.

5) The operations can be categorized into the three ones, including the unique-consensus, the any-consensus, and the broad-consensus.

6) In the mechanism, the consensus operation is performed only if they are approved by all the owners.

7) One possible improvement is that the approval is performed automatically with digital signatures such that the procedure becomes efficient.

8) That is the implementation of multiple-signatures, which ensures that one operation on one machine is only dominated by the owners.

9) Since the number of owners is usually small, it is unnecessary to build a cluster for them.

========================================================

03/23/2022, Bing Li

Testing cases:

1) A private machine without any owners (owners' size = 0);

	No users can own the machine.

2) A private machine with a single owner (owners' size = 1);

	Only a single user can own the machine;
	
	Additional users cannot access the machine.

3) A public machine;

4) A private machine with two owners (owners' size = 2);

5) A private machine with three owners (owners' size = 3);

The current lines of code are 14173.

The current number of files is 211.

========================================================

03/23/2022, Bing Li

It is necessary to stop a cryptography peer.

To do that, the peer needs to notify its partners to remove cryptography relevant information, such as keys or algorithms specifications.

Two messages below are added to do that.

	SaySymmetricByeNotification
	
	SayAsymmetricByeNotification

The current lines of code are 13843.

The current number of files is 204.

========================================================

03/22/2022, Bing Li

The ownership management of distributed nodes are improved.

PrivateNotification, PrivateRequest/PrivateResponse are designed for the interaction.

If one distributed node is owned by a node, others cannot access the machine with any messages.

In the past version, an owned machine is still accessible with the messages other than the above private ones.

The current lines of code are 13096.

The current number of files is 198.

========================================================

03/19/2022, Bing Li

I attempt to implement a multi-signed sample before reading the relevant documents.

Although it might not conform to the conventions exactly, it must work in some scenarios.

The sample must simulate the banking case.

One account can be accessed only if multiple signatures are verified.

I have ever implemented an ownership system.

But the remote machine is owned by only one single user.

If it is owned by multiple users, it is equivalent to the above scenario for multi-signing.

========================================================

02/28/2022, Bing Li

More transactions besides the coin generation need to be implemented in the next version.

The current lines of code are 12048.

The current number of files is 177.

========================================================

02/28/2022, Bing Li

The multiple-node based coin generation is done.

A tiny bug is fixed, which is caused by the transaction propagation.

The networking head is responsible for the task such that the node should not retain the transactions again into its pool.

Otherwise, a dead loop is created.

The current lines of code are 11959.

The current number of files is 176.

========================================================

02/27/2022, Bing Li

The version is able to validate transactions in a chain with multiple nodes.

The performance is high since the recursive traversal is replaced with an asynchronous interactions.

After one validation, it is necessary to reset the system.

1) Blocks should be disposed;

2) ChainHead should be cleared;

3) The coordinator should be notified for the updated and relevant resetting is required.

The current lines of code are 11937.

The current number of files is 176.

========================================================

02/26/2022, Bing Li

It is necessary to consider an asynchronous mechanism to raise the performance of the transactions mining.

Or in other words, an asynchronous distributed recursive traversal is needed in the block chain implementation.

The networking head still takes this responsibility.

Before the validation or the transaction mining, it is necessary to be aware of the length of the chain.

After each peer in the chain finishes their tasks, it is necessary for them to notify the results to the head.

The approach avoids the low performance caused by the synchronous approach.

========================================================

02/26/2022, Bing Li

The current version has not been executed yet.

The primary update in the version is to use the distributed recursive traversal started from the head of the networking.

The recursive traversal is not good since it is performed in a totally synchronous way. It is very slow, especially when the length of the chain is long.

Just backup the version before updating.

The current lines of code are 11220.

The current number of files is 165.

========================================================

02/25/2022, Bing Li

The current can validate in multiple peers rather than one single node.

But the consistency management is not performed well.

I need to update that in the next version.

The current lines of code are 10805.

The current number of files is 157.

========================================================

02/19/2022, Bing Li

It is necessary to employ the ATM to do that. Before the next step, the ATM improvement is critical.

Another difficulty is that it is necessary to find a proper way to keep consistent for the decentralized system.

1) Data propagation.

	When to start?
	
	Where to start?

2) Chain validation.

	When to start?
	
	Where to start?

If consistency cannot be maintained well, the block chain does not work at all.

The coordinator and the networking head are responsible for the management in the current version.

The current lines of code are 10758.

The current number of files is 157.

========================================================

02/18/2022, Bing Li

The coin generation moves forward one step.

Coin is generated correctly and its transaction is validated.

However, only one node works as a coin node correctly.

That indicates that the block chain for transaction verification is not constructed properly.

I need to resolve the problem.

The current lines of code are 10670.

The current number of files is 156.

========================================================

02/17/2022, Bing Li

The current version works partially.

Now the TransactionPool needs to be updated.

Transactions in the pool are processed periodically.

But now it is not done properly.

The current lines of code are 10512.

The current number of files is 155.

========================================================

02/17/2022, Bing Li

An executable version of the bitcoin architecture is done in code.

I will start to test it.

Bitcoins are generated as first transactions.

It will be verified.

If the mechanism works fine, more practical scenarios will be added.

The current lines of code are 10404.

The current number of files is 154.

========================================================

02/15/2022, Bing Li

The fundamental overall structure of the BitCoin and Wallet is done.

But the version cannot be executed yet.

However, the core issues are resolved.

The current lines of code are 9546.

The current number of files is 142.

========================================================

02/15/2022, Bing Li

I started to implement the overall structure of the bitcoin.

Because of the money issue, I spent the limited time on the wallet. Now the primary issues are listed as follows.

1) Script: not in the current implementation plan.

2) Block creation and closing: done.

3) Block storage: not in the current plan.

4) The overall structure: done.

Many details are not clarified because the book, Mastering BitCoin, is not read completely yet.

I also noticed one problem.

Although the ATM is one primary technique of the Wind, I do not use it frequently.

Its APIs are not compatible with the container patterns very well.

The ATM focuses on distributed threading.

However, in many cases, it is necessary to use the local threads.

The relevant APIs are required to improve the ATM.

The current lines of code are 8905.

The current number of files is 141.

========================================================

02/07/2022, Bing Li

To implement the wallet, it is necessary to set an owner for each mining machine.

The feature is done in the version.

The current lines of code are 7844.

The current number of files is 120.

========================================================

02/05/2022, Bing Li

I started to program the wallet on 02/03/2022.

During the procedure, I noticed that it was necessary to improve the Peer such that it can switch between various cryptography approaches.

Now the peer has the below cryptography approaches.

The plain-code notifying/reading.

The symmetric notifying/reading.

The asymmetric notifying/reading.

The signed notifying/reading.

The current lines of code are  6514.

The current number of files is 105.

========================================================

01/28/2022, Bing Li

Now the initial version of a block chain is implemented.

The fundamental functions, join, traverse, and validate, work well.

The wallet will be implemented upon it.

The current lines of code are 5413.

The current number of files is 88.

========================================================

01/27/2022, Bing Li

I complete the initial version of one block chain.

I need to strengthen it with more practical cryptography algorithms.

Anyway, the current version has already got an acceptable distributed structure.

The current lines of code are 5048.

The current number of files is 82.

========================================================

01/15/2022, Bing Li

The signature case is implemented.

Possible cheating cases:

1) One peer pretends to be another peer, whose signature is stolen to be used.

2) Even though the peer is offline, other peers cannot pretend to be him.

3) Change signature unilaterally.

The current lines of code are 3831.

The current number of files is 57.

========================================================

01/12/2022, Bing Li

Now multiple peers can interact with the asymmetric cryptography.

The current lines of code are 2997.

The current number of files is 51.

========================================================

01/12/2022, Bing Li

The asymmetric cryptography is implemented.

RSA is employed in the current version.

One problem which I am not aware of is that the data length encrypted by RSA is limited.

Thus, it is necessary to encrypt data with an symmetric algorithm.

Then, the symmetric key is encrypted by RSA.

One problem which is not resolved is that now only two peers interaction is implemented.

If more peers need to interact one by one, it is necessary to revise the key exchanging code.

The current lines of code are 2958.

The current number of files is 51.

========================================================

01/07/2022, Bing Li

One symmetric cryptography is implemented in a distributed environment with GreatFree.

The overall structure is done.

Other cryptography algorithms can be implemented in the similar manner.

The current lines of code are 1486.

The current number of files is 31.

========================================================

01/04/2022, Bing Li

I decide to design a new programming paradigm for cryptography with Wind and Bouncy Castle.

It must help spreading of the Wind.


