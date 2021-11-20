package Commands.Currency;

import Base.SlashCommand;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.Comparator;
import java.util.Map;

public class ListShopCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Get items from the shop";
    }

    @Override
    public String getCommand() {
        return "shop";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("\uD83D\uDED2 Shop Items");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        Map<String[], String> items = DatabaseUtil.getAllShopItemsFromGuild(event.getGuild().getIdLong());

        if (!items.isEmpty()) {
            StringBuilder out = new StringBuilder();
            items.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach((v) -> {
                        out.append("\n<@&");
                        out.append(v.getKey()[0]);
                        out.append("> - **");
                        out.append(v.getValue());
                        out.append(" Coins**\n");
                        out.append(v.getKey()[1]);
                        out.append("\n");
                    });
            eb.setDescription(out.toString());

        } else {
            eb.setDescription("No Roles to buy. Ask your server Mods to add Roles!");
        }
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
