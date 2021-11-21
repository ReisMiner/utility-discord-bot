package Commands.Currency;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class GiftCoinsCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Gift some of your coins to another user";
    }

    @Override
    public String getCommand() {
        return "gift";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.INTEGER, "amount", "Amount of Coins you wanna send", true));
        args.add(new SlashCommandArgs(OptionType.USER, "recipient", "To whom you wanna send the coins", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        long amount = Long.parseLong(event.getOption("amount").getAsString());
        Member recipient = event.getOption("recipient").getAsMember();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Gifted Coins");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (recipient.getUser().getIdLong() == event.getUser().getIdLong()) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Cannot send to yourself");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot send coins to yourself!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (recipient.getUser().isBot()) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Cannot send to Bot");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot send coins to Bots!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (amount < 1) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Too few Coins");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot send less than 1 coin!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        long bal = DatabaseUtil.userBalance(event.getUser(), event.getGuild().getIdLong());

        if (bal < amount) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Not enough funds");
            eb.setDescription("<a:alertsign:864083960886853683> You are too poor to send **" + amount + "** coins.\n" +
                    "You currently have **" + bal + "** coins!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (!DatabaseUtil.userExists(recipient.getUser(), event.getGuild().getIdLong())) {
            DatabaseUtil.addNewUser(recipient.getUser(), event.getGuild().getIdLong());
        }

        if (DatabaseUtil.changeBalance(event.getUser(), event.getGuild().getIdLong(), amount * -1)) {
            if (DatabaseUtil.changeBalance(recipient.getUser(), event.getGuild().getIdLong(), amount)) {
                eb.setDescription("\uD83E\uDE99 Successfully gifted **" + amount + "** coins to **" + recipient.getUser().getAsMention() + "**");
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                return;
            }
        }
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Unknown Error");
        eb.setDescription("<a:alertsign:864083960886853683> Could not connect to the Database!");
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
