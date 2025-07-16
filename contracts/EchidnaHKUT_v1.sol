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
    uint256 public constant MAX_TOTAL_SUPPLY = 1000000000 * 10**18; // maximum 1B tokens

    bool public lastMintCallerWasAdmin;
    bool public lastRedeemCallerWasAdmin;
    bool public lastAssignCallerWasAdmin;

    constructor() ERC20("HKUToken", "HKUT") {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);  // Assign admin role
        INITIAL_SUPPLY = totalSupply();
        ORIGINAL_ADMIN = msg.sender;
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


    // --------- Echidna tests ---------
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

    // Unauthorized admin role deletion / access control attacks
    function echidna_admin_role_protected() public view returns (bool) {
        return hasRole(DEFAULT_ADMIN_ROLE, ORIGINAL_ADMIN);
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