package info.mackiewicz.bankapp.presentation.dashboard.main.controller.dto;

import info.mackiewicz.bankapp.core.account.model.interfaces.DashboardAccountInfo;

import java.util.List;

public class UserAccountsInfoResponse {

    int userId;
    List<DashboardAccountInfo> accountsInfos;

    public UserAccountsInfoResponse(int userId, List<DashboardAccountInfo> accountsInfos) {
        this.userId = userId;
        this.accountsInfos = accountsInfos;
    }
}
