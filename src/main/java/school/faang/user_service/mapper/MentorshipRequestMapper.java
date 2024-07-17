package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "requesterId", source = "requester.id")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "requesterId",target = "requester.id")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> toDtoList(List<MentorshipRequest> mentorshipRequests);
}