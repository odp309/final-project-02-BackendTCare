package com.bni.finproajubackend.loader;

import com.bni.finproajubackend.model.bank.Bank;
import com.bni.finproajubackend.model.enumobject.*;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
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
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    private final TicketFeedbackRepository ticketFeedbackRepository;

    public DataLoader(UserRepository userRepository, AdminRepository adminRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, NasabahRepository nasabahRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, BankRepository bankRepository, TicketsRepository ticketsRepository, TicketsHistoryRepository ticketHistoryRepository, TicketFeedbackRepository ticketFeedbackRepository) {
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
        this.ticketFeedbackRepository = ticketFeedbackRepository;
    }

    @Bean
    CommandLineRunner loadData(UserRepository userRepository) {
        return args -> {
            loadAdmin();
            loadNasabah();
        };
    }

    private void loadAdmin() {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("admin12"));
        if (existingUser.isPresent()) {
            return;
        }

        Role role = new Role();
        role.setRoleName("admin");
        role.setRoleDescription("Role untuk admin");
        roleRepository.save(role);

        User user = new User("admin12", passwordEncoder.encode("12345678"));
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
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("dimas27"));
        if (existingUser.isPresent()) {
            return;
        }

        User user = new User("dimas27", passwordEncoder.encode("12345678"));
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
        transaction.setTransaction_type(new Random().nextBoolean() ? TransactionType.In : TransactionType.Out);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return transaction;
    }

    private void loadTickets(Transaction transaction) {
        // Mengatur kategori tiket berdasarkan kategori transaksi
        TicketCategories categories = switch (transaction.getCategory()) {
            case Transfer -> TicketCategories.Transfer;
            case TopUp -> TicketCategories.TopUp;
            case Payment -> TicketCategories.Payment;
        };

        // Mengatur target divisi berdasarkan kategori transaksi
        DivisionTarget divisionTarget = switch (transaction.getCategory()) {
            case Transfer -> DivisionTarget.CXC;
            case TopUp -> DivisionTarget.WPP;
            case Payment -> DivisionTarget.DGO;
        };

        // Mengatur status tiket secara acak
        TicketStatus ticketStatus = switch (new Random().nextInt(3)) {
            case 0 -> TicketStatus.Diajukan;
            case 1 -> TicketStatus.DalamProses;
            case 2 -> TicketStatus.Selesai;
            default -> TicketStatus.Diajukan; // Nilai default jika terjadi kesalahan
        };

        // Membuat objek tiket baru
        Tickets ticket = Tickets.builder()
                .ticketNumber(createTicketNumber(transaction))
                .transaction(transaction)
                .ticketCategory(categories)
                .ticketStatus(ticketStatus)
                .divisionTarget(divisionTarget)
                .description("Ticket for " + transaction.getDetail())
                .createdAt(generateRandomDateTime(null))
                .updatedAt(generateRandomDateTime(null))
                .build();
        ticketsRepository.save(ticket);

        // Jika status tiket adalah "Selesai", tambahkan masukan untuk tiket
        if (ticketStatus == TicketStatus.Selesai) {
            addTicketFeedback(ticket);
        }

        // Load ticket history
        createTicketHistory(ticket, transaction.getCategory());
    }

    private void createTicketHistory(Tickets ticket, TransactionCategories category) {
        Admin admin = adminRepository.findByUsername("admin12");
        String[] statuses = {"Transaksi Dibuat", "Laporan Diajukan", "Laporan Dalam Proses", "Laporan Selesai Diproses", "Laporan Diterima Pelapor"};

        int counter = switch (ticket.getTicketStatus()) {
            case Diajukan -> 2;
            case DalamProses -> 3;
            case Selesai -> 5;
        };

        for (int i = 0; i < counter; i++) {
            TicketHistory ticketHistory = new TicketHistory();
            ticketHistory.setTicket(ticket);
            ticketHistory.setAdmin(admin);
            ticketHistory.setDescription(statuses[i]);
            ticketHistory.setDate(new Date());
            ticketHistory.setLevel(i+1L); // Assuming level 1 for ticket creation
            ticketHistory.setCreatedAt(generateRandomDateTime(ticket.getCreatedAt()));
            ticketHistory.setUpdatedAt(generateRandomDateTime(ticket.getCreatedAt()));

            ticketHistoryRepository.save(ticketHistory);
        }

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

    private void addTicketFeedback(Tickets ticket) {
        // Membuat objek masukan tiket secara acak
        StarRating starRating = StarRating.values()[new Random().nextInt(StarRating.values().length)];
        TicketFeedback feedback = new TicketFeedback();
        feedback.setTicket(ticket);
        feedback.setStar_rating(starRating);
        feedback.setComment("Terima kasih atas pelayanan yang baik.");

        // Menyimpan masukan tiket
        ticketFeedbackRepository.save(feedback);
    }

    private LocalDateTime generateRandomDateTime(LocalDateTime minDate) {
        if (minDate == null) {
            minDate = LocalDateTime.of(2022, 1, 1, 0, 0); // Nilai default jika minDate null
        }
        LocalDateTime end = LocalDateTime.now();
        long minDay = minDate.toEpochSecond(ZoneOffset.UTC);
        long maxDay = end.toEpochSecond(ZoneOffset.UTC);
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
    }

}
