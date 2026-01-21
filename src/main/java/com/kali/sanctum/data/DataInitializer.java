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
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
        private final UserRepository userRepository;
        private final PermissionRepository permissionRepository;
        private final MoodRepository moodRepository;
        private final ReflectionPromptRepository reflectionPromptRepository;
        private final DailyMoodCheckRepository dailyMoodCheckRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
                createPermissions(Set.of("VIEW_ALL_USER"));
                createMoods();
                createGuidedReflections();
                createdSuperAdmin();
                createAdminWithPrivileges();
                createStandardUser();
        }

        private void createPermissions(Set<String> permissions) {
                permissions.forEach(name -> permissionRepository.findByName(name)
                                .or(() -> Optional.of(
                                                permissionRepository.save(Permission.builder().name(name).build()))));

                permissions.stream()
                                .filter(permission -> permissionRepository.findByName(permission).isEmpty())
                                .map(permission -> Permission.builder()
                                                .name(permission)
                                                .build())
                                .forEach(permissionRepository::save);

        }

        private void createMoods() {
                if (moodRepository.count() > 0)
                        return;

                List<Map<String, Object>> moodData = List.of(
                                Map.of("name", "very happy", "color", 0xFF, "icon", "\uD83D\uDE04"),
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

        private void createGuidedReflections() {
                if (reflectionPromptRepository.count() > 0)
                        return;

                Map<String, List<String>> reflectionsMap = Map.of(
                                "very happy", List.of(
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

                List<Mood> moods = moodRepository.findAll();

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

        private void createdSuperAdmin() {
                if (userRepository.existsByEmail("super_admin@email.com"))
                        return;

                User superAdmin = User.builder()
                                .username("kali")
                                .email("super_admin@email.com")
                                .password(passwordEncoder.encode("12345678"))
                                .role(Role.SUPER_ADMIN)
                                .isVerified(true)
                                .build();

                User savedAdminUser = userRepository.save(superAdmin);
                seedDailyMoodChecks(savedAdminUser);
        }

        private void createAdminWithPrivileges() {
                if (userRepository.existsByEmail("admin@email.com"))
                        return;

                Permission adminDefaultPermissions = permissionRepository.findByName("VIEW_ALL_USER")
                                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

                userRepository.save(User.builder()
                                .username("kaii.lii")
                                .email("admin@email.com")
                                .password(passwordEncoder.encode("12345678"))
                                .role(Role.ADMIN)
                                .permissions(Set.of(adminDefaultPermissions))
                                .isVerified(true)
                                .build());
        }

        private void createStandardUser() {
                if (userRepository.existsByEmail("kali@gmail.com"))
                        return;

                userRepository.save(User.builder()
                                .username("kali")
                                .email("kali@gmail.com")
                                .password(passwordEncoder.encode("12345678"))
                                .role(Role.USER)
                                .isVerified(true)
                                .build());
        }

        private void seedDailyMoodChecks(User user) {
                List<Mood> moods = moodRepository.findAll();
                Random random = new Random();

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

                for (int i = 0; i < 10; i++) {
                        Mood mood = moods.get(random.nextInt(moods.size()));
                        ReflectionPrompt reflectionPrompt = reflectionPromptRepository.findRandomByMoodId(mood.getId());

                        Set<String> summary = new HashSet<>();
                        while (summary.size() < 3)
                                summary.add(commonTriggers.get(random.nextInt(commonTriggers.size())));

                        Reflection reflection = Reflection.builder()
                                        .entry("Reflection for mood: " + mood.getName())
                                        .reflectionPrompt(reflectionPrompt)
                                        .build();

                        DailyMoodCheck dailyMoodCheck = DailyMoodCheck.builder()
                                        .user(user)
                                        .timestamp(Timestamp.builder()
                                                        .createdAt(Instant.now().minus(i, ChronoUnit.DAYS)).build())
                                        .threeWordSummary(summary)
                                        .reflection(reflection)
                                        .build();

                        reflection.setDailyMoodCheck(dailyMoodCheck);
                        dailyMoodCheckRepository.save(dailyMoodCheck);
                }
        }
}
