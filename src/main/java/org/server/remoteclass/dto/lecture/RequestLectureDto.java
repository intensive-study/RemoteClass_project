package org.server.remoteclass.dto.lecture;

import lombok.*;
import org.server.remoteclass.entity.Lecture;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class RequestLectureDto {

    // 강의 생성에 사용하는 Dto
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotNull//@NotEmpty
    @Min(0)
    private Integer price;
    @NotNull//@NotEmpty
    private LocalDateTime startDate;
    @NotNull//@NotEmpty
    private LocalDateTime endDate;
    @NotNull//@NotEmpty
    @Min(1)
    private Long categoryId;
//    private Long lecturer;

    public static RequestLectureDto from(Lecture lecture){
        if(lecture == null) return null;
        return RequestLectureDto.builder()
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .price(lecture.getPrice())
                .startDate(LocalDateTime.from(lecture.getStartDate()))
                .endDate(LocalDateTime.from(lecture.getEndDate()))
                .categoryId(lecture.getCategory().getCategoryId())
//                .lecturer(lecture.getUser().getUserId())
                .build();
    }
}