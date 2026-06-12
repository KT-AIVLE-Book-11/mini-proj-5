# :books: 도서관리시스템 개발
- AI 표지 생성을 지원하는 풀스택(React + Spring Boot) 도서 관리 시스템 프로젝트
- 기존 프론트엔드(`json-server`) 환경을 고도화하여 **Spring Boot 기반의 백엔드 서버 및 H2 데이터베이스**로 교체 및 연동

## 프로젝트 소개 :page_with_curl:
- Spring Boot를 활용한 백엔드 서버 개발 역량 강화 및 REST API 설계/구현
- 사용자가 손쉽게 도서를 등록, 조회, 수정, 삭제할 수 있는 완전한 CRUD 기능 제공
- OpenAI API를 연동하여, 도서 제목이나 내용에 기반한 표지를 자동으로 생성하는 기능 구현

## 개발 기간 :clock1:
- 2026-06-09 ~ 2026-06-12

### 멤버 구성 R&R 👥
- 김유진: 조장 / 통합, 예외 처리
- 김한별: 백엔드 개발
- 김현진: AI, FrontEnd 연동
- 임채은: 발표자 / 백엔드 개발
- 백규리: 백엔드 개발
- 강석훈: 서기 / 백엔드 개발
- 조재형: AI, FrontEnd 연동

---

## 🏗️ 시스템 기획 및 아키텍처

### 1. 기획 및 설계
* **Frontend 분석 및 ERD 도출:** 프론트엔드의 `db.json` 구조를 분석하여 백엔드 `Book` Entity의 필수 필드(`id`, `title`, `author`, `content`, `genre`, `likes`, `views`, `isPublic`, `coverImageUrl`, `createdAt`, `updatedAt`) 도출 및 설계
* **댓글 기능 확장 및 연관관계 설계:** 도서별 별점 및 댓글 기능을 위해 Comment Entity의 필드(id, book_id, content, rating, createdAt) 도출 및 설계 — Book Entity와 1:N 관계를 @ManyToOne 단방향 연관관계로 매핑하여 book_id(FK)가 Book의 id를 참조

<div align="center">


**[Book]**

| 컬럼명 | 타입 | 설명 | null 여부 |
|---|---|---|---|
| id | Long | 도서 아이디 | not null |
| title | varchar | 도서 제목 | not null |
| author | varchar | 작가 | not null |
| content | text | 내용 | not null |
| genre | varchar | 장르 | not null |
| likes | int | 좋아요 수 | null |
| views | int | 조회수 | null |
| public | boolean | 공개 여부 | not null |
| coverImageUrl | varchar | 도서 이미지 | null |
| createdAt | timestamp | 생성일 | not null |
| updatedAt | timestamp | 수정일 | not null |


**[Comment]**

| 컬럼명 | 타입 | 설명 | null 여부 |
| --- | --- | --- | --- |
| id | Long | 댓글 아이디 | not null |
| book_id | Long | 도서 아이디(FK) | not null |
| content | text | 댓글 내용 | not null |
| rating | int | 별점 | not null |
| createdAt | timestamp | 작성일  | not null |


</div>

### 2. 프로젝트 핵심 골격
Spring MVC 패턴에 따라 데이터 처리 및 비즈니스 로직을 분리하여 3계층 아키텍처 골격 구성 및 연동 설정(`WebConfig` CORS, `application.yml` H2)

**[계층별 핵심 골격 코드]**
* **Domain (Book.java):** 데이터베이스 테이블과 매핑될 엔티티 구조 설계 (생성일/수정일 자동 갱신 및 AI 표지 이미지 저장을 위한 LONGTEXT 적용)
  ```java

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
        @JsonProperty("public") 
        private Boolean isPublic;

        @Column(columnDefinition = "LONGTEXT")
        private String coverImageUrl;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        @Column(nullable = false)
        private LocalDateTime updatedAt;

        @PrePersist
        public void onCreate() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            if(this.likes==null) this.likes = 0;
            if(this.views==null) this.views = 0;
        }

        @PreUpdate
        public void onUpdate() {
            this.updatedAt = LocalDateTime.now();
        }

    }

  ```

* **Repository (BookRepository.java):** `JpaRepository` 상속을 통한 DB 접근 인터페이스 구성
  ```java
  public interface BookRepository extends JpaRepository<Book, Long> {
      List<Book> findByTitleContaining(String keyword);
      List<Book> findByAuthorContaining(String keyword);
      List<Book> findByGenre(String genre);
  }
  ```

* **Service (BookService.java):** 비즈니스 로직 처리를 위한 서비스 뼈대 작성
  ```java
  @Service
  @RequiredArgsConstructor
  public class BookService {
      private final BookRepository bookRepository;
      
      @Transactional(readOnly = true)
      public List<Book> getAll() { return bookRepository.findAll(); }
      // 비즈니스 로직 메서드 골격 정의
  }
  ```



 * **Controller (BookController.java):** 클라이언트 요청을 받을 REST API 컨트롤러 매핑 구성
    ```java
    @RestController
    @RequestMapping("/books")
    @RequiredArgsConstructor
    public class BookController {
        private final BookService bookService;
        
        @GetMapping public ResponseEntity<List<Book>> getAllBooks(@RequestParam(required = false) String searchType, @RequestParam(required = false) String keyword, @RequestParam(required = false) String genre, @RequestParam(required = false, defaultValue = "views") String sortBy, @RequestParam(required = false, defaultValue = "desc") String direction) { ... }
        @GetMapping("/{id}") public ResponseEntity<Book> getBook(@PathVariable Long id) { ... }
        @PostMapping public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) { ... }
        @PatchMapping("/{id}") public ResponseEntity<Book> updateBookInfo(...) { ... }
        @PatchMapping("/{id}/cover") public ResponseEntity<Book> updateBookCover(...) { ... }
        @DeleteMapping("/{id}") public ResponseEntity<Void> deleteBook(@PathVariable Long id) { ... }
        @PatchMapping("/{id}/views") public ResponseEntity<Void> incrementViews(@PathVariable Long id) { ... }
        @PatchMapping("/{id}/likes") public ResponseEntity<Void> incrementLikes(@PathVariable Long id) { ... }
    }
    ```

### 3. CRUD API 및 비즈니스 로직 구현 세부 내용(상세코드 GitHub에서 확인 - 일부 로직만 작성)

* **Domain & Repository (검증 및 DB 연동):**
  * `Book` Entity: `@NotBlank` 어노테이션 추가하여 필수 입력값(제목, 작가, 내용, 장르) 검증 로직 적용
  * `BookRepository`: JpaRepository를 활용한 기본 CRUD 동작 검증 및 H2 콘솔을 통한 데이터 적재 확인

     <img alt="Image" src="https://github.com/user-attachments/assets/f949690b-c0c4-4279-b498-4f4789700a32" />
 
  * `Comment` Entity:
    * `@NotBlank`·`@Size`· `@Min(1)`·`@Max(5)`로 입력 및 별점 범위 검증 로직 적용
    * `@ManyToOne` + `@JoinColumn(name = "book_id")`로 `Book`과 단방향 연관관계 매핑 (도서 1 : 댓글 N)
   
      <img alt="Image" alt="image" src="https://github.com/user-attachments/assets/4431cd51-33a9-4897-8a11-fabf3df8c485" />

  
* **Book Service (생성자 주입 및 핵심 로직 구현):**
  * `@RequiredArgsConstructor`를 활용하여 Repository 생성자 주입 적용
  * 상세 조회 시 `orElseThrow`로 커스텀 예외 처리 구현
  * 부분 수정(`updateInfo`): 전달된 데이터가 `null`이 아닐 때만 기존 엔티티 값을 변경하도록 구현하여 데이터 손실 방지
  ```java
  @Service
  @RequiredArgsConstructor
  public class BookService {
      private final BookRepository bookRepository;
      
      @Transactional(readOnly = true)
      public List<Book> getAll() { return bookRepository.findAll(); }
    
      @Transactional(readOnly = true)
      public Book getById(Long id) {
          return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
      }

      @Transactional
      public Book create(Book book) { return bookRepository.save(book); }

      @Transactional
      public Book updateInfo(Long id, Book bookDetails) {
          Book existing = getById(id);
          if (bookDetails.getTitle() != null) existing.setTitle(bookDetails.getTitle());
          if (bookDetails.getAuthor() != null) existing.setAuthor(bookDetails.getAuthor());
          if (bookDetails.getContent() != null) existing.setContent(bookDetails.getContent());
          if (bookDetails.getGenre() != null) existing.setGenre(bookDetails.getGenre());
          if (bookDetails.getIsPublic() != null) existing.setIsPublic(bookDetails.getIsPublic());
          return bookRepository.save(existing);
      }
  }
  ```

* **Comment Service (생성자 주입 및 핵심 로직 구현):**
    * `@RequiredArgsConstructor`를 활용하여 Repository 생성자 주입 적용
    * `existsById`를 통해 도서 존재 확인 및 잘못된 접근 처리, `findByBookIdOrderByCreatedAtDesc`를 통해 생성일자 기준 내림차순 필터링 조회
    * FK 위반 방지를 위해 `bookId` 로 `Book`entity 조회한 후 댓글 객체에 명확하게 매핑 및 저장
    
    ```java
    @Service
    @RequiredArgsConstructor
    public class CommentService {
        private final CommentRepository commentRepository;
        private final BookRepository bookRepository;
    
        @Transactional(readOnly = true)
        public List<Comment> getAll(Long bookId) {
            if (!bookRepository.existsById(bookId)) {
                throw new BookNotFoundException(bookId);
            }
            return commentRepository.findByBookIdOrderByCreatedAtDesc(bookId);
        }
    
        @Transactional
        public Comment create(Long bookId, Comment comment) {
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
            comment.setBook(book);
            return commentRepository.save(comment);
        }
    }

    
* **Book Controller (엔드포인트 매핑 및 입력 검증):**
  * `GET`, `POST`, `PATCH`, `DELETE` 어노테이션을 활용한 표준 REST API 매핑
  * `@Valid` 및 `@RequestBody`를 적용하여 들어오는 객체 데이터 검증 후 등록
  ```java
  @RestController
  @RequestMapping("/books")
  @RequiredArgsConstructor
  public class BookController {
      private final BookService bookService;
      
      @GetMapping
      public ResponseEntity<List<Book>> getAllBooks() {
          return ResponseEntity.ok(bookService.getAll());
      }

      @GetMapping("/{id}")
      public ResponseEntity<Book> getBook(@PathVariable Long id) {
          return ResponseEntity.ok(bookService.getById(id));
      }

      @PostMapping
      public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
          Book createdBook = bookService.create(book);
          return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
      }

      @PatchMapping("/{id}")
      public ResponseEntity<Book> updateBookInfo(@PathVariable Long id, @RequestBody Book bookDetails) {
          return ResponseEntity.ok(bookService.updateInfo(id, bookDetails));
      }
  }
  ```
  
* **Comment Controller (엔드포인트 매핑 및 입력 검증):**
  * `GET`, `POST` 어노테이션을 활용한 표준 REST API 매핑
  * `@PathVariable` 로 댓글이 작성될 대상 도서의 ID를 식별하고, `@Valid` 및 `@RequestBody`를 적용하여 들어오는 객체 데이터 검증 후 등록
  ```java
    @RestController
    @RequestMapping("/books")
    @RequiredArgsConstructor
    public class CommentController {
    
        private final CommentService commentService;
        
        @GetMapping("/{bookId}/comments")
        public ResponseEntity<List<Comment>> getAllComments(@PathVariable Long bookId) {
            List<Comment> comments = commentService.getAll(bookId);
            return ResponseEntity.ok(comments);
        }
    
        @PostMapping("/{bookId}/comments")
        public ResponseEntity<Comment> createComment(@PathVariable Long bookId, @Valid @RequestBody Comment comment) {
            Comment createdComment = commentService.create(bookId, comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        }
    }
    
    ```
  
* **통합 연동 및 테스트:**
  * **Postman 검증:** 모든 엔드포인트 개별 호출 테스트 및 `@Valid` 예외 응답(400 Bad Request) 검증 완료
  * **Frontend 1차 연동:** React 코드 내 `fetch` URL을 기존 `3000`에서 `8080`으로 변경
  * **풀스택 동작:** Postman과 React 브라우저 화면 양쪽에서 데이터 생성, 수정, 삭제가 H2 DB와 실시간 연동됨을 확인
  

* **예외 처리 및 트랜잭션 관리:**
  * `BookNotFoundException`: 도서 미존재 시 404 에러를 반환하는 사용자 정의 예외 처리
  * `GlobalExceptionHandler`: `@RestControllerAdvice`를 통해 애플리케이션 전역에서 발생하는 예외(404, 400 등)를 일관된 JSON 형태로 정제하여 반환
  ```java
  @RestControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(BookNotFoundException.class)
      public ResponseEntity<Map<String, String>> handleBookNotFoundException(BookNotFoundException e) {
          Map<String, String> error = new HashMap<>();
          error.put("error", e.getMessage());
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
      }

      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<Map<String, String>> handleValidException(MethodArgumentNotValidException e) {
          Map<String, String> errors = new HashMap<>();
          e.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
      }
  } 
  ```
  * 트랜잭션 최적화: CUD 작업에는 @Transactional을 적용하여 데이터 정합성을 보장하고, 조회 메서드에는 readOnly = true 옵션을 추가하여 조회 성능 향상

* **AI 표지 생성 및 연동:**
  * **Backend:** `PATCH /books/{id}/cover` 엔드포인트 구현 및 `BookService.updateCover()`를 통한 표지 URL 업데이트 메서드 추가
  * **Frontend:** OpenAI API 직접 호출 후 결과(base64)를 Data URL로 변환하여 렌더링 및 백엔드 저장
  ```java
  // 백엔드: 표지 업데이트 서비스 (BookService.java)
  @Transactional
  public Book updateCover(Long id, String coverImageUrl) {
      Book existing = getById(id);
      existing.setCoverImageUrl(coverImageUrl);
      return bookRepository.save(existing);
  }

  // 프론트엔드: 저장 로직 (Image.jsx)
  const handleSaveImage = async () => {
      const res = await fetch(`${API_URL}/${book.id}/cover`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ coverImageUrl: generatedImage }),
      });
      if (!res.ok) throw new Error('표지 저장 실패');
      // ... 이후 성공 처리
  }
  ```

### 4. 서비스 구성도 (Service Architecture)
기존 `json-server`를 **Spring Boot + JPA + H2 Database**로 대체하고, AI 표지 저장 API까지 확장.

* **기본 흐름 (CRUD - 도서 데이터 관리)**
  * **React** `(localhost:5173)` ⇄ `GET / POST / PATCH / DELETE` ⇄ **Spring Boot** `(localhost:8080)` ⇄ **H2 Database** (JPA 영속성 계층)
* **AI 표지 자동 생성 흐름**
  1. **React** ➔ `POST + prompt` ➔ **OpenAI API (GPT Image)**
  2. **OpenAI** ➔ `b64_json 응답` ➔ **React**
  3. **React** 내부에서 응답받은 `b64_json`을 `Data URL`로 변환하여 화면에 렌더링
  4. **React** ➔ `PATCH /books/{id}/cover` ➔ **Spring Boot** ➔ **H2 Database** (생성된 표지 결과 저장)

---

## API 명세서 :electric_plug:
Spring Boot 백엔드 서버(`localhost:8080`) 제공 RESTful API 엔드포인트

| 메서드 | 엔드포인트 | 기능 | 설명                                                            |
| :--- | :--- | :--- |:--------------------------------------------------------------|
| `GET` | `/books` | 전체 도서 목록 조회 / 검색 | 쿼리 파라미터로 검색 및 장르 필터링,  정렬 지원. 파라미터 미전달 시 DB에 등록된 모든 도서 데이터 조회 |
| `GET` | `/books/{id}` | 도서 상세 조회 | 특정 ID의 도서 세부 정보 조회                                            |
| `POST` | `/books` | 신규 도서 등록 | 새 도서 등록 (생성일/수정일/조회수 등 자동 초기화)                                |
| `PATCH` | `/books/{id}` | 도서 정보 수정 | 도서의 제목, 내용, 작가 등 세부 정보 업데이트                                   |
| `PATCH` | `/books/{id}/cover` | AI 표지 업데이트 | OpenAI로 생성된 도서 표지 이미지 URL 저장                                  |
| `PATCH` | `/books/{id}/views` | 조회수 증가 | 도서 상세 페이지 진입 시 조회수 1 증가                                       |
| `PATCH` | `/books/{id}/likes` | 좋아요 증가 | 좋아요 버튼 클릭 시 좋아요 수 1 증가                                        |
| `DELETE`| `/books/{id}` | 도서 삭제 | 특정 ID의 도서 데이터를 DB에서 삭제                                        |
| `GET` | `/books/{bookId}/comments` | 댓글 목록 조회 | 특정 도서의 전체 댓글 조회                                               |
| `POST` | `/books/{bookId}/comments` | 댓글 등록 | 별점 및 댓글 내용 등록                                                 |

---

## 주요 기능 :pencil:

| 구분 | 기능 | 설명                                                       |
|------|------|----------------------------------------------------------|
| **MAIN** | **홈 화면 (랜딩 페이지)** | 서비스 소개, 주요 기능 안내 카드 및 스크롤 가능한 도서 캐러셀 슬라이더 제공             |
| **UPLOAD** | **신규 도서 업로드** | 제목, 작가, 장르, 세부 내용 입력 및 Spring Boot Validation을 통한 유효성 검사 |
| **RESULT** | **도서 표지 생성** | OpenAI API 기반 맞춤형 표지 자동 생성 및 DB 연동 저장 기능 제공              |
| **LIST** | **독서 목록 리스트** | DB 데이터 기반 도서 카드 렌더링, 장르별 필터링, 검색창(제목/작가), 정렬 지원          |
| **DETAIL** | **도서 상세 페이지** | 도서 상세 정보 조회, 내용/표지 개별 수정, 도서 삭제 및 조회수/좋아요 실시간 반영         |
| **COMMENT** | **댓글** | 도서 상세 페이지 하단에서 별점 및 댓글 등록·조회·삭제, 등록된 댓글 수 실시간 표시         |
| **공통** | **네비게이션/반응형** | 상단 고정 네비게이션 바 및 데스크톱/모바일 환경에 맞춘 UI 최적화                   |

---

## 기술 스택 :computer:

| 분야 | 기술 명세 |
|------|------|
| **Frontend** | React 19, Vite, fetch |
| **Backend** | Spring Boot 3, Spring MVC (REST API), Spring Data JPA, Lombok |
| **Database** | H2 Database |
| **AI** | OpenAI API (GPT Image 모델) |
| **협업/관리** | GitHub, Notion |

---


## 🛠️ 트러블슈팅 (Troubleshooting)
### 1. AI 표지 이미지 저장 오류
**[문제 상황]**

* AI로 생성한 표지 이미지의 저장하기 버튼 클릭 시 "표지가 성공적으로 저장되었습니다!" 알림은 표시되나, 실제 도서 상세 페이지에서 표지 이미지가 반영되지 않는 문제 발생

**[원인 분석]**

* `Image.jsx`에서 표지 저장 요청 시 표지 수정이 아닌 도서 수정 엔드포인트로 요청을 전송하고 있었음.

* 해당 엔드포인트의 백엔드 로직에는 `coverImageUrl` 필드 처리가 포함되어 있지 않아 요청 데이터가 무시됨.

**[해결 방법]**

  * 표지 이미지 수정 엔드포인트로 요청 URL 변경

    ```js
    // 수정 전
    const res = await fetch(`${API_URL}/${book.id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
          coverImageUrl: generatedImage,
          updatedAt: new Date().toISOString(),
      }),
    })
    ```
    ```js
    // 수정 후
    const res = await fetch(`${API_URL}/${book.id}/cover`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
          coverImageUrl: generatedImage,
          prompt: prompt,
      }),
    })
    ```
### 2. 조회수 중복 증가 오류

**[문제 상황]**

* 백엔드를 json-server에서 Spring Boot로 마이그레이션한 이후, 도서 상세 페이지에 한 번 진입했을 때 조회수가 1이 아니라 2씩 증가하는 현상 발생.

**[원인 분석]**

* React StrictMode가 개발 모드에서 `useEffect`를 의도적으로 두 번 실행하면서, 조회수 증가 요청(`PATCH /books/{id}/views`)이 두 번 호출됨. 기존 json-server 방식에서는 프론트가 `현재값 + 1`을 계산해 결과값을 덮어쓰는 구조로, 두 번 실행되어도 같은 값으로 덮어써져 `+1`로만 반영되었으나, Spring Boot 백엔드에서는 서버가 기존 값에 `+1`씩 누적하는 방식으로, 두 번 호출이 그대로 `+2` 로 누적되어 문제가 발생.

**[해결 방법]**

  * `useRef`로 실행 여부를 기록하는 플래그를 두어, `useEffect`가 두 번 실행되어도 조회수 요청이 한 번만 나가도록 가드 추가.

    ```jsx
    // 수정 전
    useEffect(() => {
      const fetchBookDetail = async () => {
        ...
        await fetch(`${API_URL}/${book.id}/views`, { method: 'PATCH' })
        ...
      }
      if (book?.id) fetchBookDetail()
    }, [book.id])
    ```
  
    ```jsx
    // 수정 후
    const hasFetched = useRef(false)
    
    useEffect(() => {
      if (hasFetched.current) return   // 두 번째 실행이면 차단
      hasFetched.current = true
  
      const fetchBookDetail = async () => {
        ...
        await fetch(`${API_URL}/${book.id}/views`, { method: 'PATCH' })
        ...
      }
      if (book?.id) fetchBookDetail()
    }, [book.id])
    ```


## 실행 가이드 :wrench:
Frontend와 Backend 서버를 각각 독립적으로 실행.

* Frontend GitHub: https://github.com/KT-AIVLE-Book-11/mini-proj-4.git
* Backend GitHub: https://github.com/KT-AIVLE-Book-11/mini-proj-5.git

    ```bash
    git clone 주소 # Frontend, Backend 폴더 로컬 환경으로 가져오기
    ```

### 1. Backend 서버 실행 (Spring Boot)
1. Java 17 이상 및 IntelliJ IDEA 환경 준비
2. 백엔드 프로젝트 폴더를 IntelliJ로 열기
3. `src/main/java/ki/aivle/mini_proj5/MiniProj5Application.java` 파일 실행(Run)
4. 콘솔 출력 확인
     
     - **H2 콘솔 접속:** 브라우저에서 `http://localhost:8080/h2-console` 접속
       - JDBC URL: `jdbc:h2:mem:bookdb`
       - User Name: `sa`
       - Password: (공란)

### 2. Frontend 서버 실행 (React)
1. Node.js 설치 확인 (https://nodejs.org)
2. 프론트엔드 프로젝트 폴더에서 터미널 열기 및 패키지 설치
   ```bash
   npm install
   ```
3. 개발 서버 실행
   ```bash
   npm run dev
   ```
4. 브라우저에서 `http://localhost:5173` 접속하여 서비스 이용
