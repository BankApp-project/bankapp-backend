package info.mackiewicz.bankapp.core.account.model.interfaces;

import java.math.BigDecimal;

public interface DashboardAccountInfo extends AccountInfo {
    public BigDecimal getBalance();
}
