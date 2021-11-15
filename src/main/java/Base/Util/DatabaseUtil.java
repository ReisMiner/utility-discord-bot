package Base.Util;

import Base.Bot;
import net.dv8tion.jda.api.entities.User;

import java.sql.*;

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
            String s = String.format("UPDATE t_user_coins set coin_amount=coin_amount+%d where `server_id` like '%d' and user_id like '%d'", 1 + addition, guildID, user.getIdLong());

            statement.executeUpdate(s);

        } catch (SQLException e) {
            System.err.println("DB Error:\n" + e);
        }
    }

    public static boolean userExists(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from t_user_coins where user_id like '%d' and server_id like '%d'", user.getIdLong(), guildID));
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check user: DB Error:\n" + e);
            return true;
        }
    }

    public static void addNewUser(User user, Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            // Result set get the result of the SQL query
            String s = String.format("INSERT INTO t_user_coins (user_id, server_id, coin_amount) values ( '%d', '%s', '%d')", user.getIdLong(), guildID, 0);
            statement.executeUpdate(s);
            System.out.println("Created new user in DB: " + user.getName());
        } catch (SQLException e) {
            System.err.println("Add User: DB Error:\n" + e);
        }
    }

    public static boolean serverExists(Long guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select * from t_servers where server_id like %d ", guildID));
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Check user: DB Error:\n" + e);
            return true;
        }
    }

    public static void addNewServer(Long guildID, String guildName) {
        try (Connection connection = DriverManager.getConnection(Bot.DB_HOST, Bot.DB_USER, Bot.DB_PW)) {
            Statement statement = connection.createStatement();

            String s = String.format("INSERT INTO t_servers (server_id, server_name, date_joined) values ( '%d', '%s', now())", guildID, guildName);
            statement.executeUpdate(s);
            System.out.println("Created new server in DB: " + guildID + ", " + guildName);
        } catch (SQLException e) {
            System.err.println("Add User: DB Error:\n" + e);
        }
    }
}
