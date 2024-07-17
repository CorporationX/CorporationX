package school.faang.user_service.service.country;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith({MockitoExtension.class})
class CountryServiceImplTest {
    private static final long COUNTRY_ID = 1L;

    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryServiceImpl countryService;
    private Country country;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(COUNTRY_ID);
    }

    @Test
    public void whenFindEntityByIdThenThrowsException() {
        Assert.assertThrows(EntityNotFoundException.class,
                () -> countryService.findEntityById(COUNTRY_ID));
    }

    @Test
    public void whenFindEntityByIdThenReturnCountry() {
        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(country));
        Country actual = countryService.findEntityById(COUNTRY_ID);
        assertThat(actual).isEqualTo(country);
    }
}