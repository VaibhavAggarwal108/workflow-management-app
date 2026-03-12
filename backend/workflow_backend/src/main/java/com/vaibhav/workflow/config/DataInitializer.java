package com.vaibhav.workflow.config;

import com.vaibhav.workflow.entity.Role;
import com.vaibhav.workflow.entity.User;
import com.vaibhav.workflow.enums.RoleName;
import com.vaibhav.workflow.repository.RoleRepository;
import com.vaibhav.workflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMIN)));

        Role managerRole = roleRepository.findByName(RoleName.MANAGER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.MANAGER)));

        Role employeeRole = roleRepository.findByName(RoleName.EMPLOYEE)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.EMPLOYEE)));

        if (userRepository.findByEmail("admin@workflow.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@workflow.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("manager@workflow.com").isEmpty()) {
            User manager = new User();
            manager.setFullName("Workflow Manager");
            manager.setEmail("manager@workflow.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRole(managerRole);
            userRepository.save(manager);
        }

        if (userRepository.findByEmail("employee@workflow.com").isEmpty()) {
            User employee = new User();
            employee.setFullName("Workflow Employee");
            employee.setEmail("employee@workflow.com");
            employee.setPassword(passwordEncoder.encode("employee123"));
            employee.setRole(employeeRole);
            userRepository.save(employee);
        }
    }
}