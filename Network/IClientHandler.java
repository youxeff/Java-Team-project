package Network;

import java.io.IOException;

public interface IClientHandler {
    
        void registerUser() throws IOException;

        void loginUser() throws IOException;
    
        void showUserMenu() throws IOException;
     
        void displayUserProfile();

        void buyOrSell() throws IOException;
    
        void sellItem() throws IOException;

        void buyItem() throws IOException;

        void viewMessages();

}
