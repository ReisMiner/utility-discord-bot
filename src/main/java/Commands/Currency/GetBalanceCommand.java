package Commands.Currency;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class GetBalanceCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Get your/someone else's balance for this server!";
    }

    @Override
    public String getCommand() {
        return "balance";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.USER, "member", "The Person you wanna check the balance from", false));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        User user;

        if (event.getOption("member") != null)
            user = event.getOption("member").getAsUser();
        else {
            user = event.getUser();
        }

        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Balance of " + user.getAsTag());
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (DatabaseUtil.userExists(user, event.getGuild().getIdLong())) {
            int bal = DatabaseUtil.userBalance(user, event.getGuild().getIdLong());
            if (bal >= 0)
                eb.setDescription("\uD83D\uDCB8 **Balance: **" + bal);
            else {
                eb.setColor(Color.decode("#c0392b"));
                eb.setTitle("Balance Error");
                eb.setDescription("<a:alertsign:864083960886853683> Database Error!\nCouldn't fetch the balance of *" + user.getAsTag() + "*");
            }
        } else {
            eb.setDescription("Couldn't find user *" + user.getAsTag() + "* in the Database.\nThey probably never wrote a message!");
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
