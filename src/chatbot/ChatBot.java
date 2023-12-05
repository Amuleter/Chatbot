/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.Statement;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
//////////////////// Import AIML //////////////////////
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;
/////////////////////////////////

/**
 *
 * @author OverLoading
 */
public class ChatBot extends JFrame implements ActionListener{
    JButton an1,an2;
    JTextField bjk;
    JTextArea area;
    JLabel bq1,bq2;
    JPanel p1;
    JTable table;
    ResultSet tableresultSet;
    String username = "chatdb";
    String password = "chatdb234";
    String question_words[] = {"who", "where", "when", "what", "why","how"};
    String article[] = {"is", "are", "the", "of", "to" };
    String Exclamation[]    = {"hi", "hello"};
    String Pronoun[]    =  {"i", "my", "you", "your", "their"};
    String sad_emoji = "\uD83D\uDE25";
    String happy_emoji = "\uD83D\uDE03";
    String teeth_emoji = "\uD83D\uDE01";
    String question_emoji = "";
    // ini AIML
    private static final boolean TRACE_MODE = false;
	static String botName = "JUZEON";
    // Load AIML Script
    String scriptPath = getScriptPath();
    Bot bot = new Bot("juzeon", scriptPath);
	Chat chatSession = new Chat(bot);
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ChatBot();
    }
    
    public ChatBot() {
 
        an1=new JButton("Send");  
        an2=new JButton("Exit");
        bjk=new JTextField(10);
        area=new JTextArea();
        JScrollPane textscrollPane = new JScrollPane(area);
        bq1=new JLabel("ChatBot~");
        bq2=new JLabel("Type a message:");
        table = new JTable();
        table.setCellSelectionEnabled(true);
        // AIML setting
        MagicBooleans.trace_mode = TRACE_MODE;
		bot.brain.nodeStats();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                String selectedData = "";
                if (table.getSelectedRow() > -1) {
                    // print first column value from selected row
                    selectedData = table.getValueAt(table.getSelectedRow(), 0).toString();
                }
                if(!selectedData.isEmpty()){
                    try {
                        update_selection_count(selectedData);
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                    selectedData = selectedData.replaceAll("my", "your");
                    area.append("\n\nMe：No.\n\n    [JUZEON]Robot：" + selectedData + question_emoji);
                }
              DefaultTableModel model = (DefaultTableModel) table.getModel();
              model.setRowCount(0);
            }

          });
       
        JScrollPane scrollPane = new JScrollPane(table);
        
        p1=new JPanel();
        area.append("[JUZEON]Robot say：Hi");
        p1.add(bq2);p1.add(bjk);p1.add(an1);p1.add(an2);
 
        an1.addActionListener(this);
        an1.setActionCommand("go");
         
        an2.addActionListener(this);
        an2.setActionCommand("exit");
        this.add(bq1,BorderLayout.NORTH);
        this.add(textscrollPane);
        this.add(p1,BorderLayout.SOUTH);
        this.add(scrollPane,BorderLayout.EAST);
        this.setTitle("ChatBot");
        this.setSize(800,400);
        this.setLocation(100,100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getActionCommand().equals("go")){
            String text=bjk.getText();
            String texttest=robotedit();
            if(!area.getText().isEmpty()){
                area.append("\n\n");
            }
            area.append("    Me："+text+"\n\n    [JUZEON]Robot："+texttest);
            bjk.setText("");
        }else if(e.getActionCommand().equals("exit")){
            System.exit(0);
        }
    }
    
    public String robotedit (){
        String text=bjk.getText();
        
        if(text.isEmpty())
            return "can't be empty.";
         
        if (MagicBooleans.trace_mode)
						System.out.println(
								"STATE=" + text + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0)
										+ ":TOPIC=" + chatSession.predicates.get("topic"));

		return chatSession.multisentenceRespond(text);
        //return "~Sorry~, I am not understand what are you say";
         
    }
    
    public boolean in_array(String[] array, String word){
        for(int x = 0; x < array.length; x++){
            if(array[x].contentEquals(word.toLowerCase())){
                return true;
            }
         }
        
        return false;
    }
    
    public boolean update_selection_count(String input) throws SQLException{
        String SQL_Update = "UPDATE STATEMENT_RECORD SET SELECTED_COUNT = SELECTED_COUNT +1 WHERE LOWER(WHOLE_STATEMENT) LIKE ?";
         String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
        PreparedStatement preparedStatement = con.prepareStatement(SQL_Update);

        preparedStatement.setString(1, input);
        int affacted_rows = preparedStatement.executeUpdate();
        
        if( affacted_rows > 0 ){
            return true;
        }
        return false;
    }
    
    public ArrayList<String> retrive_or_keyword(String input){
        String delimiter = " ";
        String[] tempArray;
        ArrayList<String> newarray = new ArrayList<String>();
        input = input.toLowerCase();
        boolean in_array = false;
        
        input = input.replace("?", "");
        tempArray = input.split(delimiter);
        
        for (int i = 0; i < tempArray.length; i++){
            
            if( in_array(question_words, tempArray[i]) ){
                in_array = true;
            }else if( in_array(article, tempArray[i]) ){
                in_array = true;
            }
            
            if(!in_array){
                newarray.add(tempArray[i]);
            }
            in_array = false;
        }
        
        return newarray;
    }
    
    public ArrayList<String> retrive_and_keyword(String input){
        String delimiter = " ";
        String[] tempArray;
        ArrayList<String> newarray = new ArrayList<String>();
        input = input.toLowerCase();
        boolean in_array = false;
        
        input = input.replace("?", "");
        tempArray = input.split(delimiter);
        
        for (int i = 0; i < tempArray.length; i++){
            if( in_array(question_words, tempArray[i]) && tempArray.length > i+1 ){
                if( !in_array(question_words, tempArray[i+1]) && !in_array(article, tempArray[i+1]) ){
                    newarray.add(tempArray[i+1]);
                }
            }else if( in_array(article, tempArray[i]) && tempArray.length > i+1 ){
                if( !in_array(article, tempArray[i+1]) ){
                    newarray.add(tempArray[i+1]);
                }
            }else if( in_array(Pronoun, tempArray[i]) && tempArray.length > i+1 ){
                    newarray.add(tempArray[i+1]);
            }
        }
        
        return newarray;
    }
    
    public String Statement_Record(String Statement) throws SQLException, ClassNotFoundException{
        String SQL_SELECT = "SELECT COUNT(*) as count FROM STATEMENT_RECORD WHERE LOWER(WHOLE_STATEMENT) LIKE ?";
        String SQL_INSERT = "INSERT INTO STATEMENT_RECORD ( WHOLE_STATEMENT ) VALUES ( ? )";
        
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
        PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
        preparedStatement.setString(1, Statement);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        resultSet.next();
        int count = resultSet.getInt(1);
        
        if( count == 0 ){
            preparedStatement = con.prepareStatement(SQL_INSERT);

            preparedStatement.setString(1, Statement);
            preparedStatement.executeUpdate();
            return "Okay. I will remember it.";
        }else{
            return "Yes. I know it.";
        }
    }
    
    public static DefaultTableModel buildTableModel(ResultSet rs)
        throws SQLException {
   
    ResultSetMetaData metaData = rs.getMetaData();

    // names of columns
    Vector<String> columnNames = new Vector<String>();
    int columnCount = metaData.getColumnCount();
    for (int column = 1; column <= columnCount; column++) {
        columnNames.add(metaData.getColumnName(column));
    }

    // data of the table
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    while (rs.next()) {
        Vector<Object> vector = new Vector<Object>();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            vector.add(rs.getObject(columnIndex));
        }
        data.add(vector);
    }

    return new DefaultTableModel(data, columnNames);

}

//////////////// load AIML script file //////////////////////////////
private static String getScriptPath() {
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		path = path.substring(0, path.length() - 2);
		String scriptPath = path + File.separator + "src" + File.separator + "resources";
		return scriptPath;
	}

}



