package com.businesschat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BusinessProfileDAO {
    // Insert a new business profile
    public boolean insertBusinessProfile(String name, String category, String chatLink) {
        String query = "INSERT INTO business_profiles (name, category, chat_link) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, chatLink);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all business profiles
    public List<BusinessProfile> getAllBusinessProfiles() {
        String query = "SELECT * FROM business_profiles";
        List<BusinessProfile> profiles = new ArrayList<>();
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BusinessProfile profile = new BusinessProfile();
                profile.setId(rs.getInt("id"));
                profile.setName(rs.getString("name"));
                profile.setCategory(rs.getString("category"));
                profile.setChatLink(rs.getString("chat_link"));
                profiles.add(profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profiles;
    }
}
