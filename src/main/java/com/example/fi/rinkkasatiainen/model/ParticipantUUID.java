package com.example.fi.rinkkasatiainen.model;

import com.example.fi.rinkkasatiainen.util.Struct;

import java.util.UUID;

public class ParticipantUUID {

    private final UUID uuid;

    private ParticipantUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getId() {
        return uuid;
    }

    public static ParticipantUUID generate() {
        return new ParticipantUUID( UUID.randomUUID() );
    }

    public static ParticipantUUID from(String uuid) {
        return new ParticipantUUID( UUID.fromString(uuid) );
    }


    @Override
    public boolean equals(Object o) {
        return new Struct.ForClass(this).equals( o );
    }

    @Override
    public int hashCode() {
        return new Struct.ForClass(this).hashCode();
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}