package Base.Util;

import Base.Bot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtil {

    private static final String serverTable = "servers";
    private static final String coinsTable = "user_coins";
    private static final String shopTable = "shop_items";

    public static boolean changeBalance(User user, Long guildID, int amount) {

        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();

            String s = String.format("UPDATE %s set coin_amount=coin_amount+%d where `server_id` like '%d' and user_id like '%d'", coinsTable, amount, guildID, user.getIdLong());

            statement.executeUpdate(s);
            return true;
        } catch (SQLException e) {
            System.err.println("Increase Currency Error:\n" + e);
            return true;
        }
    }

    public static int getCoinFormula(Long guildID) {
        int addition = 0;
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + serverTable + " where server_id like " + guildID);
            if (resultSet.next()) {
                addition = resultSet.getInt(4);
            } else {
                throw new SQLException();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return addition;
    }

    public static boolean changeCoinFormula(Long guildID, int formula) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();

            String s = String.format("UPDATE %s set coin_formula=%d where `server_id` like '%d'", serverTable, formula, guildID);
            statement.executeUpdate(s);
            return true;

        } catch (SQLException e) {
            System.err.println("Increase Currency Error:\n" + e);
            return false;
        }
    }

    public static boolean userExists(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from %s where user_id like '%d' and server_id like '%d'", coinsTable, user.getIdLong(), guildID));
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check user Error:\n" + e);
            return true;
        }
    }

    public static int userBalance(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from %s where user_id like '%d' and server_id like '%d'", coinsTable, user.getIdLong(), guildID));
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
            String s = String.format("INSERT INTO %s (user_id, server_id, coin_amount) values ( '%d', '%s', '%d')", coinsTable, user.getIdLong(), guildID, 0);
            statement.executeUpdate(s);
            System.out.println("Created new user in DB: " + user.getName());
        } catch (SQLException e) {
            System.err.println("Add User Error:\n" + e);
        }
    }

    public static boolean serverExists(Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from %s where server_id like %d ", serverTable, guildID));
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return true;
        }
    }

    public static ArrayList<Long> getAllServers() {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + serverTable);
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
            String s = String.format("INSERT INTO %s (server_id, server_name, date_joined) values ( '%d', '%s', now())", serverTable, guildID, guildName);
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
            String s = String.format("INSERT INTO %s (server_id, role, description, price) values ( '%d', '%d', '%s', '%d')", serverTable, guildID, role.getIdLong(), description, intPrice);
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
            String s = String.format("select * from %s where server_id like '%d' and role like '%d'", shopTable, guildID, roleID);
            ResultSet resultSet = statement.executeQuery(s);
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Shop Item Check Error:\n" + e);
            return true;
        }
    }

    public static int getPriceOfShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            String s = String.format("select * from %s where server_id like '%d' and role like '%d'", shopTable, guildID, roleID);
            ResultSet resultSet = statement.executeQuery(s);
            resultSet.next();
            return resultSet.getInt(5);
        } catch (SQLException e) {
            System.err.println("Shop Item Check Error:\n" + e);
            return -1;
        }
    }

    public static boolean deleteShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            String s = String.format("delete from %s where server_id like '%d' and role like '%d'", shopTable, guildID, roleID);
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
            ResultSet resultSet = statement.executeQuery(String.format("select * from %s where server_id like %d", shopTable, guildID));
            Map<String, String> servers = new HashMap<>();
            while (resultSet.next()) {
                servers.put(resultSet.getLong(3) + "_" + resultSet.getInt(5), resultSet.getString(4));
            }
            return servers;
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return null;
        }
    }
}
