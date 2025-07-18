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

When prompted, use password: `initaccount123`

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
     --unlock <account_public_address> \
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

1. **Compile Smart Contract**
   - Open [Remix IDE](https://remix.ethereum.org/)
   - Load your `.sol` file
   - Go to Advanced configurations
   - Set EVM version to: `paris`
   - Compile the contract

2. **Deploy Contract**
   - Navigate to "Deploy and run transactions"
   - Set Environment to: `Custom - External Http Provider`
   - Enter HTTP provider: `http://localhost:8545`
   - Deploy the contract
   - **Important:** Copy the deployed contract address

## Android App Integration

### 1. Setup ngrok for External Access

1. Download and install [ngrok](https://ngrok.com/downloads/windows?tab=download)
2. Create ngrok account and authenticate
3. Run ngrok to expose local blockchain:

```bash
ngrok http --host-header=rewrite 8545
```

4. Copy the generated HTTPS URL (e.g., `https://696e-118-140-62-207.ngrok-free.app`)

### 2. Generate Smart Contract Wrapper

1. **Install web3j CLI**
   - Download [web3j-1.7.0.zip](https://github.com/LFDT-web3j/web3j-cli/releases/tag/v1.7.0)
   - Extract and add to system PATH
   - Verify installation: `web3j -version`

2. **Prepare Contract Files**
   - Copy ABI and binary files from Remix to:
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
   - Select keystore file from: `blockchain/test_chain/keystore/UTC--[TIMESTAMP]--[ADDRESS]`
   - Enter password: `initaccount123`
   - Navigate to "portfolio value" → "view paper wallet"
   - Copy the private key

2. **Update Configuration**
   - Edit: `app/src/main/assets/secure_config.properties`
   - Add: `admin.private.key=<YOUR_PRIVATE_KEY>`

**Note:** Replace `<YOUR_PRIVATE_KEY>` with the actual private key obtained from MyEtherWallet.

### 4. Android Studio Setup

```bash
# Clean and rebuild project
Build → Clean Project
# Restart Gradle daemon
```

## Troubleshooting

### Common Issues

- **Connection Issues**: Ensure ngrok is running and the URL is updated in app configuration
- **Mining Problems**: Check if the unlock address matches your admin account
- **Compilation Errors**: Verify EVM version is set to "paris" in Remix
- **Web3j Issues**: Ensure web3j is properly added to system PATH

### Useful Commands

```javascript
// Check account balance (replace with actual address)
eth.getBalance("0x9506a3aae006e7ca2a4849ad8840483d47c8ea34")

// Monitor transaction pool
txpool.content

// Check current block number
eth.blockNumber
```

## Security Notes

⚠️ **Warning**: This setup is for development purposes only. Never use these configurations in production environments.

- The private key is stored in plain text for development convenience
- The `--allow-insecure-unlock` flag should never be used in production
- Always use secure password management in production deployments

## File Path Configuration

Before running the commands, make sure to:

1. Replace `<PROJECT_ROOT>` with your actual project directory path
2. Replace `<YOUR_BLOCKCHAIN_DIRECTORY>` with your blockchain setup directory
3. Replace `<YOUR_PRIVATE_KEY>` with the actual private key from your wallet
4. Update keystore file paths to match your generated files

---

**Example Path Replacements:**
- `<PROJECT_ROOT>` → `/home/username/HKUTokenFlow` (Linux) or `C:\Users\username\HKUTokenFlow` (Windows)
- `<YOUR_BLOCKCHAIN_DIRECTORY>` → `/home/username/blockchain` (Linux)