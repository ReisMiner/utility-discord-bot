package Commands.Currency;

import Base.SlashCommand;
import Base.SlashCommandArgs;
import Base.Util.DatabaseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;

public class BuyRoleCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Buy a role from the shop";
    }

    @Override
    public String getCommand() {
        return "buy";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.ROLE, "role", "The role you wanna buy", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().queue();
        Role role = event.getOption("role").getAsRole();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Role Acquired");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Permission Error");
            eb.setDescription("<a:alertsign:864083960886853683> Bot does not have sufficient permissions!\n" +
                    "Add the **Manage Roles** Permission to the bot role!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        boolean above = false;
        for (Role r : event.getGuild().getSelfMember().getRoles()) {
            if (r.getPosition() > role.getPosition()) {
                above = true;
                break;
            }
        }
        if (!above) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Role Position Error");
            eb.setDescription("<a:alertsign:864083960886853683> Bot is under the wanted role!\n" +
                    "Move the bot Role above " + role.getAsMention());
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (event.getMember().getRoles().contains(role)) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Cannot buy this Role");
            eb.setDescription("<a:alertsign:864083960886853683> You already own this role!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        long price = DatabaseUtil.getPriceOfShopItem(event.getGuild().getIdLong(), role.getIdLong());

        if (price < 0) {
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Database Error");
            eb.setDescription("<a:alertsign:864083960886853683> Cannot fetch the price!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if(DatabaseUtil.userBalance(event.getUser(),event.getGuild().getIdLong())<=price){
            eb.setColor(Color.decode("#27ae60"));
            eb.setTitle("Not enough money");
            eb.setDescription("<a:alertsign:864083960886853683> You dont have sufficient funds!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        DatabaseUtil.changeBalance(event.getUser(), event.getGuild().getIdLong(), price * -1L);
        DatabaseUtil.changeBankBalance(event.getGuild().getIdLong(), price);
        event.getGuild().addRoleToMember(event.getMember(), role).queue();

        eb.setDescription("\uD83D\uDED2 Successfully bought " + role.getAsMention());
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
