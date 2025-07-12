package app.sigorotalk.backend.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 커피챗 신청(application_id)에 연결된 리뷰 찾기
    List<Review> findByCoffeeChatApplicationMentorId(Long mentorId);
}
