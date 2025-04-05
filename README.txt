# Team Project - Marketplace System

## Overview
This project is a **Marketplace System** that allows users to register, log in, and interact with a marketplace. Users can buy and sell items, manage their profiles, and search for items or sellers. The system is implemented in Java and follows an object-oriented design.

## Features
1. **User Management**:
    - Register new users with first name, last name, username, and password.
    - Login functionality with credential verification.
    - Update user balance and profile information.
    - Persistent user data storage in `users.txt`.

2. **Marketplace Functionality**:
    - Add, search, and manage items in the marketplace.
    - Support for multiple item categories:
      - **Apparel**: Includes size, color, and brand.
      - **Electronics**: Includes type and year.
      - **Home Items**: Includes type.
      - **Vehicles**: Includes mileage, year, and brand.
      - **Collectibles**: Includes type and condition.
    - Search for items by name or category.
    - Search for sellers by name.

3. **Transactions**:
    - Users can purchase items if they have sufficient balance.
    - Items are marked as sold after purchase.
    - Persistent item data storage in `items.txt`.

4. **Concurrency**:
    - Thread-safe operations using synchronized methods and locks.

## Project Structure
The project is organized into the following packages and files:

### 1. **Main**
- `Main.java`: Entry point of the application. Handles user registration, login, and menu navigation.

### 2. **Service**
- `Marketplace.java`: Implements the core marketplace functionality, including user and item management, file persistence, and search operations.

### 3. **Model**
#### Users
- `User.java`: Interface defining user-related methods.
- `MarketplaceUser.java`: Implementation of the `User` interface. Handles user creation, login, and data persistence.

#### Items
- `Item.java`: Interface defining item-related methods.
- `AbstractItem.java`: Abstract class implementing common item functionality.
- `Apparel.java`: Represents apparel items.
- `Electronic.java`: Represents electronic items.
- `Home.java`: Represents home-related items.
- `Vehicle.java`: Represents vehicles.
- `Collectible.java`: Represents collectible items.

### 4. **Util**
- Placeholder for utility classes (currently empty).

## How to Run
1. Compile the project using a Java compiler.
2. Run the `Main.java` file to start the application.
3. Follow the on-screen instructions to register, log in, and interact with the marketplace.

## File Persistence
- **Users**: Stored in `users.txt` with the format:
  ```
  username,password,firstName,lastName,balance
  ```
- **Items**: Stored in `items.txt` with category-specific formats.
