package school.faang.user_service.service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorFilter extends MentorshipRequestFilter {
    @Override
    protected boolean applyFilter(MentorshipRequest req, RequestFilter filter) {
        return filter.getReceiverId().equals(req.getReceiver().getId());
    }

    @Override
    public boolean isApplicable(RequestFilter filter) {
        return filter.getReceiverId() != null;
    }
}
