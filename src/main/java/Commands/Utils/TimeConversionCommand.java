package Commands.Utils;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class TimeConversionCommand extends SlashCommand {

    @Override
    public String getDescription() {
        return "Convert countries";
    }

    @Override
    public String getCommand() {
        return "time-convert";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.STRING, "country", "The country you wanna know which time it is. Timezones/Cities also work.", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#ffd166"));

        String country = event.getOption("country").getAsString();
        country = StringUtils.capitalize(country.toLowerCase(Locale.ROOT));

        try {

            Document document = Jsoup.connect("https://time.is/" + country).get();

            Element clock = document.select("#clock").get(0);
            Element timezone = document.select(".keypoints > ul:nth-child(1) > li:nth-child(1)").get(0);

            eb.setTitle("Time in " + country);
            eb.setDescription("\n**" + clock.text() + "**\nTimezone: " + timezone.text().replaceAll("\\P{InBasic_Latin}", " "));


        } catch (
                Exception e) {
            eb.setTitle("Time was not Converted");
            eb.setDescription("<a:alertsign:864083960886853683> Couldn't fetch the Time of " + country +
                    "\nMake sure you spelled it correctly!");
            eb.setColor(Color.decode("#c0392b"));
            e.printStackTrace();
        }

        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
