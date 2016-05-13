package com.austin.goaltracker.Model;

import java.util.ArrayList;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Holds fetched accounts for friends list
 */
public class GetAccounts {

    public ArrayList<GetAccount> Accounts;

    public GetAccounts() {
        Accounts = new ArrayList<>();
    }

    public boolean addAccount(GetAccount getAccount) {
        return Accounts.add(getAccount);
    }
}
