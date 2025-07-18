# HKU TokenFlow  
*An Initiative of a Token-based Ecosystem in HKU.*  

HKU TokenFlow is a blockchain-based system that:  
- Rewards students with digital tokens for campus activity participation.  
- Enables token usage for rewards such as:  
  - Canteen services  
  - Uprint  
  - Gift store purchases  
  - Event tickets  
- Enhances student engagement through tangible incentives.  

---

## Blockchain Network Setup  
For detailed instructions on setting up the private Ethereum blockchain network and deploying smart contracts, please refer to [BlockchainSetup.md](BlockchainSetup.md).

## Compile and Run  

1. **Java Version Requirement**:  
   - Ensure Java version ≥ 11.  
   - For lower versions: In Android Studio → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK`, select Java 11+.  

2. **Import Project**:  
  - **Option 1: Download ZIP**
    - Download the ZIP project and import it into Android Studio, then run. 
  - **Option 2: Git Clone**   
    - Open Android Studio → `New` → `Project from Version Control` → `Git`.  
    - URL: `https://github.com/hkutokenflow/HKUTokenFlow.git` → `Clone` → `Run`.  

---

## Initial Settings  
- **Default Admin Account**:  
  - Username: `admin`  
  - Password: `admin123`  
- **Account Creation**:  
  - Admins can approve vendor account registrations via *"Manage Vendor"*.  
  - Students can register through the app.  

---

## Project Structure  
```plaintext
.
├── Admin
│   ├── AdminHome
│   ├── Event
│   ├── RecentTransaction
│   ├── Vendor
│   └── AdminActivity
├── contracts
├── Ethereum
├── Login
├── SQLite
├── Student
│   ├── EventCheckin
│   ├── RedeemReward
│   ├── StudentHome
│   ├── TokenFlow
│   ├── YourReward
│   └── StudentActivity
├── Utils
└── Vendor
    ├── changePassword
    ├── VendorHome
    ├── Voucher
    └── VendorActivity