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
        User user2 = new User("alvin12", passwordEncoder.encode("12345678"));
        User user3 = new User("iqbal12", passwordEncoder.encode("12345678"));
        User user4 = new User("ratu12", passwordEncoder.encode("12345678"));
        User user5 = new User("kiki12", passwordEncoder.encode("12345678"));
        User user6 = new User("agoy12", passwordEncoder.encode("12345678"));
        User user7 = new User("fredrick12", passwordEncoder.encode("12345678"));
        User user8 = new User("alfan12", passwordEncoder.encode("12345678"));
        User user9 = new User("fajru12", passwordEncoder.encode("12345678"));
        User user10 = new User("mily12", passwordEncoder.encode("12345678"));

        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);
        userRepository.save(user10);

        Nasabah nasabah1 = new Nasabah();
        nasabah1.setAddress("Balige");
        nasabah1.setAge(24);
        nasabah1.setEmail("daji18201@gmail.com");
        nasabah1.setFirst_name("Dimas");
        nasabah1.setGender(Gender.Male);
        nasabah1.setLast_name("Pangestu");
        nasabah1.setNik("1212784693778572");
        nasabah1.setNoHP("082265746357");
        nasabah1.setUser(user);

        Nasabah nasabah2 = new Nasabah();
        nasabah2.setAddress("Pontianak");
        nasabah2.setAge(24);
        nasabah2.setEmail("alvin.alamien@gmail.com");
        nasabah2.setFirst_name("Alvin");
        nasabah2.setGender(Gender.Male);
        nasabah2.setLast_name("Dwi");
        nasabah2.setNik("1212983547999995");
        nasabah2.setNoHP("082256783458");
        nasabah2.setUser(user2);

        Nasabah nasabah3 = new Nasabah();
        nasabah3.setAddress("Bumi Serpong Damai");
        nasabah3.setAge(24);
        nasabah3.setEmail("iqbal010698@gmail.com");
        nasabah3.setFirst_name("Iqbal");
        nasabah3.setGender(Gender.Male);
        nasabah3.setLast_name("Naufal");
        nasabah3.setNik("1212983547992995");
        nasabah3.setNoHP("082256783478");
        nasabah3.setUser(user3);

        Nasabah nasabah4 = new Nasabah();
        nasabah4.setAddress("Pandeglang");
        nasabah4.setAge(24);
        nasabah4.setEmail("ratuannisag@gmail.com");
        nasabah4.setFirst_name("Ratu");
        nasabah4.setGender(Gender.Female);
        nasabah4.setLast_name("Gandasari");
        nasabah4.setNik("1212983547999965");
        nasabah4.setNoHP("082256781478");
        nasabah4.setUser(user4);

        Nasabah nasabah5 = new Nasabah();
        nasabah5.setAddress("Surabaya");
        nasabah5.setAge(24);
        nasabah5.setEmail("anandariskiwp@gmail.com");
        nasabah5.setFirst_name("Kiki");
        nasabah5.setGender(Gender.Female);
        nasabah5.setLast_name("Widya");
        nasabah5.setNik("1212983547299965");
        nasabah5.setNoHP("081256781478");
        nasabah5.setUser(user5);

        Nasabah nasabah6 = new Nasabah();
        nasabah6.setAddress("Cirebon");
        nasabah6.setAge(24);
        nasabah6.setEmail("himawanhidan@gmail.com");
        nasabah6.setFirst_name("Himawan");
        nasabah6.setGender(Gender.Male);
        nasabah6.setLast_name("Yoga");
        nasabah6.setNik("1212983547295965");
        nasabah6.setNoHP("081356781478");
        nasabah6.setUser(user6);

        Nasabah nasabah7 = new Nasabah();
        nasabah7.setAddress("Medan");
        nasabah7.setAge(24);
        nasabah7.setEmail("fredrick.theodorus@gmail.com");
        nasabah7.setFirst_name("Fredrick");
        nasabah7.setGender(Gender.Male);
        nasabah7.setLast_name("Pardosi");
        nasabah7.setNik("1213983547295965");
        nasabah7.setNoHP("083356781478");
        nasabah7.setUser(user7);

        Nasabah nasabah8 = new Nasabah();
        nasabah8.setAddress("Lamongan");
        nasabah8.setAge(24);
        nasabah8.setEmail("alfanmarzaqi@gmail.com");
        nasabah8.setFirst_name("Alfan");
        nasabah8.setGender(Gender.Male);
        nasabah8.setLast_name("Arzaqi");
        nasabah8.setNik("1214983547295965");
        nasabah8.setNoHP("082356781478");
        nasabah8.setUser(user8);

        Nasabah nasabah9 = new Nasabah();
        nasabah9.setAddress("Lampung");
        nasabah9.setAge(24);
        nasabah9.setEmail("fajru234@gmail.com");
        nasabah9.setFirst_name("Fajru");
        nasabah9.setGender(Gender.Male);
        nasabah9.setLast_name("Ramadhan");
        nasabah9.setNik("1214983547295065");
        nasabah9.setNoHP("082356781448");
        nasabah9.setUser(user9);

        Nasabah nasabah10 = new Nasabah();
        nasabah10.setAddress("Bandung");
        nasabah10.setAge(24);
        nasabah10.setEmail("milyandaaa@gmail.com");
        nasabah10.setFirst_name("Milyanda");
        nasabah10.setGender(Gender.Female);
        nasabah10.setLast_name("Vania");
        nasabah10.setNik("1214983547795065");
        nasabah10.setNoHP("082356781848");
        nasabah10.setUser(user10);

        nasabahRepository.save(nasabah1);
        nasabahRepository.save(nasabah2);
        nasabahRepository.save(nasabah3);
        nasabahRepository.save(nasabah4);
        nasabahRepository.save(nasabah5);
        nasabahRepository.save(nasabah6);
        nasabahRepository.save(nasabah7);
        nasabahRepository.save(nasabah8);
        nasabahRepository.save(nasabah9);
        nasabahRepository.save(nasabah10);

        Account account1 = new Account();
        account1.setNasabah(nasabah1);
        account1.setType("Rekening Tabungan");
        account1.setAccountNumber("12345677890");
        account1.setBalance(1000000L);
        account1.setCreatedAt(LocalDateTime.now());
        account1.setUpdatedAt(LocalDateTime.now());

        Account account2 = new Account();
        account2.setNasabah(nasabah1);
        account2.setType("Rekening Tabungan");
        account2.setAccountNumber("12346567790");
        account2.setBalance(1000000L);
        account2.setCreatedAt(LocalDateTime.now());
        account2.setUpdatedAt(LocalDateTime.now());

        Account account3 = new Account();
        account3.setNasabah(nasabah1);
        account3.setType("Rekening Tabungan");
        account3.setAccountNumber("14232567790");
        account3.setBalance(1000000L);
        account3.setCreatedAt(LocalDateTime.now());
        account3.setUpdatedAt(LocalDateTime.now());

        Account account4 = new Account();
        account4.setNasabah(nasabah2);
        account4.setType("Rekening Tabungan");
        account4.setAccountNumber("18345678290");
        account4.setBalance(1000000L);
        account4.setCreatedAt(LocalDateTime.now());
        account4.setUpdatedAt(LocalDateTime.now());

        Account account5 = new Account();
        account5.setNasabah(nasabah2);
        account5.setType("Rekening Tabungan");
        account5.setAccountNumber("17345167890");
        account5.setBalance(1000000L);
        account5.setCreatedAt(LocalDateTime.now());
        account5.setUpdatedAt(LocalDateTime.now());

        Account account6 = new Account();
        account6.setNasabah(nasabah3);
        account6.setType("Rekening Tabungan");
        account6.setAccountNumber("12394568890");
        account6.setBalance(1000000L);
        account6.setCreatedAt(LocalDateTime.now());
        account6.setUpdatedAt(LocalDateTime.now());

        Account account7 = new Account();
        account7.setNasabah(nasabah3);
        account7.setType("Rekening Tabungan");
        account7.setAccountNumber("12348547890");
        account7.setBalance(1000000L);
        account7.setCreatedAt(LocalDateTime.now());
        account7.setUpdatedAt(LocalDateTime.now());

        Account account8 = new Account();
        account8.setNasabah(nasabah3);
        account8.setType("Rekening Tabungan");
        account8.setAccountNumber("15347567890");
        account8.setBalance(1000000L);
        account8.setCreatedAt(LocalDateTime.now());
        account8.setUpdatedAt(LocalDateTime.now());

        Account account9 = new Account();
        account9.setNasabah(nasabah4);
        account9.setType("Rekening Tabungan");
        account9.setAccountNumber("11634567890");
        account9.setBalance(1000000L);
        account9.setCreatedAt(LocalDateTime.now());
        account9.setUpdatedAt(LocalDateTime.now());

        Account account10 = new Account();
        account10.setNasabah(nasabah4);
        account10.setType("Rekening Tabungan");
        account10.setAccountNumber("19305675890");
        account10.setBalance(1000000L);
        account10.setCreatedAt(LocalDateTime.now());
        account10.setUpdatedAt(LocalDateTime.now());

        Account account11 = new Account();
        account11.setNasabah(nasabah5);
        account11.setType("Rekening Tabungan");
        account11.setAccountNumber("193305647890");
        account11.setBalance(1000000L);
        account11.setCreatedAt(LocalDateTime.now());
        account11.setUpdatedAt(LocalDateTime.now());

        Account account12 = new Account();
        account12.setNasabah(nasabah5);
        account12.setType("Rekening Tabungan");
        account12.setAccountNumber("19305367890");
        account12.setBalance(1000000L);
        account12.setCreatedAt(LocalDateTime.now());
        account12.setUpdatedAt(LocalDateTime.now());

        Account account13 = new Account();
        account13.setNasabah(nasabah5);
        account13.setType("Rekening Tabungan");
        account13.setAccountNumber("19320567890");
        account13.setBalance(1000000L);
        account13.setCreatedAt(LocalDateTime.now());
        account13.setUpdatedAt(LocalDateTime.now());

        Account account14 = new Account();
        account14.setNasabah(nasabah6);
        account14.setType("Rekening Tabungan");
        account14.setAccountNumber("19305617890");
        account14.setBalance(1000000L);
        account14.setCreatedAt(LocalDateTime.now());
        account14.setUpdatedAt(LocalDateTime.now());

        Account account15 = new Account();
        account15.setNasabah(nasabah6);
        account15.setType("Rekening Tabungan");
        account15.setAccountNumber("19305667890");
        account15.setBalance(1000000L);
        account15.setCreatedAt(LocalDateTime.now());
        account15.setUpdatedAt(LocalDateTime.now());

        Account account16 = new Account();
        account16.setNasabah(nasabah7);
        account16.setType("Rekening Tabungan");
        account16.setAccountNumber("19305567890");
        account16.setBalance(1000000L);
        account16.setCreatedAt(LocalDateTime.now());
        account16.setUpdatedAt(LocalDateTime.now());

        Account account17 = new Account();
        account17.setNasabah(nasabah7);
        account17.setType("Rekening Tabungan");
        account17.setAccountNumber("19305678940");
        account17.setBalance(1000000L);
        account17.setCreatedAt(LocalDateTime.now());
        account17.setUpdatedAt(LocalDateTime.now());

        Account account18 = new Account();
        account18.setNasabah(nasabah7);
        account18.setType("Rekening Tabungan");
        account18.setAccountNumber("19305647890");
        account18.setBalance(1000000L);
        account18.setCreatedAt(LocalDateTime.now());
        account18.setUpdatedAt(LocalDateTime.now());

        Account account19 = new Account();
        account19.setNasabah(nasabah8);
        account19.setType("Rekening Tabungan");
        account19.setAccountNumber("19303567890");
        account19.setBalance(1000000L);
        account19.setCreatedAt(LocalDateTime.now());
        account19.setUpdatedAt(LocalDateTime.now());

        Account account20 = new Account();
        account20.setNasabah(nasabah8);
        account20.setType("Rekening Tabungan");
        account20.setAccountNumber("19305467890");
        account20.setBalance(1000000L);
        account20.setCreatedAt(LocalDateTime.now());
        account20.setUpdatedAt(LocalDateTime.now());

        Account account21 = new Account();
        account21.setNasabah(nasabah9);
        account21.setType("Rekening Tabungan");
        account21.setAccountNumber("19310567890");
        account21.setBalance(1000000L);
        account21.setCreatedAt(LocalDateTime.now());
        account21.setUpdatedAt(LocalDateTime.now());

        Account account22 = new Account();
        account22.setNasabah(nasabah9);
        account22.setType("Rekening Tabungan");
        account22.setAccountNumber("19230567890");
        account22.setBalance(1000000L);
        account22.setCreatedAt(LocalDateTime.now());
        account22.setUpdatedAt(LocalDateTime.now());

        Account account23 = new Account();
        account23.setNasabah(nasabah9);
        account23.setType("Rekening Tabungan");
        account23.setAccountNumber("19305657890");
        account23.setBalance(1000000L);
        account23.setCreatedAt(LocalDateTime.now());
        account23.setUpdatedAt(LocalDateTime.now());

        Account account24 = new Account();
        account24.setNasabah(nasabah10);
        account24.setType("Rekening Tabungan");
        account24.setAccountNumber("19330567890");
        account24.setBalance(1000000L);
        account24.setCreatedAt(LocalDateTime.now());
        account24.setUpdatedAt(LocalDateTime.now());

        Account account25 = new Account();
        account25.setNasabah(nasabah10);
        account25.setType("Rekening Tabungan");
        account25.setAccountNumber("19305267890");
        account25.setBalance(1000000L);
        account25.setCreatedAt(LocalDateTime.now());
        account25.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);
        accountRepository.save(account4);
        accountRepository.save(account5);
        accountRepository.save(account6);
        accountRepository.save(account7);
        accountRepository.save(account8);
        accountRepository.save(account9);
        accountRepository.save(account10);
        accountRepository.save(account11);
        accountRepository.save(account12);
        accountRepository.save(account13);
        accountRepository.save(account14);
        accountRepository.save(account15);
        accountRepository.save(account16);
        accountRepository.save(account17);
        accountRepository.save(account18);
        accountRepository.save(account19);
        accountRepository.save(account20);
        accountRepository.save(account21);
        accountRepository.save(account22);
        accountRepository.save(account23);
        accountRepository.save(account24);
        accountRepository.save(account25);

        Bank bank = loadBank();

        for (int i = 1; i <= 50; i++) {
            Transaction transaction = loadTransaction(account1, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction2 = loadTransaction(account2, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction3 = loadTransaction(account3, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction4 = loadTransaction(account4, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction5 = loadTransaction(account5, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction6 = loadTransaction(account6, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction7 = loadTransaction(account7, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction8 = loadTransaction(account8, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction9 = loadTransaction(account9, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction10 = loadTransaction(account10, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction11 = loadTransaction(account11, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction12 = loadTransaction(account12, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction13 = loadTransaction(account13, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction14 = loadTransaction(account14, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction15 = loadTransaction(account15, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction16 = loadTransaction(account16, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction17 = loadTransaction(account17, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction18 = loadTransaction(account18, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction19 = loadTransaction(account19, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction20 = loadTransaction(account20, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction21 = loadTransaction(account21, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction22 = loadTransaction(account22, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction23 = loadTransaction(account23, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction24 = loadTransaction(account24, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);
            Transaction transaction25 = loadTransaction(account25, bank, "Transaction " + i, 500000L + i, "Berhasil", TransactionCategories.values()[i % TransactionCategories.values().length]);

            loadTickets(transaction);
            loadTickets(transaction2);
            loadTickets(transaction3);
            loadTickets(transaction4);
            loadTickets(transaction5);
            loadTickets(transaction6);
            loadTickets(transaction7);
            loadTickets(transaction8);
            loadTickets(transaction9);
            loadTickets(transaction10);
            loadTickets(transaction11);
            loadTickets(transaction12);
            loadTickets(transaction13);
            loadTickets(transaction14);
            loadTickets(transaction15);
            loadTickets(transaction16);
            loadTickets(transaction17);
            loadTickets(transaction18);
            loadTickets(transaction19);
            loadTickets(transaction20);
            loadTickets(transaction21);
            loadTickets(transaction22);
            loadTickets(transaction23);
            loadTickets(transaction24);
            loadTickets(transaction25);
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
        if (transaction.getTransaction_type() == TransactionType.Out){
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

    }

    private void createTicketHistory(Tickets ticket, TransactionCategories category) {
        Admin admin = adminRepository.findByUsername("admin12");
        String[] statuses = {"Laporan Diajukan", "Laporan Diajukan", "Laporan Dalam Proses", "Laporan Selesai Diproses", "Laporan Selesai Diproses"};

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
        feedback.setStarRating(starRating);
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