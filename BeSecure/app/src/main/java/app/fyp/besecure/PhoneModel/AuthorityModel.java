package app.fyp.besecure.PhoneModel;

public class AuthorityModel {

    public String getPhone() {
        return phone;
    }





    public AuthorityModel() {

    }

    String phone, email, name;

    public AuthorityModel(String phone, String email, String name) {
        this.phone = phone;
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
