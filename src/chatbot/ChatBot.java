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
         
        
        String[] tempArray = text.split(" ");
        for(int i=0;i<question_words.length;i++){
            if(text.toLowerCase().contains(question_words[i]) || text.toLowerCase().contains("?") ){
                return question_process(text);
            }
        }
        
        for(int i=0;i<Exclamation.length;i++){
            if( in_array(tempArray, Exclamation[i])){
                return "Yes, maybe I help you?";
            }
        }
        
        try{
            is_location(tempArray, text);
            return Statement_Record(text);
       } catch (SQLException e) {
           System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
       }
        
        
        return "~Sorry~, I am not understand what are you say";
         
    }
    
    public boolean is_location(String[] array, String statement) throws SQLException{
        String name = "";
        String SQL_INSERT = "INSERT INTO LOCATION ( STATEMENT, NAME ) VALUES ( ?, ? )";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
         for(int i = 0;i < array.length;i++){
            if( array[i].equalsIgnoreCase("is") ){
                if( array[i+1].equalsIgnoreCase("a") && i+1 < array.length ){
                    String word = array[i+2].replace(".", "");
                    if( ( word.equalsIgnoreCase("state") || word.equalsIgnoreCase("country") || word.equalsIgnoreCase("village") ) && i+2 < array.length ){
                        for(int x = 0; x < i ; x++){
                            name+=" "+array[x];
                        }

                        try {
                            Class.forName(driver); 
                        } catch(java.lang.ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
                        PreparedStatement preparedStatement = con.prepareStatement(SQL_INSERT);

                        preparedStatement.setString(1, statement);
                        preparedStatement.setString(2, name);
                        preparedStatement.executeUpdate();

                        return true;
                    }   
                }
            }
        }
         
        return false;
    }
    public boolean UserResponse(String input){
        input = input.replace(".", "");
        String delimiter = " ";
        String[] tempArray;
        tempArray = input.split(delimiter);
        if( tempArray.length == 1 && tempArray[0].toLowerCase().contentEquals("no") ){
            return false;
        }
        return false;
    }
    
    public String question_process(String input){
        String text = "";
        String[] tempArray;
        String unknow = "";
        String delimiter = " ";
        
        tempArray = input.split(delimiter);
       
            boolean in_array = false;
           
            if(question_words[0].contentEquals(tempArray[0])){
                text = who_question(input);
                in_array = true;
            }else if( question_words[1].contentEquals(tempArray[0]) ){
                text = where_question(input);
                in_array = true;
            }else if( question_words[2].contentEquals(tempArray[0]) ){
                text = when_question(input);
                in_array = true;
            }else if( question_words[0].contentEquals(tempArray[0]) ){
                text = what_question(input);
                in_array = true;
            }else if( tempArray[tempArray.length - 1].contains("?") && !in_array){
                if(actual_question(input)){
                    text = "Yes.";
                }else{
                    text = "No.";
                }
                in_array = true;
            }
            
         for (int i = 0; i < tempArray.length; i++){
            for(int x = 0; x < article.length; x++){
                if(article[x].contentEquals(tempArray[i])){
                    in_array = true;
                }
            }
            
            if(!in_array){
                unknow = tempArray[i];   
            }
        }
        
        
        if( !unknow.isEmpty() && text.isEmpty()){
            text = "What is " + unknow;
        }
        return text;
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
    
    public String who_question(String input){
        ArrayList<String> keywordarray = new ArrayList<String>();
        keywordarray = retrive_and_keyword(input);
        
        String SQL_SELECT = "SELECT WHOLE_STATEMENT FROM STATEMENT_RECORD WHERE LOWER(WHOLE_STATEMENT) LIKE ? ";
        
        for (int i = 0; i < keywordarray.size(); i++) {
            if(i > 0 ){
                SQL_SELECT += " AND WHOLE_STATEMENT LIKE ? ";
            }
        }
        SQL_SELECT +=" ORDER BY SELECTED_COUNT ASC";
        
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        String result = "Sorry, I dont know." + sad_emoji;
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
            
            for (int i = 0; i < keywordarray.size(); i++) { 		      
                preparedStatement.setString(i+1, "%"+keywordarray.get(i).toLowerCase()+"%");
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString("WHOLE_STATEMENT")+ happy_emoji;
                }
            result = result.replaceAll("my", "your") ;
            resultSet = preparedStatement.executeQuery(); 
            table.setModel(buildTableModel(resultSet));
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
     return result;
    }
    
    public String where_question(String input){
        String result = "Sorry, I don't know." + sad_emoji;
        
        ArrayList<String> keywordarray = new ArrayList<String>();
        keywordarray = retrive_or_keyword(input);
        
        String SQL_SELECT = "SELECT STATEMENT FROM LOCATION WHERE LOWER(NAME) LIKE ? ";
        
        for (int i = 0; i < keywordarray.size(); i++) {
            if(i > 0 ){
                SQL_SELECT += " OR LOWER(NAME) LIKE ? ";
            }
        }
        
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
            
            for (int i = 0; i < keywordarray.size(); i++) { 		      
                preparedStatement.setString(i+1, "%"+keywordarray.get(i).toLowerCase()+"%");
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString("STATEMENT") + teeth_emoji;
                }
            
            resultSet = preparedStatement.executeQuery(); 
            table.setModel(buildTableModel(resultSet));
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        return result;
    }
    
    public String when_question(String input){
        String result = "Sorry, I don't know." + sad_emoji;
        
        ArrayList<String> keywordarray = new ArrayList<String>();
        keywordarray = retrive_and_keyword(input);
        
        String SQL_SELECT = "SELECT WHOLE_STATEMENT FROM STATEMENT_RECORD ";
        SQL_SELECT += " JOIN DATETIME ON LOWER(WHOLE_STATEMENT) LIKE  '%' || LOWER(MONTHANDTIME) || '%' ";
        SQL_SELECT += " WHERE LOWER(WHOLE_STATEMENT) LIKE ? ";
        for (int i = 0; i < keywordarray.size(); i++) {
            if(i > 0 ){
                SQL_SELECT += " AND WHOLE_STATEMENT LIKE ? ";
            }
        }
        //SQL_SELECT +=" ORDER BY SELECTED_COUNT ASC ";
        SQL_SELECT += " GROUP BY WHOLE_STATEMENT ";
        //System.out.println(SQL_SELECT);
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
            
            for (int i = 0; i < keywordarray.size(); i++) { 		      
                preparedStatement.setString(i+1, "%"+keywordarray.get(i).toLowerCase()+"%");
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString("WHOLE_STATEMENT");
                }
                
            resultSet = preparedStatement.executeQuery(); 
            table.setModel(buildTableModel(resultSet));
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        return result;
    }
    
    public String what_question(String input){
        String result = "Sorry, I don't know." + sad_emoji;
        
        ArrayList<String> keywordarray = new ArrayList<String>();
        keywordarray = retrive_and_keyword(input);
        
        String SQL_SELECT = "SELECT WHOLE_STATEMENT FROM STATEMENT_RECORD WHERE LOWER(WHOLE_STATEMENT) LIKE ? ";
        
        for (int i = 0; i < keywordarray.size(); i++) {
            if(i > 0 ){
                SQL_SELECT += " AND WHOLE_STATEMENT LIKE ? ";
            }
        }
        SQL_SELECT +=" ORDER BY SELECTED_COUNT ASC";
        
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
            
            for (int i = 0; i < keywordarray.size(); i++) { 		      
                preparedStatement.setString(i+1, "%"+keywordarray.get(i).toLowerCase()+"%");
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString("WHOLE_STATEMENT");
                }
                
            resultSet = preparedStatement.executeQuery(); 
            table.setModel(buildTableModel(resultSet));
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        return result;
    }
    
    public String why_question(String input){
        String result = "Sorry, I don't know." + sad_emoji;
        
        ArrayList<String> keywordarray = new ArrayList<String>();
        keywordarray = retrive_and_keyword(input);
        
        String SQL_SELECT = "SELECT WHOLE_STATEMENT FROM STATEMENT_RECORD WHERE LOWER(WHOLE_STATEMENT) LIKE ? ";
        
        for (int i = 0; i < keywordarray.size(); i++) {
            if(i > 0 ){
                SQL_SELECT += " AND WHOLE_STATEMENT LIKE ? ";
            }
        }
        SQL_SELECT +=" ORDER BY SELECTED_COUNT ASC";
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
            
            for (int i = 0; i < keywordarray.size(); i++) { 		      
                preparedStatement.setString(i+1, "%"+keywordarray.get(i).toLowerCase()+"%");
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString("WHOLE_STATEMENT");
                }
                
            resultSet = preparedStatement.executeQuery(); 
            table.setModel(buildTableModel(resultSet));
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        return result;
    }
    
    public String how_question(String input){
        String result = "Sorry, I don't know." + sad_emoji;
        
        ArrayList<String> keywordarray = new ArrayList<String>();
        keywordarray = retrive_and_keyword(input);
        
        String SQL_SELECT = "SELECT WHOLE_STATEMENT FROM STATEMENT_RECORD WHERE LOWER(WHOLE_STATEMENT) LIKE ? ";
        
        for (int i = 0; i < keywordarray.size(); i++) {
            if(i > 0 ){
                SQL_SELECT += " AND WHOLE_STATEMENT LIKE ? ";
            }
        }
        
        SQL_SELECT +=" ORDER BY SELECTED_COUNT ASC";
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_SELECT);
            
            for (int i = 0; i < keywordarray.size(); i++) { 		      
                preparedStatement.setString(i+1, "%"+keywordarray.get(i).toLowerCase()+"%");
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString("WHOLE_STATEMENT");
                }
                
            resultSet = preparedStatement.executeQuery(); 
            table.setModel(buildTableModel(resultSet));
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        return result;
    }
    
    public boolean actual_question(String input){
        boolean result = false;
        input = input.replace("?", "");
        
        String SQL_INSERT = "SELECT WHOLE_STATEMENT FROM STATEMENT_RECORD WHERE LOWER(WHOLE_STATEMENT) LIKE ? ";
        String word = "";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        
        try {
            Class.forName(driver); 
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

         try {
            Connection con=DriverManager.getConnection("jdbc:derby://localhost:1527/chatdb;create=true", username,password);
            PreparedStatement preparedStatement = con.prepareStatement(SQL_INSERT);
            	      
            preparedStatement.setString(1, "%"+input.toLowerCase()+"%");
            
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                    result = true;
            }
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        return result;
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
}



class Write{
    static String wen[]=new String[1000];
    static String da[]=new String[1000];
 
    static FileWriter fw;
    static BufferedWriter bw;
 
    static FileReader fr;
    static BufferedReader br;
    static int shu=0;
    public static void write(){
        try{
            fw=new FileWriter("./talk.txt");
            bw=new BufferedWriter(fw);
            bw.write("sorry hahaha~\n");
            bw.write("作者 是居正这个BT的家伙创造了我~\n");
            bw.write("住 我住在我家~\n");
            bw.write("石头 我出布，你输了~\n");
            bw.write("剪刀 我出石头，你输了~\n");
            bw.write("布 我出剪刀，我赢了~~\n");
            bw.write("玩 来玩石头剪刀布吧~\n");
        }catch(Exception e){}
        finally{
            try{
                bw.flush();
                fw.close();
                bw.close();
            }catch(Exception e){System.out.println(e.toString());}
        }
    }
    public static void read(){
        System.out.println("readStart");
        try{
            File f=new File("./talk.txt");
            System.out.println("set");
            if(f.exists()==false){
                write();
                System.out.println("write");
            }
            fr=new FileReader("./talk.txt");
            br=new BufferedReader(fr);
 
            String s="";
            int i=0;
            while((s=br.readLine())!=null){
                System.out.println(s);
                String[] st=s.split(" ");
                System.err.println(st[0]);
                System.err.println(st[1]);
                wen[i]=st[0];
                da[i]=st[1];
                i++;
                System.out.println("No."+i+":"+wen[i]);
                System.out.println("No."+i+":"+da[i]);
            }
 
        }catch(Exception e){System.err.println(e.toString());}
 
    }
}