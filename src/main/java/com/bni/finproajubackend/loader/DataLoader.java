package com.bni.finproajubackend.loader;

import com.bni.finproajubackend.model.bank.Bank;
import com.bni.finproajubackend.model.enumobject.*;
import com.bni.finproajubackend.model.ticket.TicketHistory;
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
    private final TransactionRepository transactionRepository;
    private final BankRepository bankRepository;
    private final TicketsRepository ticketsRepository;
    private final TicketsHistoryRepository ticketHistoryRepository;

    public DataLoader(UserRepository userRepository, AdminRepository adminRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, NasabahRepository nasabahRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, BankRepository bankRepository, TicketsRepository ticketsRepository, TicketsHistoryRepository ticketHistoryRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.nasabahRepository = nasabahRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.bankRepository = bankRepository;
        this.ticketsRepository = ticketsRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    @Bean
    CommandLineRunner loadData(UserRepository userRepository) {
        return args -> {
            loadAdmin();
            loadNasabah();
        };
    }

    private void loadAdmin() {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("admin"));
        if (existingUser.isPresent()) {
            return;
        }

        Role role = new Role();
        role.setRoleName("admin");
        role.setRoleDescription("Role untuk admin");
        roleRepository.save(role);

        User user = new User("admin", passwordEncoder.encode("123456"));
        userRepository.save(user);

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

    private void loadNasabah() {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("dimas"));
        if (existingUser.isPresent()) {
            return;
        }

        User user = new User("dimas", passwordEncoder.encode("123456"));
        userRepository.save(user);

        Nasabah nasabah = new Nasabah();
        nasabah.setAddress("Balige");
        nasabah.setAge(24);
        nasabah.setEmail("daji18201@gmail.com");
        nasabah.setFirst_name("Dimas");
        nasabah.setGender(Gender.Male);
        nasabah.setLast_name("Pangestu");
        nasabah.setNik("1212784693778572");
        nasabah.setNoHP("082265746357");
        nasabah.setUser(user);

        nasabahRepository.save(nasabah);

        Account account = new Account();
        account.setNasabah(nasabah);
        account.setType("Rekening Tabungan");
        account.setAccount_number("1234567890");
        account.setBalance(1000000L);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);

        Bank bank = loadBank();

        for (int i = 1; i <= 50; i++) {
            Transaction transaction = loadTransaction(account, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            loadTickets(transaction);
        }
    }

    private Bank loadBank() {
        Optional<Bank> existingBank = bankRepository.findById(1L);
        if (existingBank.isPresent()) {
            return existingBank.get();
        }

        Bank bank = new Bank();
        bank.setCode("009");
        bank.setBankName("Bank Negara Indonesia");
        bankRepository.save(bank);

        return bank;
    }

    private Transaction loadTransaction(Account account, Bank bank, String detail, Long amount, String status, TransactionCategories category) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setBank(bank);
        transaction.setDetail(detail);
        transaction.setAmount(amount);
        transaction.setStatus(status);
        transaction.setCategory(category);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return transaction;
    }

    private void loadTickets(Transaction transaction) {
        TicketCategories categories = transaction.getCategory() == TransactionCategories.Payment ? TicketCategories.Gagal_Payment
                : transaction.getCategory() == TransactionCategories.TopUp ? TicketCategories.Gagal_TopUp
                : TicketCategories.Gagal_Transfer;

        DivisionTarget divisionTarget = transaction.getCategory() == TransactionCategories.Payment ? DivisionTarget.DGO
                : transaction.getCategory() == TransactionCategories.TopUp ? DivisionTarget.WPP
                : DivisionTarget.CXC;

        Tickets ticket = Tickets.builder()
                .ticketNumber(createTicketNumber(transaction))
                .transaction(transaction)
                .ticketCategory(categories)
                .ticketStatus(TicketStatus.Diajukan)
                .divisionTarget(divisionTarget)
                .description("Ticket for " + transaction.getDetail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ticketsRepository.save(ticket);

        // Load ticket history
        createTicketHistory(ticket);
    }

    private void createTicketHistory(Tickets ticket) {
        Admin admin = adminRepository.findByUsername("admin");

        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDescription("Ticket created with status " + ticket.getTicketStatus());
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(1L); // Assuming level 1 for ticket creation
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());

        ticketHistoryRepository.save(ticketHistory);
    }

    public String createTicketNumber(Transaction transaction) {
        String categoryCode = switch (transaction.getCategory()) {
            case Transfer -> "TF"; // ID kategori 1 untuk Gagal Transfer
            case TopUp -> "TU"; // ID kategori 2 untuk Gagal Top Up
            case Payment -> "PY"; // ID kategori 3 untuk Gagal Pembayaran
            default -> ""; // ID kategori tidak valid
        };

        LocalDateTime createdAt = transaction.getCreatedAt();
        String year = String.valueOf(createdAt.getYear()); // Mengambil dua digit terakhir dari tahun
        String month = String.format("%02d", createdAt.getMonthValue());
        String day = String.format("%02d", createdAt.getDayOfMonth());

        String transactionId = String.valueOf(transaction.getId());
        String baseTicketNumber = categoryCode + year + month + day + transactionId;

        if (baseTicketNumber.length() < 15) {
            int zerosToAdd = 15 - baseTicketNumber.length();
            transactionId = "0".repeat(zerosToAdd) + transactionId;
        }

        return categoryCode + year + month + day + transactionId;
    }
}
