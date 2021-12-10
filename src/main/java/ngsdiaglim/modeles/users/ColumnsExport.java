package ngsdiaglim.modeles.users;

import ngsdiaglim.enumerations.VariantsTableColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ColumnsExport {

    private final Logger logger = LogManager.getLogger(ColumnsExport.class);
    private final Set<VariantsTableColumns> columns = new HashSet<>();
    private static final String tokenSplitter = ";";

    public ColumnsExport() {}

    public ColumnsExport(String columns) {
        setColumnsFromString(columns);
    }

    public Set<VariantsTableColumns> getColumns() {return columns;}

    public String getColumnsAsString() {
        StringJoiner sj = new StringJoiner(tokenSplitter);
        columns.forEach(c -> sj.add(c.name()));
        return sj.toString();
    }

    private void setColumnsFromString(String s) {
        for (String token : s.split(tokenSplitter)) {
            try {
                VariantsTableColumns col = VariantsTableColumns.valueOf(token);
                columns.add(col);
            } catch (IllegalArgumentException e) {
                logger.warn(e);
            }

        }
    }

    public boolean hasColumn(VariantsTableColumns column) {
        return columns.contains(column);
    }

    public void addColumn(VariantsTableColumns column) {
        columns.add(column);
    }

    public void removeColumn(VariantsTableColumns column) {
        columns.remove(column);
    }
}
