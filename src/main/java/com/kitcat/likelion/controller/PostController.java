package com.kitcat.likelion.controller;

import com.kitcat.likelion.requestDTO.PostCommentRequestDTO;
import com.kitcat.likelion.requestDTO.PostCreateRequestDTO;
import com.kitcat.likelion.responseDTO.PostDetailDTO;
import com.kitcat.likelion.responseDTO.PostListDTO;
import com.kitcat.likelion.security.custom.CustomUserDetails;
import com.kitcat.likelion.security.jwt.JwtUtil;
import com.kitcat.likelion.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping("/create")
    @Operation(summary = "게시글 작성 기능", description = "게시글작성에 사용되는 API")
    @ApiResponse(responseCode = "201", description = "게시글 작성 성공")
    @Parameters({
            @Parameter(name = "title", description = "게시글 이름"),
            @Parameter(name = "content", description = "게시글 내용"),
    })
    public String createPost(@RequestPart(value = "dto")PostCreateRequestDTO requestDTO,
                                @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.createPost(userDetails.getUserId(), requestDTO, files);
        return "good";
    }
    @Operation(summary = "댓글 작성 기능", description = "댓글 작성 사용되는 API")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = "댓글 작성 성공"),
            @ApiResponse (responseCode = "400", description = "댓글이 없는 곳에 대댓글을 달려고 할 때 에러")
    })
    @Parameters({
            @Parameter(name = "postId", description = "댓글이 달리는 게시글의 ID"),
            @Parameter(name = "parentId", description = "대댓글일 경우 대댓글이 달리는 댓글의 ID(없으면 null로 작성)"),
            @Parameter(name = "content", description = "댓글 내용"),

    })
    @PostMapping("/comment")
    public ResponseEntity<String> comment(@RequestBody PostCommentRequestDTO requestDTO,
                                @AuthenticationPrincipal CustomUserDetails userDetails){
        String commentStatus = postService.createComment(userDetails.getUserId(), requestDTO);
        if(commentStatus.equals("not found parentComment")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commentStatus);
        }
        return ResponseEntity.status(HttpStatus.OK).body(commentStatus);
    }

    @Operation(summary = "게시글을 자세히 보는 기능", description = "게시글을 들어갈 때 작성 사용되는 API")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = "게시글 보기")
    })
    @Parameters({
            @Parameter(name = "postId", description = "상세 조회 게시글 ID", example = "1")
    })
    @GetMapping("/show/{postId}")
    public PostDetailDTO show(@PathVariable("postId") Long postId,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        return postService.findDetailPost(userDetails.getUserId(), postId);
    }

    @Operation(summary = "게시글에 좋아요 누르는 기능", description = "게시글 좋아요 누를 때 사용되는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 성공"),
            @ApiResponse(responseCode = "400", description = "중복좋아요 입력 실패")
    })
    @Parameters({
            @Parameter(name = "postId", description = "좋아요를 누르는 게시글 ID", example = "1")
    })
    @GetMapping("/heart/insert/{postId}")
    public ResponseEntity<String> insertHeart(@PathVariable("postId") Long postId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        String heartStatus =  postService.insertHeart(userDetails.getUserId(), postId);

        if(heartStatus.equals("fail to add heart to post")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(heartStatus);
        }

        return ResponseEntity.status(HttpStatus.OK).body(heartStatus);
    }

    @Operation(summary = "좋아요를 삭제하는 기능", description = "좋아요 삭제시 사용되는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 좋아요 삭제")
    })
    @Parameters({
            @Parameter(name = "postId", description = "삭제를 원하는 게시글 ID", example = "1")
    })
    @GetMapping("/heart/delecte/{postId}")
    public ResponseEntity<String> deleteHeart(@PathVariable("postId") Long postId,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        String heartStatus = postService.deleteHeart(userDetails.getUserId(), postId);
        if(heartStatus.equals("fail to delete heart to post")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(heartStatus);
        }

        return ResponseEntity.status(HttpStatus.OK).body(heartStatus);
    }

    @Operation(summary = "게시글 목록보기(전체)", description = "게시글 전체 목록 볼 때 사용되는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 목록 조회 성공"),
    })

    @GetMapping("/show/all")
    public List<PostListDTO> showAll(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return postService.postList(userDetails.getUserId());
    }




}
