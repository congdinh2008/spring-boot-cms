package com.congdinh.cms.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.congdinh.cms.constants.Constants;
import com.congdinh.cms.entities.Category;
import com.congdinh.cms.entities.Role;
import com.congdinh.cms.entities.User;
import com.congdinh.cms.enums.UserStatus;
import com.congdinh.cms.repositories.CategoryRepository;
import com.congdinh.cms.repositories.RoleRepository;
import com.congdinh.cms.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Database initializer that runs on application startup.
 * Creates default roles, users, and categories if they don't exist.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            initRoles();
            initAdminUser();
            initCategories();
        };
    }

    /**
     * Initialize default roles: ROLE_ADMIN and ROLE_REPORTER
     */
    private void initRoles() {
        // Create ROLE_ADMIN if not exists
        if (!roleRepository.existsByName(Constants.ROLE_ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(Constants.ROLE_ADMIN);
            adminRole.setDescription("Administrator with full access");
            roleRepository.save(adminRole);
            log.info("Created default role: {}", Constants.ROLE_ADMIN);
        }

        // Create ROLE_REPORTER if not exists
        if (!roleRepository.existsByName(Constants.ROLE_REPORTER)) {
            Role reporterRole = new Role();
            reporterRole.setName(Constants.ROLE_REPORTER);
            reporterRole.setDescription("Reporter who can create and manage own news articles");
            roleRepository.save(reporterRole);
            log.info("Created default role: {}", Constants.ROLE_REPORTER);
        }
    }

    /**
     * Initialize default admin user for testing
     */
    private void initAdminUser() {
        String adminUsername = "admin";
        String reporterUsername = "reporter";

        // Create admin user if not exists
        if (!userRepository.existsByUsername(adminUsername)) {
            Role adminRole = roleRepository.findByName(Constants.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail("admin@domain.com");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            admin.setStatus(UserStatus.ACTIVE);
            admin.setRoles(Set.of(adminRole));
            admin.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(admin);
            log.info("Created default admin user: {} / Admin@1234", adminUsername);
        }

        // Create reporter user if not exists
        if (!userRepository.existsByUsername(reporterUsername)) {
            Role reporterRole = roleRepository.findByName(Constants.ROLE_REPORTER)
                    .orElseThrow(() -> new RuntimeException("Reporter role not found"));

            User reporter = new User();
            reporter.setUsername(reporterUsername);
            reporter.setEmail("reporter@domain.com");
            reporter.setPassword(passwordEncoder.encode("Reporter@1234"));
            reporter.setStatus(UserStatus.ACTIVE);
            reporter.setRoles(Set.of(reporterRole));
            reporter.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(reporter);
            log.info("Created default reporter user: {} / Reporter@1234", reporterUsername);
        }
    }

    /**
     * Initialize default categories for the CMS
     */
    private void initCategories() {
        List<String[]> defaultCategories = List.of(
            new String[]{"Tin tức", "tin-tuc"},
            new String[]{"Thể thao", "the-thao"},
            new String[]{"Công nghệ", "cong-nghe"},
            new String[]{"Giải trí", "giai-tri"},
            new String[]{"Kinh doanh", "kinh-doanh"}
        );

        for (String[] categoryData : defaultCategories) {
            String name = categoryData[0];
            String slug = categoryData[1];

            if (!categoryRepository.existsBySlug(slug)) {
                Category category = new Category();
                category.setName(name);
                category.setSlug(slug);
                categoryRepository.save(category);
                log.info("Created default category: {}", name);
            }
        }
    }
}
