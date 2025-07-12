package app.sigorotalk.backend.domain.mentor;

import app.sigorotalk.backend.common.exception.BusinessException;
import app.sigorotalk.backend.common.exception.CommonErrorCode;
import app.sigorotalk.backend.domain.mentor.dto.MentorDetailResponseDto;
import app.sigorotalk.backend.domain.mentor.dto.MentorListResponseDto;
import app.sigorotalk.backend.domain.review.Review;
import app.sigorotalk.backend.domain.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

    private final MentorRepository mentorRepository;
    private final ReviewRepository reviewRepository;

    public Page<MentorListResponseDto> getMentorList(Pageable pageable) {
        // TODO: 필터링 기능 추가 (region, expertise 등)
        return mentorRepository.findAllWithUser(pageable)
                .map(MentorListResponseDto::from);
    }

    public MentorDetailResponseDto getMentorDetail(Long mentorId) {
        Mentor mentor = mentorRepository.findByIdWithUser(mentorId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        // 멘토의 리뷰 목록 조회
        List<Review> reviews = reviewRepository.findByCoffeeChatApplicationMentorId(mentorId);

        return MentorDetailResponseDto.from(mentor, reviews);
    }

    // TODO: (관리자용) 멘토 등록 로직 구현

    // TODO: (중요) 리뷰 작성 시 멘토 평점 및 리뷰 수 업데이트 로직 구현

}
