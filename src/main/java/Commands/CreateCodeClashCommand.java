package Commands;

import Base.Secrets;
import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class CreateCodeClashCommand extends SlashCommand {

    @Override
    public String getDescription() {
        return "Creates a Code Clash. Define the modes which can be played. Default is all modes.";
    }

    @Override
    public String getCommand() {
        return "codeclash";
    }

    @Override
    public ArrayList<SlashCommandArgs> getCommandArgs() {
        ArrayList<SlashCommandArgs> args = new ArrayList<>();
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "shortest-mode", "Enable shortest Mode?", true));
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "fastest-mode", "Enable fastest Mode?", true));
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "reverse-mode", "Enable reverse Mode?", true));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        String reply = "Creating Code Clash...";
        event.reply(reply).queue();
        if (event.getOptions().size() != 3) {
            event.getHook().sendMessage("Please Set either True or false for each mode!").queue();
            return;
        }
        System.setProperty("webdriver.gecko.driver", Secrets.DRIVER_LOCATION);
        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        try {
            //go to site and open login popup
            driver.get("https://www.codingame.com/multiplayer/clashofcode");
            wait.until(presenceOfElementLocated((By.cssSelector("button[translate='cgCookiesBanner.accept']")))).click();
            wait.until(presenceOfElementLocated(By.cssSelector("a[translate='content-details-clashofcode.privateclash.externalLink']"))).click();

            //login
            wait.until(presenceOfElementLocated(By.cssSelector("button[data-test='go-to-login']"))).click();
            wait.until(presenceOfElementLocated(By.cssSelector("input[data-test='login-email']"))).sendKeys(Secrets.CODE_CLASH_EMAIL);
            wait.until(presenceOfElementLocated(By.cssSelector("input[data-test='login-password']"))).sendKeys(Secrets.CODE_CLASH_PW);
            wait.until(presenceOfElementLocated(By.cssSelector("button[type='submit']"))).click();

            //open popup to start CC
            Thread.sleep(1000);
            wait.until(presenceOfElementLocated(By.cssSelector("a[translate='content-details-clashofcode.privateclash.externalLink']"))).click();

            //set checkboxes
            WebElement reverseCbx = wait.until(presenceOfElementLocated(By.cssSelector("cg-checkbox[checkbox-id='reverse']")));
            WebElement shortestCbx = wait.until(presenceOfElementLocated(By.cssSelector("cg-checkbox[checkbox-id='shortest']")));
            WebElement fastestCbx = wait.until(presenceOfElementLocated(By.cssSelector("cg-checkbox[checkbox-id='fastest']")));

            if (event.getOption("shortest-mode") != null)
                if (!shortestCbx.getAttribute("checked").equalsIgnoreCase(event.getOption("shortest-mode").getAsString())) {
                    shortestCbx.click();
                }
            if (event.getOption("reverse-mode") != null)
                if (!reverseCbx.getAttribute("checked").equalsIgnoreCase(event.getOption("reverse-mode").getAsString())) {
                    reverseCbx.click();
                }
            if (event.getOption("fastest-mode") != null)
                if (!fastestCbx.getAttribute("checked").equalsIgnoreCase(event.getOption("fastest-mode").getAsString())) {
                    fastestCbx.click();
                }

            //join and leave when first guy joins
            wait.until(presenceOfElementLocated(By.cssSelector("a[translate='clashPrivatePopup.externalLink']"))).click();
            Thread.sleep(3000);
            reply = driver.getCurrentUrl();
            event.getHook().editOriginal(reply).queue();
            while(driver.findElements(By.xpath("/html/body/div[7]/div[2]/div[1]/div/div/ui-view/clash-lobby/div/div[1]/div[1]/div[1]/span/span[1]")).size() > 0){
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        reply += "\nI left the game. The Person who first joined has to start it when y'all are ready!";
        event.getHook().editOriginal(reply).queue();
    }
}
