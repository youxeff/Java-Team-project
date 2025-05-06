MARKETPLACE - READ ME!
===========================================


COMPILATION AND RUNNING INSTRUCTIONS
--------------------------------

To compile and run the marketplace project, follow these steps:

1. Navigate to the Project Directory:
   - Open Terminal/Command Prompt
   - Use 'cd' to navigate to the Team-Project directory
   - Verify correct directory:
     * macOS/Linux: Use 'pwd' command
     * Windows: Use 'cd' command
   - You should see "Team-Project" when using 'ls' (macOS/Linux) or 'dir' (Windows)

2. Compile the Project:
   - On macOS/Linux:
     javac -d . Team-Project/Network/*.java Team-Project/Service/*.java Team-Project/model/*/*.java
   
   - On Windows:
     javac -d . Team-Project\Network\*.java Team-Project\Service\*.java Team-Project\model\*\*.java

3. Start the Server:
   java Network.Server

4. Start the Client(s):
   - Open a new terminal window for each client
   - Run: java Network.Client

Note: You must start the Server before running any Clients.
      Multiple Clients can connect to the same Server.

SUBMISSIONS
-----------

Youssef Abdelkader : Submitted report on BrightSpace
Youssef Abdelkader : Submitted Vocareum workspace.
Caroline Murphy : Submitted Project Video


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
- Central business logic hub for managing marketplace operations
- Manages:
  * User data operations
  * Item inventory
  * Transaction processing
  * Search functionality
  * Data persistence
- Core methods:
  * Constructor - Initializes marketplace and loads data
  * updateUserData() - Updates user information in persistence
  * initializeUserDataFile() - Creates/verifies data files exist
  * loadAllUsers() - Loads users from persistence
  * addItem() - Adds new item to marketplace
  * searchSeller() - Finds sellers by name
  * searchByName() - Searches items by name (case-insensitive)
  * searchByCategory() - Filters items by exact category match
  * authenticateUser() - Verifies user credentials
  * getAvailableItems() - Lists all unsold items
  * purchaseItem() - Processes item purchase
  * saveTransaction() - Records transaction details
  * loadTransactions() - Gets user's transaction history
  * loadAllItems() - Loads items from storage
  * parseItem() - Converts stored item data to objects
  * itemToString() - Serializes items for storage
  * rewriteItemsFile() - Updates item storage after changes
  * saveItemToFile() - Persists new items to storage

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

5.3 NETWORK IMPLEMENTATION
------------------------------

Server.java
- Core server implementation handling client connections
- Features:
  * Multithreaded design using ExecutorService
  * Shared marketplace instance across all clients
  * Graceful shutdown handling
- Key components:
  * startServer() - Accepts client connections
  * threadPool - Manages concurrent client handling

Client.java
- Client-side interface for marketplace access
- Features:
  * Separate thread for server responses
  * Non-blocking I/O for user input
  * Connection status monitoring
  * Clean disconnection handling
- Key components:
  * serverResponseThread - Handles async server messages
  * Buffered I/O streams for efficient communication

ClientHandler.java
- Per-client connection manager
- Features:
  * Full marketplace functionality
  * Session management
  * Synchronized resource access
  * Input validation
- Key operations:
  * User registration/authentication
  * Profile management
  * Item listing/purchasing
  * Messaging system
- Thread safety:
  * Synchronized methods
  * Resource cleanup in finally blocks
  * Proper stream management

Nested Classes:
1. MessageCellRenderer:
   * Extends DefaultListCellRenderer
   * Custom formatting for message list items
   * Displays: sender, date, message preview
   * HTML-based layout with bold sender name
   * Message preview truncated to 30 chars
   * Handles "No messages" special case

2. StarRatingRenderer:
   * Extends DefaultListCellRenderer
   * Visual representation of ratings (1-5)
   * Converts numeric ratings to stars (★★★☆☆)
   * Used in rating selection dropdowns
   * Consistent star display format

Transaction History Implementation:
1. Panel Creation (createTransactionHistoryPanel):
   * Tabbed interface (Purchases/Sales)
   * Table-based transaction display
   * Real-time updates support
   * Columns:
     - Date
     - Item Name
     - Category
     - Seller/Buyer
     - Price

2. Transaction Updates (updateTransactionHistory):
   * Thread-safe using tableLock
   * SwingUtilities.invokeLater for EDT safety
   * Dual-view updates (buyer/seller)
   * Real-time table population
   * Price formatting with currency

3. Historical Data (loadUserTransactions):
   * Initial data loading on login
   * Parses transaction records
   * Filters user-relevant transactions
   * Populates both transaction views
   * Error handling for file operations

5.4 USER INTERFACE IMPROVEMENTS
------------------------------

Navigation:
1. 'back' option available at every input prompt
2. Users can return to previous menus at any time
3. Clear menu hierarchy and navigation paths
4. Same-line input prompts for better readability

Input Formatting:
1. Input prompts appear on same line as user input
2. Consistent formatting across all menus
3. Clear indication of available options
4. Input validation with helpful error messages

5. THREAD SAFETY AND SYNCHRONIZATION
------------------------------

5.1 LOCK OBJECTS
- userLock: Protects user-related operations
- marketplaceLock: Protects marketplace operations
- tableLock: Protects transaction history updates
- messageLock: Protects message operations
- STATIC_LOCK: Protects static user credentials
- MESSAGE_LOCKS_GUARD: Protects message file locks map

5.2 SYNCHRONIZED RESOURCES
- User Operations:
  * Registration/login
  * Balance updates
  * Profile modifications
  * Credential verification
  
- Marketplace Operations:
  * Item listing/purchasing
  * Transaction processing
  * Search operations
  * Category filtering
  
- Data Access:
  * File operations (read/write)
  * Database updates
  * Message handling
  * Transaction logging

5.3 CONCURRENCY MANAGEMENT
- GUI Updates:
  * SwingUtilities.invokeLater() for EDT safety
  * Atomic transaction updates
  * Thread-safe model modifications
  
- Resource Cleanup:
  * Proper stream closure
  * Lock release in finally blocks
  * Socket cleanup on disconnect

5.4 FILE OPERATIONS
- Atomic Writes:
  * User data updates
  * Item listing changes
  * Transaction records
  * Message persistence
  
- Synchronized Reads:
  * User profile loading
  * Item availability checks
  * Transaction history retrieval
  * Message fetching

5.5 CLIENT HANDLER METHODS

Core Methods:
- run() - Main processing loop managing client connections
  * Handles authentication flow
  * Manages GUI lifecycle
  * Ensures proper resource cleanup

GUI Methods:
- createAuthGUI() - Creates authentication interface
  * Welcome screen
  * Login form
  * Registration form
  * Navigation between screens

- welcomePanel() - Creates landing page
  * Welcome message
  * Login/Register options
  * Exit functionality

- loginPanel() - Implements login interface
  * Username/password inputs
  * Login button handling
  * Navigation controls

- registerPanel() - Implements registration
  * All required user fields
  * Input validation
  * Account creation

Update Methods:
- updateBalanceGUI() - Updates user balance
  * Atomic balance modification
  * File persistence
  * UI synchronization

- refreshItemsList() - Updates item displays
  * Sell panel items table
  * Buy panel item grid
  * Available items filtering

- refreshMessages() - Updates message inbox
  * Thread-safe message loading
  * UI updates via EDT
  * Empty state handling

Display Methods:
- displayItems() - Renders item grid
  * Card-based layout
  * Item details formatting
  * Action button wiring

- showItemDetail() - Shows item details
  * Detailed item information
  * Purchase functionality
  * Seller details

Message System:
- createMessageComposeWindow() - New message interface
  * Recipient selection
  * Message composition
  * Send functionality

- createMessageInboxPanel() - Message center
  * Message list with custom renderer
  * Message preview
  * Real-time updates

Transaction Management:
- updateTransactionHistory() - Records transactions
  * Purchase/sale logging
  * Table updates
  * Date formatting

Security:
- register() - New account creation
  * Input validation
  * Duplicate checking
  * Account initialization

- login() - User authentication
  * Credential verification
  * Session initialization
  * Profile loading

6. MARKETPLACE SERVICE DETAILS
------------------------------

6.1 CORE FEATURES
- User Management:
  * updateUserData() - Updates user information atomically
  * initializeUserDataFile() - Creates/verifies data files
  * loadAllUsers() - Loads users with thread safety
  * authenticateUser() - Thread-safe credential verification

- Item Operations:
  * addItem() - Adds new items with persistence
  * searchByName() - Case-insensitive name search
  * searchByCategory() - Category-based filtering
  * getAvailableItems() - Lists unsold items
  * purchaseItem() - Atomic purchase transaction

- Transaction Management:
  * saveTransaction() - Records purchase details
  * loadTransactions() - Retrieves user history
  * rewriteItemsFile() - Updates item availability

6.2 DATA PERSISTENCE
- File Structure:
  * users.txt - User credentials and profiles
  * items.txt - Item listings and status
  * transactions.txt - Purchase records
  * messages/ - User communication
  * ratings/ - Seller feedback

- File Format:
  * Users: username,password,firstName,lastName,balance
  * Items: className,name,cost,seller,available,image,category,[specific attributes]
  * Transactions: buyer,category,itemName,seller,date,cost,category

6.3 SYNCHRONIZATION MECHANISMS
- Object-Level Locks:
  * Per-user message locks
  * Marketplace operation lock
  * Transaction record lock
  * File access synchronization

- Method-Level Synchronization:
  * All public interface methods
  * File operations
  * User data modifications
  * Item state changes
