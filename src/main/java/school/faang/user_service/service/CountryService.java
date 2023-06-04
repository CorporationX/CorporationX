package school.faang.user_service.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable("countries")
    public List<Country> getCountries() {
        return StreamSupport.stream(countryRepository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Country create(String title) {
        Country country = new Country();
        country.setTitle(title);
        return countryRepository.save(country);
    }
}