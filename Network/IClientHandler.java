package Network;

import java.io.IOException;
import model.users.User;

public interface IClientHandler {
    
        void registerUser() throws IOException;

        void loginUser() throws IOException;
    
        void showUserMenu() throws IOException;
     
        void displayUserProfile();

        void buyOrSell() throws IOException;
    
        void sellItem() throws IOException;

        void buyItem() throws IOException;

        void viewMessages();

        void promptForSellerRating(User seller) throws IOException;

}
