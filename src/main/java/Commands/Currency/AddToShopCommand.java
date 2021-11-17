package Commands.Currency;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class AddToShopCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Add a new Role to the shop.";
    }

    @Override
    public String getCommand() {
        return "shop-add";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.ROLE, "role", "The role users should be able to buy", true));
        args.add(new SlashCommandArgs(OptionType.STRING, "description", "Description for the shop item", true));
        args.add(new SlashCommandArgs(OptionType.INTEGER, "price", "How much should it cost", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("New Role added to the shop!");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (DatabaseUtil.checkShopItem(event.getGuild().getIdLong(), event.getOption("role").getAsRole().getIdLong())) {
            eb.setTitle("Role already exists in shop!");
            eb.setColor(Color.decode("#c0392b"));
            eb.setDescription("<a:alertsign:864083960886853683> Did not add the role!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (DatabaseUtil.addNewShopItem(event.getGuild().getIdLong(),
                event.getOption("role").getAsRole(),
                event.getOption("description").getAsString(),
                event.getOption("price").getAsString())) {
            eb.setDescription("Operation Successful!");
        } else {
            eb.setColor(Color.decode("#c0392b"));
            eb.setDescription("<a:alertsign:864083960886853683> An unknown error occurred!");
        }
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
