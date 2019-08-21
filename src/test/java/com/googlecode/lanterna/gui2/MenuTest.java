package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;

import java.io.File;
import java.io.IOException;

public class MenuTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new MenuTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        MenuBar menubar = new MenuBar();

        // "File" menu
        Menu menuFile = new Menu("File");
        menubar.addMenu(menuFile);
        menuFile.addMenuItem("Open...", new Runnable() {
            public void run() {
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null)
                    MessageDialog.showMessageDialog(
                            textGUI, "Open", "Selected file:\n" + file, MessageDialogButton.OK);
            }
        });
        menuFile.addMenuItem("Exit", new Runnable() {
            public void run() {
                System.exit(0);
            }
        });

        Menu countryMenu = new Menu("Country");
        menubar.addMenu(countryMenu);

        Menu germanySubMenu = new Menu("Germany");
        countryMenu.addSubMenuItem(germanySubMenu);
        for (String state: GERMANY_STATES) {
            germanySubMenu.addMenuItem(state, DO_NOTHING);
        }
        Menu japanSubMenu = new Menu("Japan");
        countryMenu.addSubMenuItem(japanSubMenu);
        for (String prefecture: JAPAN_PREFECTURES) {
            japanSubMenu.addMenuItem(prefecture, DO_NOTHING);
        }

        // "Help" menu
        Menu menuHelp = new Menu("Help");
        menubar.addMenu(menuHelp);
        menuHelp.addMenuItem("Homepage", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "Homepage", "https://github.com/mabe02/lanterna", MessageDialogButton.OK);
            }
        });
        menuHelp.addMenuItem("About", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "About", "Lanterna drop-down menu", MessageDialogButton.OK);
            }
        });

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        Panel contentPane = new Panel(new BorderLayout());
        contentPane.addComponent(menubar, BorderLayout.Location.TOP);
        contentPane.addComponent(new EmptySpace(TextColor.ANSI.YELLOW, new TerminalSize(40, 15)));
        window.setComponent(contentPane);

        // Create textGUI and start textGUI
        textGUI.addWindow(window);
    }

    private static final Runnable DO_NOTHING = new Runnable() {
        @Override
        public void run() {
        }
    };

    private static final String[] GERMANY_STATES = new String[]{
            "Baden-Württemberg","Bayern","Berlin","Brandenburg","Bremen","Hamburg","Hessen","Mecklenburg-Vorpommern",
            "Niedersachsen","Nordrhein-Westfalen","Rheinland-Pfalz","Saarland","Sachsen","Sachsen-Anhalt",
            "Schleswig-Holstein","Thüringen",
    };

    private static final String[] JAPAN_PREFECTURES = new String[]{
            "Aichi","Akita","Aomori","Chiba","Ehime","Fukui","Fukuoka","Fukushima","Gifu","Gunma","Hiroshima","Hokkaido",
            "Hyōgo","Ibaraki","Ishikawa","Iwate","Kagawa","Kagoshima","Kanagawa","Kōchi","Kumamoto","Kyoto","Mie",
            "Miyagi","Miyazaki","Nagano","Nagasaki","Nara","Niigata","Ōita","Okayama","Okinawa","Osaka","Saga","Saitama",
            "Shiga","Shimane","Shizuoka","Tochigi","Tokushima","Tokyo","Tottori","Toyama","Wakayama","Yamagata",
            "Yamaguchi","Yamanashi",
    };
}
