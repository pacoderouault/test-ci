module NGSDiagLim {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.web;
    requires java.sql;
    requires java.net.http;
    requires org.apache.logging.log4j;
    requires com.dlsc.gemsfx;
    requires com.h2database;
    requires org.controlsfx.controls;
    requires htsjdk;
    requires org.jfxtras.styles.jmetro;
    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires org.apache.commons.codec;
    requires commons.math3;
    requires org.apache.commons.collections4;
    requires org.apache.pdfbox;
    requires poi.ooxml.schemas;
    requires poi.ooxml;
    requires xmlbeans;
//    requires ooxml.schemas;
    requires java.desktop;
    requires java.instrument;
    requires jol.core;
    requires de.gsi.chartfx.chart;
    requires de.gsi.chartfx.dataset;
    requires univocity.parsers;

    exports ngsdiaglim.modeles;
    exports ngsdiaglim.modeles.users;
    exports ngsdiaglim.modeles.users.Roles;
    exports ngsdiaglim.modeles.analyse;
    exports ngsdiaglim.modeles.parsers;
    exports ngsdiaglim.modeles.variants;
    exports ngsdiaglim.modeles.variants.predictions;
    exports ngsdiaglim.modeles.variants.populations;
    exports ngsdiaglim.modeles.biofeatures;
    exports ngsdiaglim.modeles.ciq;
    exports ngsdiaglim.modeles.igv;
    exports ngsdiaglim.modeles.reports;
    exports ngsdiaglim.modeles.reports.bgm;
    exports ngsdiaglim.controllers;
    exports ngsdiaglim.controllers.dialogs;
    exports ngsdiaglim.controllers.cells;
    exports ngsdiaglim.controllers.analysisview;
    exports ngsdiaglim.controllers.analysisview.cnv;
    exports ngsdiaglim.controllers.analysisview.reports.bgm;
    exports ngsdiaglim.controllers.analysisview.reports.anapath;
    exports ngsdiaglim.controllers.charts;
    exports ngsdiaglim.database;
    exports ngsdiaglim.database.dao;
    exports ngsdiaglim.skins;
    exports ngsdiaglim;
    exports ngsdiaglim.modules;
    exports ngsdiaglim.enumerations;
    exports ngsdiaglim.exceptions;
    exports ngsdiaglim.stats;
    exports ngsdiaglim.cnv;
    exports ngsdiaglim.cnv.parsers;
    exports ngsdiaglim.cnv.caller;
    exports ngsdiaglim.controllers.analysisview.ciq;

    opens ngsdiaglim to javafx.fxml;
    opens ngsdiaglim.controllers to javafx.fxml;
    opens ngsdiaglim.controllers.dialogs to javafx.fxml;
    opens ngsdiaglim.controllers.cells to javafx.fxml;
    opens ngsdiaglim.controllers.analysisview to javafx.fxml;
    opens ngsdiaglim.controllers.analysisview.cnv to javafx.fxml;
    opens ngsdiaglim.controllers.analysisview.reports.bgm to javafx.fxml;
    opens ngsdiaglim.controllers.analysisview.reports.anapath to javafx.fxml;
    opens ngsdiaglim.controllers.ui.popupfilters to javafx.fxml;
    opens ngsdiaglim.skins to javafx.fxml;
    opens ngsdiaglim.modules to javafx.fxml;
    opens ngsdiaglim.controllers.charts to javafx.fxml;
    opens ngsdiaglim.modeles.reports.bgm to javafx.fxml;
    opens ngsdiaglim.controllers.analysisview.ciq to javafx.fxml;
}