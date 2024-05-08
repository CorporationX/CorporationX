package school.faang.user_service.service.skill;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillRepository;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    public boolean existsById(long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new NoSuchElementException(String.format("skill with id: %d is not exists", skillId));
        }
        return true;
    }
}
