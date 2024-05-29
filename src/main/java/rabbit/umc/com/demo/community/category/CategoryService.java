package rabbit.umc.com.demo.community.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.community.dto.PatchCategoryImageReq;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.service.UserQueryService;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.user.Domain.UserPermission.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserQueryService userQueryService;

    public Category getCategory(Long id) throws BaseException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(DONT_EXIST_CATEGORY));
    }

    public void editCategoryImage(Long userId, Long categoryId, PatchCategoryImageReq patchCategoryImageReq) throws BaseException {

        User user = userQueryService.getUserByUserId(userId);
        // 유저 권한 (묘집사[HOST]인지) 체크
        if (!user.getUserPermission().equals(HOST)) {
            throw new BaseException(INVALID_USER_JWT);
        }
        Category targetCategory = getCategory(categoryId);
        //해당 카테고리의 묘집사인지 확인
        if (targetCategory.getUser().getId().equals(userId)) {
            throw new BaseException(INVALID_USER_JWT);
        }

        targetCategory.changeImage(patchCategoryImageReq.getFilePath());
        categoryRepository.save(targetCategory);
    }

    public List<Category> findMyHostCategories(Long userId){
        return categoryRepository.findAllByUserId(userId);
    }

    public void changeCategoryHost(MainMission mainMission, User newHost){
        Category category = mainMission.getCategory();
        category.changeHostUser(newHost);
        categoryRepository.save(category);
    }
}
