package rabbit.umc.com.demo.community.category;

import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.repository.UserRepository;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.user.Domain.UserPermission.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public Category getCategory(Long id) throws BaseException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(DONT_EXIST_CATEGORY));
    }

    public void editCategoryImage(Long userId, Long categoryId, CategoryController.PatchCategoryImageReq patchCategoryImageReq) throws BaseException {
        try {
            User user = userRepository.getReferenceById(userId);

            // 유저 권한 (묘집사[HOST]인지) 체크
            if (user.getUserPermission() != HOST) {
                throw new BaseException(INVALID_USER_JWT);
            }

            Category category = categoryRepository.getReferenceById(categoryId);
            //해당 카테고리의 묘집사인지 확인
            if (category.getUser().getId() != userId) {
                throw new BaseException(INVALID_USER_JWT);
            }
            category.changeImage(patchCategoryImageReq.getFilePath());
            categoryRepository.save(category);

        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_CATEGORY);
        }
    }
}
