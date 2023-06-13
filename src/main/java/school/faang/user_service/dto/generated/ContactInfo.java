
package school.faang.user_service.dto.generated;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class ContactInfo {

    private String email;
    private String phone;

    @JsonUnwrapped
    private Address address;

    public void setCountryId(long id) {
        address.setCountry(String.valueOf(id));
    }
}