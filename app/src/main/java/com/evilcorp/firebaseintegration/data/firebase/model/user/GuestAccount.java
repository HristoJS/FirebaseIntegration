package com.evilcorp.firebaseintegration.data.firebase.model.user;

import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.helper.Time;

/**
 * Created by hristo.stoyanov on 3/27/2017.
 */

public class GuestAccount extends UserAccount {

    public GuestAccount() {
        this.id = null;
        this.name = generateName();
        this.accountType = AccountType.GUEST;
    }

    private String generateId(){
        return String.valueOf(Time.getTime());
    }

    private String generateName(){
        return "Guest" + Time.getTime();
    }

}
