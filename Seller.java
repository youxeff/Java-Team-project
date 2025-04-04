public class Seller extends AbstractUser {
    private String sellerFirstName;
    private String sellerLastName;
    private String sellerPassword;
    private double sellerBalance;
    private String sellerUserName;

    Seller (String sellerFirstName, String sellerLastName, String sellerUserName,
            String sellerPassword,  double sellerBalance) {

        super(sellerFirstName, sellerLastName, sellerUserName, sellerPassword);

        this.sellerFirstName = sellerFirstName;
        this.sellerLastName = sellerLastName;
        this.sellerPassword = sellerPassword;
        this.sellerBalance = sellerBalance;
        this.sellerUserName = sellerUserName;

    }

    public static Seller registerNewSeller(String firstName, String lastName, String userName, String password) {
        return new Seller(firstName, lastName, userName, password, 0.0);
    }

    public String getFirstName (){
        return sellerFirstName;
    }
    public String getLastName (){
        return sellerLastName;
    }
    public String getUserName(){
        return sellerUserName;
    }
    public String getPassword (){
        return sellerPassword;
    }
    public double getBalance () {
        return sellerBalance;
    }

    public void setFirstName (String firstName) {
      sellerFirstName = firstName;

    }

    public void setLastName (String lastName) {
        sellerLastName = lastName;
    }

    public void setUsername(String userName) {
        this.sellerUserName = userName;
    }

    public void setPassword (String password) {
        sellerPassword = password;

    }
    public void setBalance (double balance) {
        sellerBalance = balance;

    }
}
