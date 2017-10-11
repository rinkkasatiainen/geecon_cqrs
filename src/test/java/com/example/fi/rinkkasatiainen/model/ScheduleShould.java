package com.example.fi.rinkkasatiainen.model;

import com.example.fi.rinkkasatiainen.web.model.Session;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScheduleShould {


    public static final String TITLE = "Title";
    private UUID random = UUID.randomUUID();
    private Supplier<UUID> randomUUIDSupplier = () -> random;
    private Schedule schedule;
    private EventStore eventStore;


    @Before
    public void setUp() throws Exception {
        eventStore = mock(EventStore.class);
        schedule = new Schedule(randomUUIDSupplier, eventStore);
    }

    @Test
    public void create_new_session() throws Exception {
        Session session = schedule.newSession();

        assertThat( session.getUUID(), equalTo(random));
    }

    @Test
    public void find_session() throws Exception {
        when(eventStore.findByUuid(random)).thenReturn(Arrays.asList(new SessionCreated(TITLE) ));

        Session s = schedule.findSession(random);
        assertThat(s.version, equalTo(1));
    }
}