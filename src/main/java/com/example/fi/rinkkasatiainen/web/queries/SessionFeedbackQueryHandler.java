package com.example.fi.rinkkasatiainen.web.queries;

import com.example.fi.rinkkasatiainen.model.session.SessionUUID;
import com.example.fi.rinkkasatiainen.model.session.repositories.Schedule;
import com.example.fi.rinkkasatiainen.model.session.projections.SessionFeedbackResult;
import com.example.fi.rinkkasatiainen.web.QueryHandler;

public class SessionFeedbackQueryHandler implements QueryHandler<SessionFeedbackQuery, SessionFeedbackResult> {
    private final Schedule schedule;

    public SessionFeedbackQueryHandler(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public SessionFeedbackResult handles(SessionFeedbackQuery feedbackQuery) {
        SessionFeedbackResult feedback = schedule.findSessionFeeback(SessionUUID.from(feedbackQuery.sessionId));

        return feedback;
    }
}
