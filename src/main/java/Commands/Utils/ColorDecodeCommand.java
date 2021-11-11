package Commands.Utils;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ColorDecodeCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Decode Color Codes";
    }

    @Override
    public String getCommand() {
        return "color";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "mode", "True = HEX Code, False = RGB Code", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "message", "Your Color Code, Hex example: #FFFFFF, RGB Example: 255,255,255", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        String message = event.getOption("message").getAsString();
        String out;
        boolean error = false;

        try {

            if (!event.getOption("mode").getAsBoolean()) {
                //RGB DECODE
                message = message.replaceAll("\\s+", "");
                String[] split = message.split(",");
                String hex = String.format("%02X%02X%02X", Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                out = "Hex: ```\n#" + hex + "\n```\nRGB: ```\n" + split[0] + ", " + split[1] + ", " + split[2] + "\n```";
                message=hex;
            } else {
                message = message.replaceAll("#", "");
                int r = Integer.valueOf(message.substring(0, 2), 16);
                int g = Integer.valueOf(message.substring(2, 4), 16);
                int b = Integer.valueOf(message.substring(4, 6), 16);

                out = "Hex: ```\n#" + message.toUpperCase(Locale.ROOT) + "\n```\nRGB: ```\n" + r + ", " + g + ", " + b + "\n```";

            }

            eb.setDescription(out);
            Color col = Color.decode("#" + message);
            eb.setColor(col);
            createIMG(eb, col);

        } catch (Exception e) {
            out = "<a:alertsign:864083960886853683> Couldn't Decode the given Color!\nMake sure to select the right mode and enter in the right format.\nHex example: #FFFFFF, RGB Example: 255,255,255";
            eb.setDescription(out);
            eb.setColor(Color.decode("#c0392b"));
            error = true;
            e.printStackTrace();
        }


        eb.setTitle(event.getOption("mode").getAsBoolean() ? "Decoded HEX Code" : "Decoded RGB Code");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (!error)
            event.getHook().editOriginalEmbeds(eb.build()).addFile(new File("output.jpg"), "output.jpg").queue();
        else
            event.getHook().editOriginalEmbeds(eb.build()).queue();

    }

    private void createIMG(EmbedBuilder eb, Color color) throws IOException {


        BufferedImage bi = new BufferedImage(50, 50, ColorSpace.TYPE_RGB);
        Graphics2D graphics = bi.createGraphics();

        graphics.setColor(color);
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        File outFile = new File("output.jpg");
        ImageIO.write(bi, "jpg", outFile);


        eb.setThumbnail("attachment://output.jpg");
    }
}
