/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/9/11
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 *
 * Requirement 1.1.8 Ability to save state in a custom file format
 * Requirement 1.1.9 Ability to open state file
 */
public class MMVSimpleFileFilter extends SimpleFileFilter {

    public String getExtension() {
        return "mmv";
    }

    public String getDescription() {
        return "MMV (application file)";
    }
}
