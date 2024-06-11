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
                .name(user.getNasabah().getFirst_name() + " " + user.getNasabah().getLast_name())
                .account_list(accountDTOList)
                .build();
    }

    private AccountDTO convertToAccountDTO(Account account) {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setAccount_number(account.getAccountNumber());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setType(account.getType());
        //accountDTO.setTransactionList(transactionDTOList);
        return accountDTO;
    }

}