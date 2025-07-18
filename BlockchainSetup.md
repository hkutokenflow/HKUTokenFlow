# Blockchain Network Setup Guide

This guide provides step-by-step instructions for setting up a private Ethereum blockchain network and deploying smart contracts for the HKUTokenFlow application.

## Prerequisites

- Linux (Ubuntu) operating system
- Internet connection for downloading required tools
- Basic knowledge of command line operations

## Table of Contents

1. [Geth Installation](#geth-installation)
2. [Private Blockchain Setup](#private-blockchain-setup)
3. [Smart Contract Deployment](#smart-contract-deployment)
4. [Android App Integration](#android-app-integration)
5. [Troubleshooting](#troubleshooting)

## Geth Installation

### Install Geth v1.10.15

```bash
# Download Geth
wget https://gethstore.blob.core.windows.net/builds/geth-linux-amd64-1.10.15-8be800ff.tar.gz

# Extract the archive
tar -xvf geth-linux-amd64-1.10.15-8be800ff.tar.gz

# Navigate to extracted directory
cd geth-linux-amd64-1.10.15-8be800ff

# Make executable and install
chmod +x geth
sudo cp geth /usr/local/bin/
```

## Private Blockchain Setup

### 1. Create Admin Account

```bash
cd <YOUR_BLOCKCHAIN_DIRECTORY>
geth --datadir ./test_chain account new
```

When prompted, enter a password and save it into a txt file names "password.txt" in your blockchain directory.

**Example Output:**
- Public address: `0xB6d5f460980B42aDC47c63e27616a1d6A7Cc06B5`
- Keystore path: `test_chain/keystore/UTC--2025-07-10T22-43-57.252914373Z--b6d5f460980b42adc47c63e27616a1d6a7cc06b5`

### 2. Initialize Blockchain

```bash
# Initialize with genesis block
geth --datadir ./test_chain init genesis.json
```

### 3. Start Blockchain Node

```bash
geth --datadir ./test_chain \
     --networkid 1234 \
     --snapshot=false \
     --http \
     --http.addr 0.0.0.0 \
     --http.port 8545 \
     --http.api eth,web3,personal,net,miner \
     --http.corsdomain "*" \
     --allow-insecure-unlock \
     --unlock <ADMIN_ACCOUNT_PUBLIC_ADDRESS> \
     --password password.txt
```

### 4. Mining Operations

Connect to Geth console:
```bash
geth attach <YOUR_BLOCKCHAIN_DIRECTORY>/test_chain/geth.ipc
```

Mining commands:
```bash
# Start mining
miner.start()

# Stop mining
miner.stop()
```

## Smart Contract Deployment

### Using Remix IDE

1. **Import Smart Contract**
    - Open [Remix IDE](https://remix.ethereum.org/)
    - Navigate to "File explorer"
    - Open Solidity smart contract file `HKUT.sol` in the `contracts` folder
    - Select the file

2. **Compile Smart Contract**
    - Navigate to "Solidity compiler" → "Advanced configurations": set EVM version to: `paris`
    - Compile the contract (Compile HKUT.sol)

3. **Deploy Contract**
   - Navigate to "Deploy & run transactions"
   - Set Environment to: `Custom - External Http Provider`
   - Enter HTTP provider: `http://localhost:8545`
   - Deploy the contract
   - Start mining in geth using `miner.start()`
   - Copy the deployed contract address
   - Edit `TOKEN_CONTRACT_ADDRESS` in `app/src/main/java/com/example/workshop1/Ethereum/BlockchainConfig.java` to the copied contract address

## Android App Integration

### 1. Ngrok setup

1. Download and install [ngrok](https://ngrok.com/downloads/windows?tab=download)
2. Create Ngrok account, copy authentication token
3. Open Ngrok CLI (`ngrok.exe`) in download directory 
   - Authenticate by running: `ngrok config add-authtoken <your-authtoken>`
   - Run: `ngrok http --host-header=rewrite 8545`
   - Copy the generated HTTPS URL (e.g., `https://696e-118-140-62-207.ngrok-free.app`)
   - Edit `BLOCKCHAIN_URL` value in `app/src/main/java/com/example/workshop1/Ethereum/BlockchainConfig.java` to the copied Ngrok URL
   

### 2. Generate Smart Contract Wrapper (Optional)

**Note:** If the Java wrapper files are already included in the project under `app/src/main/java/com/example/workshop1/contracts/`, you can skip this section.

This section is only needed if:
- The smart contract has been modified and you need to regenerate the wrapper
- The wrapper files are missing from the project
- You want to understand how the wrapper was generated

1. **Install web3j CLI**
   - Download [web3j-1.7.0.zip](https://github.com/LFDT-web3j/web3j-cli/releases/tag/v1.7.0)
   - Extract and add to system PATH
   - Verify installation: `web3j -version`

2. **Prepare Contract Files**
   - Copy ABI and binary files from Remix IDE (under Solidity Compiler) to:
     `contracts/build/HKUT.abi` and `contracts/build/HKUT.bin`

3. **Generate Java Wrapper**
```bash
web3j generate solidity \
  -a <PROJECT_ROOT>/contracts/build/HKUT.abi \
  -b <PROJECT_ROOT>/contracts/build/HKUT.bin \
  -o <PROJECT_ROOT>/app/src/main/java/ \
  -p com.example.workshop1.contracts
```

**Note:** Replace `<PROJECT_ROOT>` with your actual project directory path.

### 3. Configure Private Key

1. **Extract Private Key**
   - Visit [MyEtherWallet](https://www.myetherwallet.com/wallet/access/software?type=keystore)
   - Select keystore file from: `<YOUR_BLOCKCHAIN_DIRECTORY>/test_chain/keystore/UTC--<TIMESTAMP>--<ADMIN_ACCOUNT_PUBLIC_ADDRESS>`
   - Enter password from [Create Admin Account section](#1-create-admin-account) (saved in password.txt)
   - Navigate to "Portfolio value" → "View paper wallet"
   - Copy the private key

2. **Update Configuration**
   - Create file: `app/src/main/assets/secure_config.properties`
   - Add: `admin.private.key=<YOUR_PRIVATE_KEY>`

## Security Notes

⚠️ **Warning**: This setup is for development purposes only. Never use these configurations in production environments:

- The private key is initially stored in APK assets (extractable), then migrated to secure storage
- The `--allow-insecure-unlock` flag should not used in production
- Always use secure password management in production deployments

**Hybrid security implementation**: The app uses a hybrid approach - on first launch, it automatically migrates the private key from the assets file to encrypted Android Keystore storage. After initial setup, all key operations use hardware-backed encryption.

## File Path Configuration

Before running the commands, make sure to:

1. Replace `<PROJECT_ROOT>` with your actual project directory path
2. Replace `<YOUR_BLOCKCHAIN_DIRECTORY>` with your blockchain setup directory
3. Replace `<ADMIN_ACCOUNT_PUBLIC_ADDRESS>` with the admin account's public address
4. Replace `<YOUR_PRIVATE_KEY>` with the actual private key from your wallet
