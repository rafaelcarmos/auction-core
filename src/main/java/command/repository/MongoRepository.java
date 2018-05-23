package command.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import command.aggregate.Auction;
import command.events.*;
import command.repository.exceptions.AggregateExistentException;
import command.repository.exceptions.AggregateNonExistentException;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class MongoRepository implements Repository {

    private final Timer commitToMongoTimer;
    private final ArrayBlockingQueue<Document> uncomittedEvents;
    private final MongoCollection<Document> eventCollection;
    private final MongoDatabase db;
    private final MongoClient mongoClient;
    private final Map<UUID, Auction> auctionMap = new ConcurrentHashMap<>();

    public MongoRepository(String mongoAddress, String databaseName, String collectionName) {
        uncomittedEvents = new ArrayBlockingQueue<Document>(10000);
        mongoClient = new MongoClient(mongoAddress);
        db = mongoClient.getDatabase(databaseName);
        eventCollection = db.getCollection(collectionName);

        commitToMongoTimer = new Timer();
        commitToMongoTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!uncomittedEvents.isEmpty()) {
                    List<Document> lst = new ArrayList<>();
                    uncomittedEvents.drainTo(lst);
                    eventCollection.insertMany(lst);
                    lst.clear();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void save(Event e) throws Exception {
        uncomittedEvents.put(e.getDocument());
    }

    @Override
    public void save(Iterable<Event> events) {
        List<Document> lst = new ArrayList<>();
        for (Event e : events) {
            lst.add(e.getDocument());
        }
        eventCollection.insertMany(lst);
    }

    @Override
    public List<Event> getEvents() {
        FindIterable<Document> find = eventCollection.find();
        List<Event> result = new ArrayList<>();
        for (Document d : find) {
            result.add(parseEvent(d));
        }
        return result;
    }

    private Event parseEvent(Document d) {
        String eventType = (String) d.get("eventType");
        LocalDateTime timestamp = LocalDateTime.ofInstant(((Date) d.get("timestamp")).toInstant(), ZoneId.systemDefault());
        UUID auctionId = UUID.fromString(d.get("aggregateId").toString());
        Event event;

        switch (eventType) {
            case "AuctionCancelled":
                event = new AuctionCancelled(auctionId, timestamp);
                break;
            case "AuctionCreated": {
                Document data = (Document) d.get("eventData");
                UUID auctioneerId = UUID.fromString(data.get("auctioneerId").toString());
                UUID itemId = UUID.fromString(data.get("itemId").toString());
                double startPrice = (double) data.get("startPrice");
                event = new AuctionCreated(auctionId, timestamp, auctioneerId, itemId);
                break;
            }
            case "AuctionEnded":
                event = new AuctionEnded(auctionId, timestamp);
                break;
            case "AuctionStarted":
                event = new AuctionStarted(auctionId, timestamp);
                break;
            case "BidPlaced": {
                Document data = (Document) d.get("eventData");
                UUID bidderId = UUID.fromString(data.get("bidderId").toString());
                double amount = (double) data.get("amount");
                event = new BidPlaced(auctionId, timestamp, bidderId, amount);
                break;
            }
            default:
                throw new RuntimeException("Invalid event type " + eventType);
        }

        return event;
    }

    @Override
    public Auction getAuction(UUID id) throws AggregateNonExistentException {
        if (auctionMap.containsKey(id)) {
            return auctionMap.get(id);
        } else {
            throw new AggregateNonExistentException("Aggregate " + id.toString() + " does not exist");
        }
    }

    @Override
    public Auction createAndGetAuction(UUID id) throws AggregateExistentException {
        if (auctionMap.containsKey(id)) {
            throw new AggregateExistentException("Aggregate " + id.toString() + " already exists");
        } else {
            Auction auction = new Auction(id);
            auctionMap.put(id, auction);
            return auction;
        }
    }

    @Override
    public void close() {
        try {
            commitToMongoTimer.cancel();
            Thread.sleep(1000);
            mongoClient.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
