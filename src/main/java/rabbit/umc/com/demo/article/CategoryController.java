package rabbit.umc.com.demo.article;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.utils.JwtService;

@Api(tags = {"카테고리 관련 Controller"})
@RestController
@RequestMapping("/app")
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
    @ApiOperation(value = "카테고리 이미지 변경 하는 메소드 [메인 미션 사진동일]")
    @PatchMapping("/host/main-image/{categoryId}")
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

