package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.userAccount.AccountDTO;
import com.bni.finproajubackend.dto.userAccount.TransactionDTO;
import com.bni.finproajubackend.dto.userAccount.UserAccountDTO;
import com.bni.finproajubackend.interfaces.UserAccountInterface;
import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.PersonRepository;
import com.bni.finproajubackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAccountService implements UserAccountInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserAccountDTO getUserAccount(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        Person person = personRepository.findByUser(user);

        if(person == null){
            throw new RuntimeException("Something went wrong!");
        }

        List<AccountDTO> accountDTOList = person.getNasabah() != null ?
                person.getNasabah().getAccount().stream().map(this::convertToAccountDTO).collect(Collectors.toList()) : List.of();

        return UserAccountDTO.builder()
                .id(person.getId())
                .email(person.getEmail())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .gender(person.getGender())
                .age(person.getAge())
                .noHp(person.getNoHP())
                .address(person.getAddress())
                .nasabahCode(person.getNasabah() != null ? person.getNasabah().getNik() : null)
                .accountList(accountDTOList)
                .build();
    }

    private AccountDTO convertToAccountDTO(Account account) {
        List<TransactionDTO> transactionDTOList = account.getTransaction().stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setAccountNumber(account.getAccount_number());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setType(account.getType());
        //accountDTO.setTransactionList(transactionDTOList);
        return accountDTO;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setTransactionDetail(transaction.getDetail());
        transactionDTO.setStatus(transaction.getStatus());
        return transactionDTO;
    }
}
