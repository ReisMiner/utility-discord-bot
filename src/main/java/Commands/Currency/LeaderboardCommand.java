package Commands.Currency;

import Base.SlashCommand;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderboardCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "prints the coin leaderboard";
    }

    @Override
    public String getCommand() {
        return "coin-lb";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Coin Leaderboard");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        Map<Long, Long> persons = DatabaseUtil.getLeaderboard(event.getGuild().getIdLong());
        if (persons == null) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Unknown Error");
            eb.setDescription("<a:alertsign:864083960886853683> Could not connect to the Database!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        persons.forEach((k, v) -> {
            System.out.println("Key: " + k + " Value: " + v);
        });

        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<Long> coins = new ArrayList<>();


        persons.forEach((k, v) -> {
            ids.add(k);
            coins.add(v);
        });

        StringBuilder out = new StringBuilder();
        AtomicInteger rank = new AtomicInteger();
        AtomicInteger c = new AtomicInteger();

        persons.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach((v) -> {
                    if (c.get() < 10) {
                        out.append("**");
                        out.append(c.get() + 1);
                        out.append(".** <@");
                        out.append(v.getKey());
                        out.append(">: **");
                        out.append(v.getValue()).append("** Coins\n");
                    } else {
                        if (event.getUser().getIdLong() == v.getKey()) {
                            rank.set(c.get());
                            out.append("\n<a:righter_arrow:864097083174420480> Your rank is **");
                            out.append(c.get() + 1);
                            out.append("** with **");
                            out.append(v.getValue());
                            out.append("** coins!");
                        }
                    }
                    c.getAndIncrement();
                });

        eb.setDescription(out.toString());
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
