package Commands.Utils;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class ReplaceTextCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "find and replace text in text";
    }

    @Override
    public String getCommand() {
        return "replace";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.STRING, "text", "input text", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "search", "search for", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "replacement", "replace with ...", true));
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "trailing-space", "add a trailing space to the replacement input", false));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#74b659"));
        eb.setTitle("Modified Text");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        String text = event.getOption("text").getAsString();
        String character = event.getOption("search").getAsString();
        String r = event.getOption("replacement").getAsString();
        boolean space = false;
        if (event.getOption("trailing-space") != null)
            if (event.getOption("trailing-space").getAsBoolean())
                space = true;


        String output = text.replace(character, space ? r + " " : r);

        eb.setDescription(output);
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

    }
}
