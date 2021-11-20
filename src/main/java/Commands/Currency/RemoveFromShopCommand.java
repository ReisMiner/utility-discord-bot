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

public class RemoveFromShopCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Mod Command: Remove a role from the shop";
    }

    @Override
    public String getCommand() {
        return "shop-remove";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.ROLE, "role", "The role you wanna remove from the shop", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        event.deferReply().setEphemeral(true).queue();
        Role role = event.getOption("role").getAsRole();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#27ae60"));
        eb.setTitle("Role Removed from the Shop");
        eb.setFooter("Query performed by " + event.getMember().getUser().getAsTag());

        if (event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS) && event.getMember().hasPermission(Permission.MANAGE_ROLES)) {

            if (DatabaseUtil.checkShopItem(event.getGuild().getIdLong(), role.getIdLong())) {
                if (DatabaseUtil.deleteShopItem(event.getGuild().getIdLong(), role.getIdLong())) {
                    eb.setDescription("The role " + role.getAsMention() + " was removed from the shop!");
                } else {
                    eb.setColor(Color.decode("#c0392b"));
                    eb.setDescription("<a:alertsign:864083960886853683> An unknown error occurred!");
                }

            } else {
                eb.setColor(Color.decode("#c0392b"));
                eb.setTitle("Role not in the Shop");
                eb.setDescription("<a:alertsign:864083960886853683> The Role " + role.getAsMention() + " is not in the shop!");
            }

        } else {
            // NO PERMS
            eb.setTitle("No Permissions");
            eb.setColor(Color.decode("#c0392b"));
            eb.setDescription("<a:alertsign:864083960886853683> You have no permissions to do this!");
        }
        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
