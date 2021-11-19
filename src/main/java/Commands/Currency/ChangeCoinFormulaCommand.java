package Commands.Currency;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class ChangeCoinFormulaCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Add a new Role to the shop.";
    }

    @Override
    public String getCommand() {
        return "coin-earning-amount";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.INTEGER, "amount", "Amount of Coins a user gets when he/she writes a message!", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        long amount = Long.parseLong(event.getOption("amount").getAsString());
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Changed earning Amount");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());


        if (event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS) && event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            if (amount < 0 || amount > 10000) {
                eb.setColor(Color.decode("#27ae60"));
                eb.setTitle("Earning not in range");
                eb.setDescription("<a:alertsign:864083960886853683> Earnings must be between 0 and 10000");
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                return;
            }

            if (DatabaseUtil.changeCoinFormula(event.getGuild().getIdLong(), amount))
                eb.setDescription("Successfully updated earnings to **" + amount + "**!");
            else {
                eb.setColor(Color.decode("#27ae60"));
                eb.setTitle("Unknown Error");
                eb.setDescription("<a:alertsign:864083960886853683> Couldn't connect to the Database!");
            }

        } else {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Cannot Change Earning Rate");
            eb.setDescription("<a:alertsign:864083960886853683> You do not have sufficient permissions!");
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
