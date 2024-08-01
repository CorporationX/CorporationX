package faang.school.postservice.event.heat;

import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatUsersFeedEvent implements Event {
    private HashSet<Long> usersIds;
}
