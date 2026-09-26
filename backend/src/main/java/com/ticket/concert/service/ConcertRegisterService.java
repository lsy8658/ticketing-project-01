package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ConcertRegisterRequest;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.dto.ImageInfo;
import com.ticket.concert.dto.SeatGradeResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConcertRegisterService {
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final SeatGradeService seatGradeService;
    private final ConcertScheduleService concertScheduleService;
    private final ConcertSeatGradeService concertSeatGradeService;
    private final ScheduleSeatService scheduleSeatService;
    private final ConcertImageRepository concertImageRepository;

    @Transactional
    public Long register(Long userId, ConcertRegisterRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!request.getSalesStartAt().isBefore(request.getSalesEndAt())) {
            throw new CustomException(ErrorCode.CONCERT_SALES_PERIOD_INVALID);
        }

        Concert concert = Concert.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .createBy(user)
                .salesStartAt(request.getSalesStartAt())
                .salesEndAt(request.getSalesEndAt())
                .build();

        Concert savedConcert = concertRepository.save(concert);

        if (request.getImages() != null) {
            List<ImageInfo> images = request.getImages();
            for (int i = 0; i < images.size(); i++) {
                ImageInfo info = images.get(i);
                concertImageRepository.save(new ConcertImage(savedConcert, info.getUrl(), info.getPublicId(), i));
            }
        }

        Map<String, Long> gradeNameToId = new HashMap<>();
        for (ConcertRegisterRequest.SeatGradeInfo info : request.getSeatGrades()) {
            if (gradeNameToId.containsKey(info.getName())) {
                throw new CustomException(ErrorCode.INVALID_GRADE_NAME);
            }
            SeatGradeResponse grade = seatGradeService.create(
                    savedConcert.getId(), userId, info.getName(), info.getPrice()
            );
            gradeNameToId.put(info.getName(), grade.getId());
        }

        for (ConcertRegisterRequest.ScheduleInfo scheduleInfo : request.getSchedules()) {

            if (!scheduleInfo.getStartAt().isBefore(scheduleInfo.getEndAt())) {
                throw new CustomException(ErrorCode.CONCERT_SCHEDULE_PERIOD_INVALID);
            }

            ConcertScheduleResponse schedule = concertScheduleService.create(
                    userId, savedConcert.getId(), scheduleInfo.getVenueId(),
                    scheduleInfo.getStartAt(), scheduleInfo.getEndAt());

            Venue venue = venueRepository.findById(scheduleInfo.getVenueId())
                    .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

            for (ConcertRegisterRequest.RowAssignInfo info : request.getRowAssigns()) {
                Long gradeId = gradeNameToId.get(info.getGradeName());

                if (gradeId == null) {
                    throw new CustomException(ErrorCode.INVALID_GRADE_NAME);
                }

                concertSeatGradeService.assign(
                        savedConcert.getId(), userId, info.getRowName(), gradeId
                );

                List<Seat> seats = seatRepository
                        .findAllByVenueAndRowName(venue, info.getRowName());

                List<Long> seatIds = seats.stream().map(Seat::getId).toList();

                scheduleSeatService.create(userId, schedule.getId(), gradeId, seatIds);
            }
        }

        return savedConcert.getId();
    }
}
