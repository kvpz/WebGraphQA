package gstore;

import java.util.Calendar;
import java.util.List;

public class Utility {

    private static final int YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private static final int MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;
    private static final int DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static Module findModuleById(List<Module> modList, String id) {
        for(Module mod : modList) {
            if(mod.getId().equals(id)) {
                return mod;
            }
        }

        return null;
    }

    /**
     * Returns the current date in the format YEAR_MONTH_DAY
     * @return
     */
    public static String dateUnderscored() {
        return YEAR + "_" + MONTH + "_" + DAY;
    }
}
