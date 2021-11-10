package Commands.Utils;

import Base.BotUtils;
import Base.ReadEmbedded;
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
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "is-message-link", "Is your just entered message a link to a message?", false));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        ReadEmbedded out = new ReadEmbedded();
        EmbedBuilder eb = new EmbedBuilder();
        String message = event.getOption("message").getAsString();

        try {
            if (event.getOption("mode").getAsBoolean()) {
                if (event.getOption("is-message-link") == null || !event.getOption("is-message-link").getAsBoolean()) {
                    out.setText(new String(java.util.Base64.getEncoder().encode(message.getBytes(StandardCharsets.UTF_8))));
                } else {
                    out = BotUtils.textFromMsgLink(message, event);
                    out.setText(new String(java.util.Base64.getEncoder().encode(out.getText().getBytes(StandardCharsets.UTF_8))));
                }
            } else {
                if (event.getOption("is-message-link") == null || !event.getOption("is-message-link").getAsBoolean()) {
                    out.setText(new String(java.util.Base64.getDecoder().decode(message.getBytes(StandardCharsets.UTF_8))));
                } else {
                    out = BotUtils.textFromMsgLink(message, event);
                    out.setText(new String(java.util.Base64.getDecoder().decode(out.getText().getBytes(StandardCharsets.UTF_8))));
                }
            }
            eb.setDescription("```\n" + out.getText() + "\n```");
            eb.setColor(Color.decode("#3498db"));
        } catch (Exception e) {
            out.setText("<a:alertsign:864083960886853683> Couldn't Encode/Decode the given Message into/from Base64!");
            eb.setDescription(out.getText());
            eb.setColor(Color.decode("#c0392b"));
        }


        eb.setTitle(event.getOption("mode").getAsBoolean() ? "Encoded Base64" : "Decoded Base64");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
