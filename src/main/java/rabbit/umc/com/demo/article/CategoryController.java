package rabbit.umc.com.demo.article;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.utils.JwtService;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtService jwtService;

    /**
     * 카테고리 이미지 변경 - HOST (묘집사)유저만 수정가능
     * @param categoryId
     * @param patchCategoryImageReq
     * @return
     * @throws BaseException
     */
    @PatchMapping("/app/host/main-image/{categoryId}")
    public BaseResponse editCategoryImage(@PathVariable("categoryId") Long categoryId,
                                          @RequestBody PatchCategoryImageReq patchCategoryImageReq) throws BaseException {
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();

            categoryService.editCategoryImage(userId, categoryId, patchCategoryImageReq);
            return new BaseResponse<>("카테고리 " + categoryId + "번 사진 수정완료되었습니다.");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    @Getter
    @Setter
    @Data
    static class PatchCategoryImageReq{
        private String filePath;
    }
}

