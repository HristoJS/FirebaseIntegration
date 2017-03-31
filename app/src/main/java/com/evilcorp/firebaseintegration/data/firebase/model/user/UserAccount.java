package com.evilcorp.firebaseintegration.data.firebase.model.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserAccount implements Parcelable {
    private static final String TAG = UserAccount.class.getSimpleName();

    protected String id;
    protected String email;
    protected String name;
    protected int accountType;

    private String password;
    private String avatar;
    private long lastOnline;
    private int userStatus;

    public UserAccount(){
    }


    private UserAccount(Parcel in) {
        this.email = in.readString();
        this.password = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.accountType = in.readInt();
        this.id = in.readString();
        this.lastOnline = in.readLong();
        this.userStatus = in.readInt();
    }

    public UserAccount (Profile profile){
        this.id = profile.getId();
        this.name = profile.getName();
        this.avatar = profile.getProfilePictureUri(100,100).toString();
        this.accountType = AccountType.FACEBOOK;
    }

    public UserAccount (FirebaseUser user){
        this.id = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        if(user.getPhotoUrl()!=null)
            this.avatar = user.getPhotoUrl().toString();
        for (UserInfo userInfo : user.getProviderData()) {
            String providerId = userInfo.getProviderId();
            Log.d(TAG,providerId);
            if (!"firebase".equals(providerId)) {
                switch (providerId){
                    case "password":
                        this.accountType = AccountType.EMAIL;
                        break;
                    case "google.com":
                        this.accountType = AccountType.GOOGLE;
                        break;
                    case "facebook.com":
                        this.accountType = AccountType.FACEBOOK;
                        break;
                    case "twitter.com":
                        this.accountType = AccountType.TWITTER;
                        break;
                    default:
                        this.accountType = AccountType.UNKNOWN;
                        break;
                }
            }
        }
    }

    public static final Creator<UserAccount> CREATOR = new Creator<UserAccount>() {
        @Override
        public UserAccount createFromParcel(Parcel in) {
            return new UserAccount(in);
        }

        @Override
        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }
    };

    public UserAccount(String email, String password, String name, String avatar, int accountType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.accountType = accountType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(name);
        parcel.writeString(avatar);
        parcel.writeInt(accountType);
        parcel.writeString(id);
        parcel.writeLong(lastOnline);
        parcel.writeInt(userStatus);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getAccountType(){
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar(){
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAccount that = (UserAccount) o;

        if (accountType != that.accountType) return false;
        if (lastOnline != that.lastOnline) return false;
        if (userStatus != that.userStatus) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) return false;
        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + accountType;
        result = 31 * result + id.hashCode();
        result = 31 * result + (int) (lastOnline ^ (lastOnline >>> 32));
        result = 31 * result + userStatus;
        return result;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", accountType=" + accountType +
                ", id='" + id + '\'' +
                ", lastOnline=" + lastOnline +
                ", userStatus=" + userStatus +
                '}';
    }
}
