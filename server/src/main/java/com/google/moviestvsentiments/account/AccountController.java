package com.google.moviestvsentiments.account;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller that handles requests related to accounts.
 */
@RestController
public class AccountController {

    @Autowired
    private AccountRepository repository;

    /**
     * Returns a list of all accounts sorted in ascending order by account name.
     */
    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        Iterable<Account> accounts = repository.findAll(Sort.by("accountName"));
        return StreamSupport.stream(accounts.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Adds the given list of accounts into the database. If the accounts are added successfully, the list of
     * successfully created accounts is returned. If the accounts cannot be added, an error message is returned.
     * @param accounts The list of accounts to add.
     * @return A ResponseEntity with either the created accounts or the error message.
     */
    @PostMapping("/accounts")
    public ResponseEntity<AddAccountResponse> addAccounts(@RequestBody List<Account> accounts) {
        try {
            Iterable<Account> iterable = repository.saveAll(accounts);
            List<Account> savedAccounts = StreamSupport.stream(iterable.spliterator(), false)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(AddAccountResponse.create(savedAccounts, null));
        } catch (JpaSystemException e) {
            return ResponseEntity.badRequest().body(AddAccountResponse.create(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AddAccountResponse.create(null, e.getMessage()));
        }
    }
}