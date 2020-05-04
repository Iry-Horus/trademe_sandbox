package nz.co.enhance.HelperClasses;

import cucumber.api.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullPageScreenshot {

    //FA 8/4/19 full page screenshotting solution for debuggng purposes
    public void capturePageScreenshot(WebDriver driver, Scenario scenario) {
        capturePageScreenshot("selenium-screenshot.png", driver, scenario);
    }

    public void capturePageScreenshot(String filename, WebDriver driver, Scenario scenario) {
        File logdir = new File("target/cucumberHTML");
        File path = new File(logdir, filename);
        final Screenshot screenshot = new AShot()
                .shootingStrategy(ShootingStrategies.viewportRetina(1000, 0, 0, 2))
                //.shootingStrategy(ShootingStrategies.viewportNonRetina(1000, 0, 0))
                .takeScreenshot(driver);
        if (screenshot == null) {
            System.out.println("Can't take screenshot. No open browser found");
            return;
        }
        final BufferedImage image = screenshot.getImage();
        writeScreenshot(path, image, scenario);
    }

    protected void writeScreenshot(File path, BufferedImage img, Scenario scenario) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            ImageIO.write(img, "PNG", fos);
            fos.flush();
            byte[] imageInByte = FileUtils.readFileToByteArray(path);
            scenario.embed(imageInByte, "image/png");
        } catch (IOException e) {
            System.out.println(String.format("Can't write screenshot '%s'", path.getAbsolutePath()));
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println("Can't even close stream.");
                }
            }
            path.delete();
        }
    }
}
