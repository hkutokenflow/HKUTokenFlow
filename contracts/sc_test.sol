// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/AccessControl.sol";

contract HKUToken is ERC20, AccessControl {
    bytes32 public constant STUDENT_ROLE = keccak256("STUDENT");
    bytes32 public constant VENDOR_ROLE = keccak256("VENDOR");
    
    event TokensMinted(address indexed to, uint256 amount);
    event TokensRedeemed(address indexed from, address indexed to, uint256 amount);
    

    constructor() ERC20("HKUToken", "HKUT") {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);  // Assign admin role
        _mint(msg.sender, 1000000 * 10**decimals());  // Mint initial tokens to admin
    }

    // Function to assign roles to users
    function assignRole(address user, string memory role) public onlyRole(DEFAULT_ADMIN_ROLE) {
        if (keccak256(bytes(role)) == STUDENT_ROLE) {
            _grantRole(STUDENT_ROLE, user);
        } else {
            _grantRole(VENDOR_ROLE, user);
        }
    }

    // Mint tokens for event participation
    function mintTokens(address to, uint256 amount) public onlyRole(DEFAULT_ADMIN_ROLE) {
        _mint(to, amount);
        emit TokensMinted(to, amount);
    }

    // Transfer tokens for reward redemption
    function redeemTokens(address from, address to, uint256 amount) public onlyRole(DEFAULT_ADMIN_ROLE) {
        require(hasRole(STUDENT_ROLE, from), "Invalid student");
        require(hasRole(VENDOR_ROLE, to), "Invalid vendor");
        _transfer(from, to, amount);
        emit TokensRedeemed(from, to, amount);
    }

}