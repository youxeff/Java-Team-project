MARKETPLACE - READ ME!
===========================================

1. SYSTEM OVERVIEW
------------------
A Java-based marketplace platform with buying/selling functionality including:
- User account management (registration, login, profiles)
- Item listing system (multiple categories)
- Marketplace operations (browsing, purchasing)
- User messaging system
- Seller rating mechanism
- File-based data persistence

2. PROGRAM FILE STRUCTURE
-------------------------------------------
src/
│
├── Main/
│   ├── IMain.java          - Interface for main file
│   └── Main.java           - Application class handling console UI (will be GUI after Phase 3) and core logic
│
├── Service/
│   └── Marketplace.java    - Service class managing marketplace operations
│
├── model/
│   ├── items/
│   │   ├── AbstractItem.java - Base abstract class for all marketplace items
│   │   ├── Apparel.java      - Clothing items implementation (size, color, brand)
│   │   ├── Collectible.java  - Rare/collectible items implementation
│   │   ├── Electronic.java   - Electronics/devices implementation
│   │   ├── Home.java         - Real estate/housing implementation
│   │   ├── Vehicle.java      - Vehicles implementation (mileage, year, etc.)
│   │   ├── Item.java         - Core item interface
│   │   └── interfaces/       - Category-specific interfaces implemented by the classes above
│   │       ├── IApparel.java
│   │       ├── ICollectible.java
│   │       ├── IElectronic.java
│   │       ├── IHome.java
│   │       └── IVehicle.java
│   │
│   └── users/
│       ├── MarketplaceUser.java - Complete user implementation
│       ├── User.java            - Core user operations interface
│       └── Message.java         - Messaging functionality interface
│
└── test/                    - Comprehensive JUnit test cases
    └── TestCases.java        - All unit tests for the system

3. DETAILED CLASS DESCRIPTIONS
------------------------------

3.1 APPLICATION CLASSES

Main.java
- Handles console interface and menu navigation
- Coordinates between user actions and marketplace services
- Implements:
  * User registration/login flows
  * Item listing/purchasing workflows
  * Balance management
  * Profile viewing/editing

Marketplace.java
- Central business logic hub
- Manages:
  * User data operations
  * Item inventory
  * Transaction processing
  * Search functionality
- Core methods:
  * updateUserData() - Updates user information in persistence
  * initializeUserDataFile() - Creates/verifies data files exist
  * loadAllUsers() - Loads all users from persistence
  * addItem() - Adds new item to marketplace
  * searchSeller() - Finds sellers by name
  * searchByName() - Finds items by name
  * searchByCategory() - Finds items by category
  * authenticateUser() - Verifies user credentials
  * getAvailableItems() - Lists all unsold items
  * purchaseItem() - Processes item purchase
- Updates data to files

3.2 USER MANAGEMENT CLASSES

MarketplaceUser.java
- Complete user account implementation
- Key features:
  * Account creation/validation
  * Secure authentication
  * Balance management
  * Messaging system
  * Rating collection/calculation
- File persistence for:
  * User credentials
  * Messages
  * Ratings

User.java (Interface)
- Defines core user operations:
  * createNewUser() - Creates new user account
  * login() - Authenticates user credentials
  * verifyPassword() - Validates user password
  * get/set methods for user attributes (firstName, lastName, userName, password, balance)
  * addSellerRating() - Adds rating for seller (1-5)
  * getAverageSellerRating() - Gets seller's average rating
  * getNumberOfRatings() - Gets total number of ratings received

Message.java (Interface)
- Standardizes messaging system:
  * sendMessageTo()
  * viewMessages()

3.3 ITEM MANAGEMENT CLASSES

AbstractItem.java
- Base class for all marketplace items
- Common properties:
  * Name, price, seller
  * Availability status
  * Category
- Core methods:
  * sellItem()
  * deleteItem()
  * markSold()

Concrete Item Classes:
- Apparel.java: Clothing with size/color/brand
- Collectible.java: Rare items with condition
- Electronic.java: Tech devices with specs
- Home.java: Real estate properties
- Vehicle.java: Cars/trucks/boats/etc. with mileage/year/model/etc.

Item Interfaces:
- Item.java (Core interface):
  * sellItem() - Processes sale transaction
  * deleteItem() - Removes item from marketplace
  * markSold() - Updates availability status
  * searchByName()/searchByCategory() - Item search operations
  * Getters for item properties (name, cost, seller, availability, image, category)

- IApparel.java:
  * getSize()/setSize() - Manages apparel size
  * getColor()/setColor() - Manages apparel color
  * getBrand()/setBrand() - Manages apparel brand

- ICollectible.java:
  * getType()/setType() - Manages collectible type
  * getCondition()/setCondition() - Manages collectible condition

- IElectronic.java:
  * getType()/setType() - Manages electronic type/brand
  * getYear()/setYear() - Manages manufacturing year

- IHome.java:
  * getType()/setType() - Manages property type

- IVehicle.java:
  * getMileage()/setMileage() - Manages vehicle mileage
  * getYear()/setYear() - Manages manufacturing year
  * getBrand()/setBrand() - Manages vehicle brand

4. DATA PERSISTENCE IMPLEMENTATION
----------------------------------

File Structure:
- users.txt:
  * CSV format: username,password,firstName,lastName,balance
  * One record per line
  
- items.txt:
  * Stores all listed items
  * Serialized format with category-specific attributes
  
- messages/:
  * Each user has dedicated .txt file with messaging info
  * Format: sender, timestamp, message content
  
- ratings/:
  * Separate file per seller
  * Stores rater username and score

5. KEY FUNCTIONALITY BREAKDOWN
------------------------------

5.1 USER WORKFLOWS

Registration:
1. Validate input fields
2. Check username availability
3. Create new user file record
4. Initialize empty message/rating files

Login:
1. Verify credentials
2. Load user data
3. Initialize session

Messaging:
1. Validate recipient exists
2. Append timestamped message
3. Store in recipient's message file

5.2 ITEM WORKFLOWS

Listing Items:
1. Select category
2. Enter category-specific attributes
3. Set price/availability
4. Serialize to items.txt

Purchasing:
1. Verify buyer balance
2. Transfer funds
3. Update item status
4. Optionally rate seller
