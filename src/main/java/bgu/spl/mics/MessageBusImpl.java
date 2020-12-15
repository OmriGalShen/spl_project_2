package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static final MessageBusImpl instance = null; // singleton pattern
	private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> messagesMap; // store message queue
	private final ConcurrentHashMap<MicroService,LinkedList<Class<? extends Message>>> subscriptionMap; // subscriptions queue
	private final ConcurrentHashMap<Event, Future> eventFutureMap; // store associations of events and Future objects
	private final ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> eventReceiveQueues; // for each type of event store receiving microservices
	//private final HashMap<Class<? extends Message>, Callback> callbackMap; // Eden //////////////////////////////////////////////////////////////////

	/**
	 * Private constructor
	 * Added*
	 */

	private static class MessageBusImplHolder { // singleton pattern
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl() { // singleton pattern
		this.messagesMap = new ConcurrentHashMap<>();
		this.subscriptionMap = new ConcurrentHashMap<>();
		this.eventFutureMap = new ConcurrentHashMap<>();
		this.eventReceiveQueues = new ConcurrentHashMap<>();
		//this.callbackMap = new HashMap<>(); // Eden ///////////////////////////////////////////////////////////////////
	}

	public static MessageBusImpl getInstance() { // singleton pattern
		return MessageBusImplHolder.instance;
	}

	/**
	 * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
	 * <p>
	 * @param <T>  The type of the result expected by the completed event.
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(isRegistered(m)) {
			if (!subscriptionMap.get(m).contains(type)) { // the microService isn't subscribed
				subscriptionMap.get(m).add(type); // add subscription
				eventReceiveQueues.putIfAbsent(type, new ConcurrentLinkedQueue<>());
				eventReceiveQueues.get(type).add(m);
			}
		}
	}

	/**
	 * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
	 * <p>
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(isRegistered(m)) {
			if (!subscriptionMap.get(m).contains(type)) // the microService isn't subscribed
				subscriptionMap.get(m).add(type);
		}
    }

	/**
	 * Notifies the MessageBus that the event {@code e} is completed and its
	 * result was {@code result}.
	 * When this method is called, the message-bus will resolve the {@link Future}
	 * object associated with {@link Event} {@code e}.
	 * <p>
	 * @param <T>    The type of the result expected by the completed event.
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		if(eventFutureMap.containsKey(e)&&eventFutureMap.get(e) != null) {
			eventFutureMap.get(e).resolve(result);

			System.out.println("Universe: an event was resolved!"); ///////////////////////////////////////

		}
		else
			throw new IllegalStateException("Event wasn't registered or Future was null");
	}

	/**
	 * Adds the {@link Broadcast} {@code b} to the message queues of all the
	 * micro-services subscribed to {@code b.getClass()}.
	 * <p>
	 * @param b 	The message to added to the queues.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		subscriptionMap.forEach((microService, subscription) -> {
			if(subscription.contains(b.getClass())) { // check if microservice is subscribe this type of broadcast
				try {
					messagesMap.get(microService).put(b); // add b to microservice message queue
				} catch (InterruptedException e) {
					System.out.println("InterruptedException while trying to give microservice a broadcast");
					e.printStackTrace();
				} // catch
			} // if
		}); // lambda
	}

	/**
	 * Adds the {@link Event} {@code e} to the message queue of one of the
	 * micro-services subscribed to {@code e.getClass()} in a round-robin
	 * fashion. This method should be non-blocking.
	 * <p>
	 * @param <T>    	The type of the result expected by the event and its corresponding future object.
	 * @param e     	The event to add to the queue.
	 * @return {@link Future<T>} object to be resolved once the processing is complete,
	 * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
	 */
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		if(!(eventReceiveQueues.containsKey(e.getClass()) || eventReceiveQueues.get(e.getClass()).isEmpty()))
			return null; // there is no microservice to receive event

		// ------ there is an available micro service to receive the event ------
		Future<T> eventFuture = new Future<>(); // the future associated with the event
		eventFutureMap.put(e, eventFuture); // store the association of the future and the event

		// Round Robin: queue of MicroService who registered to this type of event
		ConcurrentLinkedQueue<MicroService> receivingQueue = eventReceiveQueues.get(e.getClass());
		MicroService receivingMicro = receivingQueue.remove(); // remove the first micro in receiving queue
		receivingQueue.add(receivingMicro); // add to the back of the receiving queue
		try {
			messagesMap.get(receivingMicro).put(e); // add message to micro
		} catch (InterruptedException interruptedException) {

			System.out.println("InterruptedException while trying to give microservice a message"); //////////////////////////////////////////

			interruptedException.printStackTrace();
		}
		return eventFuture;
	}

	/**
	 * Allocates a message-queue for the {@link MicroService} {@code m}.
	 * <p>
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		if(!isRegistered(m)) { // initialize appropriate empty blocking queues
			messagesMap.put(m, new LinkedBlockingQueue<>());
			subscriptionMap.put(m, new LinkedList<>());
		}
	}

	/**
	 * Removes the message queue allocated to {@code m} via the call to
	 * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
	 * related to {@code m} in this message-bus. If {@code m} was not
	 * registered, nothing should happen.
	 * <p>
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		messagesMap.remove(m);
		subscriptionMap.remove(m);
		eventReceiveQueues.forEach((eventType, receiveQueues) -> {
			receiveQueues.remove(m); // remove microservice from event queue (does nothing if it is not in the queue)
		});
	}

	/**
	 * Using this method, a <b>registered</b> micro-service can take message
	 * from its allocated queue.
	 * This method is blocking meaning that if no messages
	 * are available in the micro-service queue it
	 * should wait until a message becomes available.
	 * The method should throw the {@link IllegalStateException} in the case
	 * where {@code m} was never registered.
	 * <p>
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return The next message in the {@code m}'s queue (blocking).
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// checks MicroService is registered to this MessageBus
		if(!isRegistered(m))
			throw new IllegalStateException();
		return messagesMap.get(m).take(); // wait until message is available
	}

	/**
	 * *Added by Omri*
	 * @param m the MicroService to check
	 * @return if MicroService is currently register to the MessageBus
	 */
	private boolean isRegistered(MicroService m) {
		return messagesMap.containsKey(m);
	}
}
