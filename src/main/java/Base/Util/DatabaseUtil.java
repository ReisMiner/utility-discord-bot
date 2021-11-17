package Base.Util;

import Base.Bot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtil {
    public static void increaseCurrency(User user, Long guildID) {
        int addition;
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from t_servers where server_id like " + guildID);

            if (resultSet.next()) {
                addition = resultSet.getInt(4);
            } else {
                throw new SQLException();
            }
            String s = String.format("UPDATE t_user_coins set coin_amount=coin_amount+%d where `server_id` like '%d' and user_id like '%d'", addition, guildID, user.getIdLong());

            statement.executeUpdate(s);

        } catch (SQLException e) {
            System.err.println("Increase Currency Error:\n" + e);
        }
    }

    public static boolean userExists(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from t_user_coins where user_id like '%d' and server_id like '%d'", user.getIdLong(), guildID));
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check user Error:\n" + e);
            return true;
        }
    }

    public static int userBalance(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from t_user_coins where user_id like '%d' and server_id like '%d'", user.getIdLong(), guildID));
            resultSet.next();
            return resultSet.getInt(3);
        } catch (SQLException e) {
            System.err.println("Check user bal Error:\n" + e);
            return -1;
        }
    }

    public static void addNewUser(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            String s = String.format("INSERT INTO t_user_coins (user_id, server_id, coin_amount) values ( '%d', '%s', '%d')", user.getIdLong(), guildID, 0);
            statement.executeUpdate(s);
            System.out.println("Created new user in DB: " + user.getName());
        } catch (SQLException e) {
            System.err.println("Add User Error:\n" + e);
        }
    }

    public static boolean serverExists(Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from t_servers where server_id like %d ", guildID));
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return true;
        }
    }

    public static ArrayList<Long> getAllServers() {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from t_servers");
            ArrayList<Long> servers = new ArrayList<>();
            while (resultSet.next()) {
                servers.add(resultSet.getLong(1));
            }
            return servers;
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return null;
        }
    }

    public static void addNewServer(Long guildID, String guildName) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            String s = String.format("INSERT INTO t_servers (server_id, server_name, date_joined) values ( '%d', '%s', now())", guildID, guildName);
            statement.executeUpdate(s);
            System.out.println("Created new server in DB: " + guildID + ", " + guildName);
        } catch (SQLException e) {
            System.err.println("Add server Error:\n" + e);
        }
    }

    public static boolean addNewShopItem(Long guildID, Role role, String description, String price) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            int intPrice = Integer.parseInt(price);
            Statement statement = connection.createStatement();
            String s = String.format("INSERT INTO t_shop_items (server_id, role, description, price) values ( '%d', '%d', '%s', '%d')", guildID, role.getIdLong(), description, intPrice);
            statement.executeUpdate(s);
            System.out.println("Created new shop item for " + guildID);
            return true;
        } catch (SQLException e) {
            System.err.println("Shop Item Create Error:\n" + e);
            return false;
        }
    }

    public static boolean checkShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            String s = String.format("select * from t_shop_items where server_id like '%d' and role like '%d'", guildID, roleID);
            ResultSet resultSet = statement.executeQuery(s);
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Shop Item Create Error:\n" + e);
            return true;
        }
    }

    public static boolean deleteShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            String s = String.format("delete from t_shop_items where server_id like '%d' and role like '%d'", guildID, roleID);
            statement.executeUpdate(s);
            System.out.println("Deleted shop item for " + guildID);
            return true;
        } catch (SQLException e) {
            System.err.println("Shop Item Create Error:\n" + e);
            return false;
        }
    }

    public static Map<String, String> getAllShopItemsFromGuild(long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from t_shop_items where server_id like %d", guildID));
            Map<String, String> servers = new HashMap<>();
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(5));
                servers.put(resultSet.getLong(3) + "_" + resultSet.getInt(5), resultSet.getString(4));
            }
            return servers;
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return null;
        }
    }
}
