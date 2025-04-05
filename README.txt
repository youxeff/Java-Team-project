# Team Project

## Overview
This project is a marketplace application that allows users to register as either buyers or sellers. Sellers can list items for sale, while buyers can browse and purchase items. The application supports various item categories, including electronics, apparel, home goods, vehicles, and collectibles.

## Features
- **User Registration and Login**: Users can register as buyers or sellers and log in to their accounts.
- **Item Management**: Sellers can list, update, and delete items for sale.
- **Search Functionality**: Buyers can search for items by name or category.
- **Transaction Handling**: Buyers can purchase items, and sellers can manage their balances.
- **Data Persistence**: User data is stored and managed for future use.

## Project Structure
- **Interfaces**:
    - `Item`: Represents a generic item with common fields and methods.
    - `IMarketplace`: Manages marketplace operations such as searches and user data management.
    - `User`: Represents a generic user with common fields and methods.

- **Abstract Classes**:
    - `AbstractUser`: Provides a base implementation for user-related operations.
    - `AbstractItem`: Provides a base implementation for item-related operations.

- **Classes**:
    - `Buyer`: Represents a buyer in the marketplace.
    - `Seller`: Represents a seller in the marketplace.
    - `Main`: Entry point for the application.
    - `Electronic`, `Apparel`, `Home`, `Vehicle`, `Collectibles`: Specialized methods for different item categories.
    - 'Marketplace': Handles buying and selling


## How to Run
1. Compile all `.java` files in the project directory.
2. Run the `Main` class to start the application.
3. Follow the prompts to register as a buyer or seller and interact with the marketplace.


