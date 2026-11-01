package presentacion.pages;
import org.testfx.api.FxRobot;
/**
 *
 * @author inici4rsesi0n
 */
public class LoginPage {
    private final FxRobot robot;
    public LoginPage(FxRobot robot) {this.robot = robot;}
    public LoginPage escribirCodigo(String codigo) {
        robot.clickOn("#txtCodigo").write(codigo);
        return this;
    }
    public LoginPage escribirPassword(String password) {
        robot.clickOn("#txtPassword").write(password);
        return this;
    }
    public LoginPage clickLogin() {
        robot.clickOn("#btnLogin");
        return this;
    }
}
