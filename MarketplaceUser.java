public class MarketplaceUser extends AbstractUser {
    private String firstName;
    private String lastName;
    private String password;
    private double balance;
    private String userName;

    public MarketplaceUser(String firstName, String lastName, String userName, 
                          String password, Role initialRole) {
        super(firstName, lastName, userName, password, initialRole);
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balance = 0.0;
        this.userName = userName;
    }

    public static MarketplaceUser registerNewUser(String firstName, String lastName, 
                                                String userName, String password, Role role) {
        return new MarketplaceUser(firstName, lastName, userName, password, role);
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setUsername(String userName) {
        this.userName = userName;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
