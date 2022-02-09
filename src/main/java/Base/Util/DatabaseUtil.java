package Base.Util;

import Base.Bot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DatabaseUtil {

    private static final String serverTable = "servers";
    private static final String coinsTable = "user_coins";
    private static final String shopTable = "shop_items";

    public static boolean changeBalance(User user, Long guildID, long amount) {

        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {

            PreparedStatement statement = connection.prepareStatement("UPDATE " + coinsTable + " set coin_amount = coin_amount+? where server_id like ? and user_id like ?");
            statement.setString(1, String.valueOf(amount));
            statement.setString(2, String.valueOf(guildID));
            statement.setString(3, String.valueOf(user.getIdLong()));

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Increase Currency Error:\n" + e);
            return false;
        }
    }

    public static boolean changeBankBalance(Long guildID, long amount) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {

            PreparedStatement statement = connection.prepareStatement("UPDATE " + serverTable + " set bank_balance=bank_balance+? where `server_id` like ?");
            statement.setString(1, String.valueOf(amount));
            statement.setString(2, String.valueOf(guildID));

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Change bank bal Error:\n" + e);
            return false;
        }

    }

    public static long bankBalance(Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + serverTable + " where server_id like ?");
            statement.setString(1, String.valueOf(guildID));

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(5);
        } catch (SQLException e) {
            System.err.println("Check bank bal Error:\n" + e);
            return -1;
        }
    }

    public static long getCoinFormula(Long guildID) {
        long addition = 0;
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + serverTable + " where server_id like ?");
            statement.setString(1, String.valueOf(guildID));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                addition = resultSet.getLong(4);
            } else {
                throw new SQLException();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return addition;
    }

    public static boolean changeCoinFormula(Long guildID, long formula) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + serverTable + " set coin_formula=? where `server_id` like ?");
            statement.setString(1, String.valueOf(formula));
            statement.setString(2, String.valueOf(guildID));

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Increase Currency Error:\n" + e);
            return false;
        }
    }

    public static boolean userExists(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + coinsTable + " where user_id like ? and server_id like ?");
            statement.setString(1, String.valueOf(user.getIdLong()));
            statement.setString(2, String.valueOf(guildID));
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check user Error:\n" + e);
            return true;
        }
    }

    public static long userBalance(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + coinsTable + " where user_id like ? and server_id like ?");
            statement.setString(1, String.valueOf(user.getIdLong()));
            statement.setString(2, String.valueOf(guildID));
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            return resultSet.getLong(3);
        } catch (SQLException e) {
            System.err.println("Check user bal Error:\n" + e);
            return -1;
        }
    }

    public static void addNewUser(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + coinsTable + " (user_id, server_id, coin_amount) values ( ?, ?, ?)");
            statement.setString(1, String.valueOf(user.getIdLong()));
            statement.setString(2, String.valueOf(guildID));
            statement.setString(3, String.valueOf(0));

            statement.executeUpdate();
            System.out.println("Created new user in DB: " + user.getName() + ", " + user.getIdLong() + ". Server: " + guildID);
        } catch (SQLException e) {
            System.err.println("Add User Error:\n" + e);
        }
    }

    public static boolean serverExists(Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + serverTable + " where server_id like ?");
            statement.setString(1, String.valueOf(guildID));

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return true;
        }
    }

    public static Map<Long, Long> getLeaderboard(long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + coinsTable + " where server_id like ? order by coin_amount desc");
            statement.setString(1, String.valueOf(guildID));

            ResultSet resultSet = statement.executeQuery();
            Map<Long, Long> person = new TreeMap<>();
            while (resultSet.next()) {
                person.put(resultSet.getLong(1), resultSet.getLong(3));
            }
            return person;
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return null;
        }
    }

    public static ArrayList<Long> getAllServers() {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + serverTable);
            ResultSet resultSet = statement.executeQuery();
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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + serverTable + " (server_id, server_name, date_joined) values ( ?, ?, now())");
            statement.setString(1, String.valueOf(guildID));
            statement.setString(2, String.valueOf(guildName));
            statement.executeUpdate();
            System.out.println("Created new server in DB: " + guildID + ", " + guildName);
        } catch (SQLException e) {
            System.err.println("Add server Error:\n" + e);
        }
    }

    public static boolean addNewShopItem(Long guildID, Role role, String description, String price) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            long intPrice = Long.parseLong(price);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + shopTable + " (server_id, role, description, price) values ( ?, ?, ?, ?)");
            statement.setString(1, String.valueOf(guildID));
            statement.setString(2, String.valueOf(role.getIdLong()));
            statement.setString(3, description);
            statement.setString(4, String.valueOf(intPrice));
            statement.executeUpdate();
            System.out.println("Created new shop item for " + guildID);
            return true;
        } catch (SQLException e) {
            System.err.println("Shop Item Create Error:\n" + e);
            return false;
        }
    }

    public static boolean checkShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + shopTable + " where server_id like ? and role like ?");
            statement.setString(1, String.valueOf(guildID));
            statement.setString(2, String.valueOf(roleID));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Shop Item Check Error:\n" + e);
            return true;
        }
    }

    public static long getPriceOfShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + shopTable + " where server_id like ? and role like ?");
            statement.setString(1, String.valueOf(guildID));
            statement.setString(2, String.valueOf(roleID));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getLong(5);
        } catch (SQLException e) {
            System.err.println("Shop Item Check Error:\n" + e);
            return -1;
        }
    }

    public static boolean deleteShopItem(Long guildID, Long roleID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("delete from " + shopTable + " where server_id like ? and role like ?");
            statement.setString(1, String.valueOf(guildID));
            statement.setString(2, String.valueOf(roleID));
            statement.executeUpdate();
            System.out.println("Deleted shop item for " + guildID);
            return true;
        } catch (SQLException e) {
            System.err.println("Shop Item Create Error:\n" + e);
            return false;
        }
    }

    public static Map<String[], Long> getAllShopItemsFromGuild(long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            PreparedStatement statement = connection.prepareStatement("select * from " + shopTable + " where server_id like ?");
            statement.setString(1, String.valueOf(guildID));
            ResultSet resultSet = statement.executeQuery();
            Map<String[], Long> servers = new HashMap<>();
            while (resultSet.next()) {
                servers.put(new String[]{String.valueOf(resultSet.getLong(3)), resultSet.getString(4)}, resultSet.getLong(5));
            }
            return servers;
        } catch (SQLException e) {
            System.err.println("Check server Error:\n" + e);
            return null;
        }
    }
}
