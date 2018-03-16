package br.edu.ifspsaocarlos.sosprecos.model;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class User implements Serializable{
    private String uuid;
    private String name;
    private String email;
    private Date dateOfBirth;
    private Date registrationDate;
    private int qualificationsCount;
    private int commentsCount;

    public User() {
    }

    public static User getInstance(FirebaseUser user){
        User u = new User();
        u.setUuid(user.getUid());
        u.setName(user.getDisplayName());
        u.setEmail(user.getEmail());
        Date registrationDate = user.getMetadata() != null ? new Date(user.getMetadata().getCreationTimestamp()) : new Date(System.currentTimeMillis());
        u.setRegistrationDate(registrationDate);
        return u;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getQualificationsCount() {
        return qualificationsCount;
    }

    public void setQualificationsCount(int qualificationsCount) {
        this.qualificationsCount = qualificationsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
