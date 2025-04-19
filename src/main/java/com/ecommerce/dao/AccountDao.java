package com.ecommerce.dao;

import com.ecommerce.database.Database;
import com.ecommerce.entity.Account;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Base64;

public class AccountDao {

    // Method to get blob image from database (helper method)
    private String getBase64Image(Blob blob) throws SQLException, IOException {
        try (InputStream inputStream = blob.getBinaryStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    // Method to execute get account query (helper method)
    private Account queryGetAccount(String query) {
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (connection == null) {
                System.err.println("AccountDao.queryGetAccount: Database connection is null!");
                return null;
            }

            if (resultSet.next()) {
                Account account = new Account();
                account.setId(resultSet.getInt("account_id"));
                account.setUsername(resultSet.getString("account_name"));
                account.setPassword(resultSet.getString("account_password")); // Insecure to store raw password
                account.setIsSeller(resultSet.getInt("account_is_seller"));
                account.setIsAdmin(resultSet.getInt("account_is_admin"));
                account.setAddress(resultSet.getString("account_address"));
                account.setFirstName(resultSet.getString("first_name")); // Or whatever your column names are
                account.setLastName(resultSet.getString("last_name"));
                account.setEmail(resultSet.getString("account_email"));
                account.setPhone(resultSet.getString("account_phone"));
               // account.setSummary(resultSet.getString("summary")); // Assuming you have a summary column

                Blob blob = resultSet.getBlob("profile_image"); // Or account_image
                if (blob != null) {
                    account.setBase64Image(getBase64Image(blob));
                } else {
                    account.setBase64Image(null); // Explicitly set to null if no image
                }
                return account;
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error in AccountDao.queryGetAccount: " + e.getMessage());
            e.printStackTrace(); // Log the full error
        }
        return null;
    }

    // Method to get account by id.
    public Account getAccount(int accountId) { // Or getAccountById if you prefer
        String query = "SELECT * FROM account WHERE account_id = ?";
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("AccountDao.getAccount: Database connection is null!");
                return null;
            }

            preparedStatement.setInt(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Account account = new Account();
                    // ... populate account fields from resultSet as in queryGetAccount ...
                    return account;
                }
            }
        } catch (SQLException  e) {
            System.err.println("Error in AccountDao.getAccount: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    // Method to get login account from database.
    public Account checkLoginAccount(String username, String password) {
        String query = "SELECT * FROM account WHERE account_name = ? AND account_password = ?"; // Use placeholders
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("AccountDao.checkLoginAccount: Database connection is null!");
                return null;
            }

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Account account = new Account();
                    // ... populate account fields from resultSet ...
                    return account;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in AccountDao.checkLoginAccount: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Method to check is username exist or not.
    public boolean checkUsernameExists(String username) {
        String query = "SELECT * FROM account WHERE account_name = ?"; // Placeholder
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("AccountDao.checkUsernameExists: Database connection is null!");
                return false; // Or throw an exception
            }

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a row is found (username exists)
            }
        } catch (SQLException e) {
            System.err.println("Error in AccountDao.checkUsernameExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to create an account.
    public void createAccount(String username, String password, InputStream image) {
        String query = "INSERT INTO account (account_name, account_password, profile_image, account_is_seller, account_is_admin, summary) VALUES (?, ?, ?, 0, 0, ?)"; // Added summary, assuming it's nullable
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("AccountDao.createAccount: Database connection is null!");
                return; // Or throw an exception
            }

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setBinaryStream(3, image);
            preparedStatement.setString(4, ""); // Summary default value, or null if column allows
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in AccountDao.createAccount: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to edit profile information.
    public void editProfileInformation(int accountId, String firstName, String lastName, String address, String email, String phone, String summary, InputStream inputStream) {
        String query = "UPDATE account SET first_name = ?, last_name = ?, address = ?, email = ?, phone = ?, summary = ?, profile_image = ? WHERE account_id = ?";
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("AccountDao.editProfileInformation: Database connection is null!");
                return; // Or throw an exception
            }

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, summary);
            preparedStatement.setBinaryStream(7, inputStream);
            preparedStatement.setInt(8, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in AccountDao.editProfileInformation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to update profile information (without image and summary - might be redundant if editProfileInformation handles it all)
    public void updateProfileInformation(int accountId, String firstName, String lastName, String address, String email, String phone) {
        String query = "UPDATE account SET first_name = ?, last_name = ?, address = ?, email = ?, phone = ? WHERE account_id = ?";
        try (Connection connection = new Database().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("AccountDao.updateProfileInformation: Database connection is null!");
                return;
            }

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setInt(6, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in AccountDao.updateProfileInformation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}