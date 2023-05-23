package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.service.ContactPreferenceService;

@RestController
@RequiredArgsConstructor
public class ContactPreferenceController {

    private final ContactPreferenceService contactPreferenceService;

    @PostMapping("/users/{userId}/contactPreference/{preference}")
    public void setContactPreference(@PathVariable long userId, @PathVariable String preference) {
        PreferredContact preferredContact = PreferredContact.fromString(preference);
        contactPreferenceService.setContactPreference(userId, preferredContact);
    }
}
