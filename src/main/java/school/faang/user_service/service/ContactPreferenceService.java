package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.repository.ContactPreferenceRepository;

@Service
@RequiredArgsConstructor
public class ContactPreferenceService {

    private final UserService userService;
    private final ContactPreferenceRepository contactPreferenceRepository;

    @Transactional
    public void setContactPreference(long userId, PreferredContact preference) {
        User user = userService.findUser(userId);
        if (user.getContactPreference() != null) {
            ContactPreference contactPreference = user.getContactPreference();
            contactPreference.setPreference(preference);
        } else {
            ContactPreference contactPreference = ContactPreference.builder()
                    .user(user)
                    .preference(preference)
                    .build();
            contactPreferenceRepository.save(contactPreference);
        }
    }
}
