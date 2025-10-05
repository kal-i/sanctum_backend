package com.kali.sanctum.service.dailymoodcheck;

import com.kali.sanctum.dto.request.LogDailyMoodCheckRequest;
import com.kali.sanctum.dto.response.DailyMoodCheckDto;
import com.kali.sanctum.enums.DateRange;
import com.kali.sanctum.interfaces.CommonTrigger;
import com.kali.sanctum.interfaces.MoodBubble;
import com.kali.sanctum.model.DailyMoodCheck;

import java.util.List;

import org.springframework.data.domain.Page;

public interface IDailyMoodCheckService {
    DailyMoodCheck getById(Long id);
    Page<DailyMoodCheck> getUserDailyMoodCheck(int page, int size);
    Page<DailyMoodCheckDto> getUserDailyMoodCheckDto(int page, int size);
    DailyMoodCheck logDailyMoodCheck(LogDailyMoodCheckRequest request);
    List<CommonTrigger> getCommonDailyMoodTriggers(int limit);
    List<MoodBubble> getMoodBubbles(DateRange dateRange);
    DailyMoodCheckDto convertToDto(DailyMoodCheck dailyMoodCheck);
}
