package mada_huffman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * mada Programmieraufgabe Huffman
 * Klasse 2iCa
 * FS 2016
 * 
 * @author Patric Steiner
 *
 * This program can encode/decode a string with the huffman coding scheme.
 * 
 * There are functions to import and export
 *   - Raw ASCII Text (*.txt)
 *   - Huffman compressed text (*.dat)
 *   - Huffman code table (*.txt)
 *   
 * Please note that error handling is not covered in this program. It only works if every input is correct.
 * There is a very basic GUI for convenience reasons.
 */
public class HuffmanApp extends Application {
    
	//Default file names
	static final String FILE_RAWTEXT = "text.txt";
	static final String FILE_ENCODED = "output.dat";
	static final String FILE_CODETABLE = "dec_tab.txt";
	
    TextArea tex_raw;
    TextArea tex_encoded;
    TextArea tex_codetable;
    TextArea tex_frequency;
	
    public static void main(String[] args) {
    	launch();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {      
        //Menu
        Menu men_import = new Menu("Import");
        Menu men_export = new Menu("Export");
        MenuItem men_importraw = new MenuItem("Import ASCII file");
        MenuItem men_importencoded = new MenuItem("Import encoded file");
        MenuItem men_importcodetable = new MenuItem("Import code table");
        MenuItem men_exportraw = new MenuItem("Export raw text file");
        MenuItem men_exportencoded = new MenuItem("Export encoded file");
        MenuItem men_exportcodetable = new MenuItem("Export code table");
        men_import.getItems().addAll(men_importraw, men_importencoded, men_importcodetable);
        men_export.getItems().addAll(men_exportraw, men_exportencoded, men_exportcodetable);
        
        //File Choosers
        FileChooser fil_raw = new FileChooser();
        fil_raw.setInitialFileName(FILE_RAWTEXT);
        fil_raw.getExtensionFilters().add(new ExtensionFilter("TEXT files (*.txt)", "*.txt"));
        FileChooser fil_encoded = new FileChooser();
        fil_encoded.setInitialFileName(FILE_ENCODED);
        fil_encoded.getExtensionFilters().add(new ExtensionFilter("BINARY files (*.dat)", "*.dat"));
        FileChooser fil_codetable = new FileChooser();
        fil_codetable.setInitialFileName(FILE_CODETABLE);
        fil_codetable.getExtensionFilters().add(new ExtensionFilter("TEXT files (*.txt)", "*.txt"));
        
        //Menu Events
        men_importraw.setOnAction(e -> {
        	try {
				String content = IOUtil.readAsciiFile(fil_raw.showOpenDialog(primaryStage).getPath());
				tex_raw.setText(content);
			} catch (Exception e1) {
				new Alert(AlertType.ERROR, e1.getMessage()).showAndWait();
			}
        });
        men_importencoded.setOnAction(e -> { 
            try {
            	byte[] bytes = IOUtil.readBytesFromFile(fil_encoded.showOpenDialog(primaryStage).getPath());
				String content = IOUtil.byteArrayToBinaryString(bytes);
				tex_encoded.setText(content);
			} catch (Exception e1) {
				new Alert(AlertType.ERROR, e1.getMessage()).showAndWait();
			}
        });
        men_importcodetable.setOnAction(e -> { 
            try {
                String content = IOUtil.readAsciiFile(fil_codetable.showOpenDialog(primaryStage).getPath());
                tex_codetable.setText(content);
            } catch (Exception e1) {
                new Alert(AlertType.ERROR, e1.getMessage()).showAndWait();
            }
        });
        men_exportraw.setOnAction(e -> {
            try {
                String binaryString = tex_raw.getText();
                IOUtil.writeToFile(binaryString, fil_raw.showSaveDialog(primaryStage).getPath());       
            } catch (Exception e1) {
                new Alert(AlertType.ERROR, e1.getMessage()).showAndWait();
            }
        });
        men_exportencoded.setOnAction(e -> {
            try {
                String binaryString = tex_encoded.getText();
                byte[] bytes = IOUtil.binaryStringToByteArray(binaryString);
                IOUtil.writeBytesToFile(bytes, fil_encoded.showSaveDialog(primaryStage).getPath());
            } catch (Exception e1) {
                new Alert(AlertType.ERROR, e1.getMessage()).showAndWait();
            }
        });
        men_exportcodetable.setOnAction(e -> {
            try {
				String codeTable = tex_codetable.getText();
				IOUtil.writeToFile(codeTable, fil_codetable.showSaveDialog(primaryStage).getPath());
			} catch (Exception e1) {
				new Alert(AlertType.ERROR, e1.getMessage()).showAndWait();
			}
        });

        //Textareas
        tex_raw = new TextArea();
        tex_encoded = new TextArea();
        tex_codetable = new TextArea();
        tex_frequency = new TextArea();
        		
        //Labels
        Label lab_raw = new Label("ASCII text:");
        Label lab_encoded = new Label("Huffman encoded:");
        Label lab_codetable = new Label("Code table:");
        Label lab_frequency = new Label("Character frequencies:");
        
        
        //Buttons
        Button but_encode = new Button(" Encode > ");
        Button but_decode = new Button(" < Decode ");
        but_decode.setMinWidth(100);
        but_encode.setMinWidth(100);
        
        //Button Events
        but_encode.setOnAction(e -> {
        	int[] frequencies = Huffman.getCharacterFrequency(tex_raw.getText());
        	HuffmanTree tree = new HuffmanTree(frequencies);
        	CodeTable codeTable = new CodeTable(tree);
        	tex_frequency.setText(Huffman.frequenciesToString(frequencies));
        	tex_encoded.setText(Huffman.encode(codeTable, tex_raw.getText()));
        	tex_codetable.setText(codeTable.toString());
        });
        but_decode.setOnAction(e -> {
            CodeTable codeTable = new CodeTable(tex_codetable.getText());
            tex_raw.setText(Huffman.decode(codeTable, tex_encoded.getText()));
        });
        
        //Main pane, Layout
        BorderPane pane = new BorderPane();
        pane.setPrefWidth(800);
        pane.setPrefHeight(600);
        primaryStage.setTitle("mada Huffman");
        pane.setTop(new MenuBar(men_import, men_export));
        pane.setCenter(new HBox(new VBox(lab_raw, tex_raw), new VBox(but_encode, but_decode), new VBox(lab_encoded, tex_encoded)));
        pane.setBottom(new HBox(new VBox(lab_frequency, tex_frequency), new VBox(lab_codetable, tex_codetable)));
        for (TextArea tex : new TextArea[] { tex_raw, tex_encoded,tex_frequency,tex_codetable} ) {
        	tex.setStyle("-fx-font-family: monospace;");
        	tex.prefWidthProperty().bind(pane.widthProperty().divide(2));
        	tex.prefHeightProperty().bind(pane.heightProperty().divide(2));
        	tex.setWrapText(true);
        }
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }
}
