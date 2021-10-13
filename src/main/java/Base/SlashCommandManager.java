package Base;

import Commands.CreateCodeClashCommand;
import Commands.PingCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;

public class SlashCommandManager {

    private final ArrayList<SlashCommand> slashCommands;

    public ArrayList<SlashCommand> getSlashCommands() {
        return slashCommands;
    }

    //commands for specific servers only
    public SlashCommandManager(JDA jda, Long guildID) {
        slashCommands = new ArrayList<>();
        slashCommands.add(new PingCommand());
        slashCommands.add(new CreateCodeClashCommand());

        registerSlashCommands(jda, guildID);
    }

    private void registerSlashCommands(JDA jda, Long guildID) {
        if (guildID != null) {
            for (SlashCommand command : getSlashCommands()) {
                if (command.getCommandArgs() == null) {
                    jda.getGuildById(guildID).upsertCommand(command.getCommand(), command.getDescription()).queue();
                    //when options are specified
                } else {
                    CommandData commandData = new CommandData(command.getCommand(), command.getDescription());
                    for (SlashCommandArgs args : command.getCommandArgs()) {
                        commandData = commandData.addOption(args.getOptionType(), args.getName(), args.getDescription(), args.isRequired());
                    }
                    jda.getGuildById(guildID).upsertCommand(commandData).queue();
                }
            }
        } else {
            for (SlashCommand command : getSlashCommands()) {
                if (command.getCommandArgs() == null) {
                    jda.upsertCommand(command.getCommand(), command.getDescription()).queue();
                    //when options are specified
                } else {
                    CommandData commandData = new CommandData(command.getCommand(), command.getDescription());
                    for (SlashCommandArgs args : command.getCommandArgs()) {
                        commandData = commandData.addOption(args.getOptionType(), args.getName(), args.getDescription(), args.isRequired());
                    }
                    jda.upsertCommand(commandData).queue();
                }
            }
        }
    }

    public void runCommand(SlashCommandEvent e) {
        for (SlashCommand cmd : getSlashCommands()) {
            if (cmd.getCommand().equals(e.getName())) {
                cmd.onExecute(e);
            }
        }
    }
}
