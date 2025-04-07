# Team Project: Marketplace System

## Overview
This project is a **Marketplace System** that allows users to buy and sell various items. It provides functionality for user registration, login, item management, and transactions. The system is implemented in Java and follows an object-oriented design.

## Features
- **User Management**:
  - Register new users.
  - Login and authenticate users.
  - Update user profiles and balances.
  - Send and view messages between users.

- **Item Management**:
  - Add items for sale in categories such as Apparel, Collectibles, Electronics, Homes, and Vehicles.
  - Search for items by name or category.
  - Mark items as sold or delete them.

- **Marketplace Operations**:
  - Buy items from other users.
  - View available items for purchase.
  - Persist user and item data to files for future use.

- **Testing**:
  - Comprehensive unit tests for all major components using JUnit.

## Project Structure
The project is organized into the following directories and files:

### 1. `model/items`
Contains the core item-related classes and interfaces:
- `AbstractItem.java`: Base class for all items.
- `Item.java`: Interface defining common item functionality.
- Specific item types:
  - `Apparel.java`
  - `Collectible.java`
  - `Electronic.java`
  - `Home.java`
  - `Vehicle.java`
- Interfaces for item types:
  - `IApparel.java`
  - `ICollectible.java`
  - `IElectronic.java`
  - `IHome.java`
  - `IVehicle.java`
- `IMarketplace.java`: Interface for