package com.example.fi.rinkkasatiainen.atdd;


import com.example.fi.rinkkasatiainen.model.participants.ParticipantUUID;
import com.example.fi.rinkkasatiainen.model.session.SessionUUID;
import com.example.fi.rinkkasatiainen.model.session.Stars;
import com.example.fi.rinkkasatiainen.model.session.projections.SessionFeedbackResult;
import com.example.fi.rinkkasatiainen.web.participants.ParticipantsRoute;
import com.example.fi.rinkkasatiainen.web.session.commands.*;
import com.example.fi.rinkkasatiainen.web.session.queries.SessionFeedbackRoute;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CanGiveAndReceiveFeedback {

    public static final String description = "Description of the talk";
    private Wiring wiring;

    @Before
    public void setUp() throws Exception {
        wiring = new Wiring();
    }


    @Test
    public void can_create_user_and_give_feedback_to_session() throws Exception {
        SessionUUID session = given_a_session();
        ParticipantUUID participant = and_I_have_registered_as_participant();

        when(participant).rates(session).as( Stars.FIVE );

        then_session(session).should_have_average_rating_of( 5.0 );
    }

    @Test
    public void can_reset_rating_by_giving_0() throws Exception {
        SessionUUID session = given_a_session();
        ParticipantUUID participant = and_I_have_registered_as_participant();

        when(participant).rates(session).as( Stars.FIVE );
        when(participant).rates(session).as( Stars.ZERO );

        then_session(session).should_have_average_rating_of( 0.0 );
    }

    private SessionResponse then_session(SessionUUID session) {
        return ( rating ) -> {
            ResponseEntity<SessionFeedbackResult> feedbackResult = new SessionFeedbackRoute(wiring.getSchedule()).getSession(session.getId().toString());
            SessionFeedbackResult body = feedbackResult.getBody();
            assertThat(body.getAverageRating(), equalTo(rating));
        };
    }

    private RateSession when(ParticipantUUID participant) {
        RateSession rateSession = sessionUUID -> stars -> {
            new SessionRoute(
                    wiring.getRegisterParticipantCommandHandler(),
                    wiring.getRateSessionCommandHandler()
            ).register( sessionUUID.getId().toString() , new ParticipantDto(participant.toString()));
            new SessionRoute(
                    wiring.getRegisterParticipantCommandHandler(),
                    wiring.getRateSessionCommandHandler()
            ).rate( sessionUUID.getId().toString(), new SessionFeedback(stars.ordinal(), participant.toString()));
        };
        return rateSession;
    }


    private ParticipantUUID and_I_have_registered_as_participant() {
        ResponseEntity<ParticipantsRoute.ParticipantDTO> responseEntity = new ParticipantsRoute().create();
        String uuid = getUUIDFromLocationHeader(responseEntity);
        return ParticipantUUID.from(uuid);
    }


    private SessionUUID given_a_session() {
        ResponseEntity<SessionsRoute.NewSessionResponse> sessionResponseEntity=
                wiring.getSessionsRoute().create(new NewSession("title", description));
        String uuid = getUUIDFromLocationHeader(sessionResponseEntity);
        return SessionUUID.from(uuid);
    }

    private <T> String getUUIDFromLocationHeader(ResponseEntity<T> sessionResponseEntity) {
        return sessionResponseEntity.getHeaders().get("location")
                .stream().map( str -> str.replaceAll(".*/", "") ).findFirst().get();
    }

    @FunctionalInterface
    public interface Rate {
        void as(Stars stars);
    }

    @FunctionalInterface
    public interface RateSession {
        Rate rates(SessionUUID sessionUUID);
    }

    @FunctionalInterface
    public interface SessionResponse {

        void should_have_average_rating_of(Double rating);
    }
}
