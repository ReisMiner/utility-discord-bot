package Commands;

import Base.BotUtils;
import Base.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class InviteCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Get an invite for the bot";
    }

    @Override
    public String getCommand() {
        return "invite";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Click here to invite the bot!", "https://discord.com/api/oauth2/authorize?client_id=897819560902787133&permissions=131072&scope=bot%20applications.commands");
        eb.setDescription("Have fun with the bot. I hope you enjoy it :)");
        eb.setFooter("Made by " + BotUtils.getContributor());
        eb.setColor(Color.decode("#7289da"));
        event.replyEmbeds(eb.build()).queue();
    }
}
