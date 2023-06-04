
package school.faang.user_service.dto.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class ContactInfo {


    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonUnwrapped
    private Address address;
}