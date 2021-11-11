package Commands.Utils;

import Base.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HexConvertCommand extends SlashCommand {

    @Override
    public String getDescription() {
        return "Encode/Decode Hex Messages!";
    }

    @Override
    public String getCommand() {
        return "hex";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "mode", "Encode = true, Decode = false", true));
        args.add(new SlashCommandArgs(OptionType.INTEGER, "type", "Ignored when decoding. Text = 0, Int = 1, Float = 2", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "message", "Your Message, can also be a link to a Discord Message", true));
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "is-message-link", "Is your just entered message a link to a message?", false));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {

        ReadEmbedded out = new ReadEmbedded();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#9b59b6"));
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        boolean doSplit = true;
        String typePossibilities = "0 1 2";
        event.deferReply().queue();
        String message = event.getOption("message").getAsString();

        if (event.getOption("mode").getAsBoolean()) {
            //exit if specified number is not in range
            if (!typePossibilities.contains(event.getOption("type").getAsString())) {
                eb.setColor(Color.decode("#c0392b"));
                eb.setTitle("Wrong Type!");
                eb.setDescription("<a:alertsign:864083960886853683> Please Check your specified type in the command and adjust it!" +
                        "\n\nFor Text write a 0\nFor a number **without** floating point write a 1\nFor a number **with** floating point write a 2\nThis input is **ignored** when decoding!");
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                return;
            }

            if (event.getOption("is-message-link") != null) {
                if (event.getOption("is-message-link").getAsBoolean()) {

                    out = BotUtils.textFromMsgLink(message, event);

                    if (out.getText().equals("error"))
                        return;

                    message = out.getText();

                }
            }

            try {
                switch (event.getOption("type").getAsString()) {
                    case "0":
                        out.setText(Hex.encodeHexString(message.getBytes(StandardCharsets.UTF_8)));
                        break;
                    case "1":
                        out.setText(Integer.toHexString(Integer.parseInt(message)));
                        break;
                    case "2":
                        out.setText(Float.toHexString(Float.parseFloat(message)));
                        break;
                }
            } catch (NumberFormatException e) {
                out.setText("<a:alertsign:864083960886853683> Couldn't Encode the given Message!");
                eb.setColor(Color.decode("#c0392b"));
                doSplit = false;
            }

            if (doSplit) {
                out.setText(out.getText().replaceAll("..", "$0 ").toUpperCase());
                eb.setDescription("```\n" + out.getText() + "\n```");
            } else {
                eb.setDescription(out.getText());
            }
        } else {
            //===================
            //DECODE HEX
            //===================
            if (event.getOption("is-message-link") != null) {
                if (event.getOption("is-message-link").getAsBoolean()) {

                    out = BotUtils.textFromMsgLink(message, event);

                    if (out.getText().equals("error"))
                        return;

                    if (out.getEmbedded())
                        out.setText(out.getText().replaceAll("`", "").replaceAll("\n", ""));

                    message = out.getText();
                }
            }

            message = message.replaceAll("\\s+", "");
            out.setText("");

            try {
                String hex_text = new String(Hex.decodeHex(message), StandardCharsets.UTF_8);
                if (hex_text.contains("`")) {
                    hex_text = hex_text.replaceAll("`", "`\u200B");
                }
                out.setText(out.getText() + String.format("\nText: ```\n%s\n```", hex_text));
            } catch (DecoderException ignore) {
            }
            try {
                out.setText(out.getText() + String.format("\nDecimal: ```\n%d\n```", Integer.parseInt(message, 16)));
            } catch (NumberFormatException ignore) {
            }
            try {
                out.setText(out.getText() + String.format("\nFloat: ```\n%f\n```\nRounding errors can occur with the decoded float!", Float.parseFloat(message)));
            } catch (NumberFormatException ignore) {
            }
            if (out.getText().equals("")) {
                out.setText("<a:alertsign:864083960886853683> Couldn't Decode the given Message!");
                eb.setColor(Color.decode("#c0392b"));
            }

            eb.setDescription(out.getText());
        }

        eb.setTitle(event.getOption("mode").getAsBoolean() ? "Encoded HEX" : "Decoded HEX");
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

}
