package faang.school.postservice.event.heat;

import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;

@Data
@Builder
@AllArgsConstructor
public class HeatUsersFeedEvent implements Event {
    private HashSet<Long> usersIds;
}
