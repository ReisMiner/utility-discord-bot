package Commands.Utils;

import Base.SlashCommand;
import Base.SlashCommandArgs;
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
        args.add(new SlashCommandArgs(OptionType.STRING, "message", "Your Message", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String output = "";
        String typePossibilities = "012";
        event.deferReply().queue();
        String message = event.getOption("message").getAsString();

        if (event.getOption("mode").getAsBoolean()) {
            System.out.println("x" + event.getOption("type").getAsString() + "x");
            //exit if specified number is not in range
            if (!typePossibilities.contains(event.getOption("type").getAsString())) {
                eb.setColor(Color.decode("#c0392b"));
                eb.setTitle("Wrong Type!");
                eb.setDescription("<a:alertsign:864083960886853683> Please Check your specified type in the command and adjust it!" +
                        "\n\nFor Text write a 0\nFor a number **without** floating point write a 1\nFor a number **with** floating point write a 2\nThis input is **ignored** when decoding!");
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                return;
            }

            switch (event.getOption("type").getAsString()) {
                case "0":
                    output = Hex.encodeHexString(message.getBytes(StandardCharsets.UTF_8));
                    break;
                case "1":
                    output = Integer.toHexString(Integer.parseInt(message));
                    break;
                case "2":
                    output = Float.toHexString(Float.parseFloat(message));
                    break;
            }

            if (!output.equals(""))
                output = output.replaceAll("..", "$0 ").toUpperCase();

        } else {
            message = message.replaceAll("\\s+","");
            try {
                String hex_text = new String(Hex.decodeHex(message), StandardCharsets.UTF_8);
                if(hex_text.contains("`")){
                    hex_text = hex_text.replaceAll("`", "`\u200B");
                }
                output += String.format("\nText: ```\n%s\n```", hex_text);
            } catch (DecoderException ignore) {
            }
            try {
                output += String.format("\nDecimal: ```\n%d\n```", Integer.parseInt(message, 16));
            } catch (NumberFormatException ignore) {
            }
            try {
                output += String.format("\nFloat: ```\n%f\n```\nRounding errors can occur with the decoded float!", Float.parseFloat(message));
            } catch (NumberFormatException ignore) {
            }
            if (output.equals("")) {
                output = "```diff\nCouldn't Decode/Encode the given Message!\n```";
            }
        }


        eb.setColor(Color.decode("#9b59b6"));
        eb.setTitle(event.getOption("mode").getAsBoolean() ? "Encoded HEX" : "Decoded HEX");
        eb.setDescription(output);
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());
        event.getHook().editOriginalEmbeds(eb.build()).queue();

    }
}
