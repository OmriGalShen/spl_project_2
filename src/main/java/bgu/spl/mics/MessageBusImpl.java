package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance;
	// for each MicroService store message queue
	private ConcurrentHashMap<MicroService, LinkedList<Message>> messagesMap;
	// for each MicroService store subscriptions queue
	private ConcurrentHashMap<MicroService,LinkedList<Class<? extends Message>>> subscriptionMap;
	// store associations of events and Future objects
	private ConcurrentHashMap<Event,Future> eventFutureMap;
	// for each type of event store receiving microservices
	private ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> eventReceiveQueues;

	/**
	 * Private constructor
	 * Added*
	 */
	private MessageBusImpl() { //Singleton pattern
		messagesMap = new ConcurrentHashMap<>();
		subscriptionMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		eventReceiveQueues = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){ //Singleton pattern
		if(instance == null){
			//only on creation of first instance synchronize:
			// this is to make sure only one thread creates the first instance
			synchronized (MessageBusImpl.class){
				if(instance==null)
					instance = new MessageBusImpl();
			}
		}
		return instance;
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
			if (subscriptionMap.get(m).contains(type))
				return; //MicroService was already subscribed
			subscriptionMap.get(m).add(type); // add subscription
			if (eventReceiveQueues.containsKey(type)) { // add to appropriate event map
				eventReceiveQueues.get(type).add(m);
			}
			else{ // new type of event
				// create new queue of microservices subscribed to this event type
				eventReceiveQueues.put(type,new LinkedList<>());
				eventReceiveQueues.get(type).add(m); // add the MicroService to the queue
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
			if (subscriptionMap.get(m).contains(type))
				return;//MicroService was already subscribed
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
	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if(eventFutureMap.containsKey(e)&&eventFutureMap.get(e)!=null)
			eventFutureMap.get(e).resolve(result);
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
		subscriptionMap.forEach((microService,subscription)-> {
			//check if microservice is subscribe this type of broadcast
			if(subscription.contains(b.getClass())){
				// In case microService is waiting for this broadcast in awaitMessage()
				if(messagesMap.get(microService).isEmpty()){
					synchronized (this){
						notifyAll();// make sure receivingMicro is out of wait loop in awaitMessage()
					}
				}
				messagesMap.get(microService).add(b); // add b to microservice message queue
			}
		});
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
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> eventFuture = new Future<>(); // the future associated with the event
		eventFutureMap.put(e,eventFuture); // store the association of the future and the event

		// no microservice to receive event
		if(!eventReceiveQueues.containsKey(e.getClass())||
				eventReceiveQueues.get(e.getClass()).isEmpty()){
			return eventFuture;
		}
		// queue of MicroService who registered to this type of event
		LinkedList<MicroService> receivingQueue = eventReceiveQueues.get(e.getClass());
		MicroService receivingMicro = receivingQueue.remove(); // remove the first micro in receiving queue
		receivingQueue.add(receivingMicro); // add to the back of the receiving queue
		messagesMap.get(receivingMicro).add(e); // add message to micro
		// In case receivingMicro is waiting for this broadcast in awaitMessage()
		if(messagesMap.get(receivingMicro).isEmpty()){
			synchronized (this){
				notifyAll(); // make sure receivingMicro is out of wait loop in awaitMessage()
			}
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
		if(isRegistered(m))
			return;//already registered
		LinkedList<Message> messagesQueue = new LinkedList<Message>();
		LinkedList<Class<? extends Message>> messageTypeQueue = new LinkedList<Class<? extends Message>>();
		messagesMap.put(m,messagesQueue);
		subscriptionMap.put(m,messageTypeQueue);
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
		eventReceiveQueues.forEach((eventType,receiveQueues)->{
			receiveQueues.remove(m);
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
		if(!isRegistered(m)) throw new IllegalStateException();
		LinkedList<Message> messageQueue = messagesMap.get(m); // get MicroService messageQueue
		while(messageQueue.isEmpty()){
			synchronized (this){
				try {
					wait();
				}
				catch (InterruptedException e){
					System.out.println("interrupted while waiting for a message");
					e.printStackTrace();
				}
			}
		}
		// remove message from message queue and return it
		return messageQueue.remove();
	}

	/**
	 * *Added by Omri*
	 * @param m the MicroService to check
	 * @return if MicroService is currently register to the MessageBus
	 */
	private boolean isRegistered(MicroService m){
		return messagesMap.containsKey(m);
	}
}
