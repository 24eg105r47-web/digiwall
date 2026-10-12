package com.mansi.wallet_system.config;

import com.mansi.wallet_system.entity.User;
import com.mansi.wallet_system.entity.Wallet;
import com.mansi.wallet_system.repository.UserRepository;
import com.mansi.wallet_system.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           WalletRepository walletRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@wallet.com")) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@wallet.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setBalance(new BigDecimal("100000.00"));
            User savedAdmin = userRepository.save(admin);

            Wallet adminWallet = new Wallet();
            adminWallet.setUserId(savedAdmin.getId());
            adminWallet.setOwnerName(savedAdmin.getName());
            adminWallet.setBalance(new BigDecimal("100000.00"));
            walletRepository.save(adminWallet);

            logger.info("Initialized default administrator: admin@wallet.com / admin123");
        }
    }
}
