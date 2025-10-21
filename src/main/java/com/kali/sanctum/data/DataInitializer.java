package com.kali.sanctum.data;

import com.kali.sanctum.enums.Role;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.model.Mood;
import com.kali.sanctum.model.Permission;
import com.kali.sanctum.model.Reflection;
import com.kali.sanctum.model.ReflectionPrompt;
import com.kali.sanctum.model.Timestamp;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.ReflectionPromptRepository;
import com.kali.sanctum.repository.DailyMoodCheckRepository;
import com.kali.sanctum.repository.MoodRepository;
import com.kali.sanctum.repository.PermissionRepository;
import com.kali.sanctum.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
        private final UserRepository userRepository;
        private final PermissionRepository permissionRepository;
        private final MoodRepository moodRepository;
        private final ReflectionPromptRepository reflectionPromptRepository;
        private final DailyMoodCheckRepository dailyMoodCheckRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
                Set<String> defaultPermissions = Set.of("VIEW_ALL_USER");
                createDefaultPermissionsIfNotExist(defaultPermissions);
                createDefaultMoods();
                createDefaultGuidedReflections();
                createdDefaultSuperAdminIfNotExist();
                createDefaultAdminWithPrivilegesIfNotExist();
                createDefaultStandardUserIfNotExist();
        }

        private void createdDefaultSuperAdminIfNotExist() {
                User superAdmin = User.builder()
                                .username("kali")
                                .email("super_admin@email.com")
                                .password(passwordEncoder.encode("12345678"))
                                .role(Role.SUPER_ADMIN)
                                .isVerified(true)
                                .build();

                User savedAdminUser = userRepository.save(superAdmin);

                // a default list of common mood triggers
                List<String> commonTriggers = List.of(
                                "nature",
                                "coffee",
                                "code",
                                "travel",
                                "food",
                                "work",
                                "finace",
                                "family",
                                "study",
                                "expectation");

                List<Map<String, List<String>>> reflections = List.of(
                                Map.of("veryHappy", List.of(
                                                "Today felt like everything finally clicked into place — I couldn’t stop smiling no matter how small the moment was.",
                                                "I’m overflowing with gratitude; it’s one of those days where life feels light and full of possibilities.")),
                                Map.of("happy", List.of(
                                                "I didn’t have a perfect day, but the little wins were enough to keep my spirits high.",
                                                "I felt genuinely content today, enjoying the simple things without overthinking too much.")),
                                Map.of("neutral", List.of(
                                                "Nothing special happened today, but that’s okay — sometimes calm and steady is what I need.",
                                                "I went through the day without much emotion, just flowing with whatever came my way.")),
                                Map.of("sad", List.of(
                                                "I tried to stay positive, but the weight in my chest made everything feel a bit heavier than usual.",
                                                "It’s one of those days where I can’t quite explain the sadness, but I know it will pass eventually.")),
                                Map.of("angry", List.of(
                                                "I felt frustrated today because things didn’t go the way I expected, and it was hard to stay patient.",
                                                "My temper got the best of me for a while, but at least I recognized it before it took full control.")));

                Random random = new Random();
                for (int i = 0; i < 10; i++) {
                        Set<String> threeWordSummary = new HashSet<>(); // = Set.of("nature", "coffee", "code");

                        // the reason why sometimes we're getting less than 3 words is because of the
                        // Set data structure
                        // it doesn't allow duplicate values
                        // so if we randomly pick the same word more than once, it will only be stored
                        // once in the Set
                        for (int j = 0; j < 3; j++) {
                                int randomIndex = random.nextInt(commonTriggers.size());
                                threeWordSummary.add(commonTriggers.get(randomIndex));
                        }

                        Long moodId = random.nextLong(1, 6); // gen between 1 - 5
                        Mood mood = moodRepository.findById(moodId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Mood not found"));

                        ReflectionPrompt reflectionPrompt = reflectionPromptRepository.findRandomByMoodId(mood.getId());

                        String reflectionEntry = reflections.stream()
                                        .filter(map -> map.containsKey(mood.getName()))
                                        .map(map -> (List<String>) map.get(mood.getName()))
                                        .findFirst()
                                        .map(list -> list.get(random.nextInt(list.size())))
                                        .orElse("No reflection found for mood: " + mood.getName());

                        Instant createdAt = Instant.now().minus(i, ChronoUnit.DAYS);

                        Timestamp timestamp = Timestamp.builder()
                                        .createdAt(createdAt)
                                        .build();

                        System.out.println("b4 index: " + i + "created at raw: " + createdAt + " created at: "
                                        + timestamp.getCreatedAt() + " updated at: " + timestamp.getUpdatedAt());

                        DailyMoodCheck dailyMoodCheck = DailyMoodCheck.builder()
                                        .threeWordSummary(threeWordSummary)
                                        .user(savedAdminUser)
                                        .timestamp(timestamp)
                                        .build();

                        Reflection reflection = Reflection.builder()
                                        .entry(reflectionEntry)
                                        .reflectionPrompt(reflectionPrompt)
                                        .dailyMoodCheck(dailyMoodCheck)
                                        .build();

                        dailyMoodCheck.setReflection(reflection);

                        dailyMoodCheckRepository.save(dailyMoodCheck);
                        System.out.println("after index: " + i + " created at: "
                                        + dailyMoodCheck.getTimestamp().getCreatedAt() + " updated at: "
                                        + dailyMoodCheck.getTimestamp().getUpdatedAt());
                }
        }

        private void createDefaultAdminWithPrivilegesIfNotExist() {
                Permission adminDefaultPermissions = permissionRepository.findByName("VIEW_ALL_USER")
                                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

                User admin = User.builder()
                                .username("kaii.lii")
                                .email("admin@email.com")
                                .password(passwordEncoder.encode("12345678"))
                                .role(Role.ADMIN)
                                .permissions(Set.of(adminDefaultPermissions))
                                .isVerified(true)
                                .build();

                userRepository.save(admin);
        }

        private void createDefaultStandardUserIfNotExist() {
                User standardUser = User.builder()
                                .username("kali")
                                .email("kali@gmail.com")
                                .password(passwordEncoder.encode("12345678"))
                                .role(Role.USER)
                                .isVerified(true)
                                .build();

                userRepository.save(standardUser);
        }

        private void createDefaultPermissionsIfNotExist(Set<String> permissions) {
                permissions.stream()
                                .filter(permission -> permissionRepository.findByName(permission).isEmpty())
                                .map(permission -> Permission.builder()
                                                .name(permission)
                                                .build())
                                .forEach(permissionRepository::save);

        }

        private void createDefaultMoods() {
                List<Map<String, Object>> moodData = List.of(
                                Map.of("name", "veryHappy", "color", 0xFF, "icon", "\uD83D\uDE04"),
                                Map.of("name", "happy", "color", 0xFF, "icon", "\uD83D\uDE42"),
                                Map.of("name", "neutral", "color", 0xFF, "icon", "\uD83D\uDE10"),
                                Map.of("name", "sad", "color", 0xFF, "icon", "\uD83D\uDE22"),
                                Map.of("name", "angry", "color", 0xFF, "icon", "\uD83D\uDE21"));

                List<Mood> moods = moodData.stream()
                                .map(data -> Mood.builder()
                                                .name((String) data.get("name")) // type cast the value
                                                .color((int) data.get("color"))
                                                .icon((String) data.get("icon"))
                                                .build())
                                .toList();

                moodRepository.saveAll(moods);
        }

        private void createDefaultGuidedReflections() {
                List<Mood> moods = moodRepository.findAll();

                Map<String, List<String>> reflectionsMap = Map.of(
                                "veryHappy", List.of(
                                                "What's one thing that made you smile today?",
                                                "Describe a moment today that brought you joy.",
                                                "Who or what brightened your day?"),
                                "happy", List.of(
                                                "What's been weighing on your heart today?",
                                                "Describe something you wish had gone differently.",
                                                "What's one comforting thing you can tell yourself right now?"),
                                "neutral", List.of(
                                                "What was the most notable moment of your day?",
                                                "What's one thing you appreciated, even if small?",
                                                "If your day had a color, what would it be?"),
                                "sad", List.of(
                                                "What's been weighing on your heart today?",
                                                "Describe something you wish had gone differently.",
                                                "What's one comforting thing you can tell yourself right now?"),
                                "angry", List.of(
                                                "What triggered your frustration today?",
                                                "Describe what happened before you started feeling this way.",
                                                "What's one way you can release this tension?"));

                List<ReflectionPrompt> reflectionPrompts = moods.stream()
                                .flatMap(mood -> reflectionsMap.getOrDefault(mood.getName(), List.of())
                                                .stream()
                                                .map(question -> ReflectionPrompt.builder()
                                                                .question(question)
                                                                .mood(mood)
                                                                .build()))
                                .toList();

                reflectionPromptRepository.saveAll(reflectionPrompts);
        }
}
