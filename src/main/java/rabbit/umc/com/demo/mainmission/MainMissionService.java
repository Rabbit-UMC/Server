package rabbit.umc.com.demo.mainmission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainMissionService {
    private final MainMissionRepository mainMissionRepository;
}
