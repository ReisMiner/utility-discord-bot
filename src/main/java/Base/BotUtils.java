package Base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class BotUtils {

    public static Long[] getIdsFromMsgLink(String link) {
        String[] x = link.split("/");
        return new Long[]{Long.valueOf(x[x.length - 3]), Long.valueOf(x[x.length - 2]), Long.valueOf(x[x.length - 1])};
    }

    public static ReadEmbedded textFromMsgLink(String link, SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        Long[] ids = BotUtils.getIdsFromMsgLink(link);
        ReadEmbedded out = new ReadEmbedded();

        event.getJDA().getTextChannelById(ids[1]).retrieveMessageById(ids[2]).queue((m) -> {
            if (m.getEmbeds().size() == 0) {
                out.setText(m.getContentRaw());
                out.setEmbedded(false);
            } else
                try {
                    out.setText(m.getEmbeds().get(0).getDescription());
                    out.setEmbedded(true);
                } catch (Exception e) {
                    out.setText("error");
                    eb.setColor(Color.decode("#c0392b"));
                    eb.setTitle("Message Fetch Error");
                    eb.setDescription("<a:alertsign:864083960886853683> Could not decipher the embedded!");
                    eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());
                    event.getHook().editOriginalEmbeds(eb.build()).queue();
                }
        }, (failure) -> {
            if (failure instanceof ErrorResponseException) {
                out.setText("error");
                eb.setColor(Color.decode("#c0392b"));
                eb.setTitle("Message Fetch Error");
                eb.setDescription("<a:alertsign:864083960886853683> Message not Found. Please check your link!\n" +
                        "Also make sure the bot is added to the server where the message is from!\n\nAdd the bot with /invite to your server.");
                eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());
                event.getHook().editOriginalEmbeds(eb.build()).queue();
            }

        });

        while (out.getText().equals("")) {
            //idk. wait for async callback lol
            System.out.print("");
        }

        return out;
    }

    public static void switchPresence(JDA jda) {
        Timer timer = new Timer();
        final int[] counter = {0};
        ArrayList<SlashCommand> cmds = Bot.slashCommandManager.getSlashCommands();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                jda.getPresence().setActivity(Activity.playing("/" + cmds.get(counter[0]).getCommand() + " -> " + cmds.get(counter[0]).getDescription()));
                counter[0]++;
                if (cmds.size() == counter[0]) {
                    counter[0] = 0;
                }
            }
        }, 0, 5000);
    }

    public static String getContributor(){
        ArrayList<String> contributors = new ArrayList<>();
        contributors.add("ReisMiner#1111 - https://reisminer.xyz");
        contributors.add("Yatharth#1564 - sussy baka");
        int randomNum = ThreadLocalRandom.current().nextInt(0, contributors.size());
        System.out.println(randomNum);
        return contributors.get(randomNum);
    }

}

