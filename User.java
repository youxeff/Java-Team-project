
public interface User {




        String getFirstName();
        String getLastName();
        String getUserName();
        String getPassword();
        double getBalance();

        void setFirstName(String firstName);
        void setLastName(String lastName);
        void setUsername(String userName);
        void setPassword(String password);
        void setBalance(double balance);

        boolean createNewUser(String firstName, String lastName, String userName, String password);
        boolean login(String userName, String password);
    }

