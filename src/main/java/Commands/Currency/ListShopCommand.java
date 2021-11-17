package Commands.Currency;

import Base.SlashCommand;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ListShopCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Add a new Role to the shop.";
    }

    @Override
    public String getCommand() {
        return "shop-list";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Shop Items");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> description = new ArrayList<>();
        ArrayList<String> roleID = new ArrayList<>();


        Map<String, String> items = DatabaseUtil.getAllShopItemsFromGuild(event.getGuild().getIdLong());
        assert items != null;
        items.forEach((k, v) -> {
            roleID.add(Arrays.stream(k.split("_")).toArray()[0].toString());
            prices.add(Arrays.stream(k.split("_")).toArray()[1].toString());
            description.add(v);
        });

        if (prices.get(0) != null) {
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < prices.size(); i++) {
                out.append("\n<@&").append(roleID.get(i)).append("> - **").append(prices.get(i)).append(" Coins**\n").append(description.get(i)).append("\n");
            }
            eb.setDescription(out.toString());
        }else{
            eb.setDescription("No Roles to buy. Ask your server Mods to add Roles!");
        }
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
