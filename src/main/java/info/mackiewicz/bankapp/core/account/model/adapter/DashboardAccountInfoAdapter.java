package info.mackiewicz.bankapp.core.account.model.adapter;

import info.mackiewicz.bankapp.core.account.model.Account;

import java.math.BigDecimal;

public class DashboardAccountInfoAdapter extends AccountInfoAdapter{

    public DashboardAccountInfoAdapter(Account account) {
        super(account);
    }

    public BigDecimal getBalance() {
        return account.getBalance();
    }
}
