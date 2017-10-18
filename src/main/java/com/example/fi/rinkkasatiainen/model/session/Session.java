package com.example.fi.rinkkasatiainen.model.session;

import com.example.fi.rinkkasatiainen.model.*;
import com.example.fi.rinkkasatiainen.model.session.commands.RateSessionCommand;
import com.example.fi.rinkkasatiainen.model.session.events.SessionCreated;
import com.example.fi.rinkkasatiainen.model.session.events.SessionRated;

import java.util.*;

public class Session implements AggregateRoot<SessionUUID> {
    private final EventSourceEntity eventSourceEntity;
    private final EventPublisher publisher;

    private Session() {
        this(new ArrayList<>());
    }

    private Session(List<Event> history) {
        eventSourceEntity = new EventSourceEntity(history);
        publisher = new EventPublisher(eventSourceEntity);
    }

    public void registerParticipant(ParticipantUUID participantUUid) {
        publisher.publish(new ParticipantRegisteredEvent(eventSourceEntity.uuid, participantUUid));
    }

    @Override
    public SessionUUID getUUID() {
        return eventSourceEntity.getUUID();
    }

    @Override
    public Integer getVersion() {
        return eventSourceEntity.getVersion();
    }

    @Override
    public List<Event> getUncommittedChanges() {
        return publisher.getUncommittedChanges();
    }

    @Override
    public void markChangesAsCommitted() {
        publisher.markChangesAsCommitted();
    }


    private void createSession(String title, SessionUUID uuid) {
        publisher.publish(new SessionCreated(title, uuid));
    }

    public void rate(RateSessionCommand command) {
        publisher.publish(new SessionRated(this.getUUID(), command.stars, command.participantUUID));
    }

    private class EventPublisher{

        private List<Event> uncommittedChanges;
        private final EventSourceEntity eventSourceEntity;
        public EventPublisher(EventSourceEntity eventSourceEntity) {
            this.eventSourceEntity = eventSourceEntity;
            this.uncommittedChanges = new ArrayList<>();
        }

        private void publish(Event event) {
            // send to event listeners
            eventSourceEntity.apply( event );
            uncommittedChanges.add( event );
        }

        public void markChangesAsCommitted() {
            this.uncommittedChanges.clear();
        }

        public List<Event> getUncommittedChanges() {
            return new ArrayList<>(Collections.unmodifiableList( uncommittedChanges ));
        }

    }
    private class EventSourceEntity{

        private SessionUUID uuid;
        private String title;
        private final EventLoader loader;
        private List<Stars> ratings;
        public EventSourceEntity(List<Event> history) {
            this.ratings = new ArrayList<>();
            loader = new EventLoader();

            history.forEach(loader::apply);
        }

        private void apply(SessionCreated sessionCreated) {
            this.uuid = sessionCreated.uuid;
            this.title = sessionCreated.title;
        }

        private void apply(SessionRated sessionRated){
            this.ratings.add(sessionRated.stars);
        }

        public SessionUUID getUUID() {
            return uuid;
        }

        public Integer getVersion() {
            return loader.getVersion();
        }

        public String getTitle() {
            return title;
        }

        protected void apply(Event event) {
            loader.apply(event);
        }

    }
    public static Session create(String title, SessionUUID uuid) {
        Session session = new Session();
        return session;
    }

    public static Session load(List<Event> events) {
        return new Session(events);
    }

    public static Session load(Event... events) {
        return new Session(Arrays.asList(events));
    }

}
