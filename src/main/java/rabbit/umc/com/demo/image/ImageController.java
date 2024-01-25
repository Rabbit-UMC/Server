package rabbit.umc.com.demo.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.BaseResponse;

@Tag(name = "image", description = "image API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class ImageController {
    private final ImageService imageService;

    /**
     * 이미지 저장 API
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    @Tag(name = "saveImage")
    @Operation(summary = "이미지 저장 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "file", description = "MultipartFile 이미지 업로드 용"),
            @Parameter(name = "path", description = "이미지 저장될 주소를 지정해 주세요 article/user/category/mainMission 중에 고르면 됩니다."),
    })
    @PostMapping("/file")
    public BaseResponse<List<String>> uploadFile(@RequestPart(value = "file") List<MultipartFile> multipartFiles, @RequestParam(name = "path") String path) throws IOException {
        List<String> filePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = imageService.createImage(multipartFile, path );
            filePathList.add(filePath);
        }

        return new BaseResponse<>(filePathList);
    }

}
