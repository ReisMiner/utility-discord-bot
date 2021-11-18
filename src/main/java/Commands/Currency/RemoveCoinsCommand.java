package Commands.Currency;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class RemoveCoinsCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Mod Command: Remove coins from a user";
    }

    @Override
    public String getCommand() {
        return "remove";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.INTEGER, "amount", "Amount of coins you wanna add", true));
        args.add(new SlashCommandArgs(OptionType.USER, "recipient", "To whom you wanna add coins", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        int amount = Integer.parseInt(event.getOption("amount").getAsString());
        Member recipient = event.getOption("recipient").getAsMember();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Removed Coins");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (recipient.getUser().isBot()) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Cannot remove coins from Bot");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot remove coins from Bots!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (amount < 1) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Too few Coins");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot remove less than 1 coin!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS) && event.getMember().hasPermission(Permission.MANAGE_ROLES)) {

            if (!DatabaseUtil.userExists(recipient.getUser(), event.getGuild().getIdLong())) {
                DatabaseUtil.addNewUser(recipient.getUser(), event.getGuild().getIdLong());
                eb.setTitle("No Balance");
                eb.setColor(Color.decode("#c0392b"));
                eb.setDescription("<a:alertsign:864083960886853683> The user " + recipient.getAsMention() + " has no balance!");
                return;
            }

            int bal = DatabaseUtil.userBalance(recipient.getUser(), event.getGuild().getIdLong());

            if (bal - amount < 0) {
                eb.setTitle("Not enough Balance");
                eb.setColor(Color.decode("#c0392b"));
                eb.setDescription("<a:alertsign:864083960886853683> The user " + recipient.getAsMention() + " has not enough balance (" + bal + " Coins)!");
                return;
            }
            if (DatabaseUtil.changeBalance(recipient.getUser(), event.getGuild().getIdLong(), amount * -1)) {
                eb.setDescription("\uD83E\uDE99 Successfully removed **" + amount + "** coins from **" + recipient.getUser().getAsMention() + "**\n"
                        + (bal - amount) + " Coins are left!");
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                return;
            }

            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Unknown Error");
            eb.setDescription("<a:alertsign:864083960886853683> Could not connect to the Database!");
        } else {
            // NO PERMS
            eb.setTitle("No Permissions");
            eb.setColor(Color.decode("#c0392b"));
            eb.setDescription("<a:alertsign:864083960886853683> You have no permissions to do this!");
        }
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
