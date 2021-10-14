package Commands;

import Base.Bot;
import Base.SlashCommand;
import Base.SlashCommandArgs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class CreateCodeClashCommand extends SlashCommand {

    public static boolean codeClashStarted = false;
    public static String codeClashURL = "";

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
        args.add(new SlashCommandArgs(OptionType.BOOLEAN, "ping-code-clash-role", "Should it ping the Code Clash Role when the Battle is ready?", false));
        return args;
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        if (!codeClashStarted) {
            ChromeOptions options = new ChromeOptions();

            codeClashStarted = true;
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode("#fcba03"));
            eb.setTitle("Code Clash");
            eb.setDescription("Creating Code Clash <a:Loading:865347649829208064>");
            event.replyEmbeds(eb.build()).queue();

            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows"))
                System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");
            else {
                options.setBinary("/app/.apt/usr/bin/google-chrome");
                System.setProperty("webdriver.chrome.driver", System.getenv("CHROMEDRIVER_PATH"));
            }
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            try {
                //go to site and open login popup
                driver.get("https://www.codingame.com/multiplayer/clashofcode");
                wait.until(presenceOfElementLocated((By.cssSelector("button[translate='cgCookiesBanner.accept']")))).click();
                wait.until(presenceOfElementLocated(By.cssSelector("a[translate='content-details-clashofcode.privateclash.externalLink']"))).click();

                //login
                wait.until(presenceOfElementLocated(By.cssSelector("button[data-test='go-to-login']"))).click();
                wait.until(presenceOfElementLocated(By.cssSelector("input[data-test='login-email']"))).sendKeys(Bot.CC_EMAIL);
                wait.until(presenceOfElementLocated(By.cssSelector("input[data-test='login-password']"))).sendKeys(Bot.CC_PW);
                wait.until(presenceOfElementLocated(By.cssSelector("button[type='submit']"))).click();

                //open popup to start CC
                Thread.sleep(2000);
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

                if (event.getOptions().size() > 3 && event.getOption("ping-code-clash-role").getAsBoolean()) {
                    event.getTextChannel().sendMessage("<@&884056398323937341>").queue();
                }
                codeClashURL = driver.getCurrentUrl();
                System.out.println("Created Code Clash: " + codeClashURL);
                eb.setTitle("Code Clash Ready to Join", codeClashURL);
                eb.setDescription("<a:success:862960208388161626> " + event.getMember().getAsMention() + " created a code clash. Join it via the link in the embed Title!");
                eb.setFooter("First joiner has to start the Clash!");
                event.getHook().editOriginalEmbeds(eb.build()).queue();
                while (driver.findElements(By.xpath("/html/body/div[7]/div[2]/div[1]/div/div/ui-view/clash-lobby/div/div[1]/div[1]/div[1]/span/span[1]")).size() > 0) {
                    Thread.sleep(1000);
                    if (driver.findElement(By.xpath("/html/body/div[7]/div[2]/div[1]/div/div/ui-view/clash-lobby/div/div[1]/div[1]/div[1]/div[4]/span[2]")).getText().equals("00:05")) {
                        driver.quit();
                        eb.setTitle("Code Clash Expired");
                        eb.setDescription("<a:alertsign:864083960886853683> Code Clash expired because no one joined. Create a new one!");
                        eb.setFooter("");
                        event.getHook().editOriginalEmbeds(eb.build()).queue();
                        return;
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                driver.quit();
            }
            eb.setTitle("Code Clash Ready to Join", codeClashURL);
            eb.setDescription("I left the clash. First joiner has to start the Clash!\nJoin via the Link in the Embed title!");
            eb.setFooter("First joiner has to start the Clash!");
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            codeClashStarted = false;
            codeClashURL = "";
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            if (!codeClashURL.equals("")) {
                eb.setTitle("Someone Already Created a Code Clash", codeClashURL);
                eb.setDescription("<a:success:862960208388161626> Join via the Link in the Embed title!");
            } else {
                eb.setTitle("Code clash is already being created");
                eb.setDescription("I'm already creating a Clash <a:Loading:865347649829208064>\nLook in the Chat history or enter this command again in a few seconds!");
            }
            eb.setFooter("First joiner has to start the Clash!");
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
