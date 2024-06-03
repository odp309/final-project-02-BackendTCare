package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.userAccount.TransactionDTO;
import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.interfaces.UserMutationInterface;
//import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMutationService implements UserMutationInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private NasabahRepository nasabahRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserMutationDTO getUserMutations(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }


        Nasabah nasabah = nasabahRepository.findByUser(user);

        Account account = accountRepository.findByNasabah(nasabah);

        List<Transaction> transactions = transactionRepository.findByAccount(account);
        List<TransactionDTO> transactionDTOList = transactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());

        return UserMutationDTO.builder()
                .transactionList(transactionDTOList)
                .build();
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction){
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setTransactionDetail(transaction.getDetail());
        transactionDTO.setStatus(transaction.getStatus());
        return transactionDTO;
    }
}
