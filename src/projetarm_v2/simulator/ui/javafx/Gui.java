package projetarm_v2.simulator.ui.javafx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import projetarm_v2.simulator.boilerplate.ArmSimulator;

import org.dockfx.DockPane;
import org.dockfx.DockPos;

import java.util.Random;

public class Gui extends Application {

	private static int nbRamView = 1;
	
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	ArmSimulator simulator = new ArmSimulator();
    	
    	simulator.setProgram("b start;" + 
				"kek: .asciz \"test\";" + 
				".align;" + 
				"start: ldr r0,=kek;" + 
				"mov r1,#0xFF04;" +
				"blx r1");
    	
    	primaryStage.setTitle("ARMStrong");
        Image applicationIcon = new Image("file:logo.png");
        primaryStage.getIcons().add(applicationIcon);

        // create a dock pane that will manage our dock nodes and handle the layout
        DockPane dockPane = new DockPane();

        // create a default test node for the center of the dock area
        Pane editorPane = new Pane();

        // load an image to caption the dock nodes
        //Image dockImage = new Image(Gui.class.getResource("docknode.png").toExternalForm());

        //MENU

        final Menu fileMenu = new Menu("File");
        final Menu windowMenu = new Menu("Window");
        final Menu editMenu = new Menu("Edit");
        final Menu runMenu = new Menu("Run");
        final Menu helpMenu = new Menu("Help");
        
        fileMenu.getItems().add(new MenuItem("New"));
        fileMenu.getItems().add(new MenuItem("Open File..."));
        fileMenu.getItems().add(new MenuItem("Save As..."));
        final MenuItem exitMenu = new MenuItem("Exit");
        fileMenu.getItems().add(exitMenu);
        
        final Menu newMenu = new Menu("New Window");
        final MenuItem newMemoryWindow = new MenuItem("Memory");
        newMenu.getItems().add(newMemoryWindow);
        
        windowMenu.getItems().add(newMenu);
        windowMenu.getItems().add(new MenuItem("Preferences"));
        
        final MenuItem runMenuItem = new MenuItem("Run");
        runMenu.getItems().add(runMenuItem);
        runMenu.getItems().add(new MenuItem("Run Step by Step"));
        
        helpMenu.getItems().add(new MenuItem("About"));
        
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, windowMenu, editMenu, runMenu, helpMenu);
        
        ToolBar toolBar = new ToolBar(
                new Button("", new ImageView(new Image(getClass().getResource("/resources/switch.png").toExternalForm()))),
                new Separator(),
                new Button("", new ImageView(new Image(getClass().getResource("/resources/run.png").toExternalForm()))),
                new Button("", new ImageView(new Image(getClass().getResource("/resources/runByStep.png").toExternalForm()))),
                new Button("", new ImageView(new Image(getClass().getResource("/resources/reload.png").toExternalForm()))),
                new Button("", new ImageView(new Image(getClass().getResource("/resources/stop.png").toExternalForm())))
        );

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, toolBar, dockPane);
        VBox.setVgrow(dockPane, Priority.ALWAYS);

        primaryStage.setScene(new Scene(vbox, 800, 500));
        primaryStage.sizeToScene();

        RegistersView firstRegistersView = new RegistersView();
        firstRegistersView.getNode().dock(dockPane, DockPos.LEFT);

        CodeEditor codeEditor = new CodeEditor();
        codeEditor.getNode().dock(dockPane, DockPos.LEFT);

        RamView firstRamView = new RamView();
        firstRamView.getNode().dock(dockPane, DockPos.RIGHT);

        ConsoleView console = new ConsoleView();
        console.getNode().dock(dockPane, DockPos.BOTTOM);
        
        primaryStage.show();
        
        newMemoryWindow.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	if(Gui.nbRamView == 1) {
		    		RamView moreRamView = new RamView();
		        	moreRamView.getNode().dock(dockPane, DockPos.RIGHT);
		        	console.getNode().dock(dockPane, DockPos.BOTTOM);
		        	Gui.nbRamView += 1;
            	}else {
            		//TODO display error in the console
            	}
            }
        });
        
        exitMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	primaryStage.close();
            }
        });
        
        runMenuItem.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
            	simulator.run();
            }
        });
        
        // test the look and feel with both Caspian and Modena
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        // initialize the default styles for the dock pane and undocked nodes using the DockFX
        // library's internal Default.css stylesheet
        // unlike other custom control libraries this allows the user to override them globally
        // using the style manager just as they can with internal JavaFX controls
        // this must be called after the primary stage is shown
        // https://bugs.openjdk.java.net/browse/JDK-8132900
        DockPane.initializeDefaultUserAgentStylesheet();

        // TODO: after this feel free to apply your own global stylesheet using the StyleManager class
    }

    private TreeView<String> generateRandomTree() {
        // create a demonstration tree view to use as the contents for a dock node
        TreeItem<String> root = new TreeItem<String>("Root");
        TreeView<String> treeView = new TreeView<String>(root);
        treeView.setShowRoot(false);

        // populate the prototype tree with some random nodes
        Random rand = new Random();
        for (int i = 4 + rand.nextInt(8); i > 0; i--) {
            TreeItem<String> treeItem = new TreeItem<String>("Item " + i);
            root.getChildren().add(treeItem);
            for (int j = 2 + rand.nextInt(4); j > 0; j--) {
                TreeItem<String> childItem = new TreeItem<String>("Child " + j);
                treeItem.getChildren().add(childItem);
            }
        }

        return treeView;
    }
}