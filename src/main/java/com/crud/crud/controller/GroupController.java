package com.crud.crud.controller;

import com.crud.crud.dto.ApiResponse;
import com.crud.crud.dto.GroupDTO;
import com.crud.crud.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private LdapService ldapService;

    // Show all groups
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> showAllGroups() {
        try {
            List<Map<String, String>> groups = ldapService.showAllGroups();
            return ResponseEntity.ok(new ApiResponse("Groups retrieved successfully", true, groups));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Add user to group
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addUserToGroup(@RequestBody GroupDTO groupDTO) {
        try {
            ldapService.addUserToGroup(groupDTO);
            return ResponseEntity.ok(new ApiResponse("User added to group successfully", true));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Remove user from group
    @PostMapping("/remove")
    public ResponseEntity<ApiResponse> removeUserFromGroup(@RequestBody GroupDTO groupDTO) {
        try {
            ldapService.removeUserFromGroup(groupDTO);
            return ResponseEntity.ok(new ApiResponse("User removed from group successfully", true));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
