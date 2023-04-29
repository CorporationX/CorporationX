package school.faang.user_service.service.filter.mentorship;

import school.faang.user_service.dto.mentorship.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public abstract class MentorshipRequestFilter {
    public Stream<MentorshipRequest> applyFilter(Stream<MentorshipRequest> reqs, RequestFilter filter) {
        return reqs.filter(req -> applyFilter(req, filter));
    }

    protected abstract boolean applyFilter(MentorshipRequest req, RequestFilter filter);

    public abstract boolean isApplicable(RequestFilter filter);
}
