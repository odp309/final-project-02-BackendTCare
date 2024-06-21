package com.bni.finproajubackend.loader;

import com.bni.finproajubackend.model.bank.Bank;
import com.bni.finproajubackend.model.enumobject.*;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.admin.Role;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.*;
import org.hibernate.sql.ast.tree.expression.Star;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
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
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final TicketResponseTimeRepository ticketResponseTimeRepository;

    public DataLoader(UserRepository userRepository, AdminRepository adminRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, NasabahRepository nasabahRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, BankRepository bankRepository, TicketsRepository ticketsRepository, TicketsHistoryRepository ticketHistoryRepository, TicketFeedbackRepository ticketFeedbackRepository,
                      TicketResponseTimeRepository ticketResponseTimeRepository) {
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
        this.ticketResponseTimeRepository = ticketResponseTimeRepository;
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
        role.setDivisionName("System");
        role.setRoleDescription("Role untuk admin");
        roleRepository.save(role);

        String[] usernames = {"admin", "wpp1", "wpp2", "wpp3", "dgo1", "dgo2", "dgo3", "cxc1", "cxc2", "cxc3"};
        String[] addresses = {"Balige", "Pontianak", "Bumi Serpong Damai", "Pandeglang", "Surabaya", "Cirebon", "Medan", "Lamongan", "Lampung", "Bandung"};
        String[] emails = {"daji18201@gmail.com", "alvin.alamien@gmail.com", "iqbal010698@gmail.com", "ratuannisag@gmail.com", "anandariskiwp@gmail.com", "himawanhidan@gmail.com", "fredrick.theodorus@gmail.com", "alfanmarzaqi@gmail.com", "fajru234@gmail.com", "milyandaaa@gmail.com"};
        String[] firstNames = {"Dimas", "Alvin", "Iqbal", "Ratu", "Kiki", "Himawan", "Fredrick", "Alfan", "Fajru", "Milyanda"};
        String[] lastNames = {"Pangestu", "Dwi", "Naufal", "Gandasari", "Widya", "Yoga", "Pardosi", "Arzaqi", "Ramadhan", "Vania"};
        String[] npp = {"T094459", "T094457", "T094455", "T094451", "T094450", "T094424", "T092574", "T095935", "T094421", "T094423"};
        String[] noHPs = {"082265746357", "082256783458", "082256783478", "082256781478", "081256781478", "081356781478", "083356781478", "082356781478", "082356781448", "082356781848"};
        Gender[] genders = {Gender.Male, Gender.Male, Gender.Male, Gender.Female, Gender.Female, Gender.Male, Gender.Male, Gender.Male, Gender.Male, Gender.Female};


        for (int i = 0; i < Arrays.stream(usernames).count(); i++) {
            User user = new User(usernames[i], passwordEncoder.encode("12345678"));
            userRepository.save(user);

            Admin admin = new Admin();
            admin.setNpp(npp[i]);
            admin.setFirstName(firstNames[i]);
            admin.setLastName(lastNames[i]);
            admin.setEmail(emails[i]);
            admin.setGender(genders[i]);
            admin.setAddress(addresses[i]);
            admin.setNoHP(noHPs[i]);
            admin.setAge(20);
            admin.setRole(role);
            admin.setUser(user);
            adminRepository.save(admin);

            user.setAdmin(admin);
            userRepository.save(user);
        }
    }

    private void loadNasabah() {
        // Cek user yang ada
        if (userRepository.findByUsername("dimas27") != null) {
            return;
        }

        // Data pengguna
        String[] usernames = {"dimas27", "alvin12", "iqbal12", "ratu12", "kiki12", "agoy12", "fredrick12", "alfan12", "fajru12", "mily12"};
        String[] addresses = {"Balige", "Pontianak", "Bumi Serpong Damai", "Pandeglang", "Surabaya", "Cirebon", "Medan", "Lamongan", "Lampung", "Bandung"};
        String[] emails = {"daji18201@gmail.com", "alvin.alamien@gmail.com", "iqbal010698@gmail.com", "ratuannisag@gmail.com", "anandariskiwp@gmail.com", "himawanhidan@gmail.com", "fredrick.theodorus@gmail.com", "alfanmarzaqi@gmail.com", "fajru234@gmail.com", "milyandaaa@gmail.com"};
        String[] firstNames = {"Dimas", "Alvin", "Iqbal", "Ratu", "Kiki", "Himawan", "Fredrick", "Alfan", "Fajru", "Milyanda"};
        String[] lastNames = {"Pangestu", "Dwi", "Naufal", "Gandasari", "Widya", "Yoga", "Pardosi", "Arzaqi", "Ramadhan", "Vania"};
        String[] niks = {"1212784693778572", "1212983547999995", "1212983547992995", "1212983547999965", "1212983547299965", "1212983547295965", "1213983547295965", "1214983547295965", "1214983547295065", "1214983547795065"};
        String[] noHPs = {"082265746357", "082256783458", "082256783478", "082256781478", "081256781478", "081356781478", "083356781478", "082356781478", "082356781448", "082356781848"};
        Gender[] genders = {Gender.Male, Gender.Male, Gender.Male, Gender.Female, Gender.Female, Gender.Male, Gender.Male, Gender.Male, Gender.Male, Gender.Female};

        // Membuat dan menyimpan pengguna dan nasabah
        for (int i = 0; i < usernames.length; i++) {
            User user = new User(usernames[i], passwordEncoder.encode("12345678"));
            userRepository.save(user);

            Nasabah nasabah = new Nasabah();
            nasabah.setAddress(addresses[i]);
            nasabah.setAge(24);
            nasabah.setEmail(emails[i]);
            nasabah.setFirst_name(firstNames[i]);
            nasabah.setGender(genders[i]);
            nasabah.setLast_name(lastNames[i]);
            nasabah.setNik(niks[i]);
            nasabah.setNoHP(noHPs[i]);
            nasabah.setUser(user);

            nasabahRepository.save(nasabah);

            // Membuat dan menyimpan akun untuk setiap nasabah
            for (int j = 0; j < 3; j++) {
                Account account = new Account();
                account.setNasabah(nasabah);
                account.setType("Rekening Tabungan");
                account.setAccountNumber("123456" + (i + 1) + j);
                account.setBalance(1000000L);
                account.setCreatedAt(LocalDateTime.now());
                account.setUpdatedAt(LocalDateTime.now());
                accountRepository.save(account);

                if (i == 0) break;

                // Membuat dan menyimpan transaksi untuk setiap akun
                Bank bank = loadBank();
                int size = 100;
                for (int k = 1; k <= size; k++) {
                    Nasabah recipient = nasabahRepository.findByUsername("dimas27");
                    if (recipient != null) {
                        Account recipient_account = accountRepository.findByNasabah(recipient);
                        Transaction transaction = loadTransaction(k, account, recipient_account, bank, "Transaction " + k, 500000L + k, "Berhasil", TransactionCategories.values()[k % TransactionCategories.values().length]);
                        if (k % 2 == 0) {
                            loadTickets(transaction, k + 1);
                        }
                    }
                }
            }
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

    private Transaction loadTransaction(int size, Account account, Account recipientAccount, Bank bank, String detail, Long amount, String status, TransactionCategories category) {
        LocalDateTime createdAt = generateRandomDateTimeWithin30Days();
        boolean isOutgoing = new Random().nextBoolean();

        Transaction transaction = createTransaction(account, bank, detail, amount, isOutgoing ? account.getBalance() - amount : account.getBalance() + amount, status, category, recipientAccount, isOutgoing, createdAt);

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (size % 2 == 0) {
            Transaction recipientTransaction = createTransaction(recipientAccount, bank, detail, amount, !isOutgoing ? account.getBalance() - amount : account.getBalance() + amount, status, category, account, !isOutgoing, createdAt);
            recipientTransaction.setReferenced_id(savedTransaction.getId());

            Transaction savedRecipientTransaction = transactionRepository.save(recipientTransaction);

            savedTransaction.setReferenced_id(savedRecipientTransaction.getId());
            transactionRepository.save(savedTransaction);
        }

        return savedTransaction;
    }

    private Transaction createTransaction(Account account, Bank bank, String detail, Long amount, Long totalAmount, String status, TransactionCategories category, Account recipientAccount, boolean isOutgoing, LocalDateTime timestamp) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setBank(bank);
        transaction.setDetail(detail);
        transaction.setAmount(amount);
        transaction.setTotal_amount(totalAmount);
        transaction.setStatus(status);
        transaction.setCategory(category);
        transaction.setRecipient_bank(bank);
        transaction.setRecipient_account(recipientAccount);
        transaction.setTransaction_type(isOutgoing ? TransactionType.Out : TransactionType.In);
        transaction.setCreatedAt(timestamp);
        transaction.setUpdatedAt(timestamp);

        return transaction;
    }

    private void loadTickets(Transaction transaction, int size) {
        Admin admin = adminRepository.findByUsername("admin12");
        if (transaction.getTransaction_type() == TransactionType.Out) {
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
                    .ticketNumber(createTicketNumber(transaction, null))
                    .transaction(transaction)
                    .ticketCategory(categories)
                    .ticketStatus(ticketStatus)
                    .admin(admin)
                    .divisionTarget(divisionTarget)
                    .admin(adminRepository.findByDivisionName())
                    .description("Ticket for " + transaction.getDetail())
                    .createdAt(generateRandomDateTime(null))
                    .updatedAt(generateRandomDateTime(null))
                    .build();
            ticketsRepository.save(ticket);

            // Jika status tiket adalah "Selesai", tambahkan masukan untuk tiket
            if (ticketStatus == TicketStatus.Selesai) {
                addTicketResponeTime(ticket);
                if (size % 3 == 0) {
                    addTicketFeedback(ticket, true);
                    addTicketReopened(ticket);
                } else {
                    addTicketFeedback(ticket, false);
                }
            }

            // Load ticket history
            createTicketHistory(ticket, transaction.getCategory());
        }

    }

    private void addTicketResponeTime(Tickets ticket) {
        TicketResponseTime ticketResponseTime = new TicketResponseTime();
        ticketResponseTime.setTicket(ticket);
        ticketResponseTime.setResponseTime(ticket.getTicketCategory().toString().equals("Transfer") ? 0 : (int) (Math.random() * 10));
        ticketResponseTime.setCreatedAt(LocalDateTime.now());
        ticketResponseTime.setUpdatedAt(LocalDateTime.now());
        ticketResponseTimeRepository.save(ticketResponseTime);
    }

    private void addTicketReopened(Tickets ticket) {
        Tickets reOpenedTicket = Tickets.builder()
                .ticketNumber(createTicketNumber(ticket.getTransaction(), ticket))
                .transaction(ticket.getTransaction())
                .ticketCategory(ticket.getTicketCategory())
                .ticketStatus(TicketStatus.Diajukan)
                .divisionTarget(ticket.getDivisionTarget())
                .admin(adminRepository.findByUsername("admin12"))
                .referenceNumber(ticket.getTicketNumber())
                .description("Ticket for " + ticket.getTransaction().getDetail())
                .createdAt(generateRandomDateTime(ticket.getUpdatedAt()))
                .updatedAt(generateRandomDateTime(ticket.getUpdatedAt()))
                .build();
        ticketsRepository.save(reOpenedTicket);

        // Load ticket history
        createTicketHistory(reOpenedTicket, ticket.getTransaction().getCategory());
    }

    private void createTicketHistory(Tickets ticket, TransactionCategories category) {
        Admin admin = adminRepository.findByUsername("admin12");
        String[] statuses = {"transaksi dilakukan", "laporan diajukan", "laporan dalam proses", "laporan selesai diproses", "laporan diterima pelapor"};

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
            ticketHistory.setLevel(i + 1L); // Assuming level 1 for ticket creation
            ticketHistory.setCreatedAt(generateRandomDateTime(ticket.getCreatedAt()));
            ticketHistory.setUpdatedAt(generateRandomDateTime(ticket.getCreatedAt()));

            ticketHistoryRepository.save(ticketHistory);
        }

    }

    public String createTicketNumber(Transaction transaction, Tickets tickets) {
        String categoryCode = switch (transaction.getCategory()) {
            case Transfer -> "TF"; // ID kategori 1 untuk Gagal Transfer
            case TopUp -> "TU"; // ID kategori 2 untuk Gagal Top Up
            case Payment -> "PY"; // ID kategori 3 untuk Gagal Pembayaran
            default -> ""; // ID kategori tidak valid
        };

        LocalDateTime createdAt = transaction.getCreatedAt();
        if (tickets != null) {
            createdAt = LocalDateTime.of(tickets.getCreatedAt().getYear(), tickets.getCreatedAt().getMonthValue(), tickets.getCreatedAt().getDayOfMonth(), 0, 0);
            ;
        }
        String year = String.valueOf(createdAt.getYear()); // Mengambil dua digit terakhir dari tahun
        String month = String.format("%02d", createdAt.getMonthValue());
        String day = String.format("%02d", createdAt.getDayOfMonth());

        String transactionId = String.valueOf(transaction.getId());
        if (tickets != null)
            transactionId = transactionId + "0";
        String baseTicketNumber = categoryCode + year + month + day + transactionId;

        if (baseTicketNumber.length() < 15) {
            int zerosToAdd = 15 - baseTicketNumber.length();
            transactionId = "0".repeat(zerosToAdd) + transactionId;
        }

        return categoryCode + year + month + day + transactionId;
    }

    private void addTicketFeedback(Tickets ticket, boolean isTicketFeedback) {
        // Membuat objek masukan tiket secara acak
        StarRating starRating = StarRating.values()[new Random().nextInt(StarRating.values().length)];
        TicketFeedback feedback = new TicketFeedback();
        feedback.setTicket(ticket);
        if (isTicketFeedback) {
            feedback.setStar_rating(null);
            feedback.setComment("");
        } else {
            feedback.setComment("Terima kasih atas pelayanan yang baik.");
            feedback.setStar_rating(starRating);
        }
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());

        // Menyimpan masukan tiket
        ticketFeedbackRepository.save(feedback);
    }

    private LocalDateTime generateRandomDateTimeWithin30Days() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(30);
        long minDay = start.toEpochSecond(ZoneOffset.UTC);
        long maxDay = now.toEpochSecond(ZoneOffset.UTC);
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
    }

    private LocalDateTime generateRandomDateTime(LocalDateTime minDate) {
        if (minDate == null) {
            minDate = LocalDateTime.of(2024, 1, 1, 0, 0); // Nilai default jika minDate null
        }
        LocalDateTime end = LocalDateTime.now();
        long minDay = minDate.toEpochSecond(ZoneOffset.UTC);
        long maxDay = end.toEpochSecond(ZoneOffset.UTC);
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
    }
}
