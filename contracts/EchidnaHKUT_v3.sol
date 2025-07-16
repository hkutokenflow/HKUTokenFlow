// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/AccessControl.sol";

contract HKUToken is ERC20, AccessControl {
    bytes32 public constant STUDENT_ROLE = keccak256("STUDENT");
    bytes32 public constant VENDOR_ROLE = keccak256("VENDOR");
    
    event TokensMinted(address indexed to, uint256 amount);
    event TokensRedeemed(address indexed from, address indexed to, uint256 amount);

    uint256 private immutable INITIAL_SUPPLY;
    address private immutable ORIGINAL_ADMIN;
    mapping(address => bool) private authorizedAdmins; // Track authorized admins

    uint256 public constant MAX_MINT_PER_CALL = 1000 * 10**18;   // maximum 1000 tokens per mint
    uint256 public constant MAX_TOTAL_SUPPLY = 1000000000 * 10**18; // maximum 1B tokens

    bool public lastMintCallerWasAdmin;
    bool public lastRedeemCallerWasAdmin;
    bool public lastAssignCallerWasAdmin;

    constructor() ERC20("HKUToken", "HKUT") {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);  // Assign admin role
        INITIAL_SUPPLY = totalSupply();

        ORIGINAL_ADMIN = msg.sender;
        authorizedAdmins[msg.sender] = true; // authorize original admin
    }

    // Function to assign roles to users
    function assignRole(address user, string memory role) public onlyRole(DEFAULT_ADMIN_ROLE) {
        lastAssignCaller = msg.sender; // Track caller for testing
        lastAssignCallerWasAdmin = hasRole(DEFAULT_ADMIN_ROLE, msg.sender);
        
        if (keccak256(bytes(role)) == STUDENT_ROLE) {
            _grantRole(STUDENT_ROLE, user);
        } else if (keccak256(bytes(role)) == VENDOR_ROLE) {
            _grantRole(VENDOR_ROLE, user);
        } else {
            revert("Invalid role specified");
        }
    }

    // Mint tokens for event participation
    function mintTokens(address to, uint256 amount) public onlyRole(DEFAULT_ADMIN_ROLE) {
        lastMintCaller = msg.sender; 
        lastMintCallerWasAdmin = hasRole(DEFAULT_ADMIN_ROLE, msg.sender); // Check if caller is admin
        
        require(amount <= MAX_MINT_PER_CALL, "Amount exceeds max mint per call");
        require(totalSupply() + amount <= MAX_TOTAL_SUPPLY, "Would exceed max total supply");

        _mint(to, amount);
        emit TokensMinted(to, amount);
    }

    // Transfer tokens for reward redemption
    function redeemTokens(address from, address to, uint256 amount) public onlyRole(DEFAULT_ADMIN_ROLE) {
        lastRedeemCaller = msg.sender;
        lastRedeemCallerWasAdmin = hasRole(DEFAULT_ADMIN_ROLE, msg.sender);
        
        require(hasRole(STUDENT_ROLE, from), "Invalid student");
        require(hasRole(VENDOR_ROLE, to), "Invalid vendor");
        _transfer(from, to, amount);
        emit TokensRedeemed(from, to, amount);
    }

    function grantRole(bytes32 role, address account) public virtual override onlyRole(DEFAULT_ADMIN_ROLE) {
        if (role == DEFAULT_ADMIN_ROLE) {
            require(msg.sender == ORIGINAL_ADMIN, "Only original admin can grant admin role");
            authorizedAdmins[account] = true; // Mark as authorized
        }
        super.grantRole(role, account);
    }

    function revokeRole(bytes32 role, address account) public virtual override onlyRole(DEFAULT_ADMIN_ROLE) {
        if (role == DEFAULT_ADMIN_ROLE) {
            require(msg.sender == ORIGINAL_ADMIN, "Only original admin can revoke admin role");
            require(account != ORIGINAL_ADMIN, "Cannot revoke original admin role");
            authorizedAdmins[account] = false; // Mark as unauthorized
        }
        super.revokeRole(role, account);
    }

    function renounceRole(bytes32 role, address callerConfirmation) public virtual override {
        require(role != DEFAULT_ADMIN_ROLE, "Cannot renounce admin role");
        super.renounceRole(role, callerConfirmation);
    }


    // ---------- Echidna tests ----------

    address public lastMintCaller;      
    address public lastAssignCaller;    
    address public lastRedeemCaller;    

    // Underflow error
    function echidna_no_negative_balances() public view returns (bool) {
        return balanceOf(msg.sender) >= 0;
    }

    function echidna_balance_upper_bound() public view returns (bool) {
        return balanceOf(msg.sender) <= totalSupply();
    }

    // Overflow error, supply manipulation
    function echidna_reasonable_supply() public view returns (bool) {
        return totalSupply() <= MAX_TOTAL_SUPPLY;
    }

    // Unauthorized burning
    function echidna_no_burning() public view returns (bool) {
        return totalSupply() >= INITIAL_SUPPLY;
    }

    // --- Access control attacks ---
    // Unauthorized admin role deletion
    function echidna_admin_role_protected() public view returns (bool) {
        return hasRole(DEFAULT_ADMIN_ROLE, ORIGINAL_ADMIN) && authorizedAdmins[ORIGINAL_ADMIN];
    }

    // Unauthorized admin role addition
    function echidna_only_authorized_admins() public view returns (bool) {
        if (hasRole(DEFAULT_ADMIN_ROLE, msg.sender)) {
            return authorizedAdmins[msg.sender];
        }
        return true; // Non-admins are fine
    }

    // Unauthorized (non-admin) minting
    function echidna_only_admin_can_mint() public view returns (bool) {
        if (lastMintCaller != address(0)) {
            return lastMintCallerWasAdmin;
        }
        return true;
    }

    // Unauthorized (non-admin) redeeming/transfer
    function echidna_only_admin_can_redeem() public view returns (bool) {
        if (lastRedeemCaller != address(0)) {
            return lastRedeemCallerWasAdmin;
        }
        return true;
    }

    // Unauthorized (non-admin) role assignment
    function echidna_only_admin_can_assign() public view returns (bool) {
        if (lastAssignCaller != address(0)) {
            return lastAssignCallerWasAdmin;
        }
        return true;
    }

}