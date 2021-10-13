package Base;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;

public abstract class SlashCommand {
    public abstract String getDescription();
    public abstract String getCommand();
    public ArrayList<SlashCommandArgs> getCommandArgs(){
        return null;
    }
    public void onExecute(SlashCommandEvent event){
        event.reply("Command not defined!").setEphemeral(true).queue();
    }
}
