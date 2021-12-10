package ngsdiaglim.modeles.users;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.utils.TableViewUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

import java.util.*;

public class UserVariantTableColumns {

    private final Logger logger = LogManager.getLogger(UserVariantTableColumns.class);

    private final Map<VariantsTableColumns, Boolean> columnsVisibility = new HashMap<>();
    private final Map<VariantsTableColumns, Double> columnsSize = new HashMap<>();
    private final List<VariantsTableColumns> columnsOrder = new ArrayList<>();

    private static final String tokenSplitter = ";";
    private static final String keyValueSplitter = ":";

    public UserVariantTableColumns(){}

    public UserVariantTableColumns(String columnsVisibilityStr, String columnsSizeStr, String columnsOrderStr) {
        setColumnsVisibilityFromString(columnsVisibilityStr);
        setColumnsSizeFromString(columnsSizeStr);
        setColumnsOrderFromString(columnsOrderStr);
    }

    public UserVariantTableColumns(Map<VariantsTableColumns, Boolean> columnsVisibility, Map<VariantsTableColumns, Double> columnsSize, List<VariantsTableColumns> columnsOrder) {
        this.columnsVisibility.putAll(columnsVisibility);
        this.columnsSize.putAll(columnsSize);
        this.columnsOrder.addAll(columnsOrder);
    }

    public Map<VariantsTableColumns, Boolean> getColumnsVisibility() {return columnsVisibility;}

    public String getColumnsVisibilityAsString() {
        StringJoiner sj = new StringJoiner(tokenSplitter);
        for (Map.Entry<VariantsTableColumns, Boolean> e : columnsVisibility.entrySet()) {
            sj.add(e.getKey().name() + keyValueSplitter + e.getValue());
        }
        return sj.toString();
    }

    private void setColumnsVisibilityFromString(String s) {
        for (String tokens : s.split(tokenSplitter)) {
            String[] token = tokens.split(keyValueSplitter);
            if (token.length == 2) {
                try {
                    VariantsTableColumns col = VariantsTableColumns.valueOf(token[0]);
                    Boolean val = Boolean.parseBoolean(token[1]);
                    columnsVisibility.put(col, val);
                } catch (IllegalArgumentException e) {
                    logger.warn(e);
                }
            }
        }
    }

    public Map<VariantsTableColumns, Double> getColumnsSize() {return columnsSize;}

    public String getColumnsSizeAsString() {
        StringJoiner sj = new StringJoiner(tokenSplitter);
        for (Map.Entry<VariantsTableColumns, Double> e : columnsSize.entrySet()) {
            sj.add(e.getKey().name() + ":" + e.getValue());
        }
        return sj.toString();
    }

    private void setColumnsSizeFromString(String s) {
        for (String tokens : s.split(tokenSplitter)) {
            String[] token = tokens.split(keyValueSplitter);
            if (token.length == 2) {
                try {
                    VariantsTableColumns col = VariantsTableColumns.valueOf(token[0]);
                    Double val = Double.parseDouble(token[1]);
                    columnsSize.put(col, val);
                } catch (IllegalArgumentException e) {
                    logger.warn(e);
                }
            }
        }
    }

    public List<VariantsTableColumns> getColumnsOrder() {return columnsOrder;}

    public String getColumnsOrderAsString() {
        StringJoiner sj = new StringJoiner(tokenSplitter);
        columnsOrder.forEach(c -> sj.add(c.name()));
        return sj.toString();
    }

    private void setColumnsOrderFromString(String s) {
        for (String token : s.split(tokenSplitter)) {
            try {
                VariantsTableColumns col = VariantsTableColumns.valueOf(token);
                if (!columnsOrder.contains(col)) {
                    columnsOrder.add(col);
                }
            } catch (IllegalArgumentException e) {
                logger.warn(e);
            }

        }
    }

    public void setColumnsOrderFromTable(TableView<?> table) {
        columnsOrder.clear();
        for (TableColumn<?, ?> col : table.getColumns()) {
            String title = TableViewUtils.getColmunTitle(col);
            if (title != null) {
                try {
                    VariantsTableColumns column = VariantsTableColumns.getFromName(title);
                    if (!columnsOrder.contains(column)) {
                        columnsOrder.add(column);
                    }
                } catch (Exception e) {
                    logger.warn(e);
                }
            }
        }
    }

    public void setVisibleColumnsFromTable(TableView<?> table) {
        columnsVisibility.clear();
        for (TableColumn<?, ?> col : table.getColumns()) {
            String title = TableViewUtils.getColmunTitle(col);
            if (title != null) {
                try {
                    VariantsTableColumns column = VariantsTableColumns.getFromName(title);
                    columnsVisibility.put(column, col.isVisible());
                } catch (Exception e) {
                    logger.warn(e);
                }
            }
        }
    }


    public void setColumnsSizeFromTable(TableView<?> table) {
        columnsVisibility.clear();
        for (TableColumn<?, ?> col : table.getColumns()) {
            String title = TableViewUtils.getColmunTitle(col);
            if (title != null) {
                try {
                    VariantsTableColumns column = VariantsTableColumns.getFromName(title);
                    columnsSize.put(column, col.getWidth());
                } catch (Exception e) {
                    logger.warn(e);
                }
            }
        }
    }
}
