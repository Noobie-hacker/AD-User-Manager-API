package com.crud.crud.service;

import com.crud.crud.dto.GroupDTO;
import com.crud.crud.dto.UserDTO;
import com.crud.crud.dto.UserDetailsDTO;
import com.crud.crud.exception.UserNotFoundException;
//import com.crud.crud.util.Utils;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
public class LdapService {

    private LdapContext getLdapContext() throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldaps://{Hostname}:636");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "{Bind DN}"); // Admin credentials
        env.put(Context.SECURITY_CREDENTIALS, "{Password}");
        env.put("java.naming.ldap.attributes.binary", "objectGUID");

        //put credentials according to your host

        return new InitialLdapContext(env, null);
    }

    public void createUser(UserDTO userDTO) throws Exception {
        LdapContext ctx = getLdapContext();
        try {
            Attributes attributes = new BasicAttributes();
            Attribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("user");

            attributes.put(objectClass);
            attributes.put("sAMAccountName", userDTO.getSamAccountName());
            attributes.put("userPrincipalName", userDTO.getUserPrincipalName());
            attributes.put("givenName", userDTO.getFirstName());
            attributes.put("sn", userDTO.getLastName());
            attributes.put("mail", userDTO.getEmail());
//            attributes.put("displayName", userDTO.getDisplayName());

            // Encode the password in UTF-16LE and enclose it in quotes for AD requirements
            String quotedPassword = "\"" + userDTO.getPassword() + "\"";
            byte[] passwordBytes = quotedPassword.getBytes(StandardCharsets.UTF_16LE);
            attributes.put("unicodePwd", passwordBytes);

            // Set up DN (Distinguished Name)
            String dn = "cn=" + userDTO.getUsername() + ",cn=Users,dc=mylab,dc=local";

            // Create the user in AD
            ctx.createSubcontext(dn, attributes);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }


    public void updateUser(UserDTO userDTO) throws Exception {
        LdapContext ctx = getLdapContext();
        try {
            String dn = "cn=" + userDTO.getUsername() + ",cn=Users,dc=mylab,dc=local";
            Attributes attributes = new BasicAttributes();
            // LDAP update attributes logic...
            if (userDTO.getFirstName() != null) {
                attributes.put("givenName", userDTO.getFirstName());
            }
            if (userDTO.getLastName() != null) {
                attributes.put("sn", userDTO.getLastName());
            }
            if (userDTO.getEmail() != null) {
                attributes.put("mail", userDTO.getEmail());
            }

            ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attributes);
        } finally {
            ctx.close(); // Ensure this is always executed
        }
    }

    //Delete User
    public void deleteUser(String username) throws Exception {
        LdapContext ctx = null;
        try {
            ctx = getLdapContext();
            String dn = "cn=" + username + ",cn=Users,dc=mylab,dc=local";

            // Check if the user exists using the second method
            if (!userExists(ctx, dn)) {
                throw new UserNotFoundException("User does not exist.");
            }

            ctx.destroySubcontext(dn);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    //Enable User
    public void enableUser(String username) throws Exception {
        LdapContext ctx = null;
        try {
            ctx = getLdapContext();
            String userDn = "cn=" + username + ",cn=Users,dc=mylab,dc=local";

            // Check if the user exists before trying to enable
            if (!userExists(ctx, userDn)) {
                throw new UserNotFoundException("User does not exist");
            }

            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod = new BasicAttribute("userAccountControl", "512"); // Normal account
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod);
            ctx.modifyAttributes(userDn, mods);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    //Disable User
    public void disableUser(String username) throws Exception {
        LdapContext ctx = null;
        try {
            ctx = getLdapContext();
            String userDn = "cn=" + username + ",cn=Users,dc=mylab,dc=local";

            // Check if the user exists before trying to disable
            if (!userExists(ctx, userDn)) {
                throw new UserNotFoundException("User does not exist");
            }

            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod = new BasicAttribute("userAccountControl", "514"); // Disabled account
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod);
            ctx.modifyAttributes(userDn, mods);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    // Helper method to check if a user exists
    private boolean userExists(LdapContext ctx, String userDn) {
        try {
            ctx.getAttributes(userDn);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    //Get all user
    public NamingEnumeration<SearchResult> getAllUsers() throws Exception {
        LdapContext ctx = getLdapContext(); // Assume this method initializes the LDAP context
        String searchBase = "CN=Users,DC=mylab,DC=local";
        String searchFilter = "(objectClass=user)";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"distinguishedName"}); // Only fetch distinguishedName

        // Perform LDAP search
        return ctx.search(searchBase, searchFilter, searchControls);
    }

    // Show all groups
    public List<Map<String, String>> showAllGroups() throws Exception {
        LdapContext ctx = null;
        List<Map<String, String>> groups = new ArrayList<>();
        try {
            ctx = getLdapContext();
            String searchFilter = "(objectClass=group)";
            String searchBase = "cn=Users,dc=mylab,dc=local";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchControls);
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attributes = result.getAttributes();
                String groupName = attributes.get("cn").get().toString();
                String dn = result.getNameInNamespace();

                byte[] objectGUIDBytes = (byte[]) attributes.get("objectGUID").get();
                String formattedObjectGUID = convertObjectGUIDToUUID(objectGUIDBytes);

                Map<String, String> groupMap = new HashMap<>();
                groupMap.put("groupName", groupName);
                groupMap.put("DN", dn);
                groupMap.put("objectGUID", formattedObjectGUID);
                groups.add(groupMap);

            }
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
        return groups;
    }




    // Add user to group
    public void addUserToGroup(GroupDTO groupDTO) throws Exception {
        LdapContext ctx = null;
        try {
            ctx = getLdapContext();
            String userDn = "cn=" + groupDTO.getUsername() + ",cn=Users,dc=mylab,dc=local";
            String groupDn = "cn=" + groupDTO.getGroupName() + ",cn=Users,dc=mylab,dc=local";

            // Check if the user exists
            if (!userExists(ctx, userDn)) {
                throw new UserNotFoundException("User does not exist");
            }

            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod = new BasicAttribute("member", userDn);
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod);

            ctx.modifyAttributes(groupDn, mods);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    // Remove user from group
    public void removeUserFromGroup(GroupDTO groupDTO) throws Exception {
        LdapContext ctx = null;
        try {
            ctx = getLdapContext();
            String userDn = "cn=" + groupDTO.getUsername() + ",cn=Users,dc=mylab,dc=local";
            String groupDn = "cn=" + groupDTO.getGroupName() + ",cn=Users,dc=mylab,dc=local";

            // Check if the user exists
            if (!userExists(ctx, userDn)) {
                throw new UserNotFoundException("User does not exist");
            }

            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod = new BasicAttribute("member", userDn);
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod);

            ctx.modifyAttributes(groupDn, mods);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public List<UserDetailsDTO> getAllUserDetails() throws Exception {
        List<UserDetailsDTO> users = new ArrayList<>();
        LdapContext ctx = getLdapContext();
        String searchBase = "CN=Users,DC=mylab,DC=local";
        String searchFilter = "(objectClass=user)";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{
                "sAMAccountName", "distinguishedName", "userPrincipalName",
                "objectGUID", "givenName", "sn", "mail", "memberOf", "userAccountControl"
        });

        NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchControls);
        while (results.hasMore()) {
            SearchResult result = results.next();
            Attributes attributes = result.getAttributes();

            byte[] objectGUIDBytes = (byte[]) attributes.get("objectGUID").get();
            String formattedObjectGUID = convertObjectGUIDToUUID(objectGUIDBytes);

            List<Map<String, String>> memberOfList = new ArrayList<>();
            if (attributes.get("memberOf") != null) {
                NamingEnumeration<?> memberOfEnum = attributes.get("memberOf").getAll();
                while (memberOfEnum.hasMore()) {
                    Map<String, String> dnMap = new HashMap<>();
                    dnMap.put("DN", (String) memberOfEnum.next());
                    memberOfList.add(dnMap);
                }
            }

            UserDetailsDTO user = new UserDetailsDTO(
                    getAttributeValue(attributes, "sAMAccountName"),
                    getAttributeValue(attributes, "distinguishedName"),
                    getAttributeValue(attributes, "userPrincipalName"),
                    formattedObjectGUID,
                    getAttributeValue(attributes, "givenName"),
                    getAttributeValue(attributes, "sn"),
                    getAttributeValue(attributes, "mail"),
                    memberOfList,
                    getAttributeValue(attributes, "userAccountControl")
            );

            users.add(user);
        }
        ctx.close();
        return users;
    }

    //Get user details
    public UserDetailsDTO getUserDetails(String username) throws Exception {
        LdapContext ctx = getLdapContext();
        String dn = "cn=" + username + ",cn=Users,dc=mylab,dc=local";
        String[] attributeNames = {
                "sAMAccountName", "distinguishedName", "userPrincipalName",
                "objectGUID", "givenName", "sn", "mail", "memberOf", "userAccountControl"
        };
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes(attributeNames);
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> results = ctx.search(dn, "(objectClass=user)", controls);
        if (results.hasMore()) {
            Attributes attributes = results.next().getAttributes();
            byte[] objectGUIDBytes = (byte[]) attributes.get("objectGUID").get();
            String formattedObjectGUID = convertObjectGUIDToUUID(objectGUIDBytes);

            List<Map<String, String>> memberOfList = new ArrayList<>();
            if (attributes.get("memberOf") != null) {
                NamingEnumeration<?> memberOfEnum = attributes.get("memberOf").getAll();
                while (memberOfEnum.hasMore()) {
                    Map<String, String> dnMap = new HashMap<>();
                    dnMap.put("DN", (String) memberOfEnum.next());
                    memberOfList.add(dnMap);
                }
            }

            return new UserDetailsDTO(
                    getAttributeValue(attributes, "sAMAccountName"),
                    getAttributeValue(attributes, "distinguishedName"),
                    getAttributeValue(attributes, "userPrincipalName"),
                    formattedObjectGUID,
                    getAttributeValue(attributes, "givenName"),
                    getAttributeValue(attributes, "sn"),
                    getAttributeValue(attributes, "mail"),
                    memberOfList,
                    getAttributeValue(attributes, "userAccountControl")
            );
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    // Convert objectGUID to UUID
    private String convertObjectGUIDToUUID(byte[] objectGUID) {
        if (objectGUID == null || objectGUID.length != 16) {
            return "Invalid GUID format";
        }

        return String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
                objectGUID[3], objectGUID[2], objectGUID[1], objectGUID[0],
                objectGUID[5], objectGUID[4],
                objectGUID[7], objectGUID[6],
                objectGUID[8], objectGUID[9],
                objectGUID[10], objectGUID[11], objectGUID[12], objectGUID[13], objectGUID[14], objectGUID[15]);
    }

    private String getAttributeValue(Attributes attributes, String attributeName) {
        try {
            Attribute attribute = attributes.get(attributeName);
            if (attribute != null && attribute.get() != null) {
                return attribute.get().toString();
            }
        } catch (Exception e) {
            System.err.println("Error retrieving attribute " + attributeName + ": " + e.getMessage());
        }
        return null;
    }

}

