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

public class AddCoinsCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Mod Command: Add Coins to a user";
    }

    @Override
    public String getCommand() {
        return "give";
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
        eb.setTitle("Added Coins");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (recipient.getUser().isBot()) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Cannot add coins to Bot");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot add coins to Bots!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (amount < 1) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Too few Coins");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot add less than 1 coin!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS) && event.getMember().hasPermission(Permission.MANAGE_ROLES)) {

            if (!DatabaseUtil.userExists(recipient.getUser(), event.getGuild().getIdLong())) {
                DatabaseUtil.addNewUser(recipient.getUser(), event.getGuild().getIdLong());
            }

            if (DatabaseUtil.changeBalance(recipient.getUser(), event.getGuild().getIdLong(), amount)) {
                eb.setDescription("\uD83E\uDE99 Successfully added **" + amount + "** coins to **" + recipient.getUser().getAsMention() + "**");
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
