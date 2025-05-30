# HKU TokenFlow  
*An Initiative of a Token-based Ecosystem in HKU.*  

---

## Compile and Run  
1. **Java Version Requirement**:  
   - Ensure Java version ≥ 11.  
   - For lower versions: In Android Studio → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK`, select Java 11+.  

2. **Import Project**:  
   - Download the ZIP project and import it into Android Studio, then run.  

3. **GitHub Clone**:  
   - Open Android Studio → `New` → `Project from Version Control` → `Git`.  
   - URL: `https://github.com/hkutokenflow/Blockchain.git` → `Clone` → `Run`.  
[app](app)
---

## Initial Settings  
- **Default Admin Account**:  
  - Username: `admin`  
  - Password: `admin123`  
- **Account Creation**:  
  - Admins can add vendor accounts via *"Manage Vendor"*.  
  - Students can register through the app.  

---

## Project Aims  
HKU TokenFlow is a blockchain-based system that:  
- Rewards students with digital tokens for campus activity participation.  
- Enables token usage for:  
  - Canteen services  
  - Uprint  
  - Gift store purchases  
  - Event tickets  
- Enhances student engagement through tangible incentives.  

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
├── Login
├── SQLite
├── Student
│   ├── EventCheckin
│   ├── RedeemReward
│   ├── StudentHome
│   ├── TokenFlow
│   ├── YourReward
│   └── StudentActivity
└── Vendor
    ├── changePassword
    ├── VendorHome
    ├── Voucher
    └── VendorActivity