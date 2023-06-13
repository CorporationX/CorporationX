package school.faang.user_service.mapper.person;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.generated.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.service.CountryService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CountryInfoMapper {

    private final CountryService countryService;

    public void setCountryIdToPerson(Person person, String country) {
        List<Country> countries = countryService.getCountries();

        countries.stream()
                .filter(existing -> existing.getTitle().equalsIgnoreCase(country))
                .findFirst().ifPresentOrElse(
                        existingCountry -> person.getContactInfo().setCountryId(existingCountry.getId()),
                        () -> {
                            Country newCountry = countryService.create(country);
                            person.getContactInfo().setCountryId(newCountry.getId());
                        }
                );
    }
}