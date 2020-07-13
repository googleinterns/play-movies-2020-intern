package com.google.moviestvsentiments.account;

import java.time.Instant;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        Iterable<Account> accounts = repository.findAll(Sort.by("name"));
        return StreamSupport.stream(accounts.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Adds the given name and timestamp into the accounts table. If the account name already exists, its timestamp
     * will be updated. If the account is saved successfully, the saved account is returned. If the account cannot be
     * saved, an error message is returned.
     * @param name The name of the account to add.
     * @param timestamp The timestamp of the account to add.
     * @return A ResponseEntity with either the saved account or the error message.
     */
    @PostMapping("/account")
    public ResponseEntity addAccount(@RequestParam String name, @RequestParam Instant timestamp) {
        try {
            Account account = Account.create(name, timestamp);
            account = repository.save(account);
            return ResponseEntity.ok().body(account);
        } catch (JpaSystemException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Adds the given list of accounts into the database. If the accounts are added successfully, the list of
     * successfully created accounts is returned. If the accounts cannot be added, an error message is returned.
     * @param accounts The list of accounts to add.
     * @return A ResponseEntity with either the created accounts or the error message.
     */
    @PostMapping("/accounts")
    public ResponseEntity addAccounts(@RequestBody List<Account> accounts) {
        try {
            Iterable<Account> iterable = repository.saveAll(accounts);
            List<Account> savedAccounts = StreamSupport.stream(iterable.spliterator(), false)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(savedAccounts);
        } catch (JpaSystemException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}