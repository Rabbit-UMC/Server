package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.article.domain.Category;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Domain.UserPermision;
import rabbit.umc.com.demo.user.UserRepository;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.user.Domain.UserPermision.*;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public void editCategoryImage(Long userId, Long categoryId, CategoryController.PatchCategoryImageReq patchCategoryImageReq) throws BaseException {
        User user = userRepository.getReferenceById(userId);
        if (user.getUserPermission() != HOST) {
            throw new BaseException(INVALID_USER_JWT);
        }
        Category category = categoryRepository.getReferenceById(categoryId);
        category.setImage(patchCategoryImageReq.getFilePath());
        categoryRepository.save(category);

    }
}
