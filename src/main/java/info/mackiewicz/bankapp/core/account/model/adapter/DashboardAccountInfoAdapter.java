package info.mackiewicz.bankapp.core.account.model.adapter;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.interfaces.DashboardAccountInfo;
import lombok.NonNull;

import java.math.BigDecimal;


public class DashboardAccountInfoAdapter extends AccountInfoAdapter implements DashboardAccountInfo {

    public DashboardAccountInfoAdapter(Account account) {
        super(account);
    }

    public static DashboardAccountInfo fromAccount(@NonNull Account account) {
        return new DashboardAccountInfoAdapter(account);
    }

    @Override
    public BigDecimal getBalance() {
        return account.getBalance();
    }
}
