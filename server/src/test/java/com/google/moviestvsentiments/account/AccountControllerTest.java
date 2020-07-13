package com.google.moviestvsentiments.account;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    private static final Account ACCOUNT_1 = Account.create("Test Name 1", Instant.ofEpochSecond(0));
    private static final Account ACCOUNT_2 = Account.create("Test Name 2", Instant.ofEpochSecond(1));
    private static final String ACCOUNT_LIST_JSON = "[ { \"name\": \"Test Name 1\", \"timestamp\": 0 }, " +
            "{\"name\": \"Test Name 2\", \"timestamp\": 1} ]";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository mockRepository;

    @Test
    public void accountController_getAccounts_returnsAccounts() throws Exception {
        when(mockRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(ACCOUNT_1, ACCOUNT_2));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo(ACCOUNT_1.getName())))
                .andExpect(jsonPath("$[0].timestamp", equalTo(ACCOUNT_1.getTimestamp().toString())))
                .andExpect(jsonPath("$[1].name", equalTo(ACCOUNT_2.getName())))
                .andExpect(jsonPath("$[1].timestamp", equalTo(ACCOUNT_2.getTimestamp().toString())));
    }

    @Test
    public void accountController_addAccounts_invokesRepository() throws Exception {
        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON));

        verify(mockRepository).saveAll(Arrays.asList(ACCOUNT_1, ACCOUNT_2));
    }

    @Test
    public void accountController_addAccountsSuccessful_returnsOk() throws Exception {
        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void accountController_addAccountsSuccessful_returnsAccounts() throws Exception {
        when(mockRepository.saveAll(any(Iterable.class))).thenReturn(Arrays.asList(ACCOUNT_1, ACCOUNT_2));

        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(jsonPath("$.accounts[0].name", equalTo(ACCOUNT_1.getName())))
                .andExpect(jsonPath("$.accounts[0].timestamp", equalTo(ACCOUNT_1.getTimestamp().toString())))
                .andExpect(jsonPath("$.accounts[1].name", equalTo(ACCOUNT_2.getName())))
                .andExpect(jsonPath("$.accounts[1].timestamp", equalTo(ACCOUNT_2.getTimestamp().toString())));
    }

    @Test
    public void accountController_addAccountsSuccessful_returnsNullError() throws Exception {
        when(mockRepository.saveAll(any(Iterable.class))).thenReturn(Arrays.asList(ACCOUNT_1, ACCOUNT_2));

        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(jsonPath("$.error", equalTo(null)));
    }

    @Test
    public void accountController_addAccountsJpaException_returnsBadRequest() throws Exception {
        when(mockRepository.saveAll(any(Iterable.class))).thenThrow(JpaSystemException.class);

        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void accountController_addAccountsOtherException_returnsServerError() throws Exception {
        when(mockRepository.saveAll(any(Iterable.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void accountController_addAccountsFailure_returnsNullList() throws Exception {
        when(mockRepository.saveAll(any(Iterable.class))).thenThrow(JpaSystemException.class);

        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(jsonPath("$.accounts", equalTo(null)));
    }

    @Test
    public void accountController_addAccountsFailure_returnsError() throws Exception {
        final String errorMessage = "Invalid account name";
        when(mockRepository.saveAll(any(Iterable.class))).thenThrow(new JpaSystemException(new RuntimeException(errorMessage)));

        mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(ACCOUNT_LIST_JSON))
                .andExpect(jsonPath("$.error", containsString(errorMessage)));
    }
}
