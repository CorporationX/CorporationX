
package school.faang.user_service.dto.generated;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class Person {

    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;

    @JsonUnwrapped
    private ContactInfo contactInfo;

    @JsonUnwrapped
    private Education education;

    private String status;
    private String admissionDate;
    private String graduationDate;
    private Boolean scholarship;
    private String employer;
}