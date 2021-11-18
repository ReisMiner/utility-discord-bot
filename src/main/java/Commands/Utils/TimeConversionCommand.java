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
        return "Get the current time from a location/timezone";
    }

    @Override
    public String getCommand() {
        return "time-info";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.STRING, "location", "The country you wanna know which time it is. Timezones/Cities also work.", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        String country = event.getOption("location").getAsString();
        country = StringUtils.capitalize(country.toLowerCase(Locale.ROOT));

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#ffd166"));
        eb.setTitle("Time in " + country);

        try {
            Document document = Jsoup.connect("https://time.is/" + country).get();

            Element clock = document.select("#clock").get(0);
            Element timezone = document.select(".keypoints > ul:nth-child(1) > li:nth-child(1)").get(0);

            Element sunset = null;
            Element sunrise = null;

            try {
                sunset = document.select("#time_zone > ul:nth-child(7) > li:nth-child(2)").get(0);
                sunrise = document.select("#time_zone > ul:nth-child(7) > li:nth-child(1)").get(0);
                eb.setDescription("\n**" + clock.text() + "**" +
                        "\nTimezone: " + timezone.text().replaceAll("\\P{InBasic_Latin}", " ")+
                        "\nSunrise: "+sunrise.text().substring(sunrise.text().length()-5)+
                        "\nSunset: "+sunset.text().substring(sunset.text().length()-5));
            } catch (Exception e) {
                eb.setDescription("\n**" + clock.text() + "**" +
                        "\nTimezone: " + timezone.text().replaceAll("\\P{InBasic_Latin}", " "));
            }

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
