package com.kali.sanctum.dto.response;

import java.util.List;

import com.kali.sanctum.interfaces.CommonTrigger;
import com.kali.sanctum.interfaces.MoodBubble;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMoodProfileDto {
    private String username;
    private List<MoodBubble> recentMoodDistribution;
    private List<CommonTrigger> commonTriggers;
    private String lastReflectionSummary;
}
