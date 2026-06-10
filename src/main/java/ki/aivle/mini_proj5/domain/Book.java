package ki.aivle.mini_proj5.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @NotBlank
    private String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String content;

    @Column(nullable = false)
    @NotBlank
    private String genre;

    @Column
    private Integer likes = 0;

    @Column
    private Integer views = 0;

    @Column(nullable = false)
    @NotNull
    @JsonProperty("public")     // 예약어
    private Boolean isPublic;

    @Column(columnDefinition = "LONGTEXT")
    private String coverImageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 최초 저장(INSERT) 전 자동 호출 -> 생성일/수정일 초기화
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 수정 반영(UPDATE) 전 자동 호출 -> 수정일 갱신
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
