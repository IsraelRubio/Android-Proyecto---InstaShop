package Models;

public class User {
    // Atributes
    private String name;
    private String email;
    private String nick;
    private String password;
    private String mobile;
    private int access;
    private String image;

    public User() {
        this.name = "Default";
        this.email = "Default";
        this.nick = "Default";
        this.password = "Default";
        this.mobile = "Default";
        this.access = 0;
        this.image = "Default";
    }

    public User(String name, String email, String nick, String password, String mobile, int access, String image) {
        this.name = name;
        this.email = email;
        this.nick = nick;
        this.password = password;
        this.mobile = mobile;
        this.access = access;
        this.image = image;
    }
}
