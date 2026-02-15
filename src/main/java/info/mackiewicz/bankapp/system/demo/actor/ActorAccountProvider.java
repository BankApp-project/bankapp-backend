package info.mackiewicz.bankapp.system.demo.actor;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Provides access to the 5 permanent actor accounts used for generating demo transaction history.
 * Actor accounts are created by Flyway migration V8 with known negative IDs.
 * Accounts are fetched lazily on first access and cached for subsequent calls.
 */
@Component
@RequiredArgsConstructor
public class ActorAccountProvider {

    private static final int ACME_CORP_ACCOUNT_ID = -2;
    private static final int LANDLORD_ACCOUNT_ID = -3;
    private static final int STARBUGS_ACCOUNT_ID = -4;
    private static final int FROGSHOP_ACCOUNT_ID = -5;
    private static final int KOLEGA_MAREK_ACCOUNT_ID = -6;

    private final AccountService accountService;

    private Account acmeCorpAccount;
    private Account landlordAccount;
    private Account starbugsAccount;
    private Account frogShopAccount;
    private Account kolegaMarekAccount;

    public Account getAcmeCorpAccount() {
        if (acmeCorpAccount == null) {
            acmeCorpAccount = accountService.getAccountById(ACME_CORP_ACCOUNT_ID);
        }
        return acmeCorpAccount;
    }

    public Account getLandlordAccount() {
        if (landlordAccount == null) {
            landlordAccount = accountService.getAccountById(LANDLORD_ACCOUNT_ID);
        }
        return landlordAccount;
    }

    public Account getStarbugsAccount() {
        if (starbugsAccount == null) {
            starbugsAccount = accountService.getAccountById(STARBUGS_ACCOUNT_ID);
        }
        return starbugsAccount;
    }

    public Account getFrogShopAccount() {
        if (frogShopAccount == null) {
            frogShopAccount = accountService.getAccountById(FROGSHOP_ACCOUNT_ID);
        }
        return frogShopAccount;
    }

    public Account getKolegaMarekAccount() {
        if (kolegaMarekAccount == null) {
            kolegaMarekAccount = accountService.getAccountById(KOLEGA_MAREK_ACCOUNT_ID);
        }
        return kolegaMarekAccount;
    }
}
