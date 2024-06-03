package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.userAccount.AccountDTO;
import com.bni.finproajubackend.dto.userAccount.TransactionDTO;
import com.bni.finproajubackend.dto.userAccount.UserAccountDTO;
import com.bni.finproajubackend.interfaces.UserAccountInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
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

    @Override
    public UserAccountDTO getUserAccount(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        List<AccountDTO> accountDTOList = user.getNasabah() != null ?
                user.getNasabah().getAccount().stream().map(this::convertToAccountDTO).collect(Collectors.toList()) : List.of();

        return UserAccountDTO.builder()
                .id(user.getNasabah().getId())
                .email(user.getNasabah().getEmail())
                .firstName(user.getNasabah().getFirstName())
                .lastName(user.getNasabah().getLastName())
                .gender(user.getNasabah().getGender())
                .age(user.getNasabah().getAge())
                .noHp(user.getNasabah().getNoHP())
                .address(user.getNasabah().getAddress())
                .nasabahCode(user.getNasabah() != null ? user.getNasabah().getNik() : null)
                .accountList(accountDTOList)
                .build();
    }

    private AccountDTO convertToAccountDTO(Account account) {
        List<TransactionDTO> transactionDTOList = account.getTransaction().stream()
                .map(this::convertToTransactionDTO)
                .toList();

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