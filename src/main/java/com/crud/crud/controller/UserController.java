package com.crud.crud.controller;

import com.crud.crud.dto.ApiResponse;
import com.crud.crud.dto.UserDTO;
import com.crud.crud.dto.UserDetailsDTO;
import com.crud.crud.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private LdapService ldapService;

    // Create user using JSON request body
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDTO userDTO) throws Exception {
        ldapService.createUser(userDTO); // This should throw an exception if something goes wrong
        return ResponseEntity.ok(new ApiResponse("User created successfully", true));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserDTO userDTO) throws Exception {
        ldapService.updateUser(userDTO); // This should throw an exception if something goes wrong
        return ResponseEntity.ok(new ApiResponse("User updated successfully", true));
    }

    //Delete User
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteUser(@RequestBody UserDTO user) throws Exception {
        ldapService.deleteUser(user.getUsername());
        return ResponseEntity.ok(new ApiResponse("User deleted successfully", true));
    }

    //Enable User
    @PutMapping("/enable")
    public ResponseEntity<ApiResponse> enableUser(@RequestBody UserDTO user) throws Exception {
        ldapService.enableUser(user.getUsername());
        return ResponseEntity.ok(new ApiResponse("User enabled successfully", true));
    }

    //Disable User
    @PutMapping("/disable")
    public ResponseEntity<ApiResponse> disableUser(@RequestBody UserDTO user) throws Exception {
        ldapService.disableUser(user.getUsername());
        return ResponseEntity.ok(new ApiResponse("User disabled successfully", true));
    }

    // Get all user
    @GetMapping("/all")
    public Map<String, List<Map<String, String>>> getAllUsers() {
        List<Map<String, String>> users = new ArrayList<>();
        try {
            NamingEnumeration<SearchResult> results = ldapService.getAllUsers();
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attributes = result.getAttributes();

                // Extract CN from distinguishedName
                String distinguishedName = (String) attributes.get("distinguishedName").get();
                String cn = extractCN(distinguishedName);
                if (cn != null) {
                    Map<String, String> user = new HashMap<>();
                    user.put("userName", cn);
                    users.add(user);
                }
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("userName", "Error retrieving users: " + e.getMessage());
            users.add(error);
        }
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("data", users);
        return response;
    }

    // Helper method to extract only the CN value
    private String extractCN(String distinguishedName) {
        if (distinguishedName != null && distinguishedName.startsWith("CN=")) {
            int endIdx = distinguishedName.indexOf(",");
            if (endIdx > -1) {
                return distinguishedName.substring(3, endIdx); // Remove "CN=" and return just the name
            } else {
                return distinguishedName.substring(3); // No comma found, return the entire string after "CN="
            }
        }
        return null; // Return null if "CN=" is not found
    }


    // Endpoint to get user details by username provided in request body
    @PostMapping("/details")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getUserDetails(@RequestBody Map<String, String> requestBody) throws Exception {
        String username = requestBody.get("username");
        UserDetailsDTO userDetails = ldapService.getUserDetails(username);

        // Convert UserDetailsDTO to a map
        Map<String, Object> userDetailsMap = new LinkedHashMap<>();
        userDetailsMap.put("samAccountName", userDetails.getSamAccountName());
        userDetailsMap.put("distinguishedName", userDetails.getDistinguishedName());
        userDetailsMap.put("userPrincipalName", userDetails.getUserPrincipalName());
        userDetailsMap.put("objectGUID", userDetails.getObjectGUID());
        userDetailsMap.put("firstName", userDetails.getFirstName());
        userDetailsMap.put("lastName", userDetails.getLastName());
        userDetailsMap.put("email", userDetails.getEmail());
        userDetailsMap.put("memberOf", userDetails.getMemberOf());
        userDetailsMap.put("userAccountControl", userDetails.getUserAccountControl());

        // Add the map to a list
        List<Map<String, Object>> userDetailsList = new ArrayList<>();
        userDetailsList.add(userDetailsMap);

        // Wrap the list in a map
        Map<String, List<Map<String, Object>>> responseData = new HashMap<>();
        responseData.put("data", userDetailsList);

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/new")
    public ResponseEntity<Map<String, List<UserDetailsDTO>>> getAllUserDetails() throws Exception {
        List<UserDetailsDTO> users = ldapService.getAllUserDetails();

        Map<String, List<UserDetailsDTO>> response = new HashMap<>();
        response.put("data", users);
        return ResponseEntity.ok(response);
    }


}
