# Team Project

## Overview
This project is a marketplace application that enables users to register as buyers or sellers. Sellers can list items for sale, while buyers can browse and purchase items. The application supports multiple item categories, including electronics, apparel, home goods, vehicles, and collectibles.

## Features
- **User Registration and Login**: Users can create accounts and log in to access the marketplace.
- **Item Management**: Sellers can add, update, and remove items from their listings.
- **Search Functionality**: Buyers can search for items by name or category.
- **Transaction Management**: Buyers can purchase items, and sellers can track their earnings.
- **Data Persistence**: User data is stored and managed for future sessions.

## Project Structure
- **Interfaces**:
    - `Item`: Defines common fields and methods for all items.
    - `IElectronic`, `IApparel`, `IHome`, `IVehicle`, `ICollectibles`: Specialized interfaces for specific item categories.
    - `IMarketplace`: Handles marketplace operations such as searches and user data management.
    - `User`: Represents a generic user with shared fields and methods.

- **Abstract Classes**:
    - `AbstractUser`: Provides a base implementation for user-related functionality.

- **Classes**:
    - `MarketplaceUser`: Represents a user in the marketplace with buyer and seller roles.
    - `Testing`: Contains test cases and a sample workflow for the application.