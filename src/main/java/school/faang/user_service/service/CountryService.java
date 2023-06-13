package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public List<Country> getCountries() {
        return StreamSupport.stream(countryRepository.findAll().spliterator(), false).toList();
    }

    @Transactional
    public Country create(String title) {
        Country country = new Country();
        country.setTitle(title);
        return countryRepository.save(country);
    }
}