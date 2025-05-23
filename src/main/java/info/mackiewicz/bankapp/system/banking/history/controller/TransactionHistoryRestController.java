package info.mackiewicz.bankapp.system.banking.history.controller;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.system.banking.history.controller.dto.TransactionFilterRequest;
import info.mackiewicz.bankapp.system.banking.history.service.TransactionHistoryService;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/banking/history")
@RequiredArgsConstructor
public class TransactionHistoryRestController implements TransactionHistoryRestControllerInterface {

    private static final String DEFAULT_EXPORT_FORMAT = "csv";

    private final TransactionHistoryService transactionHistoryService;

    @PreAuthorize("@idAccountAuthorizationService.validateAccountOwnership(#filter.accountId, authentication.principal)")
    @GetMapping
    @Override
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid TransactionFilterRequest filter
    ) {
        log.debug("Fetching transactions for account {} (user: {})", filter.getAccountId(), user.getUsername());
        return ResponseEntity.ok(transactionHistoryService.getTransactionHistory(filter));
    }

    @PreAuthorize("@idAccountAuthorizationService.validateAccountOwnership(#filter.accountId, authentication.principal)")
    @GetMapping("/export")
    @Override
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid TransactionFilterRequest filter,
            @RequestParam(defaultValue = DEFAULT_EXPORT_FORMAT) String format
    ) {
        log.debug("Exporting transactions for account {} (user: {}) in {} format",
                filter.getAccountId(), user.getUsername(), format);
        return transactionHistoryService.exportTransactions(filter, format);
    }
}