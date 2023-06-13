package school.faang.user_service.mapper.person;

import org.mapstruct.*;
import school.faang.user_service.dto.generated.Person;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "username", source = "person", qualifiedByName = "mapName")
    @Mapping(target = "email", source = "person.contactInfo.email")
    @Mapping(target = "phone", source = "person.contactInfo.phone")
    @Mapping(target = "city", source = "person.contactInfo.address.city")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = "mapAboutMe")
    @Mapping(target = "country.id", source = "person.contactInfo.address.country")
    User toUser(Person person);

    @Named("mapName")
    default String mapName(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }

    @Named("mapAboutMe")
    default String mapAboutMe(Person person) {
        return String.format("%s, studying at %s for %s now, currently working at %s",
                person.getContactInfo().getAddress().getState(), person.getEducation().getFaculty(),
                person.getEducation().getYearOfStudy(), person.getEmployer());
    }
}