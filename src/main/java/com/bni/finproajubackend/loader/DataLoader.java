package com.bni.finproajubackend.loader;
import com.bni.finproajubackend.model.enumobject.Gender;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.admin.Role;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Component
public class DataLoader {

    private final UserRepository userRepository;
    private final NasabahRepository nasabahRepository;
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
//    private final TransactionRepository transactionRepository;
//    private final TicketsRepository ticketsRepository;

    public DataLoader(UserRepository userRepository, AdminRepository adminRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, NasabahRepository nasabahRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, TicketsRepository ticketsRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.nasabahRepository = nasabahRepository;
        this.accountRepository = accountRepository;
//        this.transactionRepository = transactionRepository;
//        this.ticketsRepository = ticketsRepository;
    }

    @Bean
    CommandLineRunner loadData(UserRepository userRepository) {
        return args -> {
            loadAdmin();
            loadNasabah();
            loadAccount();
            //loadTransaction();
            //loadTickets();
        };
    }

    private void loadAdmin(){
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("admin"));
        if (existingUser.isPresent()) {
            return;
        }

        // Menambahkan role
        Role role = new Role();
        role.setRoleName("admin");
        role.setRoleDescription("Role untuk admin");
        roleRepository.save(role);

        // Menambahkan user
        User user = new User("admin", passwordEncoder.encode("123456"));
        userRepository.save(user);

        // Menambahkan admin
        Admin admin = new Admin();
        admin.setNpp("T094459");
        admin.setFirstName("aju");
        admin.setLastName("agoy");
        admin.setEmail("aju@gmail.com");
        admin.setGender(Gender.Male);
        admin.setAddress("Jakarta");
        admin.setNoHP("082526365969");
        admin.setAge(20);
        admin.setRole(role);
        admin.setUser(user);
        adminRepository.save(admin);

        user.setAdmin(admin);
        userRepository.save(user);

    }

    private void loadNasabah(){
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("dimas"));
        if (existingUser.isPresent()) {
            return;
        }

        // Menambahkan user
        User user = new User("dimas", passwordEncoder.encode("123456"));
        userRepository.save(user);

        // Menambahkan nasabah
        Nasabah nasabah = new Nasabah();
        nasabah.setAddress("Balige");
        nasabah.setAge(24);
        nasabah.setEmail("daji18201@gmail.com");
        nasabah.setFirstName("Dimas");
        nasabah.setGender(Gender.Male);
        nasabah.setLastName("Pangestu");
        nasabah.setNik("1212784693778572");
        nasabah.setNoHP("082265746357");
        nasabah.setUser(user);

        nasabahRepository.save(nasabah);

    }

    private void loadAccount(){

        Optional<Nasabah> existingNasabah = nasabahRepository.findByEmail("daji18201@gmail.com");
        if (existingNasabah.isPresent()) {
            return;
        }
        Nasabah nasabah = existingNasabah.get();

        // Menambahkan account
        Account account = new Account();
        account.setNasabah(nasabah);
        account.setType("Rekening Tabungan");
        account.setAccount_number("1234567890");
        account.setBalance(1000000L); // Initial balance, can be any value
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);
    }

//    private void loadTransaction(){
//
//        Optional<Account> existingAccount = accountRepository.findByAccount("1234567890");
//        if (existingAccount.isPresent()){
//            Account account = existingAccount.get();
//
//            Transaction transaction = new Transaction();
//            transaction.setAccount(account);
//            transaction.setDetail("Top Up Dana");
//            transaction.setAmount(500000L);
//            transaction.setStatus("Gagal");
//            transaction.setUpdatedAt(LocalDateTime.now());
//            transaction.setCreatedAt(LocalDateTime.now());
//        }
//    }

    private void loadTickets(){
//        Optional<Transaction> existingTransaction = transactionRepository.
    }


}
