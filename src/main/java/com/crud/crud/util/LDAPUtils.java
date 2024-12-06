//package com.crud.crud.util;
//
//import javax.naming.directory.Attributes;
//import javax.naming.directory.Attribute;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.UUID;
//
//public class LDAPUtils {
//    public static String convertObjectGUIDToUUID(byte[] objectGUID) {
//        if (objectGUID == null || objectGUID.length != 16) {
//            return "Invalid GUID format";
//        }
//        ByteBuffer bb = ByteBuffer.wrap(objectGUID);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        long firstLong = bb.getLong();
//        long secondLong = bb.getLong();
//        UUID uuid = new UUID(firstLong, secondLong);
//        return uuid.toString();
//    }
//
//    public static String getAttributeValue(Attributes attributes, String attributeName) {
//        try {
//            Attribute attribute = attributes.get(attributeName);
//            if (attribute != null && attribute.get() != null) {
//                return attribute.get().toString();
//            }
//        } catch (Exception e) {
//            System.err.println("Error retrieving attribute " + attributeName + ": " + e.getMessage());
//        }
//        return null;
//    }
//}
