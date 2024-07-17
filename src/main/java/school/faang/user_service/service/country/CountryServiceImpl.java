package school.faang.user_service.service.country;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public Country findEntityById(long countryId) {
        return countryRepository.findById(countryId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Country with id=%d not found", countryId)));
    }
}