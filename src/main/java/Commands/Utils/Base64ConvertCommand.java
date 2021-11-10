package Commands.Utils;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;


import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class Base64ConvertCommand extends SlashCommand {

    @Override
    public String getDescription() {
        return "Encode/Decode Base64 Messages!";
    }

    @Override
    public String getCommand() {
        return "base64";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "mode", "Encode = true, Decode = false", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "message", "Your Message", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        String output;
        event.deferReply().queue();

        if (event.getOption("mode").getAsBoolean()) {
            output = new String(java.util.Base64.getEncoder().encode(event.getOption("message").getAsString().getBytes(StandardCharsets.UTF_8)));
        }else{
            output = new String(java.util.Base64.getDecoder().decode(event.getOption("message").getAsString().getBytes(StandardCharsets.UTF_8)));
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#3498db"));
        eb.setTitle(event.getOption("mode").getAsBoolean() ? "Encoded Base64" : "Decoded Base64");
        eb.setDescription("```\n"+output+"\n```");
        eb.setFooter("Query performed by "+event.getMember().getUser().getAsTag());
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
