package Commands;

import Base.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class SourceCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "View the Source Code of the bot on Github!";
    }

    @Override
    public String getCommand() {
        return "source";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Click here to view the source!", "https://github.com/ReisMiner/codeclasher-discord-bot");
        eb.setDescription("Make a pull request if you found some issues!");
        eb.setFooter("Made by ReisMiner#1111 - https://reisminer.xyz");
        eb.setColor(Color.decode("#7289da"));
        event.replyEmbeds(eb.build()).queue();
    }
}
