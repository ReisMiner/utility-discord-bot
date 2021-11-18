package Commands.Utils;

import Base.Util.BotUtils;
import Base.ReadEmbedded;
import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class HashCommand extends SlashCommand {

    @Override
    public String getDescription() {
        return "Hash Messages!";
    }

    @Override
    public String getCommand() {
        return "hash";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.STRING, "hash-function", "Possible functions: sha1, sha256, md5", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "message", "Your Message, can also be a link to a Discord Message", true));
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "is-message-link", "Is your just entered message a link to a message?", false));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        Set<String> typePossibilities = Set.of(
                "sha256","sha1","md5"
        );
        ReadEmbedded out = new ReadEmbedded();
        EmbedBuilder eb = new EmbedBuilder();
        String message = event.getOption("message").getAsString();

        if (!typePossibilities.contains(event.getOption("hash-function").getAsString().toLowerCase(Locale.ROOT))) {
            eb.setColor(Color.decode("#c0392b"));
            eb.setTitle("Wrong Function!");
            eb.setDescription("<a:alertsign:864083960886853683> Please Check your specified type in the command and adjust it!" +
                    "\nPossible hash functions are sha1, sha256 and md5");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        try {

            switch(event.getOption("hash-function").getAsString().toLowerCase(Locale.ROOT)){
                case "sha1":
                    if (event.getOption("is-message-link") == null || !event.getOption("is-message-link").getAsBoolean()) {
                        out.setText(DigestUtils.sha1Hex(message));

                    }else{
                        out = BotUtils.textFromMsgLink(message, event);
                        out.setText(DigestUtils.sha1Hex(out.getText()));
                    }
                    eb.setTitle("Hashed with SHA-1");
                    break;
                case "sha256":
                    if (event.getOption("is-message-link") == null || !event.getOption("is-message-link").getAsBoolean()) {
                        out.setText(DigestUtils.sha256Hex(message));

                    }else{
                        out = BotUtils.textFromMsgLink(message, event);
                        out.setText(DigestUtils.sha256Hex(out.getText()));
                    }
                    eb.setTitle("Hashed with SHA-256");
                    break;
                case "md5":
                    if (event.getOption("is-message-link") == null || !event.getOption("is-message-link").getAsBoolean()) {
                        out.setText(DigestUtils.md5Hex(message));

                    }else{
                        out = BotUtils.textFromMsgLink(message, event);
                        out.setText(DigestUtils.md5Hex(out.getText()));
                    }
                    eb.setTitle("Hashed with MD5");
                    break;
            }

            eb.setDescription("```\n" + out.getText() + "\n```");
            eb.setColor(Color.decode("#f1c40f"));
        } catch (Exception e) {
            out.setText("<a:alertsign:864083960886853683> Couldn't Hash the given Message!");
            eb.setDescription(out.getText());
            eb.setColor(Color.decode("#c0392b"));
        }


        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
