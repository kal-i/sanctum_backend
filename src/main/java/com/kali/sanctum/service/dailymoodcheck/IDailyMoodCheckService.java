package com.kali.sanctum.service.dailymoodcheck;

import com.kali.sanctum.dto.request.CreateDailyMoodCheckEntryRequest;
import com.kali.sanctum.model.DailyMoodCheck;
import org.springframework.data.domain.Page;

public interface IDailyMoodCheckService {
    DailyMoodCheck getById(Long id);
    Page<DailyMoodCheck> getUserDailyMoodCheck(int page, int size);
    DailyMoodCheck logDailyMoodCheck(CreateDailyMoodCheckEntryRequest request);
}
