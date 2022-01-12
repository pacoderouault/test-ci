package ngsdiaglim.database.dao;

import org.apache.commons.math3.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RunsStatisticsDAO extends DAO {

    public RunsStatistics getRunsStatistics() throws SQLException {
        final String sql = "SELECT A.run_id AS aRunId, L.name as lName " +
                "FROM analysis AS A " +
                "JOIN analysisparameters AS AP " +
                "JOIN panels AS L " +
                "WHERE A.analysisparameters_id=AP.id AND L.id=AP.panel_id;";

        try (Connection connection=getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            Set<Long> runIds = new HashSet<>();
            int analysisIdsCount = 0;
            HashMap<String, Integer> analysisByPanelNb = new HashMap<>();
            HashMap<String, Set<Long>> runsByPanelNb = new HashMap<>();
            while(rs.next()) {
                long runId = rs.getLong("aRunId");
                String panelName = rs.getString("lName");

                analysisIdsCount++;
                runIds.add(runId);
                analysisByPanelNb.putIfAbsent(panelName, 0);
                analysisByPanelNb.put(panelName, analysisByPanelNb.get(panelName)+1);
                runsByPanelNb.putIfAbsent(panelName, new HashSet<>());
                runsByPanelNb.get(panelName).add(runId);
            }
            RunsStatistics runsStatistics = new RunsStatistics(runIds.size(), analysisIdsCount);
            for (String panelName : analysisByPanelNb.keySet()) {
                int runNb = runsByPanelNb.get(panelName).size();
                int analysisNb = analysisByPanelNb.get(panelName);
                runsStatistics.getAnalysisByPanelNb().put(panelName, new Pair<>(runNb, analysisNb));
            }
            return runsStatistics;
        }
    }

    public static class RunsStatistics {

        private int runNb;
        private int analysisNb;
        private final HashMap<String, Pair<Integer, Integer>> analysisByPanelNb = new HashMap<>();

        public RunsStatistics(int runNb, int analysisNb) {
            this.runNb = runNb;
            this.analysisNb = analysisNb;
        }

        public int getRunNb() {return runNb;}

        public void setRunNb(int runNb) {
            this.runNb = runNb;
        }

        public int getAnalysisNb() {return analysisNb;}

        public void setAnalysisNb(int analysisNb) {
            this.analysisNb = analysisNb;
        }

        public HashMap<String, Pair<Integer, Integer>> getAnalysisByPanelNb() {return analysisByPanelNb;}
    }
}
