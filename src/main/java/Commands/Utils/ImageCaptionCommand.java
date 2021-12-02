package Commands.Utils;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ImageCaptionCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Add Caption To An Image";
    }

    @Override
    public String getCommand() {
        return "caption";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.STRING, "text", "Text To Put On The Image", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "url", "URL of Image to be edited (.png, .jpg, .jpeg)", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Captioned Image");
        eb.setColor(Color.decode("#1abc9c"));
        String out;
        boolean error = false;

        String text = Objects.requireNonNull(event.getOption("text")).getAsString();
        String url = Objects.requireNonNull(event.getOption("url")).getAsString();
        String[] a;
        String[] validTypes = {"png", "jpg", "jpeg"};
        String fileName = "";

        try {
            a = url.split("\\.");
            fileName = event.getUser().getIdLong() + "." + validateFileType(a[a.length - 1], validTypes);
            if (!Arrays.asList(validTypes).contains(fileName.split("\\.")[1])) {
                throw new Exception("Not Valid File Type!");
            }
            addCaption(text, url, fileName);
        } catch (Exception e) {
            out = "<a:alertsign:864083960886853683> Couldn't Edit the Image!\nCheck If the filetype is valid (.png, .jpg, .jpeg)\nCheck if the url is correct.";
            eb.setDescription(out);
            eb.setColor(Color.decode("#c0392b"));
            error = true;
            e.printStackTrace();
        }

        eb.setFooter("Query performed by " + Objects.requireNonNull(event.getMember()).getUser().getAsTag());

        if (!error)
            try {
                eb.setImage("attachment://" + fileName);
                event.getHook().editOriginalEmbeds(eb.build()).addFile(new File(fileName), fileName).queue();
            } catch (Exception e) {
                out = "<a:alertsign:864083960886853683> Couldn't Edit the Image!\nCheck If the filetype is valid (.png, .jpg, .jpeg)";
                eb.setDescription(out);
                eb.setColor(Color.decode("#c0392b"));
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                e.printStackTrace();
            }
        else
            event.getHook().editOriginalEmbeds(eb.build()).queue();

        //wait so we can delete the file and is not used anymore
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
    }

    public void addCaption(String caption, String url, String fileName) throws IOException, URISyntaxException, FontFormatException {
        BufferedImage image = ImageIO.read(new URL(url));
        BufferedImage finalImage = makeImage(image, caption);
        File output = new File(fileName);
        String fileType = fileName.split("\\.")[1];
        ImageIO.write(finalImage, fileType.equals("jpg") ? "jpeg" : fileType, output);
    }

    public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) { // source: stackoverflow (https://stackoverflow.com/a/9417836)
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static void setTextCenter(Graphics2D graphics2DImage, String string, BufferedImage bgImage) { // source stackoverflow (idk the url)
        int stringWidthLength = (int) graphics2DImage.getFontMetrics().getStringBounds(string, graphics2DImage).getWidth();
        int stringHeightLength = (int) graphics2DImage.getFontMetrics().getStringBounds(string, graphics2DImage).getHeight();
        int horizontalCenter = bgImage.getWidth() / 2 - stringWidthLength / 2;
        int verticalCenter = bgImage.getHeight() / 2 + stringHeightLength / 2;
        graphics2DImage.drawString(string, horizontalCenter, verticalCenter);
    }

    public static String validateFileType(String fileType, String[] types) {
        if (Arrays.asList(types).contains(fileType.substring(0, 2))) {
            return fileType.substring(0, 2);
        } else if (Arrays.asList(types).contains(fileType.substring(0, 3))) {
            return fileType.substring(0, 3);
        }
        return fileType;
    }

    public BufferedImage makeImage(BufferedImage image, String caption) throws IOException, URISyntaxException, FontFormatException {
        BufferedImage whiteBG = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wb.jpg")));
        whiteBG = resizeImage(whiteBG, image.getWidth(), 100);

        Graphics2D gf = whiteBG.createGraphics();
        InputStream file = getClass().getResourceAsStream("/impact.ttf");
        Font f = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(file)).deriveFont((float) whiteBG.getWidth() / 10);
        gf.setFont(f);
        gf.setColor(new Color(0, 0, 0));

        if ((int) gf.getFontMetrics().getStringBounds(caption, gf).getWidth() < whiteBG.getWidth())
            setTextCenter(gf, caption, whiteBG);
        else {
            String[] words = caption.split(" ");
            String disp = "";
            int curY = gf.getFontMetrics().getHeight();

            for (String word : words) {
                int stringWidth = (int) gf.getFontMetrics().getStringBounds(disp + word, gf).getWidth();
                if (stringWidth >= whiteBG.getWidth()) {
                    disp = "";
                    curY += gf.getFontMetrics().getHeight();
                }

                if (curY + gf.getFontMetrics().getHeight() > whiteBG.getHeight()) {
                    whiteBG = resizeImage(whiteBG, whiteBG.getWidth(), whiteBG.getHeight() + gf.getFontMetrics().getHeight());
                }
                disp += word + " ";
            }

            disp = "";
            curY = gf.getFontMetrics().getHeight();
            gf = whiteBG.createGraphics();
            gf.setFont(f);
            gf.setColor(new Color(0, 0, 0));

            for (String word : words) {
                int stringWidth = (int) gf.getFontMetrics().getStringBounds(disp + word, gf).getWidth();
                if (stringWidth >= whiteBG.getWidth()) {
                    stringWidth = (int) gf.getFontMetrics().getStringBounds(disp, gf).getWidth();
                    gf.drawString(disp, whiteBG.getWidth() / 2 - stringWidth / 2, curY);
                    disp = "";
                    curY += gf.getFontMetrics().getHeight();
                }
                disp += word + " ";
            }

            int stringWidth = (int) gf.getFontMetrics().getStringBounds(disp, gf).getWidth();
            gf.drawString(disp, whiteBG.getWidth() / 2 - stringWidth / 2, curY);
        }
        gf.dispose();

        BufferedImage finalImage = new BufferedImage(image.getWidth(), whiteBG.getHeight() + image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D gfinal = finalImage.createGraphics();
        gfinal.drawImage(whiteBG, 0, 0, null);
        gfinal.drawImage(image, 0, whiteBG.getHeight(), null);
        gfinal.dispose();

        return finalImage;
    }
}
