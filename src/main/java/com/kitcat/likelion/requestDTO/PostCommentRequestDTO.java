package com.kitcat.likelion.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCommentRequestDTO {

    @Schema(description = "댓글이 달리는 게시글의 ID")
    private Long postId;

    @Schema(description = "대댓글일때 댓글의 ID값 대댓글이 아닐 때는 null입니다.")
    private Long parentId;

    @Schema(description = "댓글의 내용")
    private String content;

}
