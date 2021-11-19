package Commands.Currency;

import Base.SlashCommand;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class BankBalanceCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Get the balance of the servers bank";
    }

    @Override
    public String getCommand() {
        return "bank-balance";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Balance of the bank");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        long bal = DatabaseUtil.bankBalance(event.getGuild().getIdLong());
        if (bal >= 0)
            eb.setDescription("\uD83D\uDCB8 **Balance: **" + bal + "\n\nThe bank gets all the money which is spent at the shop!");
        else {
            eb.setColor(Color.decode("#c0392b"));
            eb.setTitle("Balance Error");
            eb.setDescription("<a:alertsign:864083960886853683> Database Error!\nCouldn't fetch the balance of the bank!");
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
