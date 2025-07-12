package app.sigorotalk.backend.domain.coffeechat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoffeeChatApplicationRepository extends JpaRepository<CoffeeChatApplication, Long> {

    // 특정 사용자가 멘티 또는 멘토로 속한 모든 커피챗 목록을 조회 (N+1 문제 해결을 위해 fetch join 사용)
    @Query("SELECT ca FROM CoffeeChatApplication ca " +
            "JOIN FETCH ca.mentee " +
            "JOIN FETCH ca.mentor m " +
            "JOIN FETCH m.user " +
            "WHERE ca.mentee.id = :userId OR m.user.id = :userId " +
            "ORDER BY ca.applicationDate DESC")
    List<CoffeeChatApplication> findMyChatsByUserId(@Param("userId") Long userId);
}