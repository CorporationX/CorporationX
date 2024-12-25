package school.faang.user_service.repository.contact;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.contact.Contact;

public interface ContactRepository extends CrudRepository<Contact, Long> {
}
