public class Buyer extends AbstractUser {

        private String BuyerFirstName;
        private String BuyerLastName;
        private String BuyerPassword;
        private double BuyerBalance;
        private String BuyerUserName;




        Buyer (String BuyerFirstName, String BuyerLastName, String BuyerUserName,
                String BuyerPassword,  double BuyerBalance) {

            super(BuyerFirstName, BuyerLastName, BuyerUserName, BuyerPassword);

            this.BuyerFirstName = BuyerFirstName;
            this.BuyerLastName = BuyerLastName;
            this.BuyerPassword = BuyerPassword;
            this.BuyerBalance = BuyerBalance;
            this.BuyerUserName = BuyerUserName;

        }


        public static Buyer registerNewSeller(String firstName, String lastName, String userName, String password) {
            return new Buyer(firstName, lastName, userName, password, 0.0);
        }


        public String getFirstName (){
            return BuyerFirstName;
        }
        public String getLastName (){
            return BuyerLastName;
        }
        public String getUserName(){
            return BuyerUserName;
        }
        public String getPassword (){
            return BuyerPassword;
        }
        public double getBalance () {
            return BuyerBalance;
        }


        public void setFirstName (String firstName) {
            BuyerFirstName = firstName;

        }

        public void setLastName (String lastName) {
            BuyerLastName = lastName;
        }


        public void setUsername(String userName) {
            this.BuyerUserName = userName;
        }

        public void setPassword (String password) {
            BuyerPassword = password;

        }
        public void setBalance (double balance) {
            BuyerBalance = balance;

        }




    }
